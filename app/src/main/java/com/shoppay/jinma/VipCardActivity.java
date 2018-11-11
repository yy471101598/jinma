package com.shoppay.jinma;

import android.app.Activity;
import android.app.Dialog;
import android.bluetooth.BluetoothAdapter;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.RemoteException;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.PersistentCookieStore;
import com.loopj.android.http.RequestParams;
import com.shoppay.jinma.bean.Dengji;
import com.shoppay.jinma.bean.SystemQuanxian;
import com.shoppay.jinma.bean.VipInfo;
import com.shoppay.jinma.bean.VipInfoMsg;
import com.shoppay.jinma.card.ReadCardOpt;
import com.shoppay.jinma.http.InterfaceBack;
import com.shoppay.jinma.tools.ActivityStack;
import com.shoppay.jinma.tools.BluetoothUtil;
import com.shoppay.jinma.tools.CommonUtils;
import com.shoppay.jinma.tools.DateUtils;
import com.shoppay.jinma.tools.DayinUtils;
import com.shoppay.jinma.tools.DialogUtil;
import com.shoppay.jinma.tools.LogUtils;
import com.shoppay.jinma.tools.PreferenceHelper;
import com.shoppay.jinma.tools.UrlTools;
import com.shoppay.jinma.wxcode.MipcaActivityCapture;

import org.json.JSONObject;

import java.lang.reflect.Type;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import cz.msebera.android.httpclient.Header;

/**
 * Created by songxiaotao on 2017/6/30.
 */

