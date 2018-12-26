package com.hrw.downapplibrary.http;

import android.content.Context;
import android.util.Log;

import com.hrw.downapplibrary.callback.DownloadCallBack;
import com.hrw.downapplibrary.util.Constant;
import com.hrw.downapplibrary.util.DownType;
import com.hrw.utilslibrary.file.MtFileUtil;
import com.hrw.utilslibrary.sharepreferences.MtSPHelper;

import java.io.File;
import java.io.InputStream;
import java.io.RandomAccessFile;
import java.util.concurrent.TimeUnit;

import io.reactivex.Observer;
import io.reactivex.disposables.Disposable;
import okhttp3.OkHttpClient;
import okhttp3.ResponseBody;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * @version 1.0.0
 * @author:hrw
 * @date:2018/12/24 17:29
 * @desc:
 */
public class RetrofitHelper {
    private static final int DEFAULT_TIMEOUT = 10;
    private String TAG = "DOWN_HELPER";

    private RetrofitHelper() {

    }

    public static RetrofitHelper getRetrofit() {
        return new RetrofitHelper();
    }

    private ApiService getApi() {
        OkHttpClient okHttpClient = new OkHttpClient.Builder()
                .connectTimeout(DEFAULT_TIMEOUT, TimeUnit.SECONDS)
                .build();
        Retrofit retrofit = new Retrofit.Builder()
                .client(okHttpClient)
                .addConverterFactory(GsonConverterFactory.create())
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .baseUrl(ApiService.BASE_URL)
                .build();
        return retrofit.create(ApiService.class);
    }

    public void addDownLoad(final Context context, final long range, final String downUrl, final String saveFileName, final DownType downType, final DownloadCallBack downloadCallback) {
        //断点续传时请求的总长度
        File file = new File(MtFileUtil.getAppPath(context) + downType.getPath(), saveFileName);
        ApiService mApiService = getApi();
        String currentSizeLength = "-";
        if (file.exists()) {
            currentSizeLength += file.length();
        }
//        System.out.println("addDownLoad " + Thread.currentThread().getName());
        mApiService.executeDownload("bytes=" + range + currentSizeLength, downUrl)
                .subscribe(new Observer<ResponseBody>() {
                    Disposable disposable;

                    @Override
                    public void onSubscribe(Disposable d) {
                        disposable = d;
                    }

                    @Override
                    public void onNext(ResponseBody responseBody) {
//                        System.out.println("onNext " + Thread.currentThread().getName());
                        RandomAccessFile randomAccessFile = null;
                        InputStream inputStream = null;
                        long currentSize = range;
                        long totalSize = 0;
                        long responseLength;
                        try {
                            byte[] buf = new byte[2048];
                            int len;
                            responseLength = responseBody.contentLength();
                            inputStream = responseBody.byteStream();
                            String filePath = MtFileUtil.getAppPath(context) + downType.getPath();
                            File file = new File(filePath, saveFileName);
                            File dir = new File(filePath);
                            if (!dir.exists()) {
                                dir.mkdirs();
                            }
                            randomAccessFile = new RandomAccessFile(file, "rwd");
                            if (range == 0) {
                                randomAccessFile.setLength(responseLength);
                                totalSize = responseLength;
                            } else {
                                totalSize = file.length();
                            }
                            randomAccessFile.seek(range);

                            int progress = 0;
                            int lastProgress;

                            while ((len = inputStream.read(buf)) != -1) {
                                randomAccessFile.write(buf, 0, len);
                                currentSize += len;
                                lastProgress = progress;
                                progress = (int) (currentSize * 100 / randomAccessFile.length());
                                if (progress > 0 && progress != lastProgress) {
                                    downloadCallback.onProgress(progress, currentSize, totalSize);
                                }
                                MtSPHelper.putLong(Constant.DOWN_APP_SP_TAG, downUrl, currentSize);
                            }
                            downloadCallback.onCompleted();

                        } catch (Exception e) {
                            Log.d(TAG, e.toString());
                            downloadCallback.onError(e.toString());
                            e.printStackTrace();
                        } finally {
                            try {
                                MtSPHelper.putLong(Constant.DOWN_APP_SP_TAG, downUrl, currentSize);
                                if (randomAccessFile != null) {
                                    randomAccessFile.close();
                                }

                                if (inputStream != null) {
                                    inputStream.close();
                                }

                            } catch (Exception e) {
                                e.printStackTrace();
                            }

                        }
                    }

                    @Override
                    public void onError(Throwable e) {
                        disposable.dispose();
                        downloadCallback.onError(e.toString());
                    }

                    @Override
                    public void onComplete() {
                        disposable.dispose();
                        downloadCallback.onCompleted();
                    }
                });

    }
}
