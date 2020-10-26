package com.pds.file.x5.core;

import android.content.Context;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import com.pds.file.x5.R;
import com.pds.file.x5.process.ProgressView;


/**
 * @author: pengdaosong
 * @CreateTime: 2020/10/23 10:56 AM
 * @Email: pengdaosong@medlinker.com
 * @Description:
 */
public class ViewHelper {

    public static void addProcessView(ViewGroup parent, View process, int direction) {
        parent.addView(process, buildProcessLayoutParams(process, direction));
    }

    public static View buildDefaultProcessView(ViewGroup root, int type) {
        Context context = root.getContext();
        RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT, dip2px(context, 3));
        View progressView = new ProgressView(context);
        progressView.setLayoutParams(params);
        return progressView;
    }

    public static RelativeLayout.LayoutParams buildProcessLayoutParams(View process,
            int direction) {
        RelativeLayout.LayoutParams params;
        if (process.getLayoutParams() instanceof RelativeLayout.LayoutParams) {
            params = (RelativeLayout.LayoutParams) process.getLayoutParams();
        } else {
            params = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT);
        }
        if (Constants.DIRECTION_TOP == direction) {
            params.addRule(RelativeLayout.ALIGN_PARENT_TOP);
        } else if (Constants.DIRECTION_BOTTOM == direction) {
            params.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
        } else {
            params.addRule(RelativeLayout.CENTER_IN_PARENT);
        }
        return params;
    }

    public static ViewGroup.LayoutParams buildPDFViewLayoutParams(View process, View fileView,
            int direction) {
        if (null == process) {
            return fileView.getLayoutParams();
        }

        RelativeLayout.LayoutParams params;
        if (fileView.getLayoutParams() instanceof RelativeLayout.LayoutParams) {
            params = (RelativeLayout.LayoutParams) fileView.getLayoutParams();
        } else {
            params = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.MATCH_PARENT);
        }
        int processId = process.getId();
        if (processId == View.NO_ID) {
            processId = R.id.pdf_process_view_id;
            process.setId(processId);
        }
        if (Constants.DIRECTION_TOP == direction) {
            params.addRule(RelativeLayout.BELOW, processId);
        } else if (Constants.DIRECTION_BOTTOM == direction) {
            params.addRule(RelativeLayout.ABOVE, processId);
        }
        return params;
    }


    public static DisplayMetrics getDisplayMetrics(Context context) {
        return context.getResources().getDisplayMetrics();
    }

    public static int dip2px(Context context, float dpValue) {
        return (int) (dpValue * getDisplayMetrics(context).density + 0.5f);
    }
}
