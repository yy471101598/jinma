package com.shoppay.jinma;

import android.app.Activity;
import android.app.Dialog;
import android.bluetooth.BluetoothAdapter;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.RemoteException;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.PersistentCookieStore;
import com.loopj.android.http.RequestParams;
import com.shoppay.jinma.adapter.JifenDuihuanAdapter;
import com.shoppay.jinma.bean.JifenDuihuan;
import com.shoppay.jinma.bean.SystemQuanxian;
import com.shoppay.jinma.bean.VipInfo;
import com.shoppay.jinma.bean.VipInfoMsg;
import com.shoppay.jinma.card.ReadCardOpt;
import com.shoppay.jinma.db.DBAdapter;
import com.shoppay.jinma.http.InterfaceBack;
import com.shoppay.jinma.tools.ActivityStack;
import com.shoppay.jinma.tools.BluetoothUtil;
import com.shoppay.jinma.tools.CommonUtils;
import com.shoppay.jinma.tools.DateUtils;
import com.shoppay.jinma.tools.DayinUtils;
import com.shoppay.jinma.tools.DialogUtil;
import com.shoppay.jinma.tools.LogUtils;
import com.shoppay.jinma.tools.NoDoubleClickListener;
import com.shoppay.jinma.tools.PreferenceHelper;
import com.shoppay.jinma.tools.UrlTools;
import com.shoppay.jinma.wxcode.MipcaActivityCapture;

import org.json.JSONObject;

import java.lang.reflect.Type;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import cz.msebera.android.httpclient.Header;

import static com.shoppay.jinma.MyApplication.context;

/**
 * Created by songxiaotao on 2017/6/30.
 */

public class JifenDuihuanActivity extends Activity {

