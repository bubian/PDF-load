package com.pds.pdf.process;

/**
 * @author: pengdaosong
 * @CreateTime: 2020/10/23 10:44 AM
 * @Email: pengdaosong@medlinker.com
 * @Description:
 */
public interface ProgressListener {
    void onProgress(int i);
    void onStartDownload();
    void onComplete();
}
