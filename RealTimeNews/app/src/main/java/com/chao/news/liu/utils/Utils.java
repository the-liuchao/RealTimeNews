package com.chao.news.liu.utils;

import android.app.Activity;
import android.graphics.Bitmap;
import android.text.TextUtils;
import android.widget.Toast;

import com.chao.news.liu.R;
import com.liuming.mylibrary.utils.DisplayHelper;
import com.liuming.mylibrary.utils.ImageHelper;
import com.umeng.socialize.ShareAction;
import com.umeng.socialize.UMShareListener;
import com.umeng.socialize.bean.SHARE_MEDIA;
import com.umeng.socialize.media.UMImage;

import net.sourceforge.pinyin4j.PinyinHelper;
import net.sourceforge.pinyin4j.format.HanyuPinyinCaseType;
import net.sourceforge.pinyin4j.format.HanyuPinyinOutputFormat;
import net.sourceforge.pinyin4j.format.HanyuPinyinToneType;
import net.sourceforge.pinyin4j.format.HanyuPinyinVCharType;
import net.sourceforge.pinyin4j.format.exception.BadHanyuPinyinOutputFormatCombination;

/**
 * Created by hp on 2017/1/17.
 */
public class Utils {

    /**
     * 将字符串中的中文转化为拼音,其他字符不变
     *
     * @param inputString
     * @return
     */
    public static String getPingYin(String inputString) {
        HanyuPinyinOutputFormat format = new HanyuPinyinOutputFormat();
        format.setCaseType(HanyuPinyinCaseType.LOWERCASE);
        format.setToneType(HanyuPinyinToneType.WITHOUT_TONE);
        format.setVCharType(HanyuPinyinVCharType.WITH_V);

        char[] input = inputString.trim().toCharArray();
        String output = "";
        try {
            for (int i = 0; i < input.length; i++) {
                if (java.lang.Character.toString(input[i]).matches("[\\u4E00-\\u9FA5]+")) {
                    String[] temp = PinyinHelper.toHanyuPinyinStringArray(input[i], format);
                    output += temp[0];
                } else
                    output += java.lang.Character.toString(input[i]);
            }
        } catch (BadHanyuPinyinOutputFormatCombination e) {
            e.printStackTrace();
        }
        return output;
    }

    /**
     * 天气背景图
     * @param weather
     * @return
     */
    public static int getWeathBg(String weather) {
        if (TextUtils.isEmpty(weather)) return R.mipmap.anim_weather_default;
        if ("晴".equals(weather) || "晴转多云".equals(weather))
            return R.mipmap.anim_weather_01;
        if ("多云".equals(weather))
            return R.mipmap.anim_weather_02;
        if ("阴".equals(weather) || "阵雨".equals(weather))
            return R.mipmap.anim_weather_03;
        if ("沙尘".equals(weather) || "扬沙".equals(weather)
                || "浮沉".equals(weather) || "强沙尘暴".equals(weather))
            return R.mipmap.anim_weather_04;
        if ("大雾".equals(weather) || "轻雾".equals(weather) || "浓雾".equals(weather))
            return R.mipmap.anim_weather_05;
        if ("雪".equals(weather) || "小雪".equals(weather)
                || "大雪".equals(weather) || "暴雪".equals(weather))
            return R.mipmap.anim_weather_06;
        if ("小雨".equals(weather) || "雨".equals(weather))
            return R.mipmap.anim_weather_07;
        if (weather.contains("夜"))
            return R.mipmap.anim_weather_08;
        if ("暴雨".equals(weather) || "大雨".equals(weather))
            return R.mipmap.anim_weather_09;
        if ("阵雨".equals(weather) || "雷阵雨".equals(weather))
            return R.mipmap.anim_weather_10;
        return R.mipmap.anim_weather_default;
    }

