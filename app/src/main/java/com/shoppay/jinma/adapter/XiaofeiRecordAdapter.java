package com.shoppay.jinma.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.shoppay.jinma.R;
import com.shoppay.jinma.bean.XiaofeiRecord;
import com.shoppay.jinma.tools.LogUtils;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;

public class XiaofeiRecordAdapter extends BaseAdapter {
    private Context context;
    private List<XiaofeiRecord> list;
    private LayoutInflater inflater;

    public XiaofeiRecordAdapter(Context context, List<XiaofeiRecord> list) {
        this.context = context;
        if (list == null) {
            this.list = new ArrayList<XiaofeiRecord>();
        } else {
            this.list = list;
        }
        inflater = LayoutInflater.from(context);
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
        ViewHolder vh = null;
        if (convertView == null) {
            convertView = inflater.inflate(R.layout.item_xiaofeijilu, null);
            vh = new ViewHolder(convertView);
            convertView.setTag(vh);
        } else {
            vh = (ViewHolder) convertView.getTag();
        }
        final XiaofeiRecord home = list.get(position);
        LogUtils.d("xxx",home.getMemCard());
         vh.mTvCode.setText(home.getOrderAccount());
        vh.mTvVipcard.setText(home.getMemCard());
        vh.mTvVipname.setText(home.getMemName());
        vh.mTvZhmoney.setText(home.getOrderDiscountMoney());
//        OrderStatus=订单状态 1:完成 2：撤销 3：挂单
//        String orderstate="";
//        switch (home.getor)
//        vh.mTvOrderstate.setText(orderstate);
//        OrderType=订单类型 （0:会员登记1:会员充值2:充值撤销3:积分变动4:积分抵现5:积分提现
// 6:积分提成7:商品消费8:商品退货9:快速消费10:快消撤单11:会员充次12:充次撤销13:会员充时
// 14:充时撤销15:积分兑换16:会员签到17:积分转盘）

        vh.mTvOrderstyle.setText(home.getOrderTypeTxt());
        vh.mTvDetail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });
        return convertView;
    }


    class ViewHolder {
        @Bind(R.id.tv_code)
        TextView mTvCode;
        @Bind(R.id.tv_vipcard)
        TextView mTvVipcard;
        @Bind(R.id.tv_vipname)
        TextView mTvVipname;
        @Bind(R.id.tv_orderstate)
        TextView mTvOrderstate;
        @Bind(R.id.tv_orderstyle)
        TextView mTvOrderstyle;
        @Bind(R.id.tv_zzzz)
        TextView mTvZzzz;
        @Bind(R.id.vvvvvv)
        View mVvvvvv;
        @Bind(R.id.tv_zhmoney)
        TextView mTvZhmoney;
        @Bind(R.id.tv_detail)
        TextView mTvDetail;

        ViewHolder(View view) {
            ButterKnife.bind(this, view);
        }
    }
}
