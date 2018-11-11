package com.shoppay.jinma;

import android.app.Activity;
import android.app.Dialog;
import android.bluetooth.BluetoothAdapter;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.PersistentCookieStore;
import com.loopj.android.http.RequestParams;
import com.shoppay.jinma.bean.Personal;
import com.shoppay.jinma.tools.BluetoothUtil;
import com.shoppay.jinma.tools.CommonUtils;
import com.shoppay.jinma.tools.DateUtils;
import com.shoppay.jinma.tools.DayinUtils;
import com.shoppay.jinma.tools.DialogUtil;
import com.shoppay.jinma.tools.LogUtils;
import com.shoppay.jinma.tools.PreferenceHelper;
import com.shoppay.jinma.tools.UrlTools;

import org.json.JSONObject;

import java.lang.reflect.Type;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import cz.msebera.android.httpclient.Header;

/**
 * Created by songxiaotao on 2018/1/23.
 */

public class PersonalActivity extends Activity {
    @Bind(R.id.img_left)
    ImageView mImgLeft;
    @Bind(R.id.rl_left)
    RelativeLayout mRlLeft;
    @Bind(R.id.tv_title)
    TextView mTvTitle;
    @Bind(R.id.rl_right)
    RelativeLayout mRlRight;
    @Bind(R.id.tv_account)
    TextView mTvAccount;
    @Bind(R.id.tv_name)
    TextView mTvName;
    @Bind(R.id.tv_quanxian)
    TextView mTvQuanxian;
    @Bind(R.id.tv_shopname)
    TextView mTvShopname;
    @Bind(R.id.tv_phone)
    TextView mTvPhone;
    @Bind(R.id.tv_createtime)
    TextView mTvCreatetime;
    @Bind(R.id.tv_lastlogin)
    TextView mTvLastlogin;
    @Bind(R.id.tv_version)
    TextView tv_version;
    private Activity ac;
    private Dialog dialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_personal);
        ButterKnife.bind(this);
        ac = this;
        dialog = DialogUtil.loadingDialog(ac, 1);
        mTvTitle.setText("个人中心");
        tv_version.setText(CommonUtils.getVersionName(ac));
        obtainPersonal();
    }


    private void obtainPersonal(){
        dialog.show();
        AsyncHttpClient client = new AsyncHttpClient();
        final PersistentCookieStore myCookieStore = new PersistentCookieStore(this);
        client.setCookieStore(myCookieStore);
        RequestParams map = new RequestParams();
        map.put("MemID",  PreferenceHelper.readString(ac,"shoppay","memid",""));
        map.put("rechargeAccount", DateUtils.getCurrentTime("yyyyMMddHHmmss"));

        LogUtils.d("xxparams",map.toString());
        String url= UrlTools.obtainUrl(ac,"?Source=3","GetUserInfo");
        LogUtils.d("xxurl",url);
        client.post(url, map, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                try {
                    dialog.dismiss();
                    LogUtils.d("xxviprechargeS", new String(responseBody, "UTF-8"));
                    JSONObject jso = new JSONObject(new String(responseBody, "UTF-8"));
                    if(jso.getInt("flag")==1){
                        Gson gson=new Gson();
//                        Toast.makeText(ac, jso.getString("msg"), Toast.LENGTH_LONG).show();
                        Type listType = new TypeToken<List<Personal>>(){}.getType();
                       List<Personal> listperson = gson.fromJson(jso.getString("vdata"), listType);
                        handleMsg(listperson.get(0));


                        JSONObject jsonObject=(JSONObject) jso.getJSONArray("print").get(0);
                        if(jsonObject.getInt("printNumber")==0){
                        }else{
                            BluetoothAdapter bluetoothAdapter=BluetoothAdapter.getDefaultAdapter();
                            if(bluetoothAdapter.isEnabled()) {
                                BluetoothUtil.connectBlueTooth(MyApplication.context);
                                BluetoothUtil.sendData(DayinUtils.dayin(jsonObject.getString("printContent")),jsonObject.getInt("printNumber"));
                            }else {
                            }
                        }
                    } else {

                        Toast.makeText(ac, jso.getString("msg"), Toast.LENGTH_SHORT).show();
                    }
                } catch (Exception e) {
                    Toast.makeText(ac, "获取信息失败，请重试", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                dialog.dismiss();
                Toast.makeText(ac, "获取信息失败，请重试", Toast.LENGTH_SHORT).show();
            }
        });


    }

    private void handleMsg(Personal personal) {
        mTvAccount.setText(personal.getUserAccount());
        mTvName.setText(personal.getUserName());
        mTvQuanxian.setText(personal.getUserGroupName());
        mTvShopname.setText(personal.getUserShopName());
        mTvPhone.setText(personal.getUserPhone());
        mTvCreatetime.setText(personal.getUserCreateTime());
        mTvLastlogin.setText(personal.getLastlogin());
    }

    @OnClick(R.id.rl_left)
    public void onViewClicked() {
        finish();
    }
}
