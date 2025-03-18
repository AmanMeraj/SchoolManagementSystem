package com.school.schoolmanagement;

import android.content.Context;
import android.graphics.LinearGradient;
import android.graphics.Shader;
import android.util.AttributeSet;
import androidx.appcompat.widget.AppCompatTextView;

public class GradientTextView extends AppCompatTextView {
    public GradientTextView(Context context, AttributeSet attrs) {
        super(context, attrs);
        applyGradient();
    }

    private void applyGradient() {
        getViewTreeObserver().addOnGlobalLayoutListener(() -> {
            int width = getWidth();
            if (width > 0) {
                LinearGradient shader = new LinearGradient(
                        0, 0, width, getTextSize(),
                        new int[]{0xFFFF7459, 0xFF00A4BD}, // Fixed Gradient Colors (Red-Orange to Cyan)
                        null,
                        Shader.TileMode.CLAMP
                );
                getPaint().setShader(shader);
                invalidate();
            }
        });
    }
}
