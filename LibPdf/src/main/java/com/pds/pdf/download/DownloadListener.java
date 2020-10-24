package com.pds.pdf.download;

/**
 * @author: pengdaosong
 * @CreateTime: 2020/10/22 5:26 PM
 * @Email: pengdaosong@medlinker.com
 * @Description:
 */
public interface DownloadListener {
    void onProgress(int i);

    void onFinishDownload(boolean b);

    void onStartDownload();

    void onFail(String message);
}
