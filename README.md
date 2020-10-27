# PDF-load
PDF-load是一个用于浏览PDF文档的开源库，支持加载本地PDF文档，网络pdf文档。开源库提供了两种加载方式。
可以根据项目具体情况选择。

### 说明

LibFileX5库不仅支持PDF打开，还支持pptx，docx，xlsx，音视频等，一共支持几十种，详细查看腾讯X5官方文档。

### 博客
关于PDF加载方方案对比可以参考我的博文：[【总结】- PDF解决方案](https://www.jianshu.com/p/8c8d2363b8a7)
##### 配置

- 使用LibPdf

        implementation 'com.pds:pdf-load:1.0.2'

- 使用LibFileX5

        implementation 'com.pds:file-x5:1.0.2'


##### LibPdf 基于[AndroidPdfViewer](https://github.com/barteksc/AndroidPdfViewer)

使用AndroidPdfViewer能够很好的加载PDF文档，但是会增加apk的体积，自己在使用的时候，包体积大概增加3.5M。

效果如下：

<img src="https://github.com/bubian/PDF-load/blob/master/screenshot/pdf-load.png" width="270" height="570" alt="图片描述文字"/>

- 加载网络PDF


        private void displayFromUrl(String url) {
            pdfFileName = FileUtils.getFileNameByUrl(url);
            pdfView.fromUrl(url)
                    .defaultPage(pageNumber)
                    .onPageChange(this)
                    .enableAnnotationRendering(true)
                    .onLoad(this)
                    .scrollHandle(new DefaultScrollHandle(this))
                    .spacing(10)
                    .onPageError(this);
            pdfView.enableCache(true)
                    .useDefaultProgressView().go();
    }


- 直接加载Asset，本地文件，参考[AndroidPdfViewer](https://github.com/barteksc/AndroidPdfViewer)

- 使用默认进度条

  LibPdf支持进度显示，LibPdf提供了默认的进度View，即在顶部显示类似加载网页一样的进度条，调用useDefaultProgressView方法即可。

- 自定义进度条

   LibPdf支持自定义进度条，调用addCustomProgress方法加入自己的进度View。

更多请参考demo

##### LibFileX5 基于[腾讯X5浏览器](https://x5.tencent.com/guide/sdkInit.html)

如果项目中使用了腾讯X5引擎，那么可以直接使用pdf-x5库浏览PDF，apk体积基本没有什么增加，如果项目中没有
使用X5引擎，要用pdf-x5加载PDF，那么需要先集成X5引擎，开源库中的web库提供的简单的X5集成。

- 使用Tbs加载：

```
// 添加到你的布局中，或者添加到布局文件中，然后
X5PDFView pdfView = new X5PDFView(this); 
pdfView.loadPDFromUrl(url);
```

- 使用QbSdk加载：

   查看demo中的QBX5Activity文件。 QbSdk支持很多种文件格式。

   使用QbSdk需要在AndroidManifest.xml配置一下内容

         <provider
            android:name="com.tencent.smtt.utils.FileProvider"
            android:authorities="${applicationId}.provider"
            android:exported="false"
            android:grantUriPermissions="true">
            <meta-data
                android:name="android.support.FILE_PROVIDER_PATHS"
                android:resource="@xml/x5webview_file_paths" />
        </provider>


x5webview_file_paths.xml

            <?xml version="1.0" encoding="utf-8"?>
                <paths>
                <external-path name="sdcard" path="."/>
            </paths>

效果和上面差不多。






