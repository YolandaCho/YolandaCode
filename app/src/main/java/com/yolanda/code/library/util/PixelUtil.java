package com.yolanda.code.library.util;

import android.content.Context;
import android.content.res.Resources;
import android.util.TypedValue;

/**
 * 像素转换工具类
 */
public class PixelUtil {

    /**
     * The context.
     */
    private Context context;

    public PixelUtil(Context context) {
        this.context = context;
    }

    public static boolean isPxVal(TypedValue val) {
        if (val != null && val.type == TypedValue.TYPE_DIMENSION &&
                getComplexUnit(val.data) == TypedValue.COMPLEX_UNIT_PX) {
            return true;
        }
        return false;
    }

    private static int getComplexUnit(int data) {
        return TypedValue.COMPLEX_UNIT_MASK & (data >> TypedValue.COMPLEX_UNIT_SHIFT);
    }

    /**
     * dpת px.
     *
     * @param value the value
     * @return the int
     */
    public int dp2px(float value) {
        final float density = context.getResources().getDisplayMetrics().density;
        return (int) (value * density + 0.5f);
    }

    /**
     * pxתdp.
     *
     * @param value the value
     * @return the int
     */
    public int px2dp(float value) {
        final float scale = context.getResources().getDisplayMetrics().densityDpi;
        return (int) ((value * 160) / scale + 0.5f);
    }


    /**
     * spתpx.
     *
     * @param value the value
     * @return the int
     */
    public int sp2px(float value) {
        Resources r;
        if (context == null) {
            r = Resources.getSystem();
        } else {
            r = context.getResources();
        }
        float spvalue = value * r.getDisplayMetrics().scaledDensity;
        return (int) (spvalue + 0.5f);
    }


    /**
     * pxתsp.
     *
     * @param value the value
     * @return the int
     */
    public int px2sp(float value) {
        final float scale = context.getResources().getDisplayMetrics().scaledDensity;
        return (int) (value / scale + 0.5f);
    }


    public int width() {
        return context.getResources().getDisplayMetrics().widthPixels;
    }

    public int height() {
        return context.getResources().getDisplayMetrics().heightPixels;
    }
}
