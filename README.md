# PDF-load
PDF-load是一个用于浏览PDF文档的开源库，支持加载本地PDF文档，网络pdf文档。开源库提供了两种方式加载方式。
可以根据项目具体情况选择。

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

##### 更多请参考demo

##### pdf-x5 基于[腾讯X5浏览器](https://x5.tencent.com/guide/sdkInit.html)

如果项目中使用了腾讯X5引擎，那么可以直接使用pdf-x5库浏览PDF，apk体积基本没有什么增加，如果项目中没有
使用X5引擎，要用pdf-x5加载PDF，那么需要先集成X5引擎，开源库中的web库提供的简单的X5集成。

- 使用：

```
// 添加到你的布局中，或者添加到布局文件中，然后
X5PDFView pdfView = new X5PDFView(this); 
pdfView.loadPDFromUrl(url);
```
效果和上面差不多。






