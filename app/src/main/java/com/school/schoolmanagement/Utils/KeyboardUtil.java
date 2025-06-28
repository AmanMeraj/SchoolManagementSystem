package com.school.schoolmanagement.Utils;

import android.app.Activity;
import android.graphics.Rect;
import android.os.Build;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;

import com.school.schoolmanagement.R;

public class KeyboardUtil {
    public static void adjustForKeyboard(final Activity activity) {
        final View rootView = activity.findViewById(R.id.main);

        rootView.getViewTreeObserver().addOnGlobalLayoutListener(() -> {
            Rect r = new Rect();
            rootView.getWindowVisibleDisplayFrame(r);

            int screenHeight = rootView.getRootView().getHeight();
            int visibleHeight = r.height();
            int heightDiff = screenHeight - visibleHeight;

            View childView = ((ViewGroup) rootView).getChildAt(0);
            if (childView == null) return;

            if (heightDiff > screenHeight * 0.15) {
                // Keyboard is probably visible
                ViewGroup.MarginLayoutParams params = (ViewGroup.MarginLayoutParams) childView.getLayoutParams();
                params.bottomMargin = heightDiff;
                childView.setLayoutParams(params);
            } else {
                // Keyboard is hidden
                ViewGroup.MarginLayoutParams params = (ViewGroup.MarginLayoutParams) childView.getLayoutParams();
                if (params.bottomMargin != 0) {
                    params.bottomMargin = 0;
                    childView.setLayoutParams(params);
                }
            }
        });
    }
}

