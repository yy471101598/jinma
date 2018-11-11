package com.shoppay.jinma;

import android.app.Activity;
import android.app.Dialog;
import android.bluetooth.BluetoothAdapter;
import android.os.Bundle;
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
import com.shoppay.jinma.adapter.XiaofeiRecordAdapter;
import com.shoppay.jinma.bean.XiaofeiRecord;
import com.shoppay.jinma.tools.BluetoothUtil;
import com.shoppay.jinma.tools.DayinUtils;
import com.shoppay.jinma.tools.DialogUtil;
import com.shoppay.jinma.tools.LogUtils;
import com.shoppay.jinma.tools.UrlTools;

import org.json.JSONObject;

import java.lang.reflect.Type;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import cz.msebera.android.httpclient.Header;

/**
 * Created by Administrator on 2018/1/20 0020.
 */

public class XiaofeiRecordActivity extends Activity {
    @Bind(R.id.img_left)
    ImageView imgLeft;
    @Bind(R.id.rl_left)
    RelativeLayout rlLeft;
    @Bind(R.id.tv_title)
    TextView tvTitle;
    @Bind(R.id.rl_right)
    RelativeLayout rlRight;
    @Bind(R.id.listview)
    ListView listview;
    private Activity ac;
    private Dialog dialog;
    private List<XiaofeiRecord> list;
    private XiaofeiRecordAdapter adapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_xiaofeijilu);
        ButterKnife.bind(this);
        ac = this;
        tvTitle.setText("消费记录");
        dialog = DialogUtil.loadingDialog(XiaofeiRecordActivity.this, 1);
        obtainXiaofeiRecord();
    }

    private void obtainXiaofeiRecord() {
        dialog.show();
        AsyncHttpClient client = new AsyncHttpClient();
        final PersistentCookieStore myCookieStore = new PersistentCookieStore(this);
        client.setCookieStore(myCookieStore);
        RequestParams params = new RequestParams();
//        LogUtils.d("xxparams",params.toString());
        String url= UrlTools.obtainUrl(ac,"?Source=3","OrderLogListGet");
        LogUtils.d("xxurl",url);
        client.post(url, params, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                dialog.dismiss();
                try {
                    LogUtils.d("xxxiaofeiS", new String(responseBody, "UTF-8"));
                    JSONObject jso = new JSONObject(new String(responseBody, "UTF-8"));
                    if(jso.getInt("flag")==1){
//                        Toast.makeText(ac, jso.getString("msg"), Toast.LENGTH_LONG).show();
                        Gson gson=new Gson();
                        Type listType = new TypeToken<List<XiaofeiRecord>>(){}.getType();
                        list = gson.fromJson(jso.getString("vdata"), listType);
                        adapter=new XiaofeiRecordAdapter(ac,list);
                        listview.setAdapter(adapter);

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
                    dialog.dismiss();
                    Toast.makeText(ac, "获取消费记录失败", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                dialog.dismiss();
                Toast.makeText(ac, "获取消费记录失败", Toast.LENGTH_SHORT).show();
            }
        });
    }

    @OnClick(R.id.rl_left)
    public void onViewClicked() {
        finish();

    }
}