    /**
     * 获得天气图标
     *
     * @param weather
     * @return
     */
    public static Bitmap getWeathIcon(String weather) {
        if (TextUtils.isEmpty(weather)) return null;
        if ("晴".equals(weather))
            return ImageHelper.getInstance().readBitmapById(R.mipmap.icon_weather_01);
        if ("晴转多云".equals(weather))
            return ImageHelper.getInstance().readBitmapById(R.mipmap.icon_weather_02);
        if ("多云".equals(weather))
            return ImageHelper.getInstance().readBitmapById(R.mipmap.icon_weather_03);
        if ("阴".equals(weather))
            return ImageHelper.getInstance().readBitmapById(R.mipmap.icon_weather_04);
        if ("霾".equals(weather))
            return ImageHelper.getInstance().readBitmapById(R.mipmap.icon_weather_05);
        if ("沙尘".equals(weather))
            return ImageHelper.getInstance().readBitmapById(R.mipmap.icon_weather_06);
        if ("阵雨".equals(weather))
            return ImageHelper.getInstance().readBitmapById(R.mipmap.icon_weather_07);
        if ("小雨".equals(weather))
            return ImageHelper.getInstance().readBitmapById(R.mipmap.icon_weather_08);
        if ("雨".equals(weather))
            return ImageHelper.getInstance().readBitmapById(R.mipmap.icon_weather_08);
        if ("大雨".equals(weather))
            return ImageHelper.getInstance().readBitmapById(R.mipmap.icon_weather_09);
        if ("暴雨".equals(weather))
            return ImageHelper.getInstance().readBitmapById(R.mipmap.icon_weather_09);
        if ("雷阵雨".equals(weather))
            return ImageHelper.getInstance().readBitmapById(R.mipmap.icon_weather_10);
        if ("小雪".equals(weather))
            return ImageHelper.getInstance().readBitmapById(R.mipmap.icon_weather_11);
        if ("雪".equals(weather))
            return ImageHelper.getInstance().readBitmapById(R.mipmap.icon_weather_11);
        if ("大雪".equals(weather))
            return ImageHelper.getInstance().readBitmapById(R.mipmap.icon_weather_12);
        if ("暴雪".equals(weather))
            return ImageHelper.getInstance().readBitmapById(R.mipmap.icon_weather_12);
        if ("雨夹雪".equals(weather))
            return ImageHelper.getInstance().readBitmapById(R.mipmap.icon_weather_13);
        if ("冰夹雪".equals(weather))
            return ImageHelper.getInstance().readBitmapById(R.mipmap.icon_weather_14);
        if ("雪夹冰".equals(weather))
            return ImageHelper.getInstance().readBitmapById(R.mipmap.icon_weather_14);
        if ("扬沙".equals(weather))
            return ImageHelper.getInstance().readBitmapById(R.mipmap.icon_weather_15);
        if ("浮沉".equals(weather))
            return ImageHelper.getInstance().readBitmapById(R.mipmap.icon_weather_15);
        if ("强沙尘暴".equals(weather))
            return ImageHelper.getInstance().readBitmapById(R.mipmap.icon_weather_16);
        if ("轻雾".equals(weather))
            return ImageHelper.getInstance().readBitmapById(R.mipmap.icon_weather_17);
        if ("雾".equals(weather))
            return ImageHelper.getInstance().readBitmapById(R.mipmap.icon_weather_17);
        if ("大雾".equals(weather))
            return ImageHelper.getInstance().readBitmapById(R.mipmap.icon_weather_18);
        if ("浓雾".equals(weather))
            return ImageHelper.getInstance().readBitmapById(R.mipmap.icon_weather_18);

        return ImageHelper.getInstance().readBitmapById(R.mipmap.icon_weather_default);
    }


    public static void share(final Activity act) {
        final Bitmap bmp = DisplayHelper.screenshot(act);
        new ShareAction(act).withTitle("新闻速递")
                .withMedia(new UMImage(act, bmp))
                .setDisplayList(SHARE_MEDIA.SINA, SHARE_MEDIA.QQ, SHARE_MEDIA.QZONE, SHARE_MEDIA.WEIXIN
                        , SHARE_MEDIA.RENREN, SHARE_MEDIA.SMS, SHARE_MEDIA.EMAIL)
                .setCallback(new UMShareListener() {
                    @Override
                    public void onResult(SHARE_MEDIA share_media) {
                        bmp.recycle();
                    }

                    @Override
                    public void onError(SHARE_MEDIA share_media, Throwable throwable) {
                        bmp.recycle();
                        Toast.makeText(act, "分享失败", Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onCancel(SHARE_MEDIA share_media) {
                        bmp.recycle();
                    }
                }).open();
    }
}
