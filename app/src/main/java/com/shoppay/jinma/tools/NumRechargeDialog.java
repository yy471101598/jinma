package com.shoppay.jinma.tools;

import android.app.Dialog;
import android.bluetooth.BluetoothAdapter;
import android.content.Context;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.PersistentCookieStore;
import com.loopj.android.http.RequestParams;
import com.shoppay.jinma.MyApplication;
import com.shoppay.jinma.R;
import com.shoppay.jinma.bean.ShopCar;
import com.shoppay.jinma.bean.SystemQuanxian;
import com.shoppay.jinma.db.DBAdapter;
import com.shoppay.jinma.http.InterfaceBack;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import cz.msebera.android.httpclient.Header;

/**
 * Created by Administrator on 2018/1/21 0021.
 */

public class NumRechargeDialog {
    public static boolean isMoney = true, isYue = false, isZhifubao = false, isYinlian = false, isQita = false, isWx = false;
    public static Dialog dialog;
    public static Dialog jiesuanDialog(MyApplication app,final Dialog loading,final Context context,
                                       int showingLocation, final String type,final double yfmoney, final InterfaceBack handler) {
        final Dialog dialog;
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.dialog_numpay, null);
        final TextView et_zfmoney = (TextView) view.findViewById(R.id.shoppay_et_money);
        final TextView tv_yfmoney = (TextView) view.findViewById(R.id.shoppay_tv_yfmoney);
        final TextView tv_jiesuan = (TextView) view.findViewById(R.id.tv_jiesuan);
        final EditText et_password = (EditText) view.findViewById(R.id.vip_et_password);
        final RelativeLayout rl_jiesuan = (RelativeLayout) view.findViewById(R.id.shoppay_rl_jiesuan);
        final RelativeLayout rl_password = (RelativeLayout) view.findViewById(R.id.vip_rl_password);
        final RadioGroup mRadiogroup = (RadioGroup) view.findViewById(R.id.radiogroup);
        final SystemQuanxian sysquanxian=app.getSysquanxian();
       RadioButton rb_isYinlian= (RadioButton) view.findViewById(R.id.rb_yinlian);
        RadioButton rb_money= (RadioButton) view.findViewById(R.id.rb_money);
        RadioButton rb_zhifubao= (RadioButton)view. findViewById(R.id.rb_zhifubao);
        RadioButton rb_wx= (RadioButton)view. findViewById(R.id.rb_wx);
        RadioButton rb_yue= (RadioButton)view. findViewById(R.id.rb_yue);
        RadioButton rb_qita= (RadioButton)view. findViewById(R.id.rb_qita);

