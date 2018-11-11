package com.shoppay.jinma;

import android.app.Application;
import android.content.Context;

import com.shoppay.jinma.bean.SystemQuanxian;
import com.shoppay.jinma.crash.CrashHandler;
import com.sunmi.pay.hardware.aidl.readcard.ReadCardOpt;
import com.sunmi.payservice.hardware.aidl.HardwareOpt;

import sunmi.paylib.SunmiPayKernel;
import sunmi.payservicelib.SunmiPayService;

/**
 * Created by songxiaotao on 2017/7/8.
 */

public class MyApplication extends Application {
    /*****************
     * SUNMI设备
     *****************/
    private static SunmiPayKernel sSunmiPayKernel;  //SUNMI支付SDK操作核心对象
    /**
     * 获取读卡模块
     */
    public static ReadCardOpt sReadCardOpt;
    public static Context context;
    //SUNMI P1N设备
    public static HardwareOpt sHardwareOpt;
    private SunmiPayService mSunmiPayService;
   private SystemQuanxian sysquanxian;
    private int payway;
    private int isPointByOilExp;
    private int oilExpPointNum;

    @Override
    public void onCreate() {
        super.onCreate();
//        conn();
        conn_sunmiP1();
        context = getApplicationContext();
        CrashHandler crashHandler = CrashHandler.getInstance();
        crashHandler.init(context);
//        BlockCanary.install(this, new AppBlockCanaryContext()).start();
    }

    public SystemQuanxian getSysquanxian() {
        return sysquanxian;
    }

    public void setSystemQuanxian(SystemQuanxian systemQuanxian) {
        this.sysquanxian = systemQuanxian;
    }

    public int getPayway(){
        return  payway;
    }
    public void setPayway(int payway){
        this.payway=payway;
    }

    public int getIsPointByOilExp(){
        return  isPointByOilExp;
    }
    public void setIsPointByOilExp(int isPointByOilExp){
        this.isPointByOilExp=isPointByOilExp;
    }

    public int getOilExpPointNum(){
        return  oilExpPointNum;
    }
    public void setOilExpPointNum(int oilExpPointNum){
        this.oilExpPointNum=oilExpPointNum;
    }
    /**
     * SUNMI-P1N连接读卡支付SDK
     */
    private void conn_sunmiP1() {
        mSunmiPayService = SunmiPayService.getInstance();
        mSunmiPayService.connectPayService(getApplicationContext(), connCallback);
    }

    /**
     * SUNMI-P1N连接状态回调
     */
    private SunmiPayService.ConnCallback connCallback = new SunmiPayService.ConnCallback() {
        @Override
        public void onServiceConnected() {
            sHardwareOpt = mSunmiPayService.mHardwareOpt;
        }

        @Override
        public void onServiceDisconnected() {

        }
    };

    /**
     * SUNMI连接支付SDK
     */
    private void conn() {
        sSunmiPayKernel = SunmiPayKernel.getInstance();
        sSunmiPayKernel.connectPayService(getApplicationContext(), mConnCallback);
    }

    /**
     * SUNMI连接状态回调
     */
    private SunmiPayKernel.ConnCallback mConnCallback = new SunmiPayKernel.ConnCallback() {
        @Override
        public void onServiceConnected() {
            try {
                sReadCardOpt = sSunmiPayKernel.mReadCardOpt;
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        @Override
        public void onServiceDisconnected() {
        }
    };
}