    @Bind(R.id.img_left)
    ImageView imgLeft;
    @Bind(R.id.rl_left)
    RelativeLayout rlLeft;
    @Bind(R.id.tv_title)
    TextView tvTitle;
    @Bind(R.id.rl_right)
    RelativeLayout rlRight;
    @Bind(R.id.vip_tv_card)
    TextView vipTvCard;
    @Bind(R.id.vip_et_card)
    EditText vipEtCard;
    @Bind(R.id.vip_tv_name)
    TextView vipTvName;
    @Bind(R.id.vip_tv_jifen)
    TextView vipTvJifen;
    @Bind(R.id.vip_tv_vipyue)
    TextView vipTvVipyue;
    @Bind(R.id.vip_tv_vipdengji)
    TextView vipTvVipdengji;
    @Bind(R.id.vip_tv_jifennum)
    TextView vipTvJifennum;
    @Bind(R.id.vip_tv_dingwei)
    TextView vipTvDingwei;
    @Bind(R.id.vip_et_code)
    EditText etCode;
    @Bind(R.id.item_tv_shopname)
    TextView itemTvShopname;
    @Bind(R.id.item_tv_kucunnum)
    TextView itemTvKucunnum;
    @Bind(R.id.item_tv_money)
    TextView itemTvMoney;
    @Bind(R.id.item_tv_jifen)
    TextView itemTvJifen;
    @Bind(R.id.item_iv_add)
    ImageView itemIvAdd;
    @Bind(R.id.item_tv_num)
    TextView itemTvNum;
    @Bind(R.id.item_iv_del)
    ImageView itemIvDel;
    @Bind(R.id.rl_duihuanmsg)
    RelativeLayout rlDuihuanmsg;
    @Bind(R.id.listview)
    ListView listview;
    @Bind(R.id.balance_tv_n)
    TextView balanceTvN;
    @Bind(R.id.tv_allnum)
    TextView tvAllnum;
    @Bind(R.id.balance_tv_z)
    TextView balanceTvZ;
    @Bind(R.id.tv_alljifen)
    TextView tvAlljifen;
    @Bind(R.id.rl_duihuan)
    RelativeLayout rlDuihuan;
    @Bind(R.id.balance_rl_d)
    RelativeLayout balanceRlD;
    private boolean isSuccess = false;
    private Activity ac;
    private List<JifenDuihuan> list;
    private JifenDuihuanAdapter adapter;
    private DBAdapter dbAdapter;
    private JifenDuihuan jifenDuihuan;
    private ShopChangeReceiver shopChangeReceiver;
    private int num=0;
    private  int point=0;
    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case 1:
                    VipInfo info = (VipInfo) msg.obj;
                    vipTvName.setText(info.getMemName());
                    vipTvVipyue.setText(info.getMemMoney());
                    vipTvJifen.setText(info.getMemPoint());
                    vipTvVipdengji.setText(info.getLevelName());
                    PreferenceHelper.write(ac, "shoppay", "memid", info.getMemID());
                    PreferenceHelper.write(ac, "shoppay", "vipcar", vipEtCard.getText().toString());
                    PreferenceHelper.write(ac, "shoppay", "Discount", info.getDiscount());
                    PreferenceHelper.write(ac, "shoppay", "DiscountPoint", info.getDiscountPoint());
                    PreferenceHelper.write(ac, "shoppay", "jifen", info.getMemPoint());
                    isSuccess = true;
                    break;
                case 2:
                    vipTvName.setText("");
                    vipTvVipdengji.setText("");
                    vipTvJifen.setText("");
                    vipTvVipyue.setText("");
                    isSuccess = false;
                    PreferenceHelper.write(ac, "shoppay", "memid", "123");
                    PreferenceHelper.write(ac, "shoppay", "vipcar", "123");
                    break;
                case 3:
//                    rlDuihuanmsg.setVisibility(View.VISIBLE);
                    jifenDuihuan=(JifenDuihuan) msg.obj;
                    List<JifenDuihuan> jl=new CopyOnWriteArrayList<>();
                    jl.addAll(list);
                    for(int i=0;i<jl.size();i++){
                        if(jl.get(i).GiftCode.equals(jifenDuihuan.GiftCode)){
                            jl.remove(jl.get(i));
                            jl.add(0,jifenDuihuan);
                        }
                    }
                    list.clear();
                    list.addAll(jl);
                    adapter.notifyDataSetChanged();
//                    itemTvJifen.setText(jifenDuihuan.GiftExchangePoint);
//                    itemTvKucunnum.setText(jifenDuihuan.GiftStockNumber);
//                    itemTvShopname.setText(jifenDuihuan.GiftName);
                    break;
                case 4:
                    rlDuihuanmsg.setVisibility(View.GONE);
                    jifenDuihuan=null;
                    itemTvJifen.setText("0");
                    itemTvKucunnum.setText("0");
                    itemTvShopname.setText("");
                    break;
            }
        }
    };
    private Dialog dialog;
    private String editString;
    private String shopcode="";
    private SystemQuanxian sysquanxian;
    private MyApplication app;
    private String password="";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_jifenduihuan);
        ac = this;
        app = (MyApplication) getApplicationContext();
        sysquanxian = app.getSysquanxian();
        ButterKnife.bind(this);
        dialog = DialogUtil.loadingDialog(ac, 1);
        PreferenceHelper.write(context, "shoppay", "viptoast", "未查询到会员");
        ActivityStack.create().addActivity(JifenDuihuanActivity.this);
        dbAdapter=DBAdapter.getInstance(ac);
        dbAdapter.deleteJifenShopCar();
        PreferenceHelper.write(ac, "shoppay", "jinfenIndex", -1);
        PreferenceHelper.write(ac,"shoppay","ischoasejifen",false);
        PreferenceHelper.write(ac, "shoppay", "ischoaseItemjifen", false);
        obtainDuihuanMsg();
        tvTitle.setText("积分兑换");
        // 注册广播
        shopChangeReceiver = new ShopChangeReceiver();
        IntentFilter iiiff = new IntentFilter();
        iiiff.addAction("com.shoppay.wy.jifenduihuan");
        registerReceiver(shopChangeReceiver, iiiff);


        vipEtCard.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                if (delayRun != null) {
                    //每次editText有变化的时候，则移除上次发出的延迟线程
                    handler.removeCallbacks(delayRun);
                }
                editString = editable.toString();

                //延迟800ms，如果不再输入字符，则执行该线程的run方法

                handler.postDelayed(delayRun, 800);
            }
        });
        etCode.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                if (codeRun != null) {
                    //每次editText有变化的时候，则移除上次发出的延迟线程
                    handler.removeCallbacks(codeRun);
                }
                shopcode = editable.toString();

                //延迟800ms，如果不再输入字符，则执行该线程的run方法

                handler.postDelayed(codeRun, 800);
            }
        });


        rlDuihuan.setOnClickListener(new NoDoubleClickListener() {
            @Override
            protected void onNoDoubleClick(View view) {
                if(tvAllnum.getText().toString().equals("0")){
                    Toast.makeText(ac,"请选择一种商品",Toast.LENGTH_SHORT).show();
                }else{
                    if(isSuccess){

                            if(Double.parseDouble(vipTvJifen.getText().toString())<point) {
                                Toast.makeText(ac, "积分不足", Toast.LENGTH_SHORT).show();
                            }else {

                               if( sysquanxian.ispassword == 1) {
                                    DialogUtil.pwdDialog(JifenDuihuanActivity.this, 1, new InterfaceBack() {
                                        @Override
                                        public void onResponse(Object response) {
                                            password = (String) response;
                                            jifenJiesuan();
                                        }

                                        @Override
                                        public void onErrorResponse(Object msg) {

                                        }
                                    });

                            }else{
                                   jifenJiesuan();
                               }
                            }
                    }else{
                        Toast.makeText(ac,"请获取会员信息",Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });

//        PreferenceHelper.write(getApplicationContext(), "PayOk", "time", "false");
//        //动态注册广播接收器
//        msgReceiver = new MsgReceiver();
//        IntentFilter intentFilter = new IntentFilter();
//        intentFilter.addAction("com.example.communication.RECEIVER");
//        registerReceiver(msgReceiver, intentFilter);
    }

    /**
     * 延迟线程，看是否还有下一个字符输入
     */
    private Runnable delayRun = new Runnable() {

        @Override
        public void run() {
            //在这里调用服务器的接口，获取数据
            ontainVipInfo();
        }
    };
    private Runnable codeRun = new Runnable() {

        @Override
        public void run() {
            //在这里调用服务器的接口，获取数据
            obtainDuihuanMsgByCode();
        }
    };

    private void obtainDuihuanMsgByCode() {
        AsyncHttpClient client = new AsyncHttpClient();
        final PersistentCookieStore myCookieStore = new PersistentCookieStore(this);
        client.setCookieStore(myCookieStore);
        RequestParams params = new RequestParams();
        params.put("key", shopcode);
        LogUtils.d("xxparams", params.toString());
        String url = UrlTools.obtainUrl(ac, "?Source=3", "GetGiftList");
        LogUtils.d("xxurl", url);
        client.post(url, params, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                try {
                    LogUtils.d("xxDuihuanS", new String(responseBody, "UTF-8"));
                    JSONObject jso = new JSONObject(new String(responseBody, "UTF-8"));
                    if (jso.getInt("flag") == 1) {
                        Gson gson = new Gson();
                        Type listType = new TypeToken<List<JifenDuihuan>>(){}.getType();
                        List<JifenDuihuan>  list = gson.fromJson(jso.getString("vdata"), listType);
                        Message msg = handler.obtainMessage();
                        msg.obj= list.get(0);
                        msg.what = 3;
                        handler.sendMessage(msg);
                    } else {
                        Message msg = handler.obtainMessage();
                        msg.what = 4;
                        handler.sendMessage(msg);
                    }
                } catch (Exception e) {
                    Message msg = handler.obtainMessage();
                    msg.what = 4;
                    handler.sendMessage(msg);
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                Message msg = handler.obtainMessage();
                msg.what = 4;
                handler.sendMessage(msg);
            }
        });

    }


    private void obtainDuihuanMsg() {
        AsyncHttpClient client = new AsyncHttpClient();
        final PersistentCookieStore myCookieStore = new PersistentCookieStore(this);
        client.setCookieStore(myCookieStore);
        RequestParams params = new RequestParams();
        params.put("key", shopcode);
        LogUtils.d("xxparams", params.toString());
        String url = UrlTools.obtainUrl(ac, "?Source=3", "GetGiftList");
        LogUtils.d("xxurl", url);
        client.post(url, params, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                try {
                    LogUtils.d("xxDuihuanS", new String(responseBody, "UTF-8"));
                    JSONObject jso = new JSONObject(new String(responseBody, "UTF-8"));
                    if (jso.getInt("flag") == 1) {
                        Gson gson = new Gson();
                        Type listType = new TypeToken<List<JifenDuihuan>>(){}.getType();
                        list = gson.fromJson(jso.getString("vdata"), listType);
                        adapter=new JifenDuihuanAdapter(ac,list);
                        listview.setAdapter(adapter);
                    } else {
                        Toast.makeText(ac,jso.getString("msg"),Toast.LENGTH_SHORT).show();

                    }
                } catch (Exception e) {
                    Toast.makeText(ac,"获取可兑换商品失败",Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                Toast.makeText(ac,"获取可兑换商品失败",Toast.LENGTH_SHORT).show();
            }
        });

    }

    private void ontainVipInfo() {
        AsyncHttpClient client = new AsyncHttpClient();
        final PersistentCookieStore myCookieStore = new PersistentCookieStore(this);
        client.setCookieStore(myCookieStore);
        RequestParams params = new RequestParams();
        params.put("MemCard", editString);
        LogUtils.d("xxparams", params.toString());
        String url = UrlTools.obtainUrl(ac, "?Source=3", "GetMem");
        LogUtils.d("xxurl", url);
        client.post(url, params, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                try {
                    LogUtils.d("xxVipinfoS", new String(responseBody, "UTF-8"));
                    JSONObject jso = new JSONObject(new String(responseBody, "UTF-8"));
                    if (jso.getInt("flag") == 1) {
                        Gson gson = new Gson();
                        VipInfoMsg infomsg = gson.fromJson(new String(responseBody, "UTF-8"), VipInfoMsg.class);
                        Message msg = handler.obtainMessage();
                        msg.what = 1;
                        msg.obj = infomsg.getVdata().get(0);
                        handler.sendMessage(msg);
                    } else {
                        Message msg = handler.obtainMessage();
                        msg.what = 2;
                        handler.sendMessage(msg);
                    }
                } catch (Exception e) {
                    Message msg = handler.obtainMessage();
                    msg.what = 2;
                    handler.sendMessage(msg);
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                Message msg = handler.obtainMessage();
                msg.what = 2;
                handler.sendMessage(msg);
            }
        });
    }


    @Override
    protected void onResume() {
        super.onResume();
        new ReadCardOpt(vipEtCard);
    }

    @Override
    protected void onStop() {
        try
        {
            new ReadCardOpt().overReadCard();
        }
        catch (RemoteException e)
        {
            e.printStackTrace();
        }
        super.onStop();
        if (delayRun != null) {
            //每次editText有变化的时候，则移除上次发出的延迟线程
            handler.removeCallbacks(delayRun);
        }    if (codeRun != null) {
            //每次editText有变化的时候，则移除上次发出的延迟线程
            handler.removeCallbacks(codeRun);
        }
    }


    public static String getStringDate() {
        Date currentTime = new Date();
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String dateString = formatter.format(currentTime);
        return dateString;
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case 111:
                if (resultCode == RESULT_OK) {
                    vipEtCard.setText(data.getStringExtra("codedata"));
                }
                break;
            case 222:
                if (resultCode == RESULT_OK) {
                    etCode.setText(data.getStringExtra("codedata"));
                }
                break;
        }
    }

    @OnClick({R.id.rl_left, R.id.rl_right, R.id.item_iv_add, R.id.item_iv_del,R.id.vip_tv_dingwei})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.rl_left:
                finish();
                break;
            case R.id.rl_right:
                Intent mipca = new Intent(ac, MipcaActivityCapture.class);
                startActivityForResult(mipca, 111);
                break;
            case R.id.item_iv_add:
                if( PreferenceHelper.readBoolean(ac,"shoppay","ischoaseItemjifen",false)){
                    Toast.makeText(ac,"只能选择一种商品",Toast.LENGTH_SHORT).show();
                }else{
                    if(num==0){
                        itemIvDel.setVisibility(View.VISIBLE);
                        itemTvNum.setVisibility(View.VISIBLE);
                    }
                    PreferenceHelper.write(ac,"shoppay","ischoasejifen",true);
                    num=num+1;
                    if(num>Integer.parseInt(jifenDuihuan.GiftStockNumber)){
                        num=num-1;
                        Toast.makeText(ac, "该商品的最大库存量为" + jifenDuihuan.GiftStockNumber, Toast.LENGTH_SHORT).show();
                    }
                        itemTvNum.setText(num + "");
                        tvAllnum.setText(num + "");
                        tvAlljifen.setText(CommonUtils.multiply(num + "", jifenDuihuan.GiftExchangePoint));

                }
                break;
            case R.id.item_iv_del:
                    num=num-1;
                    if(num==0){
                        PreferenceHelper.write(ac,"shoppay","ischoasejifen",false);
                        itemIvDel.setVisibility(View.GONE);
                        itemTvNum.setVisibility(View.GONE);
                    }
                    itemTvNum.setText(num+"");
                    tvAllnum.setText(num+"");
                    tvAlljifen.setText(CommonUtils.multiply(num+"",jifenDuihuan.GiftExchangePoint));
                break;
            case R.id.vip_tv_dingwei:
                Intent duihuan = new Intent(ac, MipcaActivityCapture.class);
                startActivityForResult(duihuan, 222);
                break;
        }
    }

    private void jifenJiesuan() {
        dialog.show();
        List<JifenDuihuan> listss = dbAdapter.getListJifenShopCar(PreferenceHelper.readString(context, "shoppay", "account", "123"));
        List<JifenDuihuan> jifenlist=new ArrayList<>();
        for(JifenDuihuan jf:listss){
            if(jf.count.equals("0")){

            }else{
                jifenlist.add(jf);
            }
        }
        AsyncHttpClient client = new AsyncHttpClient();
        final PersistentCookieStore myCookieStore = new PersistentCookieStore(this);
        client.setCookieStore(myCookieStore);
        RequestParams params = new RequestParams();
        params.put("MemID",  PreferenceHelper.readString(ac,"shoppay","memid",""));
        params.put("OrderAccount", DateUtils.getCurrentTime("yyyyMMddHHmmss"));
        params.put("GiftPoint",point);
        params.put("GiftCount",jifenlist.size());
        params.put("UserPwd",password);
        for (int i = 0; i < jifenlist.size(); i++) {
            params.put("GiftList[" + i + "][GiftID]", jifenlist.get(i).GiftID);
            params.put("GiftList[" + i + "][GiftExchangePoint]", jifenlist.get(i).GiftExchangePoint);
            params.put("GiftList[" + i + "][ExcNumber]",  jifenlist.get(i).count);
        }
        LogUtils.d("xxparams", params.toString());
        String url = UrlTools.obtainUrl(ac, "?Source=3", "PointGiftExchange");
        LogUtils.d("xxurl", url);
        client.post(url, params, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                try {
                    dialog.dismiss();
                    LogUtils.d("xxJiesuanS", new String(responseBody, "UTF-8"));
                    JSONObject jso = new JSONObject(new String(responseBody, "UTF-8"));
                    if(jso.getInt("flag")==1){
                        Toast.makeText(ac, jso.getString("msg"), Toast.LENGTH_LONG).show();
                        JSONObject jsonObject=(JSONObject) jso.getJSONArray("print").get(0);
                        dbAdapter.deleteJifenShopCar();
                        if(jsonObject.getInt("printNumber")==0){
                            finish();
                        }else{
                            BluetoothAdapter bluetoothAdapter=BluetoothAdapter.getDefaultAdapter();
                            if(bluetoothAdapter.isEnabled()) {
                                BluetoothUtil.connectBlueTooth(context);
                                BluetoothUtil.sendData(DayinUtils.dayin(jsonObject.getString("printContent")),jsonObject.getInt("printNumber"));
                                finish();
                            }else {
                               finish();
                            }
                        }
                    } else {
                        Toast.makeText(ac, jso.getString("msg"), Toast.LENGTH_SHORT).show();
                    }
                } catch (Exception e) {
                    dialog.dismiss();
                    Toast.makeText(ac,"兑换失败",Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                dialog.dismiss();
                Toast.makeText(ac,"兑换失败",Toast.LENGTH_SHORT).show();
            }
        });

    }


    private class  ShopChangeReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            Log.d("xx", "ShopChangeReceiver");
            List<JifenDuihuan> listss = dbAdapter.getListJifenShopCar(PreferenceHelper.readString(context, "shoppay", "account", "123"));
            num = 0;
            point = 0;
            for (JifenDuihuan shopCar : listss) {
                if (shopCar.count.equals("0")) {

                } else {
                    num = num + Integer.parseInt(shopCar.count);
                    point = point +Integer.parseInt(CommonUtils.multiply(shopCar.GiftExchangePoint,shopCar.count));
                }
            }
            tvAlljifen.setText(point + "");
            tvAllnum.setText(num + "");

        }
    }






    /**
     * 广播接收器
     *
     * @author len
     */
    public class MsgReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            //拿到进度，更新UI
//            String state = intent.getStringExtra("success");
//            Log.d("MsgReceiver", "MsgReceiver" + state);
//            if (state == null || state.equals("")) {
//
//            } else {
//                if (state.equals("success")) {
//                    weixinDialog.dismiss();
//                     vipRecharge();
//                } else {
//                    String msg = intent.getStringExtra("msg");
//                    Toast.makeText(ac,msg,Toast.LENGTH_SHORT).show();
//
//                }
//            }
        }

    }

    @Override
    protected void onDestroy() {
        // TODO 自动生成的方法存根
        super.onDestroy();
//        if (intent != null) {
//
//            stopService(intent);
//        }
//
//        //关闭闹钟机制启动service
//        AlarmManager manager = (AlarmManager) getSystemService(ALARM_SERVICE);
//        int anHour =2 * 1000; // 这是一小时的毫秒数 60 * 60 * 1000
//        long triggerAtTime = SystemClock.elapsedRealtime() + anHour;
//        Intent i = new Intent(this, AlarmReceiver.class);
//        PendingIntent pi = PendingIntent.getBroadcast(this, 0, i, 0);
//        manager.cancel(pi);
//        //注销广播
        unregisterReceiver(shopChangeReceiver);
    }
}
