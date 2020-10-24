/**
 * Copyright 2016 Bartosz Schiller
 * <p/>
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except
 * in compliance with the License. You may obtain a copy of the License at
 * <p/>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p/>
 * Unless required by applicable law or agreed to in writing, software distributed under the License
 * is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express
 * or implied. See the License for the specific language governing permissions and limitations under
 * the License.
 */
package com.pds.sample;

import android.os.Bundle;
import android.util.Log;
import android.view.ViewGroup;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import com.github.barteksc.pdfviewer.listener.OnLoadCompleteListener;
import com.github.barteksc.pdfviewer.listener.OnPageChangeListener;
import com.github.barteksc.pdfviewer.listener.OnPageErrorListener;
import com.github.barteksc.pdfviewer.scroll.DefaultScrollHandle;
import com.pds.pdf.core.ExtPDFView;
import com.pds.pdf.utils.FileUtils;
import com.pds.pdf.x5.X5PDFView;
import com.shockwave.pdfium.PdfDocument;
import java.util.List;

/**
 * @author pengdaosong
 */
public class PDFViewActivity extends AppCompatActivity implements OnPageChangeListener,
        OnLoadCompleteListener,
        OnPageErrorListener {

    private static final String URL = "https://pub-med-casem.medlinker.com/guanxin_paitent_test.pdf";
    private static final String TAG = PDFViewActivity.class.getSimpleName();

    ExtPDFView pdfView;
    String mUrl = URL;
    Integer pageNumber = 0;
    String pdfFileName;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pdf);
        pdfView = findViewById(R.id.pdfView);
        // displayFromUrl(mUrl);
        // TbsReaderView: not supported by:pdf
        pdfView.postDelayed(new Runnable() {
            @Override
            public void run() {
                displayFromX5(mUrl);
            }
        },1500);

        setTitle(pdfFileName);
    }

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
    private void displayFromX5(String url) {
        X5PDFView pdfView = new X5PDFView(this);
        ((ViewGroup)getWindow().getDecorView()).addView(pdfView);
        pdfView.loadPDFromUrl(url);
    }


    @Override
    public void onPageChanged(int page, int pageCount) {
        pageNumber = page;
        setTitle(String.format("%s %s / %s", pdfFileName, page + 1, pageCount));
    }

    @Override
    public void loadComplete(int nbPages) {
        PdfDocument.Meta meta = pdfView.getPDFView().getDocumentMeta();
        printBookmarksTree(pdfView.getPDFView().getTableOfContents(), "-");

    }

    public void printBookmarksTree(List<PdfDocument.Bookmark> tree, String sep) {
        for (PdfDocument.Bookmark b : tree) {
            if (b.hasChildren()) {
                printBookmarksTree(b.getChildren(), sep + "-");
            }
        }
    }

    @Override
    public void onPageError(int page, Throwable t) {
        Log.e(TAG, "Cannot load page " + page);
    }
}
