package com.shoppay.jinma;


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
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentTransaction;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.EditText;
import android.widget.LinearLayout;
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
import com.shoppay.jinma.adapter.LeftAdapter;
import com.shoppay.jinma.bean.Shop;
import com.shoppay.jinma.bean.ShopCar;
import com.shoppay.jinma.bean.ShopClass;
import com.shoppay.jinma.bean.VipInfo;
import com.shoppay.jinma.bean.VipInfoMsg;
import com.shoppay.jinma.bean.VipPayMsg;
import com.shoppay.jinma.bean.Zhekou;
import com.shoppay.jinma.card.ReadCardOpt;
import com.shoppay.jinma.db.DBAdapter;
import com.shoppay.jinma.http.InterfaceBack;
import com.shoppay.jinma.tools.BluetoothUtil;
import com.shoppay.jinma.tools.CommonUtils;
import com.shoppay.jinma.tools.DateUtils;
import com.shoppay.jinma.tools.DayinUtils;
import com.shoppay.jinma.tools.DialogUtil;
import com.shoppay.jinma.tools.ESCUtil;
import com.shoppay.jinma.tools.LogUtils;
import com.shoppay.jinma.tools.MergeLinearArraysUtil;
import com.shoppay.jinma.tools.NoDoubleClickListener;
import com.shoppay.jinma.tools.PreferenceHelper;
import com.shoppay.jinma.tools.ShopXiaofeiDialog;
import com.shoppay.jinma.tools.StringUtil;
import com.shoppay.jinma.tools.UrlTools;
import com.shoppay.jinma.wxcode.MipcaActivityCapture;

import org.json.JSONObject;

import java.io.Serializable;
import java.io.UnsupportedEncodingException;
import java.lang.reflect.Type;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

import cz.msebera.android.httpclient.Header;

import static com.shoppay.jinma.MyApplication.context;

/**
 * @author qdwang
 */
public class BalanceActivity extends FragmentActivity implements
        OnItemClickListener, View.OnClickListener {

    private ListView listView;
    private List<ShopClass> list;
    private LeftAdapter adapter;
    private BalanceFragment myFragment;
    public static int mPosition;
    private RelativeLayout rl_yes, rl_no, rl_card, rl_jiesuan, rl_left, rl_vipname, rl_vipjifen, rl_vipyue, rl_vipdengji;
    private TextView tv_yes, tv_no, tv_num, tv_money, tv_jifen, tv_title, tv_vipname, tv_vipjifen, tv_vipyue, tv_vipdengji;
    private LinearLayout li_jifen;
    private EditText et_card;
    private String type = "否";
    private Dialog dialog;
    private Dialog paydialog;
    private Context ac;
    private ShopChangeReceiver shopchangeReceiver;
    private DBAdapter dbAdapter;
    private String editString;
    private String shopString;
    private double num = 0, money = 0, jifen = 0, xfmoney = 0;
    private Dialog jiesuanDialog;
    private VipPayMsg vipPayMsg;
    private EditText et_shopcode;
    private RelativeLayout rl_right;
    private Boolean isSuccess = false;
    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case 1:
                    VipInfo info = (VipInfo) msg.obj;
                    tv_vipname.setText(info.getMemName());
                    tv_vipjifen.setText(info.getMemPoint());
                    tv_vipyue.setText(info.getMemMoney());
                    tv_vipdengji.setText(info.getLevelName());
                    PreferenceHelper.write(ac, "shoppay", "vipcar", et_card.getText().toString());
                    PreferenceHelper.write(ac, "shoppay", "vipname", tv_vipname.getText().toString());
                    PreferenceHelper.write(ac, "shoppay", "memid", info.getMemID());
                    PreferenceHelper.write(ac, "shoppay", "MemMoney", info.getMemMoney() + "");
                    PreferenceHelper.write(ac, "shoppay", "jifenall", info.getMemPoint());
                    isSuccess = true;
                    break;
                case 2:
                    tv_vipname.setText("");
                    tv_vipjifen.setText("");
                    tv_vipyue.setText("");
                    tv_vipdengji.setText("");
                    isSuccess = false;
                    break;
                case 3:
                    Zhekou zhekou = (Zhekou) msg.obj;
                    handlerShopMsg(zhekou);
                    break;
                case 4:
                    Toast.makeText(context, "获取商品信息失败", Toast.LENGTH_SHORT).show();
                    break;
            }
        }
    };
    private Intent intent;
    private MsgReceiver msgReceiver;
    private Dialog weixinDialog;
    private TextView tv_dingwei;
    private String orderAccount;
    private String paytype;
    private MyApplication app;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_balance);
        ac = context;
        app= (MyApplication) getApplication();
        dialog = DialogUtil.loadingDialog(BalanceActivity.this, 1);
        paydialog = DialogUtil.payloadingDialog(BalanceActivity.this, 1);
        dbAdapter = DBAdapter.getInstance(ac);
        PreferenceHelper.write(ac, "shoppay", "memid", "");
        PreferenceHelper.write(ac, "shoppay", "isSan", true);
        PreferenceHelper.write(ac, "shoppay", "vipcar", "无");
        PreferenceHelper.write(ac, "shoppay", "vipname", "散客");
        PreferenceHelper.write(context, "shoppay", "viptoast", "未查询到会员");
        mPosition = 0;
        dbAdapter.deleteShopCar();
        initView();
        // 注册广播
        shopchangeReceiver = new ShopChangeReceiver();
        IntentFilter iiiff = new IntentFilter();
        iiiff.addAction("com.shoppay.wy.numberchange");
        registerReceiver(shopchangeReceiver, iiiff);


