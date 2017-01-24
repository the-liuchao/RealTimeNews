package com.chao.news.liu.api;

import android.text.TextUtils;
import android.util.Log;

import com.chao.news.liu.base.BaseApplication;
import com.chao.news.liu.bean.BankCard;
import com.chao.news.liu.bean.BaseNews;
import com.chao.news.liu.bean.HotspotNews;
import com.chao.news.liu.bean.Identification;
import com.chao.news.liu.bean.LoreList;
import com.chao.news.liu.bean.MD5;
import com.chao.news.liu.bean.PostCode;
import com.chao.news.liu.bean.RealNews;
import com.chao.news.liu.bean.UnitConversion;
import com.chao.news.liu.bean.WeixinNews;
import com.chao.news.liu.bean.healthy.Classify;
import com.chao.news.liu.bean.weat.Weather;
import com.chao.news.liu.listen.CommonCallbackImpl;
import com.liuming.mylibrary.utils.SPHelper;
import com.liuming.mylibrary.utils.XHttpHelper;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.xutils.db.sqlite.WhereBuilder;
import org.xutils.ex.DbException;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import static com.liuming.mylibrary.utils.SPHelper.get;

/**
 * 网络接口API
 * Created by hp on 2017/1/13.
 */

public class HttpAPI {

    private static long UPDATE_TIME = 1 * 24 * 60 * 60 * 1000;
    private static HashMap<String, String> mNewsTypes;

    static {
        mNewsTypes = new HashMap<>();
        mNewsTypes.put(Constants.WORLDURL, "国际新闻");
        mNewsTypes.put(Constants.GOUNEIURL, "国内新闻");
        mNewsTypes.put(Constants.TECHURL, "科技新闻");
        mNewsTypes.put(Constants.SPORTSURL, "体育新闻");
        mNewsTypes.put(Constants.TRAVELURL, "旅游新闻");
        mNewsTypes.put(Constants.QIWEIURL, "奇闻轶事");
        mNewsTypes.put(Constants.WEIXINURL, "微信精选");
    }

    private static HttpAPI instance;

    public static HttpAPI getInstance() {
        if (instance == null) {
            instance = new HttpAPI();
        }
        return instance;
    }

    public interface BaseNewsListener {

        void onSuccess(List<BaseNews> results);

        void onFail(String errMsg, int errCode);
    }


    public interface WxNewsListener {

        void onSuccess(List<WeixinNews> results);

        void onFail(String errMsg, int errCode);
    }


    public interface RealNewsListener {

        void onSuccess(List<RealNews> results);

        void onFail(String errMsg, int errCode);
    }

    public interface HotspotNewsListener {
        void onSuccess(List<HotspotNews> hotspotNewses);

        void onFail(String errMsg, int errCode);
    }

    public interface WeatherListener {
        void onSuccess(Weather weathers);

        void onFail(String errMsg, int errCode);
    }

    public interface LBJTimeListener {
        void onSuccess(String stime);

        void onFail(String errMsg, int errCode);
    }

    public interface UnitConverListener {
        void onSuccess(List<UnitConversion> results);

        void onFail(String errMsg, int errCode);
    }

    public interface IdentificationListener {
        void onSuccess(Identification id);

        void onFail(String errMsg, int errCode);
    }

    public interface BankCardListener {
        void onSuccess(BankCard id);

        void onFail(String errMsg, int errCode);
    }

    public interface TranslateListener {
        void onSuccess(String id);

        void onFail(String errMsg, int errCode);
    }

    public interface PostcodeListener {
        void onSuccess(List<PostCode> codes);

        void onFail(String errMsg, int errCode);
    }
    public interface ClassifyListener {
        void onSuccess(List<Classify> classifys);

        void onFail(String errMsg, int errCode);
    }
    public interface LoreListListener {
        void onSuccess(List<LoreList> loreLists, int total);

        void onFail(String errMsg, int errCode);
    }
    public interface MD5Listener {
        void onSuccess(MD5 md5);

        void onFail(String errMsg, int errCode);
    }