public class VipCardActivity extends Activity implements View.OnClickListener {
    private RelativeLayout rl_left, rl_save, rl_boy, rl_girl, rl_vipdj;
    private EditText et_vipcard, et_bmcard, et_vipname, et_phone, et_tjcard;
    private TextView tv_title, tv_boy, tv_girl, tv_vipsr, tv_vipdj, tv_tjname, tv_endtime;
    private Activity ac;
    private String state = "男";
    private String editString;
    private Dialog dialog;
    private List<Dengji> list;
    private Dengji dengji;
    private TextView tv_passstate;
    private EditText et_password;
    private RelativeLayout rl_right;
    private boolean ispassword = false;
    private boolean isClick=true;
    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case 1:
                    VipInfo info = (VipInfo) msg.obj;
                    tv_tjname.setText(info.getMemName());
                    break;
                case 2:
                    tv_tjname.setText("获取中");
                    break;
            }
        }
    };
    private MyApplication app;
    private SystemQuanxian sysquanxian;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_vipcard);
        ac = this;
        app= (MyApplication) getApplication();
        sysquanxian=app.getSysquanxian();
        dialog = DialogUtil.loadingDialog(VipCardActivity.this, 1);
        ActivityStack.create().addActivity(ac);
        initView();
        vipDengjiList("no");

        et_tjcard.addTextChangedListener(new TextWatcher() {
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

    private void initView() {


        rl_left = (RelativeLayout) findViewById(R.id.rl_left);
        rl_save = (RelativeLayout) findViewById(R.id.vipcard_rl_save);
        rl_girl = (RelativeLayout) findViewById(R.id.rl_girl);
        rl_boy = (RelativeLayout) findViewById(R.id.rl_boy);
        rl_vipdj = (RelativeLayout) findViewById(R.id.vipcard_rl_chose);
        et_vipcard = (EditText) findViewById(R.id.vipcard_et_cardnum);
        et_bmcard = (EditText) findViewById(R.id.vipcard_et_kmnum);
        et_tjcard = (EditText) findViewById(R.id.vipcard_et_tjcard);
        et_vipname = (EditText) findViewById(R.id.vipcard_et_vipname);
        et_phone = (EditText) findViewById(R.id.vipcard_et_phone);
        tv_title = (TextView) findViewById(R.id.tv_title);
        tv_boy = (TextView) findViewById(R.id.tv_boy);
        tv_girl = (TextView) findViewById(R.id.tv_girl);
        tv_passstate = (TextView) findViewById(R.id.tv_passstate);
        et_password = (EditText) findViewById(R.id.vipcard_et_password);
        tv_vipsr = (TextView) findViewById(R.id.vipcard_tv_vipsr);
        tv_vipdj = (TextView) findViewById(R.id.vipcard_tv_vipdj);
        tv_tjname = (TextView) findViewById(R.id.vipcard_tv_tjname);
        tv_endtime = (TextView) findViewById(R.id.vipcard_tv_endtime);
        tv_title.setText("会员办卡");
        if (sysquanxian.ispassword == 1) {
            tv_passstate.setVisibility(View.VISIBLE);
            ispassword = true;
        }

        rl_right = (RelativeLayout) findViewById(R.id.rl_right);
        rl_right.setOnClickListener(this);
        rl_left.setOnClickListener(this);
        rl_save.setOnClickListener(this);
        rl_boy.setOnClickListener(this);
        rl_girl.setOnClickListener(this);
        rl_vipdj.setOnClickListener(this);
        tv_endtime.setOnClickListener(this);
        tv_vipsr.setOnClickListener(this);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case 111:
                if (resultCode == RESULT_OK) {
                    et_vipcard.setText(data.getStringExtra("codedata"));
                }
                break;

        }
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.rl_right:
                Intent mipca = new Intent(ac, MipcaActivityCapture.class);
                startActivityForResult(mipca, 111);
                break;
            case R.id.rl_left:
                finish();
                break;
            case R.id.vipcard_rl_save:
                if (et_vipcard.getText().toString().equals("")
                        || et_vipcard.getText().toString() == null) {
                    Toast.makeText(getApplicationContext(), "请输入会员卡号",
                            Toast.LENGTH_SHORT).show();
                }
//                else if (et_vipname.getText().toString().equals("")
//                        || et_vipname.getText().toString() == null) {
//                    Toast.makeText(getApplicationContext(), "请输入会员姓名",
//                            Toast.LENGTH_SHORT).show();
//                }
//                else if (et_phone.getText().toString().equals("")
//                        || et_phone.getText().toString() == null) {
//                    Toast.makeText(getApplicationContext(), "请输入手机号码",
//                            Toast.LENGTH_SHORT).show();
//                }
                else if (tv_vipdj.getText().toString().equals("请选择")) {
                    Toast.makeText(getApplicationContext(), "请选择会员等级",
                            Toast.LENGTH_SHORT).show();
                }
//                else if (CommonUtils.isMobileNO(et_phone.getText().toString())) {
//                    Toast.makeText(getApplicationContext(), "请输入正确的手机号码",
//                            Toast.LENGTH_SHORT).show();
//                }
                else if (ispassword) {
                    if (et_password.getText().toString() == null || et_password.getText().toString().equals("")) {
                        Toast.makeText(getApplicationContext(), "请输入会员卡密码",
                                Toast.LENGTH_SHORT).show();
                    }else{
                        if (CommonUtils.checkNet(getApplicationContext())) {
                            try {
                                if(isClick) {
                                    saveVipCard();
                                }
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        } else {
                            Toast.makeText(getApplicationContext(), "请检查网络是否可用",
                                    Toast.LENGTH_SHORT).show();
                        }
                    }
                } else {
                    if (CommonUtils.checkNet(getApplicationContext())) {
                        try {
                            if(isClick) {
                                saveVipCard();
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    } else {
                        Toast.makeText(getApplicationContext(), "请检查网络是否可用",
                                Toast.LENGTH_SHORT).show();
                    }
                }
                break;
            case R.id.vipcard_rl_chose:
                if (list == null || list.size() == 0) {
                    vipDengjiList("yes");
                } else {
                    DialogUtil.dengjiChoseDialog(VipCardActivity.this, list, 1, new InterfaceBack() {
                        @Override
                        public void onResponse(Object response) {
                            dengji = (Dengji) response;
                            tv_vipdj.setText(dengji.LevelName);
                        }

                        @Override
                        public void onErrorResponse(Object msg) {

                        }
                    });
                }
                break;
            case R.id.rl_boy:
                rl_boy.setBackgroundColor(getResources().getColor(R.color.theme_red));
                rl_girl.setBackgroundColor(getResources().getColor(R.color.white));
                tv_boy.setTextColor(getResources().getColor(R.color.white));
                tv_girl.setTextColor(getResources().getColor(R.color.text_30));
                state = "男";
                break;
            case R.id.rl_girl:
                rl_boy.setBackgroundColor(getResources().getColor(R.color.white));
                rl_girl.setBackgroundColor(getResources().getColor(R.color.theme_red));
                tv_boy.setTextColor(getResources().getColor(R.color.text_30));
                tv_girl.setTextColor(getResources().getColor(R.color.white));
                state = "女";
                break;
            case R.id.vipcard_tv_vipsr:
                DialogUtil.dateChoseDialog(VipCardActivity.this, 1, new InterfaceBack() {
                    @Override
                    public void onResponse(Object response) {
                        tv_vipsr.setText((String) response);
                    }

                    @Override
                    public void onErrorResponse(Object msg) {
                        tv_vipsr.setText((String) msg);
                    }
                });
                break;
            case R.id.vipcard_tv_endtime:
                DialogUtil.dateChoseDialog(VipCardActivity.this, 1, new InterfaceBack() {
                    @Override
                    public void onResponse(Object response) {
                        String data = DateUtils.timeTodata((String) response);
                        String cru = DateUtils.timeTodata(DateUtils.getCurrentTime_Today());
                        Log.d("xxTime", data + ";" + cru + ";" + DateUtils.getCurrentTime_Today() + ";" + (String) response);
                        if (Double.parseDouble(data) > Double.parseDouble(cru)) {
                            tv_endtime.setText((String) response);
                        } else {
                            Toast.makeText(ac, "过期时间要大于当前时间", Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onErrorResponse(Object msg) {
                        tv_endtime.setText((String) msg);
                    }
                });
                break;

        }
    }

    private void saveVipCard() throws Exception {
        dialog.show();
        isClick=false;
        AsyncHttpClient client = new AsyncHttpClient();
        final PersistentCookieStore myCookieStore = new PersistentCookieStore(this);
        client.setCookieStore(myCookieStore);
        RequestParams map = new RequestParams();
        map.put("MemCard", et_vipcard.getText().toString());//会员卡号
//        map.put("memName", et_vipname.getText().toString());//会员姓名
        if (state.equals("男")) {
            map.put("MemSex", "男");
        } else {
            map.put("MemSex", "女");
        }
        map.put("MemMobile", et_phone.getText().toString());
        map.put("MemLevelID", Integer.parseInt(dengji.LevelID));
//        if (et_vipname.getText().toString().equals("")
//                || et_vipname.getText().toString() == null) {
//            map.put("MemName", "");
//        } else {
        map.put("MemName", et_vipname.getText().toString());
        map.put("MemPassword", et_password.getText().toString());
//        }
//        if (et_phone.getText().toString().equals("")
//                || et_phone.getText().toString() == null) {
//            map.put("memPhone", "");
//        } else {
//            map.put("memPhone", et_phone.getText().toString());
//        }
        if (et_bmcard.getText().toString().equals("")
                || et_bmcard.getText().toString() == null) {
            map.put("MemCardNumber", "");//卡面号码
        } else {
            map.put("MemCardNumber", et_bmcard.getText().toString());//卡面号码
        }
        if (tv_vipsr.getText().toString().equals("年-月-日")) {
            map.put("MemBirthday", "");
        } else {
            map.put("MemBirthday", tv_vipsr.getText().toString());
        }
//        if (tv_tjname.getText().toString().equals("")
//                || tv_tjname.getText().toString() == null) {
//            map.put("memRecommendId", "");//推介人id
//        } else {
//            map.put("memRecommendId", Integer.parseInt( PreferenceHelper.readString(ac, "shoppay", "memid", "")));//推介人id
//        }
//        if (tv_endtime.getText().toString().equals("年-月-日")) {
//            map.put("memPastTime", "");//过期时间
//        } else {
//            map.put("memPastTime", tv_endtime.getText().toString());//过期时间
//        }
        LogUtils.d("xxparams", map.toString());
        LogUtils.d("xxurl", UrlTools.obtainUrl(ac, "?Source=3", "CreateMem"));
        client.post(UrlTools.obtainUrl(ac, "?Source=3", "CreateMem"), map, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                try {
                    dialog.dismiss();
                    LogUtils.d("xxsaveVipCardS", new String(responseBody, "UTF-8"));
                    JSONObject jso = new JSONObject(new String(responseBody, "UTF-8"));
                    if (jso.getInt("flag") == 1) {
                        Toast.makeText(ac, jso.getString("msg"), Toast.LENGTH_LONG).show();
                        JSONObject jsonObject = (JSONObject) jso.getJSONArray("print").get(0);
                        if (jsonObject.getInt("printNumber") == 0) {
                            finish();
                        } else {
                            BluetoothAdapter bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
                            if (bluetoothAdapter.isEnabled()) {
                                BluetoothUtil.connectBlueTooth(MyApplication.context);
                                BluetoothUtil.sendData(DayinUtils.dayin(jsonObject.getString("printContent")), jsonObject.getInt("printNumber"));
                                ActivityStack.create().finishActivity(ac);
                            } else {
                                ActivityStack.create().finishActivity(ac);
                            }
                        }
                    } else {
                          isClick=true;
                        Toast.makeText(ac, jso.getString("msg"), Toast.LENGTH_SHORT).show();
                    }
                } catch (Exception e) {
                    isClick=true;
                    Toast.makeText(ac, "会员卡办理失败，请重新登录", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                dialog.dismiss();
                isClick=true;
                Toast.makeText(ac, "会员卡办理失败，请重新登录", Toast.LENGTH_SHORT).show();
            }
        });


    }

    @Override
    protected void onResume() {
        super.onResume();
        new ReadCardOpt(et_vipcard);
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
        }
    }

    //把字符串转为日期
    public static Date stringToDate(String strDate) throws Exception {
        DateFormat df = new SimpleDateFormat("yyyy-MM-dd");
        return df.parse(strDate);
    }

    private void vipDengjiList(final String type) {

        AsyncHttpClient client = new AsyncHttpClient();
        final PersistentCookieStore myCookieStore = new PersistentCookieStore(this);
        client.setCookieStore(myCookieStore);
        RequestParams params = new RequestParams();
//        params.put("UserAcount", susername);
        client.post(PreferenceHelper.readString(ac, "shoppay", "yuming", "123") + "?Source=3&UserID=" + PreferenceHelper.readString(ac, "shoppay", "UserID", "123") + "&UserShopID=" + PreferenceHelper.readString(ac, "shoppay", "ShopID", "123") + "&Method=GetMemLevel", params, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                try {
                    Log.d("xxDengjiS", new String(responseBody, "UTF-8"));
                    JSONObject jso = new JSONObject(new String(responseBody, "UTF-8"));
                    if (jso.getInt("flag") == 1) {
                        String data = jso.getString("vdata");
                        Gson gson = new Gson();
                        Type listType = new TypeToken<List<Dengji>>() {
                        }.getType();
                        list = gson.fromJson(data, listType);
                        if (type.equals("no")) {

                        } else {
                            DialogUtil.dengjiChoseDialog(VipCardActivity.this, list, 1, new InterfaceBack() {
                                @Override
                                public void onResponse(Object response) {
                                    dengji = (Dengji) response;
                                    tv_vipdj.setText(dengji.LevelName);
                                }

                                @Override
                                public void onErrorResponse(Object msg) {

                                }
                            });
                        }
                    } else {
                        if (type.equals("no")) {

                        } else {
                            Toast.makeText(ac, jso.getString("msg"), Toast.LENGTH_SHORT).show();
                        }
                    }
                } catch (Exception e) {
                    if (type.equals("no")) {

                    } else {
                        Toast.makeText(ac, "获取会员等级失败，请重新登录", Toast.LENGTH_SHORT).show();
                    }
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                if (type.equals("no")) {

                } else {
                    Toast.makeText(ac, "获取会员等级失败，请重新登录", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
}
