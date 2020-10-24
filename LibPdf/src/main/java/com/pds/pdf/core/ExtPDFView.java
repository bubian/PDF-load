package com.pds.pdf.core;

import android.content.Context;
import android.graphics.Color;
import android.os.Environment;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import com.github.barteksc.pdfviewer.PDFView;
import com.github.barteksc.pdfviewer.listener.OnLoadCompleteListener;
import com.pds.pdf.download.DownLoadManager;
import com.pds.pdf.download.DownloadListener;
import com.pds.pdf.process.ProgressListener;
import com.pds.pdf.process.ProgressListenerImpl;
import com.pds.pdf.utils.FileUtils;
import java.io.File;

/**
 * @author: pengdaosong
 * @CreateTime: 2020/10/22 3:13 AM
 * @Email: pengdaosong@medlinker.com
 * @Description:
 */
public class ExtPDFView extends RelativeLayout {

    private static final String DEFAULT_CACHE_PATH = Environment.getExternalStorageDirectory().getAbsolutePath() + "/pdf/";
    private String mCacheFilePath = DEFAULT_CACHE_PATH;

    private String mPDFName;
    private PDFView.Configurator mConfigurator;
    private String mUrl;
    private File mFile;
    private PDFView mPDFView;
    private View mProgressView;

    private boolean mEnableCache = true;
    private DownloadListener mDownloadListener;
    private DownLoadManager mDownLoadManager;

    private OnLoadCompleteListener mLoadCompleteListener;

    public ExtPDFView(Context context) {
        super(context);
        init(context, null);
    }

    public ExtPDFView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs);
    }

    public ExtPDFView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context, attrs);
    }

    private void init(Context context, AttributeSet attrs) {
        mPDFView = new PDFView(context, attrs);
        LayoutParams pdfParams = new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        mPDFView.setBackgroundColor(Color.LTGRAY);
        addView(mPDFView, 0, pdfParams);
        mDownLoadManager = new DownLoadManager();
    }

    public PDFView.Configurator fromUrl(String url) {
        mUrl = url;
        mPDFName = FileUtils.getFileNameByUrl(url);
        mFile = new File(mCacheFilePath, mPDFName);
        mConfigurator = mPDFView.fromFile(mFile);
        return mConfigurator;
    }

    public ExtPDFView cachePath(String path) {
        if (!TextUtils.isEmpty(path)) {
            mCacheFilePath = path;
        }
        return this;
    }

    public ExtPDFView enableCache(boolean enable) {
        mEnableCache = enable;
        return this;
    }

    public ExtPDFView useDefaultProgressView() {
        addDefaultProgress(Constants.DEFAULT_TYPE, Constants.DIRECTION_TOP);
        return this;
    }

    public ExtPDFView addDefaultProgress(int type, int direction) {
        mProgressView = ViewHelper.buildDefaultProcessView(this, type);
        ViewHelper.addProcessView(this, mProgressView, direction);
        mPDFView.setLayoutParams(ViewHelper.buildPDFViewLayoutParams(mProgressView, mPDFView, direction));
        return this;
    }

    public ExtPDFView addCustomProgress(View view, int type, int direction) {
        mProgressView = view;
        ViewHelper.addProcessView(this, mProgressView, direction);
        mPDFView.setLayoutParams(ViewHelper.buildPDFViewLayoutParams(mProgressView, mPDFView, direction));
        return this;
    }

    public ExtPDFView setDownloadListener(DownloadListener downloadListener) {
        mDownloadListener = downloadListener;
        return this;
    }

    public void setLoadCompleteListener(OnLoadCompleteListener loadCompleteListener) {
        mLoadCompleteListener = loadCompleteListener;
    }

    private int mProgress;
    private Runnable mProgressRunnable = new Runnable() {
        @Override
        public void run() {
            if (null != mProgressView) {
                if (mProgress >= 98) {
                    mProgress = 98;
                }
                if (mProgressView instanceof ProgressListener) {
                    ((ProgressListener)mProgressView).onProgress(mProgress);
                }
            }
            if (null != mDownloadListener) {
                mDownloadListener.onProgress(mProgress);
            }
        }
    };

    private OnLoadCompleteListener mCompleteListener = new OnLoadCompleteListener() {
        @Override
        public void loadComplete(int nbPages) {
            if (null != mLoadCompleteListener) {
                mLoadCompleteListener.loadComplete(nbPages);
            }
            if (mProgressView instanceof ProgressListener) {
                ((ProgressListener)mProgressView).onComplete();
            }
            if (null != mProgressView) {
                postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        mProgressView.setVisibility(GONE);
                    }
                }, 500);
            }
        }
    };


    public void go() {
        if (null != mConfigurator) {
            mConfigurator.onLoad(mCompleteListener);
        }
        //文件已经存在，直接获取本地文件打开，否则从网络现在文件，文件下载成功之后再打开
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
                            ((ProgressListener)mProgressView).onStartDownload();
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
                            ((ProgressListener)mProgressView).onComplete();
                        }
                        if (null != mDownloadListener) {
                            mDownloadListener.onFail(message);
                        }
                    }
                });
            }
        });
    }

    /**
     * 开始加载PDF文件
     */
    private void load() {
        if (null != mConfigurator) {
            mConfigurator.load();
        }
    }

    public PDFView getPDFView() {
        return mPDFView;
    }
}
