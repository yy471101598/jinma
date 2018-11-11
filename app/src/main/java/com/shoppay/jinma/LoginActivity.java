package com.shoppay.jinma;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Environment;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.PersistentCookieStore;
import com.loopj.android.http.RequestParams;
import com.shoppay.jinma.bean.QuanxianManage;
import com.shoppay.jinma.bean.SystemQuanxian;
import com.shoppay.jinma.tools.ActivityStack;
import com.shoppay.jinma.tools.CommonUtils;
import com.shoppay.jinma.tools.DialogUtil;
import com.shoppay.jinma.tools.LogUtils;
import com.shoppay.jinma.tools.NoDoubleClickListener;
import com.shoppay.jinma.tools.PreferenceHelper;
import com.shoppay.jinma.tools.SysUtil;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.File;
import java.io.FileInputStream;
import java.math.BigInteger;
import java.util.Iterator;

import cz.msebera.android.httpclient.Header;

/**
 * Created by songxiaotao on 2017/6/30.
 */

public class LoginActivity extends Activity implements View.OnClickListener {
    private RelativeLayout rl_bang, rl_login;
    private EditText et_account, et_pwd, et_yuming;
    private TextView tv_yuming;
    private CheckBox cb;
    private Activity ac;
    private Dialog dialog;
    private ImageView img;
    File file;
    private QuanxianManage menuquanxian;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        ac = this;
        ActivityStack.create().addActivity(ac);
        initView();
        if (PreferenceHelper.readBoolean(ac, "shoppay", "remember", false)) {
            cb.setChecked(true);
            et_account.setText(PreferenceHelper.readString(ac, "shoppay", "account", "123"));
            et_pwd.setText(PreferenceHelper.readString(ac, "shoppay", "pwd", "123"));
            et_yuming.setText(PreferenceHelper.readString(ac, "shoppay", "bianhao", ""));
            tv_yuming.setVisibility(View.VISIBLE);
            if (PreferenceHelper.readString(ac, "shoppay", "yuming", "123").startsWith("http://")) {
                String yuming = PreferenceHelper.readString(ac, "shoppay", "yuming", "123");
                tv_yuming.setText("http://" + yuming.substring(7, yuming.length()).split("\\/", 2)[0]);
            } else {
                tv_yuming.setText(PreferenceHelper.readString(ac, "shoppay", "yuming", "123").split("\\/", 2)[0]);
            }
        }


