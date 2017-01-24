package com.liuming.mylibrary.utils;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.BitmapFactory;
import android.graphics.BitmapFactory.Options;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.PorterDuff.Mode;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.drawable.Drawable;
import android.media.ThumbnailUtils;
import android.net.Uri;
import android.os.Environment;
import android.os.StatFs;
import android.util.Base64;
import android.util.Log;

import com.liuming.mylibrary.XApplication;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.net.URLEncoder;

public class ImageHelper {

    private static class ImageUtilsHolder {
        private static final ImageHelper INSTANCE = new ImageHelper();
    }

    public ImageHelper() {
    }

    public static ImageHelper getInstance() {
        return ImageUtilsHolder.INSTANCE;
    }

    /**
     * 图片水平翻转
     *
     * @param drawableId
     * @return
     */
    public Bitmap horizontalFlip(int drawableId) {
        Bitmap bitmap1 = null;
        bitmap1 = readBitmapById(drawableId);
        Bitmap bitmap2 = Bitmap.createBitmap(bitmap1.getWidth(), bitmap1.getHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap2);
        Matrix orig = canvas.getMatrix();
        orig.setScale(-1, 1);                     //翻转X
        orig.postTranslate(bitmap1.getWidth(), 0);//平移
        canvas.drawBitmap(bitmap1, orig, null);
        return bitmap2;
    }

    /**
     * 读取本地资源的图片
     *
     * @param resId
     * @return
     */
    public Bitmap readBitmapById(int resId) {
        Options opt = new Options();
        opt.inPreferredConfig = Config.RGB_565;
        opt.inPurgeable = true;
        opt.inInputShareable = true;
        // 获取资源图片
        InputStream is = XApplication.getInstance().getResources().openRawResource(resId);
        return BitmapFactory.decodeStream(is, null, opt);
    }

    /***
     * 根据资源文件获取Bitmap
     *
     * @param context
     * @param drawableId
     * @return
     */
    public Bitmap readBitmapById(Context context, int drawableId,
                                 int screenWidth, int screenHight) {
        Options options = new Options();
        options.inPreferredConfig = Config.RGB_565;
        options.inInputShareable = true;
        options.inPurgeable = true;
        InputStream stream = context.getResources().openRawResource(drawableId);
        Bitmap bitmap = BitmapFactory.decodeStream(stream, null, options);
        return getBitmap(bitmap, screenWidth, screenHight);
    }


    /***
     * 等比例压缩图片
     *
     * @param bitmap
     * @param screenWidth
     * @param screenHight
     * @return
     */
    @SuppressWarnings("unused")
    public Bitmap getBitmap(Bitmap bitmap, int screenWidth, int screenHight) {
        int w = bitmap.getWidth();
        int h = bitmap.getHeight();
        Log.e("jj", "图片宽度" + w + ",screenWidth=" + screenWidth);
        Matrix matrix = new Matrix();
        float scale = (float) screenWidth / w;
        float scale2 = (float) screenHight / h;

        // scale = scale < scale2 ? scale : scale2;

        // 保证图片不变形.
        matrix.postScale(scale, scale);
        // w,h是原图的属性.
        Bitmap bmp = Bitmap.createBitmap(bitmap, 0, 0, w, h, matrix, true);
        bitmap.recycle();
        return bmp;
    }

    /***
     * 保存图片至SD卡
     *
     * @param bm
     * @param url
     * @param quantity
     */
    private static int FREE_SD_SPACE_NEEDED_TO_CACHE = 1;
    private static int MB = 1024 * 1024;
    public final static String DIR = "/sdcard/hypers";

