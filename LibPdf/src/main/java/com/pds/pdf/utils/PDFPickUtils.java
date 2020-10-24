package com.pds.pdf.utils;

import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.widget.Toast;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

/**
 * @author: pengdaosong
 * @CreateTime: 2020/10/22 2:58 AM
 * @Email: pengdaosong@medlinker.com
 * @Description:
 */
public class PDFPickUtils {
    /**
     * 存储权限
     */
    public static final String READ_EXTERNAL_STORAGE = "android.permission.READ_EXTERNAL_STORAGE";

    /**
     * 挑选PDF文档
     * @param activity
     * @param permissionCode
     * @param requestCode
     */
    void pickPDFFile(Activity activity,int permissionCode,int requestCode) {
        int permissionCheck = ContextCompat.checkSelfPermission(activity, READ_EXTERNAL_STORAGE);
        if (permissionCheck != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(activity,
                    new String[]{READ_EXTERNAL_STORAGE},
                    permissionCode
            );
            return;
        }
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("application/pdf");
        try {
            activity.startActivityForResult(intent, requestCode);
        } catch (ActivityNotFoundException e) {
            Toast.makeText(activity, "Unable to pick file. Check status of file manager", Toast.LENGTH_SHORT).show();
        }
    }
}