        dialog = DialogUtil.loadingDialog(ac, 1);
        setimg();
        file = new File(Environment.getExternalStorageDirectory(),
                "error.log");
    }

    public String str2HexStr(String str) {
        byte[] bytes = str.getBytes();
        // 如果不是宽类型的可以用Integer
        BigInteger bigInteger = new BigInteger(1, bytes);
        return bigInteger.toString(16);
    }

    private void loadError(String s) {
        AsyncHttpClient client = new AsyncHttpClient();
        final PersistentCookieStore myCookieStore = new PersistentCookieStore(this);
        client.setCookieStore(myCookieStore);
        RequestParams params = new RequestParams();
        params.put("error", s);

        Log.d("xx", s);
        client.post(PreferenceHelper.readString(ac, "shoppay", "yuming", "123") + "?Source=3&Method=logError", params, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                Log.d("xxLogS", "sd");
                try {
                    file.delete();
                    Log.d("xxLogS", new String(responseBody, "UTF-8"));
                } catch (Exception e) {
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                Log.d("xxLogE", error.getMessage());
            }
        });
    }

    private void setimg() {
        DisplayMetrics disMetrics = new DisplayMetrics();
        this.getWindowManager().getDefaultDisplay().getMetrics(disMetrics);
        int width = disMetrics.widthPixels;
        int height = disMetrics.heightPixels;
        Bitmap bitmap = BitmapFactory.decodeResource(this.getResources(), R.drawable.banner);//link the drable image
        SysUtil.setImageBackground(bitmap, img, width, dip2px(ac, 170));
    }


    public int dip2px(Context context, float dipValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (dipValue * scale + 0.5f);
    }

    private void initView() {
        img = (ImageView) findViewById(R.id.imgview);
        rl_bang = (RelativeLayout) findViewById(R.id.login_rl_bang);
        rl_login = (RelativeLayout) findViewById(R.id.rl_login);
        et_account = (EditText) findViewById(R.id.et_login_phone);
        et_pwd = (EditText) findViewById(R.id.et_login_pwd);
        et_yuming = (EditText) findViewById(R.id.login_et_yuming);
        tv_yuming = (TextView) findViewById(R.id.login_tv_yuming);
        cb = (CheckBox) findViewById(R.id.login_cb);
        cb.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if (b) {
                    Log.d("xx", "checked");
                    PreferenceHelper.write(ac, "shoppay", "remember", b);
                }
            }
        });

        rl_login.setOnClickListener(new NoDoubleClickListener() {
            @Override
            protected void onNoDoubleClick(View view) {
                if (et_account.getText().toString().equals("")
                        || et_account.getText().toString() == null) {
                    Toast.makeText(getApplicationContext(), "请输入账号",
                            Toast.LENGTH_SHORT).show();
                } else if (et_pwd.getText().toString().equals("")
                        || et_pwd.getText().toString() == null) {
                    Toast.makeText(getApplicationContext(), "请输入密码",
                            Toast.LENGTH_SHORT).show();
                } else if (tv_yuming.getText().toString().equals("")
                        || tv_yuming.getText().toString() == null) {
                    Toast.makeText(getApplicationContext(), "请先绑定域名",
                            Toast.LENGTH_SHORT).show();
                } else {
                    if (CommonUtils.checkNet(getApplicationContext())) {
                        login();
                        if (file.exists()) {
                            if (Environment.getExternalStorageState().equals(
                                    Environment.MEDIA_MOUNTED)) {
                                try {
                                    FileInputStream inputStream = new FileInputStream(file);
                                    byte[] b = new byte[inputStream.available()];
                                    inputStream.read(b);
                                    loadError(new String(b));
                                } catch (Exception e) {
                                }
                            } else {
                                // 此时SDcard不存在或者不能进行读写操作的
                            }

                        }
                    } else {
                        Toast.makeText(getApplicationContext(), "请检查网络是否可用",
                                Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });
        rl_bang.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.login_rl_bang:
                if (et_yuming.getText().toString().equals("")
                        || et_yuming.getText().toString() == null) {
                    Toast.makeText(getApplicationContext(), "请输入域名编号",
                            Toast.LENGTH_SHORT).show();
                } else {
                    if (CommonUtils.checkNet(getApplicationContext())) {
                        obtainYuming();
                    } else {
                        Toast.makeText(getApplicationContext(), "请检查网络是否可用",
                                Toast.LENGTH_SHORT).show();
                    }
                }
                break;
        }
    }


    private void obtainYuming() {
        dialog.show();

        AsyncHttpClient client = new AsyncHttpClient();
//        final PersistentCookieStore myCookieStore = new PersistentCookieStore(this);
//        client.setCookieStore(myCookieStore);
        RequestParams params = new RequestParams();
        params.put("code", et_yuming.getText().toString());
//        params.put("userPassword",et_pwd.getText().toString());
        client.get("http://srcnew.vip5968.com/AjaxService/Service.ashx?Method=CodeToDomain", params, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                dialog.dismiss();
                try {
                    LogUtils.d("xxYumingS", new String(responseBody, "UTF-8"));
                    JSONObject jso = new JSONObject(new String(responseBody, "UTF-8"));
                    if (jso.getInt("flag") == 1) {
                        PreferenceHelper.write(ac, "shoppay", "yuming", jso.getString("result"));
                        tv_yuming.setVisibility(View.VISIBLE);
                        if (PreferenceHelper.readString(ac, "shoppay", "yuming", "123").startsWith("http://")) {
                            String yuming = PreferenceHelper.readString(ac, "shoppay", "yuming", "123");
                            tv_yuming.setText("http://" + yuming.substring(7, yuming.length()).split("\\/", 2)[0]);
                        } else {
                            tv_yuming.setText(PreferenceHelper.readString(ac, "shoppay", "yuming", "123").split("\\/", 2)[0]);
                        }
                    } else {
                        Toast.makeText(ac, jso.getString("msg"), Toast.LENGTH_SHORT).show();
                        tv_yuming.setVisibility(View.GONE);
                    }
                } catch (Exception e) {
                    Toast.makeText(ac, "获取域名失败，请稍后再试", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                dialog.dismiss();
                Toast.makeText(ac, "获取域名失败，请稍后再试", Toast.LENGTH_SHORT).show();
            }
        });
    }


    private void login() {
        dialog.show();
        AsyncHttpClient client = new AsyncHttpClient();
        final PersistentCookieStore myCookieStore = new PersistentCookieStore(this);
        client.setCookieStore(myCookieStore);
        RequestParams params = new RequestParams();
        params.put("Account", et_account.getText().toString());
        params.put("password", et_pwd.getText().toString());
        client.post(PreferenceHelper.readString(ac, "shoppay", "yuming", "123") + "?Source=3&Method=login", params, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                dialog.dismiss();
                try {
                    LogUtils.d("xxLoginS", new String(responseBody, "UTF-8"));
                    JSONObject jso = new JSONObject(new String(responseBody, "UTF-8"));
                    if (jso.getInt("flag") == 1) {
                        MyApplication myApplication = (MyApplication) getApplication();
                        PreferenceHelper.write(ac, "shoppay", "account", et_account.getText().toString());
                        PreferenceHelper.write(ac, "shoppay", "pwd", et_pwd.getText().toString());
                        PreferenceHelper.write(ac, "shoppay", "bianhao", et_yuming.getText().toString());
                        PreferenceHelper.write(ac, "shoppay", "UserID", jso.getJSONObject("vdata").getString("UserID"));
                        PreferenceHelper.write(ac, "shoppay", "ShopID", jso.getJSONObject("vdata").getString("ShopID"));
                        JSONArray memuau = jso.getJSONObject("vdata").getJSONArray("MenuAuthority");
                        JSONArray dataau = jso.getJSONObject("vdata").getJSONArray("DataAuthority");
//                        "DataAuthority":[{"1.1":1,"1.2":1,"1.3":1,"1.4":1,"1.5":1,"1.6":1,"1.7":0}]}
                        menuquanxian = new QuanxianManage();
                        SystemQuanxian sysquanxian = new SystemQuanxian();
                        for (int i = 0; i < memuau.length(); i++) {
                            JSONObject j = memuau.getJSONObject(i);
                            for (Iterator<String> iterator = j.keys(); iterator.hasNext(); ) {
                                String key = iterator.next();
                                int value = (int) j.get(key);
                                switch (key) {
                                    case "1.1":
                                        menuquanxian.shopxiaofei = value;
                                        break;
                                    case "1.2":
                                        menuquanxian.fastxiaofei = value;
                                        break;
                                    case "1.3":
                                        menuquanxian.numxiaofei = value;
                                        break;
                                    case "2.1":
                                        menuquanxian.vipcard = value;
                                        break;
                                    case "2.2":
                                        menuquanxian.viprecharge = value;
                                        break;
                                    case "2.3":
                                        menuquanxian.vipnum = value;
                                        break;
                                    case "3.1":
                                        menuquanxian.jifenbiandong = value;
                                    case "3.3":
                                        menuquanxian.lipingduihuan = value;
                                        break;
                                    case "4.1":
                                        menuquanxian.jiaobanmanage = value;
                                        break;
                                }
                            }
                        }
                        JSONObject jdata = dataau.getJSONObject(0);
                        for (Iterator<String> iterator = jdata.keys(); iterator.hasNext(); ) {
                            String key = iterator.next();
                            int value = (int) jdata.get(key);
//                            1代表启用 0代表未启用
                            switch (key) {
                                case "1.1":
                                    sysquanxian.ischangemoney = value;
                                    break;
                                case "1.2":
                                    sysquanxian.isyue = value;
                                    break;
                                case "1.3":
                                    sysquanxian.isxianjin = value;
                                    break;
                                case "1.4":
                                    sysquanxian.isyinlian = value;
                                    break;
                                case "1.5":
                                    sysquanxian.iszhifubao = value;
                                    break;
                                case "1.6":
                                    sysquanxian.isweixin = value;
                                    break;
                                case "1.7":
                                    sysquanxian.ispassword = value;
                                    break;
                                case "1.8":
                                    sysquanxian.iswxpay = value;
                                    break;
                                case "1.9":
                                    sysquanxian.iszfbpay = value;
                                    break;
                                case "2.0":
                                    //是否启用通用积分支付
                                    sysquanxian.istyjf = value;
                                    break;
                                case "2.1":
                                    //2.1 是否启用限店积分支付
                                    sysquanxian.isxdjf = value;
                                    break;
                            }
                        }
                        myApplication.setSystemQuanxian(sysquanxian);
                        Intent intent = new Intent(ac, HomeActivity.class);
                        intent.putExtra("quanxian", menuquanxian);
                        startActivity(intent);
                        finish();
                    } else {
                        Toast.makeText(ac, jso.getString("msg"), Toast.LENGTH_SHORT).show();
                    }
                } catch (Exception e) {
                    dialog.dismiss();
                    Toast.makeText(ac, "登录失败，请重新登录", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                dialog.dismiss();
                Toast.makeText(ac, "登录失败，请重新登录", Toast.LENGTH_SHORT).show();
            }
        });
    }

}
