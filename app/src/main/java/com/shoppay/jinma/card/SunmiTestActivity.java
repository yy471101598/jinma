//package com.shoppay.szvipnew.card;
//
//import android.app.Activity;
//import android.os.Bundle;
//import android.os.RemoteException;
//import android.support.annotation.Nullable;
//import android.widget.TextView;
//
//import com.shoppay.wy.R;
//
///**
// * 这是一个需要刷卡的activity
// * Created by Lorenzo on 2018/3/15.
// */
//public class SunmiTestActivity extends Activity
//{
//    private TextView tv;
//
//    @Override
//    protected void onCreate(@Nullable Bundle savedInstanceState)
//    {
//        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_sunmi);
//        tv = (TextView) findViewById(R.id.textView);
//    }
//
//    /**
//     * 在这里启动刷卡检卡程序
//     * 分别传入两个参数，第一个是Context，第二个是显示卡号的控件。
//     */
//    @Override
//    protected void onResume()
//    {
//        super.onResume();
//        //启动检卡程序
//        new ReadCardOpt( tv);
//    }
//
//    /**
//     * 在这里调用检卡的终止程序
//     */
//    @Override
//    protected void onStop()
//    {
//        //终止检卡
//        try
//        {
//            new ReadCardOpt().overReadCard();
//        }
//        catch (RemoteException e)
//        {
//            e.printStackTrace();
//        }
//        super.onStop();
//    }
//
//    /**
//     * 点击系统的返回按钮的时也需要终止检卡程序
//     */
//    @Override
//    public void onBackPressed()
//    {
//        //终止检卡
//        try
//        {
//            new ReadCardOpt().overReadCard();
//        }
//        catch (RemoteException e)
//        {
//            e.printStackTrace();
//        }
//        super.onBackPressed();
//    }
//}
