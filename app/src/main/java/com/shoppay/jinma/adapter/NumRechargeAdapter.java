package com.shoppay.jinma.adapter;

import android.app.Dialog;
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
import com.shoppay.jinma.bean.Shop;
import com.shoppay.jinma.bean.ShopCar;
import com.shoppay.jinma.db.DBAdapter;
import com.shoppay.jinma.http.InterfaceBack;
import com.shoppay.jinma.tools.DialogUtil;
import com.shoppay.jinma.tools.PreferenceHelper;
import com.shoppay.jinma.tools.VipRechargeNumDialog;

import java.util.ArrayList;
import java.util.List;


public class NumRechargeAdapter extends BaseAdapter {
	private Context context;
	private List<Shop> list;
	private LayoutInflater inflater;
	private Intent intent;
	private Dialog dialog;
	private DBAdapter dbAdapter;
	public NumRechargeAdapter(Context context, List<Shop> list) {
		this.context = context;
		if (list == null) {
			this.list = new ArrayList<Shop>();
		} else {
			this.list = list;
		}
		inflater = LayoutInflater.from(context);
		intent=new Intent("com.shoppay.wy.numberchange");
		dialog= DialogUtil.loadingDialog(context,1);
		dbAdapter=DBAdapter.getInstance(context);
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
		final ViewHolder	vh;
			convertView = inflater.inflate(R.layout.item_balanceright, null);
	        vh = new ViewHolder();
			vh.tv_name = (TextView) convertView
					.findViewById(R.id.item_tv_shopname);
			vh.tv_num = (TextView) convertView
					.findViewById(R.id.item_tv_num);
			vh.tv_money = (TextView) convertView
					.findViewById(R.id.item_tv_money);
			vh.img_add= (ImageView) convertView.findViewById(R.id.item_iv_add);
			vh.img_del= (ImageView) convertView.findViewById(R.id.item_iv_del);
			convertView.setTag(vh);
		final Shop home = list.get(position);
		vh.tv_name.setText(home.GoodsName);
		vh.tv_money.setText(home.GoodsPrice);
		ShopCar dbshop=dbAdapter.getShopCar(home.GoodsID);
		if(dbshop==null){

		}else{
			if(dbshop.count!=0) {
				vh.tv_num.setVisibility(View.VISIBLE);
				vh.img_del.setVisibility(View.VISIBLE);
				vh.tv_num.setText(dbshop.count+"");
			}
		}

		vh.img_add.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				if(PreferenceHelper.readString(context, "shoppay", "memid","").equals("")){
					Toast.makeText(context,"未查询到会员",Toast.LENGTH_SHORT).show();
				}else{

					VipRechargeNumDialog.vipnumDialog(context, 1, list.get(position), new InterfaceBack() {
						@Override
						public void onResponse(Object response) {

							Shop sp = (Shop) response;
							vh.img_del.setVisibility(View.VISIBLE);
							vh.tv_num.setVisibility(View.VISIBLE);
							vh.tv_num.setText(sp.allnum);
							insertShopCar(sp);
						}

						@Override
						public void onErrorResponse(Object msg) {

						}
					});
				}
					}
		});
		vh.img_del.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				VipRechargeNumDialog.vipnumDialog(context, 1,list.get(position), new InterfaceBack() {
					@Override
					public void onResponse(Object response) {
						Shop sp=(Shop) response;
						vh.img_del.setVisibility(View.VISIBLE);
						vh.tv_num.setVisibility(View.VISIBLE);
						vh.tv_num.setText(sp.allnum);
						insertShopCar(sp);
					}

					@Override
					public void onErrorResponse(Object msg) {

					}
				});
			}
		});
		return convertView;
	}

	class ViewHolder {
		TextView tv_name,tv_money,tv_num;
		ImageView img_add,img_del;
	}


	private void insertShopCar(Shop shop){
		//加入购物车
		List<ShopCar> li=new ArrayList<ShopCar>();
		ShopCar shopCar=new ShopCar();
		shopCar.account=PreferenceHelper.readString(context,"shoppay","account","123");
		shopCar.count=Integer.parseInt(shop.allnum);
		shopCar.goodsid=shop.GoodsID;
		shopCar.goodsclassid=shop.GoodsClassID;
		shopCar.goodspoint=0;
		shopCar.goodsType=shop.GoodsType;
		shopCar.price=shop.GoodsPrice;
		shopCar.shopname=shop.GoodsName;
		shopCar.discountmoney=shop.allmoney;
		li.add(shopCar);
		dbAdapter.insertShopCar(li);
//		intent.putExtra("shopclass",shop.GoodsClassID);
//		intent.putExtra("num",num+"");
		context.sendBroadcast(intent);
	}
}
