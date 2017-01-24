package com.chao.news.liu.api;

import android.database.Cursor;

import com.chao.news.liu.base.BaseApplication;
import com.chao.news.liu.bean.Address;
import com.liuming.mylibrary.utils.FileHelper;

import org.xutils.DbManager;
import org.xutils.x;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Created by hp on 2017/1/18.
 */

public class DatabaseDao {


    private static DatabaseDao mDao = new DatabaseDao();

    public static DatabaseDao getInstance() {
        return mDao;
    }

    public interface QueryAreaListener {
        void onResult(List<Address> mCities);

        void onError(String errorMsg);
    }

    public interface PCDQueryListener {
        void onResult(List<String> mProvinces, HashMap<String, List<String>> mCitys, HashMap<String, List<String>> mDistricts);

        void onError(String errorMsg);
    }

    public void queryArea(final QueryAreaListener listener) {
        new Thread() {
            @Override
            public void run() {
                try {
                    List<Address> mCities = new ArrayList<>();
                    FileHelper helper = new FileHelper(BaseApplication.getInstance());
//                    File cacheDir = new File(helper.getSdcardPath() + "/fda_cache/city");
//                    File cacheFile = new File(cacheDir, "city.out");
//                    if (cacheFile.exists()) {
//                        mCities = helper.readListFromSdCard("city.out");
//                        if (mCities != null && mCities.size() > 0 && null != listener) {
//                            listener.onResult(mCities);
//                            return;
//                        }
//                    }
                    String path = BaseApplication.getInstance().getDatabasePath("city").getPath();
                    if (!helper.exist(path + "/" + "city.db")) {
                        helper.copyAssets("city.db", path);
                    }
                    DbManager.DaoConfig config = new DbManager.DaoConfig()
                            .setDbDir(new File(path))
                            .setDbName("city.db");
                    DbManager mDm = x.getDb(config);
                    Cursor c = mDm.execQuery("select * from t_city order by NAMEEN");
                    while (c.moveToNext()) {
                        Address addr = new Address();
                        addr.setmCityPY(c.getString(c.getColumnIndex("NAMEEN")));
                        addr.setmCity(c.getString(c.getColumnIndex("NAMECN")));
                        addr.setmPronvice(c.getString(c.getColumnIndex("PROVCN")));
                        addr.setmDistrict(c.getString(c.getColumnIndex("DISTRICTEN")));
                        mCities.add(addr);
                    }
                    c.close();
//                    if (!cacheDir.exists()) cacheDir.mkdirs();
//                    helper.writeListIntoSDcard("city.out", mCities);
                    if (null != listener)
                        listener.onResult(mCities);
                } catch (IOException e) {
                    e.printStackTrace();
                    if (null != listener)
                        listener.onError(e.getMessage());
                }
            }
        }.start();

    }

    /**
     * 地址数据查询
     *
     * @param listener
     */
    public void queryArea(final PCDQueryListener listener) {
        new Thread() {
            @Override
            public void run() {
                try {
                    List<String> mProvinces = new ArrayList<>();
                    List<String> mCities = new ArrayList<>();
                    HashMap<String, List<String>> mProvinceCities = new HashMap<>();
                    HashMap<String, List<String>> mCityDistricts = new HashMap<>();
                    FileHelper helper = new FileHelper(BaseApplication.getInstance());
                    String path = BaseApplication.getInstance().getDatabasePath("city").getPath();
                    if (!helper.exist(path + "/" + "city.db")) {
                        helper.copyAssets("city.db", path);
                    }
                    DbManager.DaoConfig config = new DbManager.DaoConfig()
                            .setDbDir(new File(path))
                            .setDbName("city.db");
                    DbManager mDm = x.getDb(config);
                    Cursor c = mDm.execQuery("select * from t_city");
                    while (c.moveToNext()) {
                        String provcn = c.getString(c.getColumnIndex("PROVCN"));
                        String city = c.getString(c.getColumnIndex("DISTRICTCN"));
                        if (!mProvinces.contains(provcn))
                            mProvinces.add(provcn);
                        if (!mCities.contains(city))
                            mCities.add(city);
                    }
                    List<String> list;
                    for (String p : mProvinces) {
                        list = new ArrayList<>();
                        Cursor cityCursor = mDm.execQuery("select * from t_city WHERE PROVCN='" + p + "'");
                        while (cityCursor.moveToNext()) {
                            String city = cityCursor.getString(c.getColumnIndex("DISTRICTCN"));
                            if (!list.contains(city))
                                list.add(city);
                        }
                        mProvinceCities.put(p, list);
                        cityCursor.close();
                    }

                    for (String city : mCities) {
                        list = new ArrayList<>();
                        Cursor dCursor = mDm.execQuery("select * from t_city WHERE DISTRICTCN='" + city + "'");
                        while (dCursor.moveToNext()) {
                            list.add(dCursor.getString(c.getColumnIndex("NAMECN")));
                        }
                        mCityDistricts.put(city, list);
                        dCursor.close();
                    }
                    if (null != listener)
                        listener.onResult(mProvinces, mProvinceCities, mCityDistricts);
                } catch (IOException e) {
                    e.printStackTrace();
                    if (null != listener)
                        listener.onError(e.getMessage());
                }
            }
        }.start();

    }

}
