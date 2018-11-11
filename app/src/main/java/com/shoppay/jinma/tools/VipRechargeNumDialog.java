package com.shoppay.jinma.tools;

import android.app.Dialog;
import android.content.Context;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.shoppay.jinma.R;
import com.shoppay.jinma.bean.Shop;
import com.shoppay.jinma.http.InterfaceBack;

/**
 * Created by songxiaotao on 2017/8/3.
 */

public class VipRechargeNumDialog {

    public static Dialog vipnumDialog(final Context context,
                                      int showingLocation, final Shop shop, final InterfaceBack back) {
        final Dialog dialog;
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.dialog_vipnum, null);
        TextView tv_name= (TextView) view.findViewById(R.id.tv_name);
        final EditText et_num= (EditText) view.findViewById(R.id.et_num);
        final EditText et_money= (EditText) view.findViewById(R.id.et_money);
        RelativeLayout rl_confirm= (RelativeLayout) view.findViewById(R.id.rl_confirm);
        dialog = new Dialog(context, R.style.DialogNotitle1);
        dialog.setCancelable(true);
        dialog.setCanceledOnTouchOutside(false);
        int screenWidth = ((WindowManager) context
                .getSystemService(Context.WINDOW_SERVICE)).getDefaultDisplay()
                .getWidth();
        dialog.setContentView(view, new LinearLayout.LayoutParams(
                screenWidth-80, LinearLayout.LayoutParams.WRAP_CONTENT));
        dialog.show();

        tv_name.setText(shop.GoodsName);
        if(null!=shop.allmoney){
            et_money.setText(shop.allmoney);
        }
        if(null!=shop.allnum){
            et_num.setText(shop.allnum);
        }
        rl_confirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(et_money.getText().toString()==null||et_money.getText().toString().equals("")){
                    shop.allmoney="0";
                }else{
                    shop.allmoney=et_money.getText().toString();
                }
                if(et_num.getText().toString()==null||et_num.getText().toString().equals("")){
                    shop.allnum="0";
                }else{
                    shop.allnum=et_num.getText().toString();
                }
                         back.onResponse(shop);
                         dialog.dismiss();
            }
        });
        et_num.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                String editString = editable.toString();

                //延迟800ms，如果不再输入字符，则执行该线程的run方法
                if(!editString.equals("")){
                    et_money.setText(CommonUtils.multiply(editString,shop.GoodsPrice));
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
            default:
                window.setGravity(Gravity.CENTER);
                break;
        }
        return dialog;
    }
}
