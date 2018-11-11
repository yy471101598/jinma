package com.shoppay.jinma.tools;

import android.content.Context;

/**
 * Created by songxiaotao on 2018/1/17.
 */

public class UrlTools
{
    public static String obtainUrl(Context ac,String url,String method){
        return   PreferenceHelper.readString(ac, "shoppay", "yuming", "123") + url+"&UserID="+ PreferenceHelper.readString(ac, "shoppay", "UserID", "123")+"&UserShopID="+PreferenceHelper.readString(ac, "shoppay", "ShopID", "123")+"&Method="+method;

    }
}
