package com.shoppay.jinma;

import android.app.Activity;
import android.app.Dialog;
import android.bluetooth.BluetoothAdapter;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.RemoteException;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.PersistentCookieStore;
import com.loopj.android.http.RequestParams;
import com.shoppay.jinma.bean.VipInfo;
import com.shoppay.jinma.bean.VipInfoMsg;
import com.shoppay.jinma.card.ReadCardOpt;
import com.shoppay.jinma.tools.ActivityStack;
import com.shoppay.jinma.tools.BluetoothUtil;
import com.shoppay.jinma.tools.DateUtils;
import com.shoppay.jinma.tools.DayinUtils;
import com.shoppay.jinma.tools.DialogUtil;
import com.shoppay.jinma.tools.LogUtils;
import com.shoppay.jinma.tools.PreferenceHelper;
import com.shoppay.jinma.tools.UrlTools;
import com.shoppay.jinma.wxcode.MipcaActivityCapture;

import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.Date;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import cz.msebera.android.httpclient.Header;

/**
 * Created by songxiaotao on 2017/6/30.
 */

public class JifenChangeActivity extends Activity {
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
    @Bind(R.id.rb_add)
    RadioButton rbAdd;
    @Bind(R.id.rb_del)
    RadioButton rbDel;
    @Bind(R.id.radiogroup)
    RadioGroup radiogroup;
    @Bind(R.id.vip_tv_jifennum)
    TextView vipTvJifennum;
    @Bind(R.id.vip_et_jifennum)
    EditText vipEtJifennum;
    @Bind(R.id.vip_rl_jiesuan)
    RelativeLayout vipRlJiesuan;
    private boolean isSuccess = false;
    private boolean isadd=true;
    private Activity ac;
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
            }
        }
    };
    private Dialog dialog;
    private String editString;
    private boolean isClick=true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_jifenchange);
        ac = this;
        ButterKnife.bind(this);
        initView();
        dialog = DialogUtil.loadingDialog(ac, 1);
        PreferenceHelper.write(MyApplication.context, "shoppay", "viptoast", "未查询到会员");
        ActivityStack.create().addActivity(JifenChangeActivity.this);
//        obtainVipRecharge();
        tvTitle.setText("加减积分");
        radiogroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, int i) {
                switch (i) {
                    case R.id.rb_add:
                        isadd = true;
                        break;
                    case R.id.rb_del:
                        isadd = false;
                        break;
                }
            }
        });

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


//        PreferenceHelper.write(getApplicationContext(), "PayOk", "time", "false");
//        //动态注册广播接收器
//        msgReceiver = new MsgReceiver();
//        IntentFilter intentFilter = new IntentFilter();
//        intentFilter.addAction("com.example.communication.RECEIVER");
//        registerReceiver(msgReceiver, intentFilter);
    }

    private void initView() {
        //定义底部标签图片大小和位置
        Drawable drawable_news = getResources().getDrawable(R.drawable.radio);
        //当这个图片被绘制时，给他绑定一个矩形 ltrb规定这个矩形
        drawable_news.setBounds(0, 0, 55, 55);
        //设置图片在文字的哪个方向
        rbAdd.setCompoundDrawables(null, null, drawable_news, null);

        //定义底部标签图片大小和位置
        Drawable drawable_live = getResources().getDrawable(R.drawable.radio);
        //当这个图片被绘制时，给他绑定一个矩形 ltrb规定这个矩形
        drawable_live.setBounds(0, 0, 55, 55);
        //设置图片在文字的哪个方向
        rbDel.setCompoundDrawables(null, null, drawable_live, null);

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
        }
    }


    public static String getStringDate() {
        Date currentTime = new Date();
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String dateString = formatter.format(currentTime);
        return dateString;
    }

    @OnClick({R.id.rl_left, R.id.rl_right,R.id.vip_rl_jiesuan})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.rl_left:
                finish();
                break;
            case R.id.rl_right:
                Intent mipca = new Intent(ac, MipcaActivityCapture.class);
                startActivityForResult(mipca, 111);
                break;
            case R.id.vip_rl_jiesuan:
                if(isSuccess){
                      if(vipEtJifennum.getText().toString()==null||vipEtJifennum.getText().toString().equals("")){
                          Toast.makeText(ac,"请输入变动的积分数量",Toast.LENGTH_SHORT).show();
                      }else {
                          if (isadd) {
                              if(isClick) {
                                  jifenChange();
                              }
                          } else {
                              if (Integer.parseInt(vipTvJifen.getText().toString()) > Integer.parseInt(vipEtJifennum.getText().toString())) {
                              if(isClick) {
                                  jifenChange();
                              }
                          } else {
                              Toast.makeText(ac, "请输入积分数量大于会员现有积分", Toast.LENGTH_SHORT).show();
                          }
                      }
                      }
                }else{
                    Toast.makeText(ac,"请输入正确的会员卡号",Toast.LENGTH_SHORT).show();
                }
                break;
        }
    }

    private void jifenChange() {
        dialog.show();
        isClick=false;
        AsyncHttpClient client = new AsyncHttpClient();
        final PersistentCookieStore myCookieStore = new PersistentCookieStore(this);
        client.setCookieStore(myCookieStore);
        RequestParams params = new RequestParams();
        params.put("MemID",  PreferenceHelper.readString(ac,"shoppay","memid",""));
        params.put("pointOrderCode", DateUtils.getCurrentTime("yyyyMMddHHmmss"));
        params.put("pointNumber", Integer.parseInt(vipEtJifennum.getText().toString()));
//        变动类型(1：增加,-1：减少)
        if(isadd){
            params.put("type", 1);
        }else{
            params.put("type", -1);
        }
        LogUtils.d("xxparams", params.toString());
        String url = UrlTools.obtainUrl(ac, "?Source=3", "MemPointChange");
        LogUtils.d("xxurl", url);
        client.post(url, params, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                try {
                    dialog.dismiss();
                    LogUtils.d("xxJifenchaneS", new String(responseBody, "UTF-8"));
                    JSONObject jso = new JSONObject(new String(responseBody, "UTF-8"));
                    if (jso.getInt("flag") == 1) {
                        Toast.makeText(ac, jso.getString("msg"), Toast.LENGTH_LONG).show();
                        JSONObject jsonObject=(JSONObject) jso.getJSONArray("print").get(0);
                        if(jsonObject.getInt("printNumber")==0){
                            finish();
                        }else{
                            BluetoothAdapter bluetoothAdapter=BluetoothAdapter.getDefaultAdapter();
                            if(bluetoothAdapter.isEnabled()) {
                                BluetoothUtil.connectBlueTooth(MyApplication.context);
                                BluetoothUtil.sendData(DayinUtils.dayin(jsonObject.getString("printContent")),jsonObject.getInt("printNumber"));
                                ActivityStack.create().finishActivity(JifenChangeActivity.class);
                            }else {
                                ActivityStack.create().finishActivity(JifenChangeActivity.class);
                            }
                        }
                    } else {
                     isClick=true;
                        Toast.makeText(ac,jso.getString("msg"),Toast.LENGTH_SHORT).show();
                    }
                } catch (Exception e) {
                 isClick=true;
                    dialog.dismiss();
                    Toast.makeText(ac,"提交失败，请重新提交",Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                dialog.dismiss();
                 isClick=true;
                Toast.makeText(ac,"提交失败，请重新提交",Toast.LENGTH_SHORT).show();
            }
        });
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
//        unregisterReceiver(msgReceiver);
    }
}
