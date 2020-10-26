package com.pds.file.x5;

import android.content.Context;
import android.os.Bundle;
import android.os.Environment;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.Toast;
import com.pds.file.x5.core.Constants;
import com.pds.file.x5.core.ViewHelper;
import com.pds.file.x5.download.DownLoadManager;
import com.pds.file.x5.download.DownloadListener;
import com.pds.file.x5.process.ProgressListener;
import com.tencent.smtt.sdk.TbsReaderView;
import com.tencent.smtt.sdk.TbsReaderView.ReaderCallback;
import java.io.File;

/**
 * @author: pengdaosong
 * @CreateTime: 2020/10/23 6:51 PM
 * @Email: pengdaosong@medlinker.com
 * @Description: 错误：TbsReaderView: not supported by:file 这是X5没有正常初始化造成的
 */
public class X5PDFView extends RelativeLayout {

    private static final String DEFAULT_CACHE_PATH =
            Environment.getExternalStorageDirectory().getAbsolutePath() + "/pdf/";
    private String mCacheFilePath = DEFAULT_CACHE_PATH;

    private TbsReaderView mTbsReaderView;
    private boolean mEnableCache = true;
    private String mUrl;
    private File mFile;
    private Bundle bundle = new Bundle();

    private String mTempPath = Environment.getExternalStorageDirectory().getPath() + "/pdf/temp/";
    private String mPDFName;
    private DownLoadManager mDownLoadManager;
    private DownloadListener mDownloadListener;

    private PDFLoadListener mPDFLoadListener;
    private TbsReaderView.ReaderCallback mReaderCallback;
    private View mProgressView;

    public X5PDFView(Context context) {
        super(context);
        init(context);
    }

    public X5PDFView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public X5PDFView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    private void init(Context context) {
        mDownLoadManager = new DownLoadManager();
        mTbsReaderView = new TbsReaderView(context, new ReaderCallback() {
            @Override
            public void onCallBackAction(Integer integer, Object o, Object o1) {
                if (null != mReaderCallback) {
                    mReaderCallback.onCallBackAction(integer, o, o1);
                }
            }
        });
        int wh = LayoutParams.MATCH_PARENT;
        LayoutParams params = new LayoutParams(wh, wh);
        addView(mTbsReaderView,0,params);
    }

    private volatile int mProgress;
    private Runnable mProgressRunnable = new Runnable() {
        @Override
        public void run() {
            if (mProgressView instanceof ProgressListener){
                ((ProgressListener)mProgressView).onProgress(mProgress);
            }
            if (null != mDownloadListener) {
                mDownloadListener.onProgress(mProgress);
            }
        }
    };

    public X5PDFView useDefaultProgressView() {
        mProgressView = ViewHelper.buildDefaultProcessView(this, Constants.DEFAULT_TYPE);
        ViewHelper.addProcessView(this, mProgressView, Constants.DIRECTION_TOP);
        mTbsReaderView.setLayoutParams(ViewHelper.buildPDFViewLayoutParams(mProgressView, mTbsReaderView,Constants.DIRECTION_TOP));
        return this;
    }

    public X5PDFView addCustomProgress(View view, int type, int direction) {
        mProgressView = view;
        ViewHelper.addProcessView(this, mProgressView, direction);
        mTbsReaderView.setLayoutParams(ViewHelper.buildPDFViewLayoutParams(mProgressView, mTbsReaderView, direction));
        return this;
    }

    public void loadPDFromUrl(String url) {
        mUrl = url;
        mPDFName = FileUtils.getFileNameByUrl(url);
        mFile = new File(mCacheFilePath, mPDFName);
        if (mFile.exists() && mEnableCache) {
            load();
            return;
        }
        mDownLoadManager.downLoad(mUrl, mFile.getAbsolutePath(), new DownloadListener() {
            @Override
            public void onProgress(int i) {
                mProgress = i;
                post(mProgressRunnable);
            }

            @Override
            public void onFinishDownload(final boolean b) {
                post(new Runnable() {
                    @Override
                    public void run() {
                        if (mProgressView instanceof ProgressListener) {
                            ((ProgressListener) mProgressView).onComplete();
                        }
                        if (null != mDownloadListener) {
                            mDownloadListener.onFinishDownload(b);
                        }
                        load();
                    }
                });
            }

            @Override
            public void onStartDownload() {
                mProgress = 0;
                post(new Runnable() {
                    @Override
                    public void run() {
                        if (mProgressView instanceof ProgressListener) {
                            ((ProgressListener) mProgressView).onStartDownload();
                        }
                        if (null != mDownloadListener) {
                            mDownloadListener.onStartDownload();
                        }
                    }
                });
            }

            @Override
            public void onFail(final String message) {
                post(new Runnable() {
                    @Override
                    public void run() {
                        if (mProgressView instanceof ProgressListener) {
                            ((ProgressListener) mProgressView).onComplete();
                        }
                        if (null != mDownloadListener) {
                            mDownloadListener.onFail(message);
                        }
                    }
                });
            }
        });
    }

    public void loadPDFFromFile(String filePath) {
        mFile = new File(filePath);
        if (!mFile.exists()) {
            Toast.makeText(getContext(), "文件不存在", Toast.LENGTH_SHORT).show();
            return;
        }
        load();
    }

    private void load() {
        bundle.putString("filePath", mFile.getAbsolutePath());
        bundle.putString("tempPath", mTempPath);
        // 加载,就是在这一步，app首次下载时，即使有X5内核 preOpen 也会返回 false
        // 只有 kill 进程后并重启才会有效果，这个问题搞了好久
        // 索性就在返回 false 之后换一种加载方式
        // 第一个参数：文件类型
        boolean result = mTbsReaderView.preOpen(parseFormat(mPDFName), false);
        if (result) {
            mTbsReaderView.openFile(bundle);
        }
        if (null != mPDFLoadListener){
            mPDFLoadListener.callback(result);
        }
    }

    public void enableCache(boolean enable) {
        mEnableCache = enable;
    }

    public X5PDFView cachePath(String path) {
        if (!TextUtils.isEmpty(path)) {
            mCacheFilePath = path;
        }
        return this;
    }

    public X5PDFView setDownloadListener(DownloadListener downloadListener) {
        mDownloadListener = downloadListener;
        return this;
    }

    public X5PDFView setReaderCallback(TbsReaderView.ReaderCallback readerCallback) {
        mReaderCallback = readerCallback;
        return this;
    }

    public X5PDFView setPDFLoadListener(PDFLoadListener pdfLoadListener) {
        mPDFLoadListener = pdfLoadListener;
        return this;
    }

    public TbsReaderView getTbsReaderView() {
        return mTbsReaderView;
    }


    public void setTempPath(String mTempPath) {
        this.mTempPath = mTempPath;
    }

    private String parseFormat(String fileName) {
        return fileName.substring(fileName.lastIndexOf(".") + 1);
    }

    public void destroy(){
        if (null != mTbsReaderView){
            // 在展示结束的时候，一定要调用。否则一直处于加载状态
            mTbsReaderView.onStop();
        }
        if (null != mDownLoadManager){
            mDownLoadManager.destroy();
        }
    }
}
