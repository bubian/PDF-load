package com.pds.sample;

import android.os.Bundle;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import com.pds.file.x5.core.X5PDFView;

/**
 * @author: pengdaosong
 * @CreateTime: 2020/10/26 2:03 PM
 * @Email: pengdaosong@medlinker.com
 * @Description:
 */
public class PDFX5Activity extends AppCompatActivity {
    private static final String URL = "https://pub-med-casem.medlinker.com/guanxin_paitent_test.pdf";
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        displayFromX5(URL);
    }

    private X5PDFView mX5PDFView;

    private void displayFromX5(String url) {
        mX5PDFView = new X5PDFView(this);
        setContentView(mX5PDFView);
        mX5PDFView.useDefaultProgressView().loadPDFromUrl(url);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (null != mX5PDFView) {
            mX5PDFView.destroy();
        }
    }
}