    /**
     * 热点列表
     *
     * @param params
     * @param listener
     */
    public void requestHotspotNews(final HashMap<String, String> params, final HotspotNewsListener listener) {
        long lastTime = (Long) get(BaseApplication.getInstance(), Constants.HOTSPOTURL, 0l);
        if (System.currentTimeMillis() - lastTime < UPDATE_TIME) {            //一天之内,而且是第一页，取缓存数据
            try {
                List<HotspotNews> hotspotNewses = BaseApplication.getInstance()
                        .getDbManager()
                        .findAll(HotspotNews.class);
                if (null != listener) {
                    listener.onSuccess(hotspotNewses);
                }
            } catch (DbException e) {
                e.printStackTrace();
                request(params, listener);
            }
        } else {
            request(params, listener);
        }
    }

    private void request(HashMap<String, String> params, final HotspotNewsListener listener) {
        XHttpHelper.doRequest(Constants.HOTSPOTURL, params, new CommonCallbackImpl<String>() {
            @Override
            public void onSuccess(String result) {
                parserHotspotResult(result, listener);
            }

            @Override
            public void onError(Throwable ex, boolean isOnCallback) {
                listener.onFail(ex.getMessage(), -100);
            }

        });
    }

    /**
     * 解析检索新闻请求结果
     *
     * @param result
     * @param listener
     */
    private void parserHotspotResult(String result, HotspotNewsListener listener) {
        try {
            if (!TextUtils.isEmpty(result) && listener != null) {
                List<HotspotNews> hotsoptNewses = new ArrayList<>();
                JSONObject obj = new JSONObject(result);
                JSONArray arr = obj.optJSONArray("result");
                for (int i = 0; i < arr.length(); i++) {
                    HotspotNews hotspotNews = new HotspotNews();
                    hotspotNews.setmHotspot(arr.optString(i));
                    hotspotNews.setmCurrTime(System.currentTimeMillis());
                    hotsoptNewses.add(hotspotNews);
                }
                SPHelper.put(BaseApplication.getInstance(), Constants.HOTSPOTURL, System.currentTimeMillis());
                BaseApplication.getInstance().getDbManager().delete(HotspotNews.class);
                BaseApplication.getInstance().getDbManager().save(hotsoptNewses);
                if (null != listener) {
                    listener.onSuccess(hotsoptNewses);
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
            listener.onFail("数据解析失败！", -100);
        } catch (DbException e) {
            e.printStackTrace();
        }
    }

    //=====================================关键搜索============================//

    /**
     * 新闻关键字搜索
     */
    public void requestKeyRealNews(final HashMap<String, String> params, final RealNewsListener listener) {
        request(params, listener);
    }

    /**
     * 时事新闻
     */
    public void requestRealNews(final HashMap<String, String> params, final RealNewsListener listener) {
        long lastTime = (Long) get(BaseApplication.getInstance(), Constants.CACHEHOTKEY, 0l);
        if (System.currentTimeMillis() - lastTime < UPDATE_TIME) {//一天之内拿去缓存数据
            try {
                List<RealNews> realNewses = BaseApplication.getInstance().getDbManager().findAll(RealNews.class);
                if (null != listener) {
                    listener.onSuccess(realNewses);
                }
            } catch (DbException e) {
                e.printStackTrace();
                request(params, listener);
            }
        } else {
            request(params, listener);
        }
    }

    /**
     * 请求时事新闻
     *
     * @param params
     * @param listener
     */
    private void request(final HashMap<String, String> params, final RealNewsListener listener) {
        XHttpHelper.doRequest(Constants.RealURL, params, new CommonCallbackImpl<String>() {
            @Override
            public void onSuccess(String result) {
                parserRealResult(params.get("keyword"), result, listener);
            }

            @Override
            public void onError(Throwable ex, boolean isOnCallback) {
                listener.onFail(ex.getMessage(), -100);
            }

        });
    }


    /**
     * 解析检索新闻请求结果
     *
     * @param result
     * @param listener
     */
    private void parserRealResult(String keyword, String result, RealNewsListener listener) {
        try {
            if (!TextUtils.isEmpty(result) && listener != null) {
                List<RealNews> realNewses = new ArrayList<>();
                JSONObject obj = new JSONObject(result);
                JSONArray arr = obj.optJSONArray("result");
                for (int i = 0; i < arr.length(); i++) {
                    RealNews realNews = new RealNews();
                    realNews.parser(keyword, arr.optJSONObject(i));
                    realNewses.add(realNews);
                }
                String city = (String) SPHelper.get(BaseApplication.getInstance(), Constants.CACHECITY, "中国");
                if (city.equals(keyword)) {                         //首页今日要闻才进行数据缓存
                    SPHelper.put(BaseApplication.getInstance(), Constants.CACHEHOTKEY, System.currentTimeMillis());
                    BaseApplication.getInstance().getDbManager().delete(RealNews.class);
                    BaseApplication.getInstance().getDbManager().save(realNewses);
                }
                if (null != listener) {
                    listener.onSuccess(realNewses);
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
            listener.onFail("数据解析失败！", -100);
        } catch (DbException e) {
            e.printStackTrace();
        }
    }

    //=====================================关键搜索============================//
    //=====================================国际，国内，体育，科技等新闻搜索============================//

    /**
     * 各类新闻
     */
    public void requestBaseNews(final String url, HashMap<String, String> params, final BaseNewsListener listener) {
        long lastTime = (Long) get(BaseApplication.getInstance(), url, 0l);
        int page = Integer.parseInt(params.get("page"));
        if (System.currentTimeMillis() - lastTime < UPDATE_TIME && page == 1) {            //一天之内,而且是第一页，取缓存数据
            try {
                List<BaseNews> baseNewses = BaseApplication.getInstance()
                        .getDbManager()
                        .selector(BaseNews.class)
                        .where("newstype", " = ", mNewsTypes.get(url))
                        .findAll();
                if (null != listener) {
                    listener.onSuccess(baseNewses);
                }
            } catch (DbException e) {
                e.printStackTrace();
                request(page, url, params, listener);
            }
        } else {
            request(page, url, params, listener);
        }

    }

    private void request(final int page, final String url, HashMap<String, String> params, final BaseNewsListener listener) {
        XHttpHelper.doRequest(url, params, new CommonCallbackImpl<String>() {
            @Override
            public void onSuccess(String result) {
                parserBaseResult(page, url, result, listener);
            }

            @Override
            public void onError(Throwable ex, boolean isOnCallback) {
                listener.onFail(ex.getMessage(), -100);
            }
        });
    }


    /**
     * 解析各类新闻请求结果
     *
     * @param result
     * @param listener
     */
    private void parserBaseResult(int page, String url, String result, BaseNewsListener listener) {
        try {
            if (!TextUtils.isEmpty(result) && listener != null) {
                List<BaseNews> baseNewses = new ArrayList<>();
                JSONObject obj = new JSONObject(result);
                JSONArray arr = obj.optJSONArray("result");
                for (int i = 0; i < arr.length(); i++) {
                    BaseNews baseNews = new BaseNews();
                    baseNews.parser(mNewsTypes.get(url), arr.optJSONObject(i));
                    baseNewses.add(baseNews);
                }
                if (page == 1) {    //保存首页数据
                    SPHelper.put(BaseApplication.getInstance(), url, System.currentTimeMillis());
                    BaseApplication.getInstance().getDbManager().delete(BaseNews.class, WhereBuilder.b("newstype", " = ", mNewsTypes.get(url)));
                    BaseApplication.getInstance().getDbManager().save(baseNewses);
                }
                if (null != listener) {
                    listener.onSuccess(baseNewses);
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
            listener.onFail("数据解析失败！", -100);
        } catch (DbException e) {
            e.printStackTrace();
        }
    }
    //=====================================国际，国内，体育，科技等新闻搜索============================//
    //======================================微信精选================================//

    /**
     * 各类新闻
     */
    public void requestWxNews(HashMap<String, String> params, final WxNewsListener listener) {
        long lastTime = (Long) get(BaseApplication.getInstance(), Constants.WEIXINURL, 0l);
        if (System.currentTimeMillis() - lastTime < UPDATE_TIME) {            //一天之内，取缓存数据
            try {
                List<WeixinNews> wxNewses = BaseApplication.getInstance()
                        .getDbManager()
                        .findAll(WeixinNews.class);
                if (null != listener) {
                    listener.onSuccess(wxNewses);
                }
            } catch (DbException e) {
                e.printStackTrace();
                request(params, listener);
            }
        } else {
            request(params, listener);
        }
    }


    private void request(HashMap<String, String> params, final WxNewsListener listener) {
        XHttpHelper.doRequest(Constants.WEIXINURL, params, new CommonCallbackImpl<String>() {
            @Override
            public void onSuccess(String result) {
                parserWxResult(result, listener);
            }

            @Override
            public void onError(Throwable ex, boolean isOnCallback) {
                listener.onFail(ex.getMessage(), -100);
            }

        });
    }


    /**
     * 解析微信精选请求结果
     *
     * @param result
     * @param listener
     */
    private void parserWxResult(String result, WxNewsListener listener) {
        try {
            if (!TextUtils.isEmpty(result) && listener != null) {
                List<WeixinNews> wxNewses = new ArrayList<>();
                JSONObject obj = new JSONObject(result);
                JSONArray arr = obj.optJSONArray("result");
                for (int i = 0; i < arr.length(); i++) {
                    WeixinNews wxNews = new WeixinNews();
                    wxNews.parser("微信精选", arr.optJSONObject(i));
                    wxNewses.add(wxNews);
                }
                SPHelper.put(BaseApplication.getInstance(), Constants.WEIXINURL, System.currentTimeMillis());
                BaseApplication.getInstance().getDbManager().delete(WeixinNews.class);
                BaseApplication.getInstance().getDbManager().save(wxNewses);
                if (null != listener) {
                    listener.onSuccess(wxNewses);
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
            listener.onFail("数据解析失败！", -100);
        } catch (DbException e) {
            e.printStackTrace();
        }
    }
//======================================微信精选================================//
//======================================天气查询================================//

    /**
     * 天气
     */
    public void requestWeather(HashMap<String, String> params, final WeatherListener listener) {
        String city = params.get("cityname");
        long lastTime = (Long) get(BaseApplication.getInstance(), Constants.WEATHERURL, 0l);
        if (System.currentTimeMillis() - lastTime < UPDATE_TIME) {            //一天之内，取缓存数据
            try {
                Weather weather = BaseApplication.getInstance()
                        .getDbManager()
                        .selector(Weather.class)
                        .where("city", "=", city)
                        .findFirst();
                if (null != listener && null != weather) {
                    listener.onSuccess(weather);
                } else {
                    request(params, city, listener);
                }
            } catch (DbException e) {
                e.printStackTrace();
                request(params, city, listener);
            }
        } else {
            request(params, city, listener);
        }
    }

    private void request(HashMap<String, String> params, final String city, final WeatherListener listener) {
        XHttpHelper.doRequest(Constants.WEATHERURL, params, new CommonCallbackImpl<String>() {
            @Override
            public void onSuccess(String result) {
                parserWeatResult(city, result, listener);
            }

            @Override
            public void onError(Throwable ex, boolean isOnCallback) {
                listener.onFail(ex.getMessage(), -100);
            }

        });
    }

    private void parserWeatResult(String city, String result, WeatherListener listener) {
        try {
            if (!TextUtils.isEmpty(result) && listener != null) {
                JSONObject obj = new JSONObject(result);
                JSONObject o = obj.optJSONObject("result");
                Weather weather = new Weather();
                weather.parser(city, o);
                SPHelper.put(BaseApplication.getInstance(), Constants.WEATHERURL, System.currentTimeMillis());
                BaseApplication.getInstance().getDbManager().delete(Weather.class, WhereBuilder.b("city", "=", city));
                BaseApplication.getInstance().getDbManager().save(weather);
                if (null != listener) {
                    listener.onSuccess(weather);
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
            listener.onFail("数据解析失败！", -100);
        } catch (DbException e) {
            e.printStackTrace();
        }
    }
//======================================天气查询================================//
//======================================北京时间查询================================//

    /**
     * 北京时间
     */
    public void requestBeijingTime(HashMap<String, String> params, final LBJTimeListener listener) {
        XHttpHelper.doRequest(Constants.BEIJINGTIMEURL, params, new CommonCallbackImpl<String>() {
            @Override
            public void onSuccess(String result) {
                parserTimeResult(result, listener);
            }

            @Override
            public void onError(Throwable ex, boolean isOnCallback) {
                listener.onFail(ex.getMessage(), -100);
            }

        });
    }

    private void parserTimeResult(String result, LBJTimeListener listener) {
        try {
            if (!TextUtils.isEmpty(result) && listener != null) {
                JSONObject obj = new JSONObject(result);
                JSONObject o = obj.optJSONObject("result");
                if (null != listener) {
                    listener.onSuccess(o == null ? "" : o.optString("stime"));
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
            listener.onFail("数据解析失败！", -100);
        }
    }
//======================================北京时间查询================================//
//======================================单位转换================================//

    /**
     * 单位转换
     *
     * @param index 0,长度 1，面积  2，体积 3，重量 4，功 5，功率 6，压强 7，存储
     */
    public void requestUnitConver(int index, HashMap<String, String> params, final UnitConverListener listener) {
        XHttpHelper.doRequest(getUnitUrl(index), params, new CommonCallbackImpl<String>() {
            @Override
            public void onSuccess(String result) {
                parserUnitResult(result, listener);
            }

            @Override
            public void onError(Throwable ex, boolean isOnCallback) {
                listener.onFail(ex.getMessage(), -100);
            }
        });
    }

    private String getUnitUrl(int index) {
        switch (index) {
            case 0:
                return Constants.BASEURL + "UnitConvert/Length";
            case 1:
                return Constants.BASEURL + "UnitConvert/Area";
            case 2:
                return Constants.BASEURL + "UnitConvert/Volume";
            case 3:
                return Constants.BASEURL + "UnitConvert/Weight";
            case 4:
                return Constants.BASEURL + "UnitConvert/Energy";
            case 5:
                return Constants.BASEURL + "UnitConvert/Power";
            case 6:
                return Constants.BASEURL + "UnitConvert/Pressure";
            case 7:
                return Constants.BASEURL + "UnitConvert/Stroge";
        }
        return null;
    }

    private void parserUnitResult(String result, UnitConverListener listener) {
        try {
            if (!TextUtils.isEmpty(result) && listener != null) {
                List<UnitConversion> results = new ArrayList<>();
                JSONObject json = new JSONObject(result);
                JSONArray arr = json.getJSONArray("result");
                int len = arr.length();
                UnitConversion unit;
                JSONObject obj;
                for (int i = 0; i < len; i++) {
                    obj = arr.getJSONObject(i);
                    unit = new UnitConversion();
                    unit.mUnit = obj.optInt("unit");
                    unit.mName = obj.optString("name");
                    unit.mValue = obj.optString("value");
                    results.add(unit);
                }
                if (null != listener) {
                    listener.onSuccess(results);
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
            listener.onFail("数据解析失败！", -100);
        }
    }

    //======================================单位转换================================//
//======================================身份证认证信息================================//
    public void requestIdAuthent(HashMap<String, String> params, final IdentificationListener listener) {
        XHttpHelper.doRequest(Constants.IDAUTHEURL, params, new CommonCallbackImpl<String>() {

            @Override
            public void onSuccess(String result) {
                try {
                    if (TextUtils.isEmpty(result) || "null".equals(result)) {
                        listener.onFail("获取信息失败", -100);
                        return;
                    }
                    JSONObject resultJson = new JSONObject(result);
                    JSONObject obj = resultJson.optJSONObject("result");
                    if (obj == null || "null".equals(obj.toString()) || "".equals(obj.toString())) {
                        if (null != listener) listener.onFail(resultJson.optString("reason"), -101);
                        return;
                    }
                    Identification id = new Identification();
                    if (null != listener) listener.onSuccess(id.parser(obj));
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onError(Throwable ex, boolean isOnCallback) {
                listener.onFail(ex.getMessage(), -100);
            }
        });
    }

    //======================================身份证认证信息================================//
//======================================银行卡信息================================//
    public void requestBankCard(HashMap<String, String> params, final BankCardListener listener) {
        XHttpHelper.doRequest(Constants.BANKCARDAUTHURL, params, new CommonCallbackImpl<String>() {

            @Override
            public void onSuccess(String result) {
                try {
                    if (TextUtils.isEmpty(result)) {
                        listener.onFail("请求失败", -100);
                        return;
                    }
                    JSONObject resultJson = new JSONObject(result);
                    JSONObject resultObj = resultJson.optJSONObject("result");
                    if (resultObj == null || "null".equals(resultObj.toString()) || "".equals(resultObj.toString())) {
                        if (null != listener) listener.onFail(resultJson.optString("reason"), -101);
                        return;
                    }
                    JSONObject resultInfo = resultJson.optJSONObject("bankCardInfo");
                    BankCard card = new BankCard();
                    card.parser(resultObj.optString("code"), resultObj.optString("message"), resultInfo);
                    if (null != listener) listener.onSuccess(card);
                } catch (JSONException e) {
                    e.printStackTrace();
                    listener.onFail("请求失败", -100);
                }
            }

            @Override
            public void onError(Throwable ex, boolean isOnCallback) {
                listener.onFail(ex.getMessage(), -100);
            }
        });
    }

    //======================================银行卡信息================================//
//======================================在线翻译================================//
    public void requestTranslate(HashMap<String, String> params, final TranslateListener listener) {
        XHttpHelper.get(Constants.TRANSLATEURL, params, new CommonCallbackImpl<String>() {
            @Override
            public void onSuccess(String result) {
                try {
                    if (TextUtils.isEmpty(result)) {
                        listener.onFail("请求失败", -100);
                        return;
                    }
                    JSONObject resultJson = new JSONObject(result);
                    JSONObject resultObj = resultJson.optJSONObject("result");
                    if (resultObj == null || "null".equals(resultObj)) {
                         listener.onFail(resultObj.optString("reason"), -101);
                        return;
                    }
                    JSONObject translateResult = resultObj.optJSONObject("trans_result");
                    if (null != listener) listener.onSuccess(translateResult.optString("dst"));
                } catch (JSONException e) {
                    e.printStackTrace();
                    listener.onFail("请求失败", -100);
                }

            }
        });
//        XHttpHelper.doRequest(Constants.TRANSLATEURL, params, new CommonCallbackImpl<String>() {
//
//            @Override
//            public void onSuccess(String result) {
//                try {
//                    if (TextUtils.isEmpty(result)) {
//                        listener.onFail("请求失败", -100);
//                        return;
//                    }
//                    JSONObject resultJson = new JSONObject(result);
//                    JSONObject resultObj = resultJson.optJSONObject("result");
//                    JSONObject translateResult = resultObj.optJSONObject("trans_result");
//                    if (null != listener) listener.onSuccess(translateResult.optString("dst"));
//                } catch (JSONException e) {
//                    e.printStackTrace();
//                    listener.onFail("请求失败", -100);
//                }
//            }
//
//            @Override
//            public void onError(Throwable ex, boolean isOnCallback) {
//                listener.onFail(ex.getMessage(), -100);
//            }
//        });
    }

    //======================================在线翻译================================//
//====================================== 邮编地址================================//
    public void requestPostcode(int index, final HashMap<String, String> params, final PostcodeListener listener) {
        XHttpHelper.doRequest(getPostcodeUrl(index), params, new CommonCallbackImpl<String>() {

            @Override
            public void onSuccess(String result) {
                try {
                    if (TextUtils.isEmpty(result)) {
                        listener.onFail("请求失败", -100);
                        return;
                    }
                    List<PostCode> postCodes = new ArrayList<>();
                    JSONObject resultJson = new JSONObject(result);
                    JSONArray arr = resultJson.optJSONArray("result");
                    int len = arr.length();
                    if (len <= 0 && null != listener) {
                        listener.onFail("暂无数据！", -1000);
                        return;
                    }
                    for (int i = 0; i < len; i++) {
                        JSONObject json = arr.optJSONObject(i);
                        PostCode postCode = new PostCode();
                        postCode.parser(json);
                        postCodes.add(postCode);
                    }
                    if (null != listener) listener.onSuccess(postCodes);
                } catch (JSONException e) {
                    e.printStackTrace();
                    listener.onFail("请求失败", -100);
                }
            }

            @Override
            public void onError(Throwable ex, boolean isOnCallback) {
                listener.onFail(ex.getMessage(), -100);
            }
        });
    }

    private String getPostcodeUrl(int index) {
        return index == 1 ? Constants.POSTADDRURL : Constants.POSTCODEURL;
    }
//======================================邮编地址================================//
// ======================================MD5破解================================//
public void requestMD5(HashMap<String, String> params, final MD5Listener listener) {
    XHttpHelper.doRequest(Constants.MD5URL, params, new CommonCallbackImpl<String>() {

        @Override
        public void onSuccess(String result) {
            Log.e("MD5", "=====result" + result);
            try {
                if (TextUtils.isEmpty(result)) {
                    listener.onFail("请求失败", -100);
                    return;
                }
                JSONObject resultJson = new JSONObject(result);
                JSONObject resultObj = resultJson.optJSONObject("result");
                if (resultObj == null || "null".equals(resultObj.toString()) || "".equals(resultObj.toString())) {
                    if (null != listener)
                        listener.onFail(resultJson.optString("reason"), resultJson.optInt("error_code"));
                    return;
                }
                MD5 md5 = new MD5();
                md5.parser(resultObj);
                if (null != listener) listener.onSuccess(md5);
            } catch (JSONException e) {
                e.printStackTrace();
                listener.onFail("请求失败", -100);
            }
        }

        @Override
        public void onError(Throwable ex, boolean isOnCallback) {
            listener.onFail(ex.getMessage(), -100);
        }
    });
}
// ======================================MD5破解================================//
   // ======================================健康知识查询================================//
    // ======================================健康知识分类================================//
    public void requestClassify(HashMap<String, String> params, final ClassifyListener listener) {
        XHttpHelper.doRequest(Constants.CLASSIFYURL, params, new CommonCallbackImpl<String>() {

            @Override
            public void onSuccess(String result) {
                Log.e("Classify", "=====result==" + result);
                try {
                    if (TextUtils.isEmpty(result)) {
                        listener.onFail("请求失败", -100);
                        return;
                    }
                    JSONObject resultJson = new JSONObject(result);
                    JSONArray jsonArray = resultJson.optJSONArray("result");
                    if (jsonArray == null || "null".equals(jsonArray.toString()) || "".equals(jsonArray.toString())) {
                        if (null != listener)
                            listener.onFail(resultJson.optString("reason"), resultJson.optInt("error_code"));
                        return;
                    }
                    if (null != listener) listener.onSuccess(parserClassifies(jsonArray));
                } catch (JSONException e) {
                    e.printStackTrace();
                    listener.onFail("请求失败", -100);
                }
            }

            @Override
            public void onError(Throwable ex, boolean isOnCallback) {
                listener.onFail(ex.getMessage(), -100);
            }
        });
    }

    /**
     * 创建时间 :
     * 功能描述 :健康知识分类集合
     */
    private List<Classify> parserClassifies(JSONArray jsonArray) {
        List<Classify> listClassify = new ArrayList<>();
        if (jsonArray == null || jsonArray.length() == 0) return null;
        for (int i = 0; i < jsonArray.length() ; i++) {
            try {
                listClassify.add(new Classify().parserClassify((JSONObject) jsonArray.get(i)));
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        return listClassify;
    }

    // ======================================健康知识分类================================//
// ======================================健康知识列表================================//
    public void requestLoreList(HashMap<String, String> params, final LoreListListener listener) {
        XHttpHelper.doRequest(Constants.LISTURL, params, new CommonCallbackImpl<String>() {

            @Override
            public void onSuccess(String result) {
                Log.e("Classify", "=====result==" + result);
                try {
                    if (TextUtils.isEmpty(result)) {
                        listener.onFail("请求失败", -100);
                        return;
                    }
                    JSONObject resultJson = new JSONObject(result);
                    JSONArray jsonArray = resultJson.optJSONArray("result");
                    if (jsonArray == null || "null".equals(jsonArray.toString()) || "".equals(jsonArray.toString())) {
                        if (null != listener)
                            listener.onFail(resultJson.optString("reason"), resultJson.optInt("error_code"));
                        return;
                    }
                    if (null != listener) listener.onSuccess(new LoreList().parserLoreLists(jsonArray),resultJson.getInt("total"));
                } catch (JSONException e) {
                    e.printStackTrace();
                    listener.onFail("请求失败", -100);
                }
            }

            @Override
            public void onError(Throwable ex, boolean isOnCallback) {
                listener.onFail(ex.getMessage(), -100);
            }
        });
    }
// ======================================健康知识列表================================//
// ======================================健康知识查询================================//

}
