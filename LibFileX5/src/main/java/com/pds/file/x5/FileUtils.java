package com.pds.file.x5;

import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.provider.OpenableColumns;
import java.util.UUID;

/**
 * @author: pengdaosong
 * @CreateTime: 2020/10/22 4:38 PM
 * @Email: pengdaosong@medlinker.com
 * @Description:
 */
public class FileUtils {

    public static String getFileName(Context context, Uri uri) {
        String result = null;
        if (uri.getScheme().equals("content")) {
            Cursor cursor = context.getContentResolver().query(uri, null, null, null, null);
            try {
                if (cursor != null && cursor.moveToFirst()) {
                    result = cursor.getString(cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME));
                }
            } finally {
                if (cursor != null) {
                    cursor.close();
                }
            }
        }
        if (result == null) {
            result = uri.getLastPathSegment();
        }
        return result;
    }

    public static String getFileNameByUrl(String url){
        String pdfName;
        int index = url.lastIndexOf("/");
        if (index < 0){
            pdfName = UUID.randomUUID().toString() + ".pdf";
        }else {
            pdfName = url.substring(index);
        }
        return pdfName;
    }
}
