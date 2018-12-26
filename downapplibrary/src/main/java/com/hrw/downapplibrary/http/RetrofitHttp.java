package com.hrw.downapplibrary.http;

import android.content.Context;
import android.util.Log;

import com.hrw.downapplibrary.callback.DownloadCallBack;
import com.hrw.downapplibrary.util.Constant;
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


public class RetrofitHttp {

    private static final int DEFAULT_TIMEOUT = 10;
    private static final String TAG = "RetrofitClient";

    private ApiService apiService;

    private OkHttpClient okHttpClient;

    public static String baseUrl = ApiService.BASE_URL;

    private static RetrofitHttp instance;

    public static RetrofitHttp getInstance() {
        if (instance == null) {
            synchronized (RetrofitHttp.class) {
                if (instance == null) {
                    instance = new RetrofitHttp();
                }
            }
        }
        return instance;
    }

    private RetrofitHttp() {
        okHttpClient = new OkHttpClient.Builder()
                .connectTimeout(DEFAULT_TIMEOUT, TimeUnit.SECONDS)
                .build();
        Retrofit retrofit = new Retrofit.Builder()
                .client(okHttpClient)
                .addConverterFactory(GsonConverterFactory.create())
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .baseUrl(baseUrl)
                .build();
        apiService = retrofit.create(ApiService.class);
    }

    public void downloadFile(final Context context, final long range, final String url, final String fileName, final DownloadCallBack downloadCallback) {
        //断点续传时请求的总长度
        File file = new File(MtFileUtil.getAppPath(context) + Constant.DOWNLOAD_DIR, fileName);
        String currentSizeLength = "-";
        if (file.exists()) {
            currentSizeLength += file.length();
        }
        System.out.println("downloadFile " + Thread.currentThread().getName());
        apiService.executeDownload("bytes=" + Long.toString(range) + currentSizeLength, url)
                .subscribe(new Observer<ResponseBody>() {
                    @Override
                    public void onSubscribe(Disposable d) {

                    }

                    @Override
                    public void onNext(ResponseBody responseBody) {
                        System.out.println("onNext " + Thread.currentThread().getName());
                        RandomAccessFile randomAccessFile = null;
                        InputStream inputStream = null;
                        long currentSize = range;
                        long totalSize = 0;
                        long responseLength = 0;
                        try {
                            byte[] buf = new byte[2048];
                            int len = 0;
                            responseLength = responseBody.contentLength();
                            inputStream = responseBody.byteStream();
                            String filePath = MtFileUtil.getAppPath(context) + Constant.DOWNLOAD_DIR;
                            File file = new File(filePath, fileName);
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
                                MtSPHelper.putLong(Constant.DOWN_APP_SP_TAG, url, currentSize);
                            }
                            downloadCallback.onCompleted();

                        } catch (Exception e) {
                            Log.d(TAG, e.getMessage());
                            downloadCallback.onError(e.getMessage());
                            e.printStackTrace();
                        } finally {
                            try {
                                MtSPHelper.putLong(Constant.DOWN_APP_SP_TAG, url, currentSize);
                                System.out.println("结束后大小:" + currentSize);
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
                        downloadCallback.onError(e.toString());
                    }

                    @Override
                    public void onComplete() {
                    }
                });
    }
}