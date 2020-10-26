package com.pds.sample;

import android.os.Bundle;
import android.view.ViewGroup;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import com.pds.file.x5.core.QbCallback;
import com.pds.file.x5.core.QbSdkManager;
import com.pds.file.x5.download.DownloadListener;
import com.pds.file.x5.process.ProgressView;

/**
 * @author: pengdaosong
 * @CreateTime: 2020/10/26 3:10 PM
 * @Email: pengdaosong@medlinker.com
 * @Description:
 */
public class QBX5Activity extends AppCompatActivity {

    private String mFileUrl;
    private String mUrl;
    private QbSdkManager mManager;
    private ProgressView mProgressView;
    private static final String URL = "https://pub-med-casem.medlinker.com/guanxin_paitent_test.pdf";
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setQbContentView();
        mFileUrl = URL;
        mManager = new QbSdkManager();
        initQbCallback();
        setDownloadListener();
        loadPDFromUrl();
    }

    public void loadPDFromUrl() {
        mManager.loadPDFromUrl(this, mFileUrl);
    }

    public QbSdkManager getQbSdkManager() {
        return mManager;
    }

    public void setDownloadListener() {
        mManager.setDownloadListener(new DownloadListener() {
            @Override
            public void onProgress(int i) {
                mProgressView.onProgress(i);
            }

            @Override
            public void onFinishDownload(boolean b) {
                mProgressView.onComplete();
            }

            @Override
            public void onStartDownload() {
                mProgressView.onStartDownload();
            }

            @Override
            public void onFail(String message) {
                mProgressView.onComplete();
            }
        });
    }

    public void setQbContentView() {
        mProgressView = new ProgressView(this);
        ViewGroup.LayoutParams params = new ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT, 9);
        setContentView(mProgressView, params);
    }

    public void initQbCallback() {
        mManager.setQbCallback(new QbCallback<String>() {
            @Override
            public void onReceiveValue(String value) {
                if (QBX5Activity.this.needClose(value)) {
                    QBX5Activity.this.finish();
                }
            }
        });
    }

    public boolean needClose(String s) {
        return "fileReaderClosed".equals(s)
                || "openFileReader open in QB".equals(s)
                || "filepath error".equals(s)
                || "TbsReaderDialogClosed".equals(s)
                || "default browser:".equals(s)
                || "filepath error".equals(s);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mManager.destroy(this);
    }
}