        if(sysquanxian.isweixin==0){
            rb_wx.setVisibility(View.GONE);
        }
        if(sysquanxian.iszhifubao==0){
            rb_zhifubao.setVisibility(View.GONE);
        }
        if(sysquanxian.isyinlian==0){
            rb_isYinlian.setVisibility(View.GONE);
        }
        if(sysquanxian.isxianjin==0){
            rb_money.setVisibility(View.GONE);
        }
        if(sysquanxian.isqita==0){
            rb_qita.setVisibility(View.GONE);
        }
        if(sysquanxian.isyue==0){
            rb_yue.setVisibility(View.GONE);
        }
        isMoney = true;
        isYue = false;
        isYinlian = false;
        isWx = false;
        isZhifubao = false;
        isQita = false;
        dialog = new Dialog(context, R.style.DialogNotitle1);
        dialog.setCancelable(true);
        dialog.setCanceledOnTouchOutside(false);
        int screenWidth = ((WindowManager) context
                .getSystemService(Context.WINDOW_SERVICE)).getDefaultDisplay()
                .getWidth();
        dialog.setContentView(view, new LinearLayout.LayoutParams(
                screenWidth - 10, LinearLayout.LayoutParams.WRAP_CONTENT));
        dialog.show();
        mRadiogroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, int i) {
                switch (i) {
                    case R.id.rb_money:
                        isMoney = true;
                        isYue = false;
                        isQita = false;
                        isWx = false;
                        isYinlian = false;
                        isZhifubao = false;
                        break;
                    case R.id.rb_zhifubao:
                        isZhifubao = true;
                        isMoney = false;
                        isYue = false;
                        isQita = false;
                        isWx = false;
                        isYinlian = false;
                        break;
                    case R.id.rb_yinlian:
                        isYinlian = true;
                        isMoney = false;
                        isYue = false;
                        isQita = false;
                        isWx = false;
                        isZhifubao = false;
                        break;
                    case R.id.rb_wx:
                        isWx = true;
                        isMoney = false;
                        isYue = false;
                        isQita = false;
                        isYinlian = false;
                        isZhifubao = false;
                        break;
                    case R.id.rb_qita:
                        isQita = true;
                        isMoney = false;
                        isYue = false;
                        isWx = false;
                        isYinlian = false;
                        isZhifubao = false;
                        break;
                    case R.id.rb_yue:
                        isYue = true;
                        isMoney = false;
                        isQita = false;
                        isWx = false;
                        isYinlian = false;
                        isZhifubao = false;
                        break;
                }
            }
        });
        tv_yfmoney.setText(StringUtil.twoNum(yfmoney + ""));
        et_zfmoney.setText(StringUtil.twoNum(yfmoney + ""));
        rl_jiesuan.setOnClickListener(new NoDoubleClickListener() {
            @Override
            protected void onNoDoubleClick(View view) {
                if (CommonUtils.checkNet(context)) {
                    if (Double.parseDouble(tv_yfmoney.getText().toString()) - Double.parseDouble(et_zfmoney.getText().toString()) < 0) {
                        Toast.makeText(context, "超过应付金额，请检查输入信息",
                                Toast.LENGTH_SHORT).show();
                    } else if (Double.parseDouble(tv_yfmoney.getText().toString()) - Double.parseDouble(et_zfmoney.getText().toString()) > 0) {
                        Toast.makeText(context, "少于应付金额，请检查输入信息",
                                Toast.LENGTH_SHORT).show();
                    }   else if(isYue&&Double.parseDouble(tv_yfmoney.getText().toString())-Double.parseDouble(  PreferenceHelper.readString(context, "shoppay", "MemMoney","0"))>0){
                        Toast.makeText(MyApplication.context, "余额不足",
                                Toast.LENGTH_SHORT).show();
                    }else {
                        if (isYue&&sysquanxian.ispassword==1) {
                            DialogUtil.pwdDialog( context, 1, new InterfaceBack() {
                                @Override
                                public void onResponse(Object response) {
                                        jiesuan(loading, type, handler, dialog, context, response.toString(),DateUtils.getCurrentTime("yyyyMMddHHmmss"));
                                }

                                @Override
                                public void onErrorResponse(Object msg) {

                                }
                            });
                        } else {
                            if(isWx){
                                if(sysquanxian.iswxpay==0){
                                    handler.onResponse("wxpay");
                                    dialog.dismiss();
                                }else {
                                    jiesuan(loading, type, handler, dialog, context,"",DateUtils.getCurrentTime("yyyyMMddHHmmss"));
                                }
                            }else if(isZhifubao){
                                if(sysquanxian.iszfbpay==0){
                                    handler.onResponse("zfbpay");
                                    dialog.dismiss();
                                }else {
                                    jiesuan(loading, type, handler, dialog, context,"",DateUtils.getCurrentTime("yyyyMMddHHmmss"));
                                }
                            }else {
                                jiesuan(loading, type, handler, dialog, context,"",DateUtils.getCurrentTime("yyyyMMddHHmmss"));
                            }
                        }
                    }
                } else {
                    Toast.makeText(context, "请检查网络是否可用",
                            Toast.LENGTH_SHORT).show();
                }
            }
        });

        Window window = dialog.getWindow();
        switch (showingLocation) {
            case 0:
                window.setGravity(Gravity.TOP); // 此处可以设置dialog显示的位置
                break;
            case 1:
                window.setGravity(Gravity.CENTER);
                break;
            case 2:
                window.setGravity(Gravity.BOTTOM);
                break;
            case 3:
                WindowManager.LayoutParams params = window.getAttributes();
                dialog.onWindowAttributesChanged(params);
                params.x = screenWidth - dip2px(context, 100);// 设置x坐标
                params.gravity = Gravity.TOP;
                params.y = dip2px(context, 45);// 设置y坐标
                Log.d("xx", params.y + "");
                window.setGravity(Gravity.TOP);
                window.setAttributes(params);
                break;
            default:
                window.setGravity(Gravity.CENTER);
                break;
        }
        return dialog;
    }

    public static int dip2px(Context context, float dipValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (dipValue * scale + 0.5f);
  }


    public static void jiesuan(final  Dialog loading,String type,final InterfaceBack handle, final Dialog dialog, final Context context, final String pwd,final String orderNum) {
       loading.show();
        if(type.equals("num")) {
            AsyncHttpClient client = new AsyncHttpClient();
            final PersistentCookieStore myCookieStore = new PersistentCookieStore(context);
            client.setCookieStore(myCookieStore);
            final DBAdapter dbAdapter = DBAdapter.getInstance(context);
            List<ShopCar> list = dbAdapter.getListShopCar(PreferenceHelper.readString(context, "shoppay", "account", "123"));
            List<ShopCar> shoplist = new ArrayList<>();
            double yfmoney = 0.0;
            double zfmoney = 0.0;
            int num = 0;
            for (ShopCar numShop : list) {
                if (numShop.count == 0) {
                } else {
                    shoplist.add(numShop);
                    zfmoney = CommonUtils.add(zfmoney, Double.parseDouble(numShop.discountmoney));
                    yfmoney = CommonUtils.add(yfmoney, Double.parseDouble(CommonUtils.multiply(numShop.count + "", numShop.price)));
                    num = num + numShop.count;
                }
            }
            RequestParams params = new RequestParams();
            params.put("MemID", PreferenceHelper.readString(context, "shoppay", "memid", ""));
            params.put("OrderAccount", orderNum);
            params.put("TotalMoney", yfmoney);
            params.put("DiscountMoney", zfmoney);
            params.put("OrderPoint", "");
            if (isMoney) {
                params.put("payType", 0);
            } else if (isWx) {
                params.put("payType", 2);
            } else if (isYinlian) {
                params.put("payType", 1);
            } else if (isYue) {
                params.put("payType", 5);
            } else if (isZhifubao) {
                params.put("payType", 3);
            } else {
                params.put("payType", 4);
            }
            params.put("UserPwd", pwd);
            params.put("GlistCount", shoplist.size());

            for (int i = 0; i < shoplist.size(); i++) {
                params.put("Glist[" + i + "][GoodsID]", shoplist.get(i).goodsid);
                params.put("Glist[" + i + "][number]", shoplist.get(i).count);
                params.put("Glist[" + i + "][GoodsPoint]", "");
                params.put("Glist[" + i + "][Price]", shoplist.get(i).discountmoney);
                params.put("Glist[" + i + "][GoodsPrice]", shoplist.get(i).price);
            }
            LogUtils.d("xxparams", params.toString());
            String url = UrlTools.obtainUrl(context, "?Source=3", "RechargeCount");
            LogUtils.d("xxurl", url);
            client.post(url, params, new AsyncHttpResponseHandler() {
                @Override
                public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                    try {
                        loading.dismiss();
                        LogUtils.d("xxjiesuanS", new String(responseBody, "UTF-8"));
                        JSONObject jso = new JSONObject(new String(responseBody, "UTF-8"));
                        if (jso.getInt("flag") == 1) {
                            dialog.dismiss();
                            Toast.makeText(context, jso.getString("msg"), Toast.LENGTH_LONG).show();
                            JSONObject jsonObject = (JSONObject) jso.getJSONArray("print").get(0);
                            if (jsonObject.getInt("printNumber") == 0) {
                                dbAdapter.deleteShopCar();
                                handle.onResponse("");
                            } else {
                                BluetoothAdapter bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
                                if (bluetoothAdapter.isEnabled()) {
                                    BluetoothUtil.connectBlueTooth(MyApplication.context);
                                    BluetoothUtil.sendData(DayinUtils.dayin(jsonObject.getString("printContent")), jsonObject.getInt("printNumber"));
                                    dbAdapter.deleteShopCar();
                                    handle.onResponse("");
                                } else {
                                    dbAdapter.deleteShopCar();
                                    handle.onResponse("");
                                }
                            }
                        }else{
                            Toast.makeText(context, jso.getString("msg"), Toast.LENGTH_LONG).show();
                        }
                    } catch (Exception e) {
                        loading.dismiss();
                    }
//				printReceipt_BlueTooth(context,xfmoney,yfmoney,jf,et_zfmoney,et_yuemoney,tv_dkmoney,et_jfmoney);
                }

                @Override
                public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                    loading.dismiss();
                    Toast.makeText(context, "结算失败，请重新结算",
                            Toast.LENGTH_SHORT).show();
                }
            });
        }else{
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
            if (isMoney) {
                params.put("payType", 0);
            } else if (isWx) {
                params.put("payType", 2);
            } else if (isYinlian) {
                params.put("payType", 1);
            } else if (isYue) {
                params.put("payType", 5);
            } else if (isZhifubao) {
                params.put("payType", 3);
            } else {
                params.put("payType", 4);
            }
            params.put("UserPwd", pwd);
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
                        loading.dismiss();
                        LogUtils.d("xxjiesuanS", new String(responseBody, "UTF-8"));
                        JSONObject jso = new JSONObject(new String(responseBody, "UTF-8"));
                        if (jso.getInt("flag") == 1) {
                            Toast.makeText(context, jso.getString("msg"), Toast.LENGTH_LONG).show();
                            JSONObject jsonObject = (JSONObject) jso.getJSONArray("print").get(0);
                            if (jsonObject.getInt("printNumber") == 0) {
                                dbAdapter.deleteShopCar();
                                handle.onResponse("");
                            } else {
                                BluetoothAdapter bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
                                if (bluetoothAdapter.isEnabled()) {
                                    BluetoothUtil.connectBlueTooth(MyApplication.context);
                                    BluetoothUtil.sendData(DayinUtils.dayin(jsonObject.getString("printContent")), jsonObject.getInt("printNumber"));
                                    dbAdapter.deleteShopCar();
                                    handle.onResponse("");
                                } else {
                                    dbAdapter.deleteShopCar();
                                    handle.onResponse("");
                                }
                            }
                        }else{
                            Toast.makeText(context, jso.getString("msg"), Toast.LENGTH_LONG).show();
                        }
                    } catch (Exception e) {
                        loading.dismiss();
                    }
//				printReceipt_BlueTooth(context,xfmoney,yfmoney,jf,et_zfmoney,et_yuemoney,tv_dkmoney,et_jfmoney);
                }

                @Override
                public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                    loading.dismiss();
                    Toast.makeText(context, "结算失败，请重新结算",
                            Toast.LENGTH_SHORT).show();
                }
            });

        }
    }
}
