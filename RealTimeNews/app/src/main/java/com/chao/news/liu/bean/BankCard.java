package com.chao.news.liu.bean;

import org.json.JSONObject;

import java.io.Serializable;

/**
 * Created by hp on 2017/1/22.
 */

public class BankCard implements Serializable {

    public String mCode;               //结果值（1000：核验一致，1001：核验不一致，1002：无法认证，不支持的卡号
    public String mMessage;            //核验一致
    public String mCardtype;           //卡类别
    public String mCardlength;         //卡号长度
    public String mCardprefixnum;      //卡号前缀
    public String mCardname;           //银行内部类型
    public String mBankname;           //所属银行
    public String mBanknum;            //所属银行编号

    public BankCard parser(String code, String message, JSONObject json) {
        this.mCode = code;
        this.mMessage = message;
        if (json != null) {
            this.mCardtype = json.optString("cardtype");
            this.mCardlength = json.optString("cardlength");
            this.mCardprefixnum = json.optString("cardprefixnum");
            this.mCardname = json.optString("cardname");
            this.mBankname = json.optString("bankname");
            this.mBanknum = json.optString("banknum");
        }
        return this;

    }

    @Override
    public String toString() {
        return "BankCard{" +
                "mCode='" + mCode + '\'' +
                ", mMessage='" + mMessage + '\'' +
                ", mCardtype='" + mCardtype + '\'' +
                ", mCardlength='" + mCardlength + '\'' +
                ", mCardprefixnum='" + mCardprefixnum + '\'' +
                ", mCardname='" + mCardname + '\'' +
                ", mBankname='" + mBankname + '\'' +
                ", mBanknum='" + mBanknum + '\'' +
                '}';
    }
}
