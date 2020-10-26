package com.pds.file.x5.download;

import android.text.TextUtils;
import android.util.Log;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.concurrent.Executors;

/**
 * @author: pengdaosong
 * @CreateTime: 2020/10/22 5:24 PM
 * @Email: pengdaosong@medlinker.com
 * @Description:
 */
public class DownLoadManager {
    private static final String TAG = "FileDownload";
    private Thread mDownloadThread;
    private boolean mRunning = false;
    private String mUrl;
    private boolean mRunMask = false;

    public void downLoad(final String url, final String filePath, final DownloadListener listener) {
        mUrl = url;
        if (TextUtils.equals(mUrl,url) && mRunning){
            Log.d(TAG,"same url task running");
            return;
        }
        mDownloadThread = Executors.defaultThreadFactory().newThread(new Runnable() {
            @Override
            public void run() {
                DownLoadManager.this.startDownLoad(url, filePath, listener);
            }
        });
        mDownloadThread.start();
        mRunning = true;
    }

    public void destroy(){
        mRunMask = false;
        if (null != mDownloadThread){
            mDownloadThread.interrupt();
        }
    }

    private void startDownLoad(String url, final String filePath, final DownloadListener listener) {
        mRunMask = true;
        FileOutputStream fos = null;
        InputStream is = null;
        long currentLen = 0;
        byte[] buf = new byte[1024];
        int len;
        try {
            HttpURLConnection httpURLConnection = getConnection(url);
            int contentLength = httpURLConnection.getContentLength();
            Log.d(TAG, "文件的大小是:" + contentLength);
            if (contentLength <= 32) {
                return;
            }
            is = httpURLConnection.getInputStream();
            File file = new File(filePath);
            File parentFile = file.getParentFile();
            if (!parentFile.exists()) {
                boolean mkdirs = parentFile.mkdirs();
            }
            if (!file.exists()) {
                boolean newFile = file.createNewFile();
            } else {
                // 若本地存在当前下载文件，且文件大小一致（说明是同一个文件）则，无需再向磁盘写入
                if (file.length() == contentLength) {
                    if (is != null) {
                        is.close();
                    }
                    listener.onProgress(100);
                    listener.onFinishDownload(true);
                    Log.e(TAG, "本地已有该文件------------->");
                    return;
                }
            }
            listener.onStartDownload();
            fos = new FileOutputStream(file);

            while ((len = is.read(buf)) != -1 && mRunMask) {
                fos.write(buf, 0, len);
                currentLen += len;
                int progress = (int) (currentLen * 100 / contentLength);
                Log.e(TAG, "下载中------------->" + progress);
                listener.onProgress(progress);
            }
            fos.flush();
            //如果下载文件成功，第一个参数为文件的绝对路径
            listener.onFinishDownload(false);

        } catch (Exception e) {
            listener.onFail(e.getMessage());
            e.printStackTrace();
        } finally {
            try {
                if (is != null) {
                    is.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }

            try {
                if (fos != null) {
                    fos.close();
                }
            } catch (Exception e) {
            }
        }
        mRunning = false;
    }

    private HttpURLConnection getConnection(String httpUrl) throws Exception {
        URL url = new URL(httpUrl);
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setRequestMethod("GET");
        connection.setRequestProperty("Content-Type", "application/octet-stream");
        connection.setDoOutput(true);
        connection.setDoInput(true);
        connection.setRequestProperty("Connection", "Keep-Alive");
        connection.connect();
        return connection;

    }
}
