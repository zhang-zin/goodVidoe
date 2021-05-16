package com.zj.hi_library.util;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Point;
import android.util.DisplayMetrics;
import android.util.TypedValue;
import android.view.Display;
import android.view.WindowManager;

import androidx.annotation.NonNull;

import java.lang.reflect.TypeVariable;

public class HiDisplayUtil {
    public static int dp2px(float dp, Resources resources) {
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp, resources.getDisplayMetrics());
    }

    public static int dp2px(float dp) {
        Resources resources = AppGlobals.INSTANCE.get().getResources();
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp, resources.getDisplayMetrics());
    }

    public static int sp2px(float sp) {
        Resources resources = AppGlobals.INSTANCE.get().getResources();
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, sp, resources.getDisplayMetrics());
    }


    public static int getScreenWidth() {
        DisplayMetrics metrics = AppGlobals.INSTANCE.get().getResources().getDisplayMetrics();
        return metrics.widthPixels;
    }

    public static int getScreenHeight() {
        DisplayMetrics metrics = AppGlobals.INSTANCE.get().getResources().getDisplayMetrics();
        return metrics.heightPixels;
    }
}
