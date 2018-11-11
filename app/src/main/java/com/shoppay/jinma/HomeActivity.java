package com.shoppay.jinma;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.KeyEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.Toast;

import com.shoppay.jinma.adapter.HomeAdapter;
import com.shoppay.jinma.bean.Home;
import com.shoppay.jinma.bean.QuanxianManage;
import com.shoppay.jinma.tools.ActivityStack;
import com.shoppay.jinma.tools.DialogUtil;
import com.shoppay.jinma.tools.SysUtil;
import com.shoppay.jinma.view.MyGridViews;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by songxiaotao on 2017/6/30.
 */

public class HomeActivity extends Activity{
    private MyGridViews gridViews;
    private List<Home> list;
    private HomeAdapter adapter;
    private Activity ac;
    private Dialog dialog;
    private long firstTime=0;
    private ImageView img;
    private QuanxianManage quanxian;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        ac=this;
        dialog= DialogUtil.loadingDialog(ac,1);
        gridViews= (MyGridViews) findViewById(R.id.gridview);
        img= (ImageView) findViewById(R.id.imgview);
        quanxian=(QuanxianManage) getIntent().getSerializableExtra("quanxian");
        setimg();
        obtainHome();
        adapter=new HomeAdapter(ac,list);
        gridViews.setAdapter(adapter);
        gridViews.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Home home=(Home) adapterView.getItemAtPosition(i);
                switch (home.name){
                    case "会员查询":
                        Intent chaxun=new Intent(ac,VipChaxunActivity.class);
                        startActivity(chaxun);
                        break;
                    case "会员办卡":
                        if(quanxian.vipcard==1){
                        Intent intent=new Intent(ac,VipCardActivity.class);
                        startActivity(intent);
                        }else{
                            Toast.makeText(ac,"该功能未授权",Toast.LENGTH_SHORT).show();
                        }
                        break;
                    case "会员签到":
                        Intent qiandao=new Intent(ac,VipQiandaoActivity.class);
                        startActivity(qiandao);
                        break;
                    case "商品消费":
                        if(quanxian.shopxiaofei==1) {
                            Intent intent2 = new Intent(ac, BalanceActivity.class);
                            startActivity(intent2);
                        }else{
                            Toast.makeText(ac,"该功能未授权",Toast.LENGTH_SHORT).show();
                        }
                        break;
                    case "快速消费":
                        if(quanxian.fastxiaofei==1) {
                            Intent intent1 = new Intent(ac, FastConsumptionActivity.class);
                            startActivity(intent1);
                        }else{
                            Toast.makeText(ac,"该功能未授权",Toast.LENGTH_SHORT).show();
                        }
                        break;
                    case "计次消费":
                        if(quanxian.numxiaofei==1) {
                            Intent intent3 = new Intent(ac, NumConsumptionActivity.class);
                            startActivity(intent3);
                        }else{
                            Toast.makeText(ac,"该功能未授权",Toast.LENGTH_SHORT).show();
                        }
                        break;
                    case "会员充值":
                        if(quanxian.viprecharge==1) {
                            Intent intentre = new Intent(ac, VipRechargeActivity.class);
                            startActivity(intentre);
                        }else{
                        Toast.makeText(ac,"该功能未授权",Toast.LENGTH_SHORT).show();
                        }
                        break;
                    case "会员充次":
                        if(quanxian.vipnum==1) {
                            Intent intentrn = new Intent(ac, NumRechargeActivity.class);
                            startActivity(intentrn);
                        }else{
                            Toast.makeText(ac,"该功能未授权",Toast.LENGTH_SHORT).show();
                        }
                        break;
                    case "加减积分":
                        if(quanxian.jifenbiandong==1){
                            Intent jifen=new Intent(ac,JifenChangeActivity.class);
                            startActivity(jifen);
                        }else{
                            Toast.makeText(ac,"该功能未授权",Toast.LENGTH_SHORT).show();
                        }
                        break;
                    case "消费记录":
                        Intent xiaofeirecord=new Intent(ac,XiaofeiRecordActivity.class);
                        startActivity(xiaofeirecord);
                        break;
                    case "礼品兑换":
                        if(quanxian.lipingduihuan==1) {
                            Intent duihuan = new Intent(ac, JifenDuihuanActivity.class);
                            startActivity(duihuan);
                        }else{
                            Toast.makeText(ac,"该功能未授权",Toast.LENGTH_SHORT).show();
                        }
                        break;
                    case "兑换记录":
                        Intent duihuanjilu=new Intent(ac,DuihuanjiluActivity.class);
                        startActivity(duihuanjilu);
                        break;
                    case "营销中心":
                        Intent yingxiao=new Intent(ac,YingxiaoCenterActivity.class);
                        startActivity(yingxiao);
                        break;
                    case "个人中心":
                        Intent personal=new Intent(ac,PersonalActivity.class);
                        startActivity(personal);
                        break;

                    case "会员列表":
                        Intent intentlist=new Intent(ac,VipListActivity.class);
                        startActivity(intentlist);
                        break;
                    case "管理中心":
                        Intent intent4=new Intent(ac,NewBossCenterActivity.class);
                        startActivity(intent4);
                        break;
                    case "退出系统":
                       outLogin();
                        break;
                }
            }
        });

    }
    private void setimg(){
        DisplayMetrics disMetrics = new DisplayMetrics();
        this.getWindowManager().getDefaultDisplay().getMetrics(disMetrics);
        int width = disMetrics.widthPixels;
        int height = disMetrics.heightPixels;
        Bitmap bitmap = BitmapFactory.decodeResource(this.getResources(), R.drawable.banner);//link the drable image
        SysUtil.setImageBackground(bitmap,img,width,dip2px(ac,230));
    }
    public  int dip2px(Context context, float dipValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (dipValue * scale + 0.5f);
    }
    private void obtainHome() {
 list=new ArrayList<Home>();
        Home h1=new Home();
        h1.name="会员查询";
        h1.iconId=R.mipmap.vipchaxun;
        list.add(h1);
        Home h2=new Home();
        h2.name="会员办卡";
        h2.iconId=R.mipmap.vipcard;
        list.add(h2);
        Home h22=new Home();
        h22.name="会员签到";
        h22.iconId=R.mipmap.vipqiandao;
        list.add(h22);
        Home h3=new Home();
        h3.name="商品消费";
        h3.iconId=R.mipmap.shopxiaofei;
        list.add(h3);
        Home h7=new Home();
        h7.name="快速消费";
        h7.iconId=R.mipmap.fastxiaofei;
        list.add(h7);
        Home h9=new Home();
        h9.name="计次消费";
        h9.iconId=R.mipmap.vipnumxiaofei;
        list.add(h9);
        Home h4=new Home();
        h4.name="会员充值";
        h4.iconId=R.mipmap.viprecharge;
        list.add(h4);
        Home h8=new Home();
        h8.name="会员充次";
        h8.iconId=R.mipmap.vipnum;
        list.add(h8);
        Home h81=new Home();
        h81.name="加减积分";
        h81.iconId=R.mipmap.jiajianjifen;
        list.add(h81);
        Home h62=new Home();
        h62.name="消费记录";
        h62.iconId=R.mipmap.xiaofeijilv;
        list.add(h62);
        Home h5=new Home();
        h5.name="礼品兑换";
        h5.iconId=R.mipmap.lipingduihuan;
        list.add(h5);
        Home h63=new Home();
        h63.name="兑换记录";
        h63.iconId=R.mipmap.duihuanjilv;
        list.add(h63);
        Home h61=new Home();
        h61.name="营销中心";
        h61.iconId=R.mipmap.jinriyinxiao;
        list.add(h61);
        Home h6=new Home();
        h6.name="个人中心";
        h6.iconId=R.mipmap.gerenzhongxin;
        list.add(h6);
        Home h611=new Home();
        h611.name="退出系统";
        h611.iconId=R.mipmap.out;
        list.add(h611);
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        // TODO Auto-generated method stub
        if (keyCode == KeyEvent.KEYCODE_BACK && event.getRepeatCount() == 0) {
            long secndTime = System.currentTimeMillis();
            if (secndTime - firstTime > 3000) {
                firstTime = secndTime;
                Toast.makeText(ac, "再按一次退出", Toast.LENGTH_LONG)
                        .show();
            } else {
                ActivityStack.create().AppExit(ac);
            }
            return true;
        }
        return false;
    }

    private void outLogin() {
        Intent intent=new Intent(ac,LoginActivity.class);
        startActivity(intent);
        finish();
//        dialog.show();
//        Retrofit retrofit = new Retrofit.Builder().baseUrl( PreferenceHelper.readString(ac,"shoppay","yuming","123")).addConverterFactory(GsonConverterFactory.create()).build();
//        RetrofitAPI api=retrofit.create(RetrofitAPI.class);
//        Map<String,Object> map=new HashMap<>();
//        Call<Login> call=api.outLogin(map);
//        call.enqueue(new Callback<Login>() {
//            @Override
//            public void onResponse(Call<Login> call, Response<Login> response) {
//                dialog.dismiss();
//                try {
//                    LogUtils.d("xxoutLoginS",response.body().toString());
//                    if(response.body().isSuccess()){
//                        Intent intent=new Intent(ac,LoginActivity.class);
//                        startActivity(intent);
//                        finish();
//                    }else{
//                        Toast.makeText(ac,response.body().getMsg(),Toast.LENGTH_SHORT).show();
//                    }
//                } catch (Exception e) {
//                    e.printStackTrace();
//                    LogUtils.d("xxE","exception");
//                }
//            }
//
//            @Override
//            public void onFailure(Call<Login> call, Throwable t) {
//                dialog.dismiss();
//                LogUtils.d("xxoutLoginE",t.getMessage());
//            }
//        });
    }

}
