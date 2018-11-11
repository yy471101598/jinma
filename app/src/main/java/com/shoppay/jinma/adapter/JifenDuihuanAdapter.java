package com.shoppay.jinma.adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.shoppay.jinma.R;
import com.shoppay.jinma.bean.JifenDuihuan;
import com.shoppay.jinma.bean.ShopCar;
import com.shoppay.jinma.db.DBAdapter;
import com.shoppay.jinma.tools.PreferenceHelper;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;

public class JifenDuihuanAdapter extends BaseAdapter {
    private Context context;
    private List<JifenDuihuan> list;
    private LayoutInflater inflater;
    private Intent intent;
    private DBAdapter dbAdapter;

    public JifenDuihuanAdapter(Context context, List<JifenDuihuan> list) {
        this.context = context;
        if (list == null) {
            this.list = new ArrayList<JifenDuihuan>();
        } else {
            this.list = list;
        }
        inflater = LayoutInflater.from(context);
        dbAdapter = DBAdapter.getInstance(context);
        intent = new Intent("com.shoppay.wy.jifenduihuan");
    }

    @Override
    public int getCount() {
        // TODO Auto-generated method stub
        return list.size();
    }

    @Override
    public Object getItem(int position) {
        // TODO Auto-generated method stub
        return list.get(position);
    }

    @Override
    public long getItemId(int position) {
        // TODO Auto-generated method stub
        return position;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        // TODO Auto-generated method stub
        final ViewHolder vh;
        convertView = inflater.inflate(R.layout.item_jifenduihuan, null);
        vh = new ViewHolder(convertView);
        convertView.setTag(vh);
        final JifenDuihuan home = list.get(position);
        vh.itemTvShopname.setText(home.GiftName);
        vh.itemTvJifen.setText(home.GiftExchangePoint);
        vh.itemTvKucunnum.setText(home.GiftStockNumber);
        ShopCar dbshop = dbAdapter.getShopCar(home.GiftID);
        if (dbshop == null) {
            vh.itemTvNum.setText("0");
        } else {
            if (dbshop.count != 0) {
                vh.itemIvDel.setVisibility(View.VISIBLE);
                vh.itemTvNum.setVisibility(View.VISIBLE);
                vh.itemTvNum.setText(dbshop.count + "");
            }
        }
        vh.itemIvAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int num = Integer.parseInt(vh.itemTvNum.getText().toString());
                if (num == 0) {
                    vh.itemTvNum.setVisibility(View.VISIBLE);
                    vh.itemIvDel.setVisibility(View.VISIBLE);
                }
                JifenDuihuan shopCar = dbAdapter.getJifenShop(home.GiftID);
                if (shopCar == null) {
                    num = 1;
                    vh.itemTvNum.setText(num + "");
                    insertJifenShopCar(home, num);
                } else {
                    num = num + 1;
                    if (Integer.parseInt(home.GiftStockNumber) < num) {
                        num = num - 1;
                        Toast.makeText(context, "该商品的最大库存量为" + home.GiftStockNumber, Toast.LENGTH_SHORT).show();
                    }
                    vh.itemTvNum.setText(num + "");
                    insertJifenShopCar(home, num);
                }

            }
        });

        vh.itemIvDel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int num = Integer.parseInt(vh.itemTvNum.getText().toString());
                num = num - 1;
                if (num == 0) {
                    PreferenceHelper.write(context, "shoppay", "ischoaseItemjifen", false);
                    vh.itemIvDel.setVisibility(View.GONE);
                    vh.itemTvNum.setVisibility(View.GONE);
                }
                vh.itemTvNum.setText(num + "");
                insertJifenShopCar(home, num);
            }
        });
        return convertView;
    }

    private void insertJifenShopCar(JifenDuihuan shop, int num) {
        //加入购物车
        List<JifenDuihuan> li = new ArrayList<JifenDuihuan>();
        JifenDuihuan shopCar = new JifenDuihuan();
        shopCar.count = num + "";
        shopCar.account=PreferenceHelper.readString(context, "shoppay", "account", "123");
        shopCar.GiftCode = shop.GiftCode;
        shopCar.GiftExchangePoint = shop.GiftExchangePoint;
        shopCar.GiftID = shop.GiftID;
        shopCar.GiftName = shop.GiftName;
        li.add(shopCar);
        dbAdapter.insertJifenShopCar(li);
        context.sendBroadcast(intent);
    }

    class ViewHolder {
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

        ViewHolder(View view) {
            ButterKnife.bind(this, view);
        }
    }
}