//        PreferenceHelper.write(getApplicationContext(), "PayOk", "time", "false");
//        //动态注册广播接收器
//        msgReceiver = new MsgReceiver();
//        IntentFilter intentFilter = new IntentFilter();
//        intentFilter.addAction("com.example.communication.RECEIVER");
//        registerReceiver(msgReceiver, intentFilter);
        obtainShopClass();

        et_card.addTextChangedListener(new TextWatcher() {
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

        et_shopcode.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                if (shopRun != null) {
                    //每次editText有变化的时候，则移除上次发出的延迟线程
                    handler.removeCallbacks(shopRun);
                }
                shopString = editable.toString();

                //延迟800ms，如果不再输入字符，则执行该线程的run方法

                handler.postDelayed(shopRun, 800);
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        new ReadCardOpt(et_card);

    }

    @Override
    protected void onStop() {
        try {
            new ReadCardOpt().overReadCard();
        } catch (RemoteException e) {
            e.printStackTrace();
        }
        super.onStop();
        if (delayRun != null) {
            //每次editText有变化的时候，则移除上次发出的延迟线程
            handler.removeCallbacks(delayRun);
        }
        if (shopRun != null) {
            handler.removeCallbacks(shopRun);
        }
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
    /**
     * 延迟线程，看是否还有下一个字符输入
     */
    private Runnable shopRun = new Runnable() {

        @Override
        public void run() {
            //在这里调用服务器的接口，获取数据
            obtainShopMsg();
        }
    };

    private void obtainShopMsg() {
        dialog.show();
        AsyncHttpClient client = new AsyncHttpClient();
        final PersistentCookieStore myCookieStore = new PersistentCookieStore(context);
        client.setCookieStore(myCookieStore);
        RequestParams params = new RequestParams();
        if (PreferenceHelper.readBoolean(context, "shoppay", "isSan", true)) {
            params.put("memid", "0");
        } else {
            params.put("memid", PreferenceHelper.readString(context, "shoppay", "memid", "0"));
        }
        params.put("GoodsCode", shopString);
        LogUtils.d("xxparams", params.toString());
        String url = UrlTools.obtainUrl(context, "?Source=3", "GetGoodsInfos");
        LogUtils.d("xxurl", url);
        client.post(url, params, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                try {
                    dialog.dismiss();
                    LogUtils.d("xxshopzkS", new String(responseBody, "UTF-8"));
                    JSONObject jso = new JSONObject(new String(responseBody, "UTF-8"));
                    if (jso.getInt("flag") == 1) {
                        Gson gson = new Gson();
                        Type listType = new TypeToken<List<Zhekou>>() {
                        }.getType();
                        List<Zhekou> zhekoulist = gson.fromJson(jso.getString("vdata"), listType);
                        Message msg = handler.obtainMessage();
                        msg.what = 3;
                        msg.obj = zhekoulist.get(0);
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
                dialog.dismiss();
                Message msg = handler.obtainMessage();
                msg.what = 4;
                handler.sendMessage(msg);
            }
        });
    }

    private void handlerShopMsg(Zhekou zhekou) {
        for (int i = 0; i < list.size(); i++) {
            if (zhekou.GoodsClassID.equals(list.get(i).ClassID)) {
                listView.setSelection(i);
                mPosition = i;
                //即使刷新adapter
                adapter.notifyDataSetChanged();
                myFragment = new BalanceFragment();
                FragmentTransaction fragmentTransaction = getSupportFragmentManager()
                        .beginTransaction();
                fragmentTransaction.replace(R.id.fragment_container, myFragment);
                Bundle bundle = new Bundle();
                List<Shop> shoplist = new CopyOnWriteArrayList<>();
                shoplist.addAll(list.get(mPosition).GoodsList);
                for (Shop sp : shoplist) {
                    if (sp.GoodsCode.equals(zhekou.GoodsCode)) {
                        shoplist.remove(sp);
                        shoplist.add(0, sp);
                    }
                }
                List<Shop> li = new ArrayList<>();
                li.addAll(shoplist);
                bundle.putSerializable(BalanceFragment.TAG, (Serializable) li);
                bundle.putString("isSan", type);
                myFragment.setArguments(bundle);
                fragmentTransaction.commit();
            }
        }
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

    /**
     * 初始化view
     */
    private void initView() {
        // TODO Auto-generated method stub
        rl_yes = (RelativeLayout) findViewById(R.id.rl_yes);
        rl_no = (RelativeLayout) findViewById(R.id.rl_no);
        rl_left = (RelativeLayout) findViewById(R.id.rl_left);
        rl_right = (RelativeLayout) findViewById(R.id.rl_right);
        rl_card = (RelativeLayout) findViewById(R.id.balance_rl_card);
        rl_vipjifen = (RelativeLayout) findViewById(R.id.balance_rl_vipjifen);
        rl_vipname = (RelativeLayout) findViewById(R.id.balance_rl_vipname);
        rl_jiesuan = (RelativeLayout) findViewById(R.id.balance_rl_jiesan);
        rl_vipyue = (RelativeLayout) findViewById(R.id.balance_rl_vipyue);
        rl_vipdengji = (RelativeLayout) findViewById(R.id.balance_rl_vipdengji);

        tv_jifen = (TextView) findViewById(R.id.balance_tv_jifen);
        tv_vipjifen = (TextView) findViewById(R.id.balance_tv_vipjifen);
        tv_vipyue = (TextView) findViewById(R.id.balance_tv_vipyue);
        tv_vipdengji = (TextView) findViewById(R.id.balance_tv_vipdengji);
        tv_vipname = (TextView) findViewById(R.id.balance_tv_vipname);
        tv_num = (TextView) findViewById(R.id.balance_tv_num);
        tv_money = (TextView) findViewById(R.id.balance_tv_money);
        tv_yes = (TextView) findViewById(R.id.tv_yes);
        tv_no = (TextView) findViewById(R.id.tv_no);
        tv_title = (TextView) findViewById(R.id.tv_title);
        tv_dingwei = (TextView) findViewById(R.id.vip_tv_dingwei);
        tv_title.setText("商品消费");
        li_jifen = (LinearLayout) findViewById(R.id.balance_li_jifen);
        et_card = (EditText) findViewById(R.id.balance_et_card);
        et_shopcode = (EditText) findViewById(R.id.balance_et_shopcode);
        listView = (ListView) findViewById(R.id.listview);

        rl_left.setOnClickListener(this);
        rl_yes.setOnClickListener(this);
        rl_no.setOnClickListener(this);
        rl_jiesuan.setOnClickListener(new NoDoubleClickListener() {
            @Override
            protected void onNoDoubleClick(View view) {
                if (tv_num.getText().toString().equals("0")) {
                    Toast.makeText(getApplicationContext(), "请选择商品",
                            Toast.LENGTH_SHORT).show();
                } else {
                    if (CommonUtils.checkNet(getApplicationContext())) {
                        if (type.equals("否")) {
                            if (tv_vipjifen.getText().toString().equals("") || tv_vipjifen.getText().toString().equals("获取中")) {
                                Toast.makeText(ac, "您选择的是会员结算，请确认会员信息是否正确", Toast.LENGTH_SHORT).show();
                            } else {//会员结算
//
                                ShopXiaofeiDialog.jiesuanDialog(app,true, dialog, BalanceActivity.this, 1, "shop", Double.parseDouble(tv_money.getText().toString()), new InterfaceBack() {
                                    @Override
                                    public void onResponse(Object response) {
                                        if (response.toString().equals("wxpay")) {
                                            paytype = "wx";
                                            Intent mipca = new Intent(ac, MipcaActivityCapture.class);
                                            startActivityForResult(mipca, 333);
                                        } else if (response.toString().equals("zfbpay")) {
                                            paytype = "zfb";
                                            Intent mipca = new Intent(ac, MipcaActivityCapture.class);
                                            startActivityForResult(mipca, 333);
                                        } else {
                                            finish();
                                        }
                                    }

                                    @Override
                                    public void onErrorResponse(Object msg) {

                                    }
                                });
                            }
                        } else {//散客结算
                            ShopXiaofeiDialog.jiesuanDialog(app,false, dialog, BalanceActivity.this, 1, "shop", Double.parseDouble(tv_money.getText().toString()), new InterfaceBack() {
                                @Override
                                public void onResponse(Object response) {
                                    if (response.toString().equals("wxpay")) {
                                        paytype = "wx";
                                        Intent mipca = new Intent(ac, MipcaActivityCapture.class);
                                        startActivityForResult(mipca, 333);
                                    } else if (response.toString().equals("zfbpay")) {
                                        paytype = "zfb";
                                        Intent mipca = new Intent(ac, MipcaActivityCapture.class);
                                        startActivityForResult(mipca, 333);
                                    } else {
                                        finish();
                                    }
                                }

                                @Override
                                public void onErrorResponse(Object msg) {

                                }
                            });
                        }
                    } else {
                        Toast.makeText(getApplicationContext(), "请检查网络是否可用",
                                Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });
        tv_dingwei.setOnClickListener(new NoDoubleClickListener() {
            @Override
            protected void onNoDoubleClick(View view) {
                Intent duihuan = new Intent(ac, MipcaActivityCapture.class);
                startActivityForResult(duihuan, 222);
            }
        });
        rl_right.setOnClickListener(new NoDoubleClickListener() {
            @Override
            protected void onNoDoubleClick(View view) {
                Intent mipca = new Intent(ac, MipcaActivityCapture.class);
                startActivityForResult(mipca, 111);
            }
        });

        listView.setOnItemClickListener(this);


        rl_yes.setBackgroundColor(getResources().getColor(R.color.white));
        rl_no.setBackgroundColor(getResources().getColor(R.color.theme_red));
        tv_yes.setTextColor(getResources().getColor(R.color.text_30));
        tv_no.setTextColor(getResources().getColor(R.color.white));
        type = "否";
        PreferenceHelper.write(ac, "shoppay", "isSan", false);
        li_jifen.setVisibility(View.VISIBLE);
        rl_card.setVisibility(View.VISIBLE);
        rl_vipname.setVisibility(View.VISIBLE);
        rl_vipdengji.setVisibility(View.VISIBLE);
        rl_vipjifen.setVisibility(View.VISIBLE);
        rl_vipyue.setVisibility(View.VISIBLE);
        PreferenceHelper.write(ac, "shoppay", "memid", "");
        et_card.setText("");
        tv_vipjifen.setText("");
        tv_vipname.setText("");
        tv_vipyue.setText("");
        tv_money.setText("0");
        tv_vipdengji.setText("0");
        tv_jifen.setText("0");
        tv_num.setText("0");

    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position,
                            long id) {
        // TODO Auto-generated method stub
        //拿到当前位置
        mPosition = position;
        //即使刷新adapter
        adapter.notifyDataSetChanged();
        myFragment = new BalanceFragment();
        FragmentTransaction fragmentTransaction = getSupportFragmentManager()
                .beginTransaction();
        fragmentTransaction.replace(R.id.fragment_container, myFragment);
        Bundle bundle = new Bundle();
        bundle.putSerializable(BalanceFragment.TAG, (Serializable) list.get(mPosition).GoodsList);
        bundle.putString("isSan", type);
        myFragment.setArguments(bundle);
        fragmentTransaction.commit();

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case 111:
                if (resultCode == RESULT_OK) {
                    et_card.setText(data.getStringExtra("codedata"));
                }
                break;
            case 222:
                if (resultCode == RESULT_OK) {
                    et_shopcode.setText(data.getStringExtra("codedata"));
                }
                break;
            case 333:
                if (resultCode == RESULT_OK) {
                    pay(data.getStringExtra("codedata"));
                }
                break;
        }
    }

    private void pay(String codedata) {
        paydialog.show();
        AsyncHttpClient client = new AsyncHttpClient();
        final PersistentCookieStore myCookieStore = new PersistentCookieStore(this);
        client.setCookieStore(myCookieStore);
        RequestParams map = new RequestParams();
        map.put("auth_code", codedata);
        map.put("UserID", PreferenceHelper.readString(ac, "shoppay", "UserID", ""));
//        （1会员充值7商品消费9快速消费11会员充次）
        map.put("ordertype", 7);
        orderAccount = DateUtils.getCurrentTime("yyyyMMddHHmmss");
        map.put("account", orderAccount);
        map.put("money", tv_money.getText().toString());
//        0=现金 1=银联 2=微信 3=支付宝
        switch (paytype) {
            case "wx":
                map.put("payType", 2);
                break;
            case "zfb":
                map.put("payType", 3);
                break;
        }
        client.setTimeout(120*1000);
        LogUtils.d("xxparams", map.toString());
        String url = UrlTools.obtainUrl(ac, "?Source=3", "PayOnLine");
        LogUtils.d("xxurl", url);
        client.post(url, map, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                try {
                    paydialog.dismiss();
                    LogUtils.d("xxpayS", new String(responseBody, "UTF-8"));
                    JSONObject jso = new JSONObject(new String(responseBody, "UTF-8"));
                    if (jso.getInt("flag") == 1) {

                        JSONObject jsonObject = (JSONObject) jso.getJSONArray("print").get(0);
                        DayinUtils.dayin(jsonObject.getString("printContent"));
                        if (jsonObject.getInt("printNumber") == 0) {
                        } else {
                            BluetoothAdapter bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
                            if (bluetoothAdapter.isEnabled()) {
                                BluetoothUtil.connectBlueTooth(MyApplication.context);
                                BluetoothUtil.sendData(DayinUtils.dayin(jsonObject.getString("printContent")), jsonObject.getInt("printNumber"));
                            } else {
                            }
                        }
                        jiesuan(orderAccount);
                    } else {
                        Toast.makeText(ac, jso.getString("msg"), Toast.LENGTH_SHORT).show();
                    }
                } catch (Exception e) {
                    paydialog.dismiss();
                    Toast.makeText(ac, "支付失败，请稍后再试", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                paydialog.dismiss();
                Toast.makeText(ac, "支付失败，请稍后再试", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private  void jiesuan(String orderNum){
         dialog.show();
        AsyncHttpClient client = new AsyncHttpClient();
        final PersistentCookieStore myCookieStore = new PersistentCookieStore(context);
        client.setCookieStore(myCookieStore);
        final DBAdapter dbAdapter = DBAdapter.getInstance(context);
        List<ShopCar> list = dbAdapter.getListShopCar(PreferenceHelper.readString(context, "shoppay", "account", "123"));
        List<ShopCar> shoplist = new ArrayList<>();
        double yfmoney = 0.0;
        double zfmoney = 0.0;
        int point=0;
        int num = 0;
        for (ShopCar numShop : list) {
            if (numShop.count == 0) {
            } else {
                shoplist.add(numShop);
                zfmoney = CommonUtils.add(zfmoney, Double.parseDouble(numShop.discountmoney));
                yfmoney = CommonUtils.add(yfmoney, Double.parseDouble(CommonUtils.multiply(numShop.count + "", numShop.price)));
                num = num + numShop.count;
                point=point+(int)numShop.point;
            }
        }
        RequestParams params = new RequestParams();
        params.put("MemID", PreferenceHelper.readString(context, "shoppay", "memid", ""));
        params.put("OrderAccount", orderNum);
        params.put("TotalMoney", yfmoney);
        params.put("DiscountMoney", zfmoney);
        params.put("OrderPoint", point);
        switch (paytype) {
            case "wx":
                params.put("payType", 2);
                break;
            case "zfb":
                params.put("payType", 3);
                break;
        }
        params.put("UserPwd", "");
        params.put("GlistCount", shoplist.size());
        LogUtils.d("xxparams",shoplist.size()+"");
        for (int i = 0; i < shoplist.size(); i++) {
            LogUtils.d("xxparams",shoplist.get(i).discount);
            params.put("Glist[" + i + "][GoodsID]", shoplist.get(i).goodsid);
            params.put("Glist[" + i + "][number]", shoplist.get(i).count);
            params.put("Glist[" + i + "][GoodsPoint]", point);
            params.put("Glist[" + i + "][discountedprice]", shoplist.get(i).discount);
            params.put("Glist[" + i + "][Price]", shoplist.get(i).discountmoney);
            params.put("Glist[" + i + "][GoodsPrice]", shoplist.get(i).price);
        }
        LogUtils.d("xxparams", params.toString());
        String url = UrlTools.obtainUrl(context, "?Source=3", "GoodsExpense");
        LogUtils.d("xxurl", url);
        client.post(url, params, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                try {
                    dialog.dismiss();
                    LogUtils.d("xxjiesuanS", new String(responseBody, "UTF-8"));
                    JSONObject jso = new JSONObject(new String(responseBody, "UTF-8"));
                    if (jso.getInt("flag") == 1) {
                        Toast.makeText(context, jso.getString("msg"), Toast.LENGTH_LONG).show();
                        JSONObject jsonObject = (JSONObject) jso.getJSONArray("print").get(0);
                        if (jsonObject.getInt("printNumber") == 0) {
                            dbAdapter.deleteShopCar();
                        } else {
                            BluetoothAdapter bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
                            if (bluetoothAdapter.isEnabled()) {
                                BluetoothUtil.connectBlueTooth(MyApplication.context);
                                BluetoothUtil.sendData(DayinUtils.dayin(jsonObject.getString("printContent")), jsonObject.getInt("printNumber"));
                                dbAdapter.deleteShopCar();
                            } else {
                                dbAdapter.deleteShopCar();
                            }
                        }
                       finish();
                    }else{
                        Toast.makeText(context, jso.getString("msg"), Toast.LENGTH_LONG).show();
                    }
                } catch (Exception e) {
                    dialog.dismiss();
                }
//				printReceipt_BlueTooth(context,xfmoney,yfmoney,jf,et_zfmoney,et_yuemoney,tv_dkmoney,et_jfmoney);
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                dialog.dismiss();
                Toast.makeText(context, "结算失败，请重新结算",
                        Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.rl_left:
                finish();
                break;
            case R.id.rl_yes:
                rl_yes.setBackgroundColor(getResources().getColor(R.color.theme_red));
                rl_no.setBackgroundColor(getResources().getColor(R.color.white));
                tv_yes.setTextColor(getResources().getColor(R.color.white));
                tv_no.setTextColor(getResources().getColor(R.color.text_30));
                type = "是";
                li_jifen.setVisibility(View.GONE);
                rl_card.setVisibility(View.GONE);
                rl_vipname.setVisibility(View.GONE);
                rl_vipjifen.setVisibility(View.GONE);
                rl_vipdengji.setVisibility(View.GONE);
                rl_vipyue.setVisibility(View.GONE);
                dbAdapter.deleteShopCar();
                tv_money.setText("0");
                tv_jifen.setText("0");
                tv_vipdengji.setText("0");
                tv_num.setText("0");
                PreferenceHelper.write(ac, "shoppay", "isSan", true);
                PreferenceHelper.write(ac, "shoppay", "memid", "");
                PreferenceHelper.write(ac, "shoppay", "vipcar", "无");
                PreferenceHelper.write(ac, "shoppay", "vipname", "散客");
                for (ShopClass c : list) {
                    c.shopnum = "";
                }
                adapter.notifyDataSetChanged();
                break;
            case R.id.rl_no:
                rl_yes.setBackgroundColor(getResources().getColor(R.color.white));
                rl_no.setBackgroundColor(getResources().getColor(R.color.theme_red));
                tv_yes.setTextColor(getResources().getColor(R.color.text_30));
                tv_no.setTextColor(getResources().getColor(R.color.white));
                type = "否";
                PreferenceHelper.write(ac, "shoppay", "isSan", false);
                li_jifen.setVisibility(View.VISIBLE);
                rl_card.setVisibility(View.VISIBLE);
                rl_vipname.setVisibility(View.VISIBLE);
                rl_vipdengji.setVisibility(View.VISIBLE);
                rl_vipjifen.setVisibility(View.VISIBLE);
                rl_vipyue.setVisibility(View.VISIBLE);
                dbAdapter.deleteShopCar();
                PreferenceHelper.write(ac, "shoppay", "memid", "");
                et_card.setText("");
                tv_vipjifen.setText("");
                tv_vipname.setText("");
                tv_vipyue.setText("");
                tv_money.setText("0");
                tv_vipdengji.setText("0");
                tv_jifen.setText("0");
                tv_num.setText("0");
                for (ShopClass c : list) {
                    c.shopnum = "";
                }
                adapter.notifyDataSetChanged();
                break;
        }
    }


    private void obtainShopClass() {
        dialog.show();
        if (list != null) {
            list.clear();
        }
        AsyncHttpClient client = new AsyncHttpClient();
        final PersistentCookieStore myCookieStore = new PersistentCookieStore(this);
        client.setCookieStore(myCookieStore);
        RequestParams map = new RequestParams();
        LogUtils.d("xxparams", map.toString());
        String url = UrlTools.obtainUrl(ac, "?Source=3", "GetGoodsClass");
        LogUtils.d("xxurl", url);
        client.post(url, map, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                dialog.dismiss();
                try {
                    LogUtils.d("xxshopclassS", new String(responseBody, "UTF-8"));
                    JSONObject jso = new JSONObject(new String(responseBody, "UTF-8"));
                    if (jso.getInt("flag") == 1) {
                        Gson gson = new Gson();
                        Type listType = new TypeToken<List<ShopClass>>() {
                        }.getType();
                        list = gson.fromJson(jso.getString("vdata"), listType);
                        //创建MyFragment对象
                        myFragment = new BalanceFragment();
                        FragmentTransaction fragmentTransaction = getSupportFragmentManager()
                                .beginTransaction();
                        fragmentTransaction.replace(R.id.fragment_container, myFragment);
                        //通过bundle传值给MyFragment
                        Bundle bundle = new Bundle();
                        bundle.putSerializable(BalanceFragment.TAG, (Serializable) list.get(mPosition).GoodsList);
                        bundle.putString("isSan", type);
                        myFragment.setArguments(bundle);
                        fragmentTransaction.commit();
                        adapter = new LeftAdapter(ac, list);
                        listView.setAdapter(adapter);
                    } else {
                        Toast.makeText(ac, jso.getString("msg"), Toast.LENGTH_SHORT).show();
                    }
                } catch (Exception e) {
                    Toast.makeText(ac, "获取商品分类失败", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                dialog.dismiss();
            }
        });
    }

    private class ShopChangeReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            List<ShopCar> listss = dbAdapter.getListShopCar(PreferenceHelper.readString(context, "shoppay", "account", "123"));
            num = 0;
            money = 0;
            jifen = 0;
            xfmoney = 0;
            for (ShopCar shopCar : listss) {
                if (shopCar.count == 0) {

                } else {
                    num = num + shopCar.count;
                    money = money + Double.parseDouble(shopCar.discountmoney);
                    LogUtils.d("xxJifen", shopCar.point + "");
                    jifen = jifen + shopCar.point;
                    xfmoney = xfmoney + shopCar.count * Double.parseDouble(shopCar.price);
                }
            }
//          if(shopClass.ClassID.equals(shopCar.goodsclassid)){
//              classnum=classnum+shopCar.count;
//          }
            for (ShopClass c : list) {
                int classnum = 0;
                for (ShopCar shopCar : listss) {
                    if (shopCar.goodsclassid.equals(c.ClassID)) {
                        classnum = classnum + shopCar.count;
                    }
                }
                c.shopnum = classnum + "";
            }
            adapter.notifyDataSetChanged();
            tv_jifen.setText((int) jifen + "");
            tv_num.setText((int) num + "");
            tv_money.setText(StringUtil.twoNum(money + ""));

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
//
//                    weixinDialog.dismiss();
//                    jiesuanDialog.dismiss();
//                jiesuan(MyApplication.context,vipPayMsg);
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
//        unregisterReceiver(msgReceiver);
        unregisterReceiver(shopchangeReceiver);
    }


    public static byte[] printReceipt_BlueTooth(final String type, Context context, VipPayMsg msg) {
        String danhao = "消费单号:" + PreferenceHelper.readString(context, "shoppay", "OrderAccount", "");
        String huiyuankahao = "会员卡号:" + PreferenceHelper.readString(context, "shoppay", "vipcar", "无");
        String huiyuanming = "会员名称:" + PreferenceHelper.readString(context, "shoppay", "vipname", "散客");
        String xfmoney = "消费金额:" + StringUtil.twoNum(msg.xfMoney);
        String obtainjifen = "获得积分:" + (int) Double.parseDouble(msg.obtainJifen);
        Log.d("xx", PreferenceHelper.readString(context, "shoppay", "vipname", "散客"));
        try {
            byte[] next2Line = ESCUtil.nextLine(2);
            //            byte[] title = titleset.getBytes("gb2312");
            byte[] title = PreferenceHelper.readString(context, "shoppay", "PrintTitle", "").getBytes("gb2312");
            byte[] bottom = PreferenceHelper.readString(context, "shoppay", "PrintFootNote", "").getBytes("gb2312");
            byte[] tickname;
            if (type.equals("num")) {
                tickname = "服务充次小票".getBytes("gb2312");
            } else {
                tickname = "商品消费小票".getBytes("gb2312");
            }
            byte[] ordernum = danhao.getBytes("gb2312");
            byte[] vipcardnum = huiyuankahao.getBytes("gb2312");
            byte[] vipname = huiyuanming.getBytes("gb2312");
            byte[] xfmmm = xfmoney.getBytes("gb2312");
            byte[] objfff = (obtainjifen + "").getBytes("gb2312");
            byte[] xiahuaxian = "------------------------------".getBytes("gb2312");

            byte[] boldOn = ESCUtil.boldOn();
            byte[] fontSize2Big = ESCUtil.fontSizeSetBig(3);
            byte[] center = ESCUtil.alignCenter();
            byte[] Focus = "网 507".getBytes("gb2312");
            byte[] boldOff = ESCUtil.boldOff();
            byte[] fontSize2Small = ESCUtil.fontSizeSetSmall(3);
            byte[] left = ESCUtil.alignLeft();
            boldOn = ESCUtil.boldOn();
            byte[] fontSize1Big = ESCUtil.fontSizeSetBig(2);
            boldOff = ESCUtil.boldOff();
            byte[] fontSize1Small = ESCUtil.fontSizeSetSmall(2);
            next2Line = ESCUtil.nextLine(2);
            byte[] nextLine = ESCUtil.nextLine(1);
            nextLine = ESCUtil.nextLine(1);
            byte[] next4Line = ESCUtil.nextLine(4);
            byte[] breakPartial = ESCUtil.feedPaperCutPartial();
            byte[][] mytitle = {nextLine, center, boldOn, title, boldOff, next2Line, left, tickname, nextLine, left, ordernum, nextLine, left,
                    vipcardnum, nextLine,
                    left, vipname, nextLine, left, xfmmm, nextLine, left, objfff, nextLine, xiahuaxian};

            byte[] headerBytes = ESCUtil.byteMerger(mytitle);
            List<byte[]> bytesList = new ArrayList<>();
            bytesList.add(headerBytes);
            //商品头
            String shopdetai = "商品名称    " + "单价    " + "数量    " + "合计";
            //商品头
            byte[] sh = shopdetai.getBytes("gb2312");
            byte[][] mticket1 = {nextLine, left, sh};
            bytesList.add(ESCUtil.byteMerger(mticket1));
            //商品明细
            DBAdapter dbAdapter = DBAdapter.getInstance(context);
            List<ShopCar> list = dbAdapter.getListShopCar(PreferenceHelper.readString(context, "shoppay", "account", "123"));
            for (ShopCar numShop : list) {
                if (numShop.count == 0) {
                } else {
                    StringBuffer sb = new StringBuffer();

                    String sn = numShop.shopname;
                    Log.d("xxleng", sb.length() + "");
                    int sbl = sn.length();
                    if (sbl < 6) {
                        sb.append(sn);
                        for (int i = 0; i < 7 - sbl; i++) {
                            sb.insert(sb.length(), " ");
                        }
                    } else {
                        sn = sn.substring(0, 6);
                        sb.append(sn);
                        sb.append(" ");
                    }
                    Log.d("xxleng", sb.length() + "");
                    byte[] a = (sb.toString() + "" + CommonUtils.lasttwo(Double.parseDouble(numShop.price)) + "    " + numShop.count + "    " + numShop.discountmoney).getBytes("gb2312");
                    byte[][] mticket = {nextLine, left, a};
                    bytesList.add(ESCUtil.byteMerger(mticket));
                }
            }
            byte[][] mtickets = {nextLine, xiahuaxian};
            bytesList.add(ESCUtil.byteMerger(mtickets));
            if (msg.isMoney == 1) {

                byte[] yfmoney = ("应付金额:" + StringUtil.twoNum(msg.zhMoney)).getBytes("gb2312");
                byte[] jinshengmoney = ("节省金额:" + StringUtil.twoNum(msg.jieshengMoney)).getBytes("gb2312");

                byte[][] mticketsn = {nextLine, left, yfmoney, nextLine, left, jinshengmoney};
                bytesList.add(ESCUtil.byteMerger(mticketsn));
                byte[] moneys = ("现金支付:" + StringUtil.twoNum(msg.xjMoney)).getBytes("gb2312");
                byte[][] mticketsm = {nextLine, left, moneys};
                bytesList.add(ESCUtil.byteMerger(mticketsm));
            }
            if (msg.isWx == 1) {
                byte[] weixin = ("微信支付:" + StringUtil.twoNum(msg.wxMoney)).getBytes("gb2312");
                byte[][] weixins = {nextLine, left, weixin};
                bytesList.add(ESCUtil.byteMerger(weixins));
            }
            if (msg.isYue == 1) {
                byte[] yue = ("余额支付:" + StringUtil.twoNum(msg.yueMoney)).getBytes("gb2312");
                byte[][] mticketyue = {nextLine, left, yue};
                bytesList.add(ESCUtil.byteMerger(mticketyue));
            }
            if (msg.isJifen == 1) {
                byte[] jifen = ("积分抵扣:" + msg.jifenDkmoney).getBytes("gb2312");
                byte[][] mticketjin = {nextLine, left, jifen};
                bytesList.add(ESCUtil.byteMerger(mticketjin));
            }
            if (!msg.vipName.equals("散客")) {
                byte[] syjinfen = ("剩余积分:" + (int) Double.parseDouble(msg.vipSyJifen)).getBytes("gb2312");
                byte[][] mticketsyjf = {nextLine, left, syjinfen};
                bytesList.add(ESCUtil.byteMerger(mticketsyjf));
            }
            if (msg.isYue == 1) {
//				double sy=CommonUtils.del(Double.parseDouble(PreferenceHelper.readString(context, "shoppay", "MemMoney","")),Double.parseDouble(et_yuemoney.getText().toString()));
                byte[] shengyu = ("卡内余额:" + StringUtil.twoNum(msg.vipYue)).getBytes("gb2312");
                byte[][] mticketsy = {nextLine, left, shengyu};
                bytesList.add(ESCUtil.byteMerger(mticketsy));
            }

            byte[] ha = ("操作人员:" + PreferenceHelper.readString(context
                    , "shoppay", "UserName", "")).trim().getBytes("gb2312");
            byte[] time = ("消费时间:" + getStringDate()).trim().getBytes("gb2312");
            byte[] qianming = ("客户签名:").getBytes("gb2312");
            Log.d("xx", PreferenceHelper.readString(context
                    , "shoppay", "UserName", ""));
            byte[][] footerBytes = {nextLine, left, ha, nextLine, left, time, nextLine, left, qianming, nextLine, left,
                    nextLine, left, nextLine, left, bottom, next2Line, next4Line, breakPartial};

            bytesList.add(ESCUtil.byteMerger(footerBytes));
            Log.d("xxprint", new String(MergeLinearArraysUtil.mergeLinearArrays(bytesList)));
            return MergeLinearArraysUtil.mergeLinearArrays(bytesList);
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
//			Log.d("xx","异常");
        }
        return null;
    }

    public static String getStringDate() {
        Date currentTime = new Date();
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String dateString = formatter.format(currentTime);
        return dateString;
    }
}
