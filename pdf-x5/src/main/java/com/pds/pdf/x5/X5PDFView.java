package com.pds.pdf.x5;

import android.content.Context;
import android.os.Bundle;
import android.os.Environment;
import android.text.TextUtils;
import android.widget.FrameLayout;
import android.widget.RelativeLayout;
import android.widget.Toast;
import com.pds.pdf.x5.download.DownLoadManager;
import com.pds.pdf.x5.download.DownloadListener;
import com.tencent.smtt.sdk.TbsReaderView;
import java.io.File;

/**
 * @author: pengdaosong
 * @CreateTime: 2020/10/23 6:51 PM
 * @Email: pengdaosong@medlinker.com
 * @Description:
 * 错误：TbsReaderView: not supported by:pdf 这是X5没有正常初始化造成的
 */
public class X5PDFView extends FrameLayout {

    private static final String DEFAULT_CACHE_PATH = Environment.getExternalStorageDirectory().getAbsolutePath() + "/pdf/";
    private String mCacheFilePath = DEFAULT_CACHE_PATH;

    private TbsReaderView mTbsReaderView;
    private boolean mEnableCache = true;
    private String mUrl;
    private File mFile;
    private String mPDFName;
    private DownLoadManager mDownLoadManager;

    public X5PDFView(Context context) {
        super(context);
        mDownLoadManager = new DownLoadManager();
    }

    public void loadPDFromUrl(String url) {
        mUrl = url;
        mPDFName = FileUtils.getFileNameByUrl(url);
        mFile = new File(mCacheFilePath, mPDFName);
        //文件已经存在，直接获取本地文件打开，否则从网络现在文件，文件下载成功之后再打开
        if (mFile.exists() && mEnableCache) {
            load();
            return;
        }

        mDownLoadManager.downLoad(mUrl, mFile.getAbsolutePath(), new DownloadListener() {
            @Override
            public void onProgress(int i) {
            }

            @Override
            public void onFinishDownload(boolean b) {
                post(new Runnable() {
                    @Override
                    public void run() {
                        load();
                    }
                });
            }

            @Override
            public void onStartDownload() {
            }

            @Override
            public void onFail(String message) {
            }
        });
    }

    public void loadPDFFromFile(String filePath) {
        mFile = new File(filePath);
        if (!mFile.exists()){
            Toast.makeText(getContext(),"文件不存在",Toast.LENGTH_SHORT).show();
            return;
        }
        load();
    }

    public void enableCache(boolean enable) {
        mEnableCache = enable;
    }

    private void load() {
        Bundle bundle = new Bundle();
        bundle.putString("filePath", mFile.getAbsolutePath());
        bundle.putString("tempPath", Environment.getExternalStorageDirectory().getPath());
        //创建 TbsReaderView 对象并将其添加到帧布局中
        mTbsReaderView = new TbsReaderView(getContext(), mReaderCallback);
        addView(mTbsReaderView, new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.MATCH_PARENT));
        //加载,就是在这一步，app首次下载时，即使有X5内核 preOpen 也会返回 false
        //只有 kill 进程后并重启才会有效果，这个问题搞了好久
        //索性就在返回 false 之后换一种加载方式
        boolean result = mTbsReaderView.preOpen(parseFormat(mPDFName), false);
        if (result) {
            mTbsReaderView.openFile(bundle);
        } else {
            //用 PDFView 加载
        }
    }

    public void cachePath(String path) {
        if (!TextUtils.isEmpty(path)) {
            mCacheFilePath = path;
        }
    }

    private String parseFormat(String fileName) {
        return fileName.substring(fileName.lastIndexOf(".") + 1);
    }

    private TbsReaderView.ReaderCallback mReaderCallback = new TbsReaderView.ReaderCallback() {
        @Override
        public void onCallBackAction(Integer integer, Object o, Object o1) {

        }
    };
}
