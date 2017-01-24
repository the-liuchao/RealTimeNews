package com.liuming.mylibrary.utils;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.PixelFormat;
import android.util.DisplayMetrics;
import android.view.Display;
import android.view.View;
import android.view.WindowManager;

import com.liuming.mylibrary.R;

import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

/**
 * Created by sll on 2015/6/17.
 */
public class DisplayHelper {
    /**
     * 将px值转换为dip或dp值，保证尺寸大小不变
     *
     * @param pxValue
     * @return
     */
    public static int px2dip(Context context, float pxValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (pxValue / scale + 0.5f);
    }

    /**
     * 将dip或dp值转换为px值，保证尺寸大小不变
     *
     * @param dipValue
     * @return
     */
    public static int dip2px(Context context, float dipValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (dipValue * scale + 0.5f);
    }

    /**
     * 将px值转换为sp值，保证文字大小不变
     *
     * @param pxValue
     * @return
     */
    public static int px2sp(Context context, float pxValue) {
        final float fontScale = context.getResources().getDisplayMetrics().scaledDensity;
        return (int) (pxValue / fontScale + 0.5f);
    }

    /**
     * 将sp值转换为px值，保证文字大小不变
     *
     * @param spValue
     * @return
     */
    public static int sp2px(Context context, float spValue) {
        final float fontScale = context.getResources().getDisplayMetrics().scaledDensity;
        return (int) (spValue * fontScale + 0.5f);
    }

    // 获取屏幕
    public static Bitmap screenshot(Activity ctx) {
        View dView = ctx.getWindow().getDecorView();
        Bitmap bmp = null;
        if (null != dView) {
            dView.setDrawingCacheEnabled(true);
            dView.buildDrawingCache();
            bmp = dView.getDrawingCache();
        } else {
            bmp = BitmapFactory.decodeResource(ctx.getResources(), R.mipmap.ic_launcher);
        }
        return bmp;
    }

    /**
     * 截屏
     *
     * @param activity
     * @return
     */
    public static Bitmap captureScreen(Activity activity) {
        // 获取屏幕大小：
        DisplayMetrics metrics = new DisplayMetrics();
        WindowManager WM = (WindowManager) activity
                .getSystemService(Context.WINDOW_SERVICE);
        Display display = WM.getDefaultDisplay();
        display.getMetrics(metrics);
        int height = metrics.heightPixels; // 屏幕高
        int width = metrics.widthPixels; // 屏幕的宽
        // 获取显示方式
        int pixelformat = display.getPixelFormat();
        PixelFormat localPixelFormat1 = new PixelFormat();
        PixelFormat.getPixelFormatInfo(pixelformat, localPixelFormat1);
        int deepth = localPixelFormat1.bytesPerPixel;// 位深
        byte[] piex = new byte[height * width * deepth];
        try {
            Runtime.getRuntime().exec(
                    new String[]{"/system/bin/su", "-c",
                            "chmod 777 /dev/graphics/fb0"});

        } catch (IOException e) {
            e.printStackTrace();
        }
        try {
            // 获取fb0数据输入流
            InputStream stream = new FileInputStream(new File(
                    "/dev/graphics/fb0"));
            DataInputStream dStream = new DataInputStream(stream);
            dStream.readFully(piex);
        } catch (Exception e) {
            e.printStackTrace();
        }
        // 保存图片
        int[] colors = new int[height * width];
        for (int m = 0; m < colors.length; m++) {
            int r = (piex[m * 4] & 0xFF);
            int g = (piex[m * 4 + 1] & 0xFF);
            int b = (piex[m * 4 + 2] & 0xFF);
            int a = (piex[m * 4 + 3] & 0xFF);
            colors[m] = (a << 24) + (r << 16) + (g << 8) + b;
        }
        // piex生成Bitmap
        Bitmap bitmap = Bitmap.createBitmap(colors, width, height,
                Bitmap.Config.ARGB_8888);
        return bitmap;
    }

}
