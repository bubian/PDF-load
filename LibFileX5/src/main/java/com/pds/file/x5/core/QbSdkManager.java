package com.pds.file.x5.core;

import android.content.Context;
import android.os.Environment;
import android.os.Handler;
import android.os.Looper;
import android.text.TextUtils;
import android.util.Log;
import com.pds.file.x5.FileUtils;
import com.pds.file.x5.download.DownLoadManager;
import com.pds.file.x5.download.DownloadListener;
import com.tencent.smtt.sdk.QbSdk;
import com.tencent.smtt.sdk.ValueCallback;
import java.io.File;
import java.util.HashMap;

/**
 * @author: pengdaosong
 * @CreateTime: 2020/10/26 4:22 PM
 * @Email: pengdaosong@medlinker.com
 * @Description:
 *
 * 调用之后，优先调起 QQ 浏览器打开文件。如果没有安装 QQ 浏览器，在 X5 内核下调起简版 QB 打开文
 * 件。如果使用的系统内核，则调起文件阅读器弹框。
 * 3、此方法暂时只支持本地文件打开，在线文件后期完善
 */
public class QbSdkManager {

    private static final String TAG = "QbSdkManager";

    private static final String DEFAULT_CACHE_PATH = Environment.getExternalStorageDirectory().getAbsolutePath() + "/pdf/";
    private String mCacheFilePath = DEFAULT_CACHE_PATH;

    private DownLoadManager mDownLoadManager;
    private DownloadListener mDownloadListener;
    private String mUrl;
    private String mName;
    private File mFile;
    private boolean mEnableCache;
    private Handler mHandle = new Handler(Looper.getMainLooper());
    private HashMap<String, String> mExtraParams = new HashMap<>();
    private QbCallback<String> mQbCallback;

    public QbSdkManager() {
        mDownLoadManager = new DownLoadManager();
        // 表示是进入文件查看器，如果不设置或设置为“false”，则进入miniqb浏览器模式。不是必须设置项
        // mExtraParams.put("local","true");
        // 0”表示文件查看器使用默认的UI样式。“1”表示文件查看器使用微信的UI样式。不设置此key或设置错误值，则为默认UI样式。
        // mExtraParams.put("style","true");
        // 定制文件查看器的顶部栏背景色。格式为“#xxxxxx”，例“#2CFC47”;不设置此key或设置错误值，则为默认UI样式。
        // mExtraParams.put("topBarBgColor","true");
        // 该参数用来定制文件右上角弹出菜单，可传入菜单项的icon的文本，用户点击菜单项后，sdk会通过startActivity+intent的方式回调。menuData是jsonObject类型，结构格式如下
        // mExtraParams.put("menuData","true");
        /**
         * String jsondata="{pkgName:\"com.example.thirdfile\","+"className:\"com.example.thirdfile.IntentActivity\","+"thirdCtx:{pp:123},"+"menuItems:"+"["+"{id:0,iconResId:"+R.drawable.ic_launcher+",text:\"menu0\"},
         * {id:1,iconResId:"+R.drawable.bookmark_edit_icon+",text:\"menu1\"},{id:2,iconResId:"+R.drawable.bookmark_folder_icon+",text:\"菜单2\"}"+"]"+"}";
         *
         * pkgName和className是回调时的包名和类名。thirdCtx是三方参数，需要是jsonObject类型，sdk不会处理该参数，只是在菜单点击事件发生的时候原样回传给调用方。
         * menuItems是json数组，表示菜单中的每一项。
         */
    }

    private volatile int mProgress;
    private Runnable mProgressRunnable = new Runnable() {
        @Override
        public void run() {
            if (null != mDownloadListener) {
                mDownloadListener.onProgress(mProgress);
            }
        }
    };

    public QbSdkManager cachePath(String path) {
        if (!TextUtils.isEmpty(path)) {
            mCacheFilePath = path;
        }
        return this;
    }

    public QbSdkManager enableCache(boolean enable) {
        mEnableCache = enable;
        return this;
    }

    public QbSdkManager setDownloadListener(DownloadListener downloadListener) {
        mDownloadListener = downloadListener;
        return this;
    }

    public void setQbCallback(QbCallback<String> mQbCallback) {
        this.mQbCallback = mQbCallback;
    }

    public void loadPDFromUrl(final Context context,String url) {
        mUrl = url;
        mName = FileUtils.getFileNameByUrl(url);
        mFile = new File(mCacheFilePath, mName);
        if (mFile.exists() && mEnableCache) {
            load(context);
            return;
        }
        mDownLoadManager.downLoad(mUrl, mFile.getAbsolutePath(), new DownloadListener() {
            @Override
            public void onProgress(int i) {
                mProgress = i;
                mHandle.post(mProgressRunnable);
            }

            @Override
            public void onFinishDownload(final boolean b) {
                mHandle.post(new Runnable() {
                    @Override
                    public void run() {
                        if (null != mDownloadListener) {
                            mDownloadListener.onFinishDownload(b);
                        }
                        load(context);
                    }
                });
            }

            @Override
            public void onStartDownload() {
                mHandle.post(new Runnable() {
                    @Override
                    public void run() {
                        if (null != mDownloadListener) {
                            mDownloadListener.onStartDownload();
                        }
                    }
                });
            }

            @Override
            public void onFail(final String message) {
                mHandle.post(new Runnable() {
                    @Override
                    public void run() {
                        if (null != mDownloadListener) {
                            mDownloadListener.onFail(message);
                        }
                    }
                });
            }
        });
    }

    public void destroy(Context context) {
        if (null != mDownLoadManager) {
            mDownLoadManager.destroy();
        }
        mHandle.removeCallbacksAndMessages(null);
        QbSdk.closeFileReader(context);
    }

    /**
     * ValueCallback：提供miniqb打开/关闭时给调用方回调通知,以便应用层做相应处理。在单独进程打开文件的场景中，回调参数出现如下字符时，表示可以关闭当前进程，避免内存占用。
     * openFileReaderopeninQB
     * filepatherror
     * TbsReaderDialogClosed
     * defaultbrowser:
     * filepatherror
     * fileReaderClosed
     * 【返回说明】1：用QQ浏览器打开2：用MiniQB打开3：调起阅读器弹框-1：filePath为空打开失败
     * @param context
     *
     * // fileReaderClosed
     */
    private void load(Context context) {
        QbSdk.openFileReader(context, mFile.getAbsolutePath(), mExtraParams,
                new ValueCallback<String>() {
                    @Override
                    public void onReceiveValue(String s) {
                        Log.d(TAG, s);
                        if (null != mQbCallback) {
                            mQbCallback.onReceiveValue(s);
                        }
                    }
                });
    }
}