    public void saveBmpToSd(Bitmap bm, String url, int quantity) {
        // 判断sdcard上的空间
        if (FREE_SD_SPACE_NEEDED_TO_CACHE > freeSpaceOnSd()) {
            return;
        }
        if (!Environment.MEDIA_MOUNTED.equals(Environment
                .getExternalStorageState()))
            return;
        String filename = url;
        // 目录不存在就创建
        File dirPath = new File(DIR);
        if (!dirPath.exists()) {
            dirPath.mkdirs();
        }

        File file = new File(DIR + "/" + filename);
        try {
            file.createNewFile();
            OutputStream outStream = new FileOutputStream(file);
            bm.compress(Bitmap.CompressFormat.PNG, quantity, outStream);
            outStream.flush();
            outStream.close();
        } catch (FileNotFoundException e) {
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * 计算sdcard上的剩余空间 * @return
     */
    @SuppressWarnings("deprecation")
    private static int freeSpaceOnSd() {
        StatFs stat = new StatFs(Environment.getExternalStorageDirectory()
                .getPath());
        double sdFreeMB = ((double) stat.getAvailableBlocks() * (double) stat
                .getBlockSize()) / MB;

        return (int) sdFreeMB;
    }

    /**
     * 根据路径获取Bitmap
     *
     * @param path
     * @return
     */
    public static Bitmap getBitmapByPath(String path) {
        Options options = new Options();
        options.inPreferredConfig = Config.RGB_565;
        options.inInputShareable = true;
        options.inPurgeable = true;
        return BitmapFactory.decodeFile(path, options);
    }

    /***
     * 获取SD卡图片
     *
     * @param url
     * @param quantity
     * @return
     */
    @SuppressWarnings("deprecation")
    public Bitmap GetBitmap(String url, int quantity) {
        InputStream inputStream = null;
        String filename = "";
        Bitmap map = null;
        URL url_Image = null;
        String LOCALURL = "";
        if (url == null)
            return null;
        try {
            filename = url;
        } catch (Exception err) {
        }

        LOCALURL = URLEncoder.encode(filename);
        if (Exist(DIR + "/" + LOCALURL)) {
            map = BitmapFactory.decodeFile(DIR + "/" + LOCALURL);
        } else {
            try {
                url_Image = new URL(url);
                inputStream = url_Image.openStream();
                map = BitmapFactory.decodeStream(inputStream);
                // url = URLEncoder.encode(url, "UTF-8");
                if (map != null) {
                    saveBmpToSd(map, LOCALURL, quantity);
                }
                inputStream.close();
            } catch (Exception e) {
                e.printStackTrace();
                return null;
            }
        }
        return map;
    }

    /***
     * 判断图片是存在
     *
     * @param url
     * @return
     */
    public boolean Exist(String url) {
        File file = new File(DIR + url);
        return file.exists();
    }

    /**
     * 从资源文件中获得Drawable图片
     *
     * @param context
     * @param imgId   资源id
     * @return
     */
    public Drawable getResDrawable(Context context, int imgId) {
        return context.getResources().getDrawable(imgId);
    }

    /**
     * 资源图片的缩放
     *
     * @param context
     * @param resId   资源ID
     * @param scale   缩放比例
     * @return
     */
    public Bitmap getCompressBitmap(Context context, int resId, int scale) {
        InputStream is = context.getResources().openRawResource(resId);
        Options opt = new Options();
        opt.inJustDecodeBounds = false;
        opt.inSampleSize = scale;
        return BitmapFactory.decodeStream(is, null, opt);
    }

    /**
     * 资源图片的缩放
     *
     * @param context
     * @param imageUri 图片URI
     * @return
     * @throws FileNotFoundException
     */
    public Bitmap getCompressBitmap(Context context, Uri imageUri, int width,
                                    int height) throws FileNotFoundException {
        Options options = new Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeStream(context.getContentResolver()//
                .openInputStream(imageUri), null, options);
        int heightRatio = (int) Math.ceil(options.outHeight / height);
        int widhtRatio = (int) Math.ceil(options.outWidth / width);
        if (heightRatio > 1 && widhtRatio > 1) {
            options.inSampleSize = heightRatio > widhtRatio ? heightRatio
                    : widhtRatio;
        }
        options.inJustDecodeBounds = false;
        return BitmapFactory.decodeStream(context.getContentResolver()//
                .openInputStream(imageUri), null, options);
    }

    /**
     * 图片的裁剪
     *
     * @param bmp
     * @param height
     * @return
     */
    public Bitmap clipImage(Bitmap bmp, int width, int height) {
        Bitmap bitmap = ThumbnailUtils.extractThumbnail(bmp, width, height);
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 90, baos);
        return BitmapFactory.decodeByteArray(baos.toByteArray(), 0,
                baos.toByteArray().length);
    }

    /**
     * 获取圆角位图的方法
     *
     * @param bitmap 需要转化成圆角的位图
     * @param pixels 圆角的度数，数值越大，圆角越大
     * @return 处理后的圆角位图
     */
    public Bitmap toRoundCorner(Bitmap bitmap, int pixels) {
        Bitmap output = Bitmap.createBitmap(bitmap.getWidth(),
                bitmap.getHeight(), Config.RGB_565);
        Canvas canvas = new Canvas(output);
        final int color = 0xff424242;
        final Paint paint = new Paint();
        final Rect rect = new Rect(0, 0, bitmap.getWidth(), bitmap.getHeight());
        final RectF rectF = new RectF(rect);
        final float roundPx = pixels;
        paint.setAntiAlias(true);
        canvas.drawARGB(0, 0, 0, 0);
        paint.setColor(color);
        canvas.drawRoundRect(rectF, roundPx, roundPx, paint);
        paint.setXfermode(new PorterDuffXfermode(Mode.SRC_IN));
        canvas.drawBitmap(bitmap, rect, rect, paint);
        return output;
    }

    /**
     * 以最省内存方式读取本地资源的图片
     *
     * @param context
     * @param imgId
     * @return
     */
    public Bitmap getResBitmap(Context context, int imgId) {
        Options opt = new Options();
        opt.inPreferredConfig = Config.RGB_565;
        opt.inPurgeable = true;// 使得内存可以被回收
        opt.inDither = false;// 图片不抖动
        opt.inTempStorage = new byte[2 * 1024];// 临时储存
        opt.inInputShareable = true;
        InputStream is = context.getResources().openRawResource(imgId);
        return BitmapFactory.decodeStream(is, null, opt);
    }

    /**
     * 图片Bitmap转换成Base64编码字符串
     *
     * @param bmp
     * @return
     */
    public String getStrByBitmap(Bitmap bmp) {
        if (bmp == null)
            return null;
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bmp.compress(Bitmap.CompressFormat.JPEG, 100, baos);
        byte[] buffer = baos.toByteArray();
        return Base64.encodeToString(buffer, Base64.DEFAULT);

    }

    /**
     * 获取指定图片文件的字节输出流
     *
     * @param file
     * @return
     */
    public ByteArrayOutputStream getImageByte(File file) {
        if (!file.exists())
            return null;
        try {
            FileInputStream fis = new FileInputStream(file);
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            byte[] buffer = new byte[1024];
            int len = 0;
            while ((len = fis.read(buffer)) >= 0) {
                baos.write(buffer, 0, len);
            }
            fis.close();
            return baos;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * 获取指定图片文件名的字节输出流
     *
     * @return
     */
    public ByteArrayOutputStream getImageByte(String filename) {
        File file = new File(Environment.getExternalStorageDirectory() + "/"
                + filename);
        return getImageByte(file);
    }

    /**
     * 获取指定图片文件Base64编码的字符串 201412047031
     *
     * @param file
     * @return
     */
    public String getImageStr(File file) {
        if (!file.exists())
            return null;
        try {
            FileInputStream fis = new FileInputStream(file);
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            byte[] buffer = new byte[1024];
            int len = 0;
            while ((len = fis.read(buffer)) >= 0) {
                baos.write(buffer, 0, len);
            }
            fis.close();
            String imageStr = new String(Base64.encode(baos.toByteArray(),
                    Base64.DEFAULT));
            return imageStr;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * 获取指定图片文件Base64编码的字符串
     *
     * @return
     */
    public String getImageStr(String filePath) {
        File file = new File(filePath);
        return getImageStr(file);
    }

    /**
     * 图片字符串转换成Bitmap
     *
     * @param result 图片字符串
     * @return
     */
    public Bitmap getBitmap(String result) {
        byte[] decode = Base64.decode(result, Base64.DEFAULT);
        ByteArrayInputStream bis = new ByteArrayInputStream(decode);
        return BitmapFactory.decodeStream(bis);
    }

    /**
     * 根据输入流得到图片字符串
     *
     * @param is
     * @return
     */
    public String getStrByStream(InputStream is) {
        try {
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            byte[] buffer = new byte[1024];
            int len = 0;
            while ((len = is.read(buffer)) >= 0) {
                baos.write(buffer, 0, len);
            }
            String imageStr = new String(Base64.encode(baos.toByteArray(),
                    Base64.DEFAULT));
            return imageStr;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    /*
     * 获取大图片图片的字节数组
     *
     * @params path 图片路径
     */
    public static byte[] decodeBitmap(String path) {
        Options opts = new Options();
        opts.inJustDecodeBounds = true;// 设置成了true,不占用内存，只获取bitmap宽高
        BitmapFactory.decodeFile(path, opts);
        opts.inSampleSize = computeSampleSize(opts, -1, 1024 * 800);
        opts.inJustDecodeBounds = false;// 这里一定要将其设置回false，因为之前我们将其设置成了true
        opts.inPurgeable = true;
        opts.inInputShareable = true;
        opts.inDither = false;
        opts.inPurgeable = true;
        opts.inTempStorage = new byte[16 * 1024];
        FileInputStream is = null;
        Bitmap bmp = null;
        ByteArrayOutputStream baos = null;
        try {
            is = new FileInputStream(path);
            bmp = BitmapFactory.decodeFileDescriptor(is.getFD(), null, opts);
            double scale = getScaling(opts.outWidth * opts.outHeight,
                    1024 * 600);
            Bitmap bmp2 = Bitmap.createScaledBitmap(bmp,
                    (int) (opts.outWidth * scale),
                    (int) (opts.outHeight * scale), true);
            bmp.recycle();
            baos = new ByteArrayOutputStream();
            bmp2.compress(Bitmap.CompressFormat.JPEG, 100, baos);
            bmp2.recycle();
            return baos.toByteArray();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                is.close();
                baos.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
            System.gc();
        }
        return null;
    }

    private static double getScaling(int src, int des) {
        /**
         * 48 目标尺寸÷原尺寸 sqrt开方，得出宽高百分比 49
         */
        double scale = Math.sqrt((double) des / (double) src);
        return scale;
    }

    public static int computeSampleSize(Options options,
                                        int minSideLength, int maxNumOfPixels) {
        int initialSize = computeInitialSampleSize(options, minSideLength,
                maxNumOfPixels);
        int roundedSize;
        if (initialSize <= 8) {
            roundedSize = 1;
            while (roundedSize < initialSize) {
                roundedSize <<= 1;
            }
        } else {
            roundedSize = (initialSize + 7) / 8 * 8;
        }
        return roundedSize;
    }

    private static int computeInitialSampleSize(Options options,
                                                int minSideLength, int maxNumOfPixels) {
        double w = options.outWidth;
        double h = options.outHeight;
        int lowerBound = (maxNumOfPixels == -1) ? 1 : (int) Math.ceil(Math
                .sqrt(w * h / maxNumOfPixels));
        int upperBound = (minSideLength == -1) ? 128 : (int) Math.min(
                Math.floor(w / minSideLength), Math.floor(h / minSideLength));
        if (upperBound < lowerBound) {
            return lowerBound;
        }
        if ((maxNumOfPixels == -1) && (minSideLength == -1)) {
            return 1;
        } else if (minSideLength == -1) {
            return lowerBound;
        } else {
            return upperBound;
        }
    }

}
