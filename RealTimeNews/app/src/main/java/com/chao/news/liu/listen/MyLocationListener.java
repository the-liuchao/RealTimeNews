package com.chao.news.liu.listen;

import android.content.Context;

import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.chao.news.liu.bean.CurrLocation;
import com.liuming.mylibrary.utils.SPHelper;

import java.io.IOException;

/**
 * Created by hp on 2017/1/15.
 */

public class MyLocationListener implements BDLocationListener {
    private Context mContext;

    public MyLocationListener(Context mContext) {
        this.mContext = mContext;
    }

    @Override
    public void onReceiveLocation(BDLocation bdLocation) {
        try {
            CurrLocation currLoction = new CurrLocation();
            currLoction.setmLatitude(String.valueOf(bdLocation.getLatitude()));
            currLoction.setmLongitude(String.valueOf(bdLocation.getLongitude()));
            currLoction.setmProvince(bdLocation.getProvince());
            currLoction.setmDistrict(bdLocation.getDistrict());
            currLoction.setmCity(bdLocation.getCity());
            SPHelper.saveObject(mContext, "location", currLoction);
        } catch (IOException e) {
            e.printStackTrace();
        }

    }
}
