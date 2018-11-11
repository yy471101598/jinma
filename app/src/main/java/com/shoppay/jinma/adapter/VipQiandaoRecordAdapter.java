package com.shoppay.jinma.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.shoppay.jinma.R;
import com.shoppay.jinma.bean.VipQiandaoRecord;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;

public class VipQiandaoRecordAdapter extends BaseAdapter {
    private Context context;
    private List<VipQiandaoRecord> list;
    private LayoutInflater inflater;

    public VipQiandaoRecordAdapter(Context context, List<VipQiandaoRecord> list) {
        this.context = context;
        if (list == null) {
            this.list = new ArrayList<VipQiandaoRecord>();
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
            convertView = inflater.inflate(R.layout.item_vipqiandao, null);
            vh = new ViewHolder(convertView);
            convertView.setTag(vh);
        } else {
            vh = (ViewHolder) convertView.getTag();
        }
        final VipQiandaoRecord home = list.get(position);
        if(position==0){
            vh.tvJifen.setText("获得积分");
            vh.tvVipcard.setText("会员卡号");
            vh.tvTime.setText("签到时间");
        }else {
            vh.tvJifen.setText(home.SignPoint);
            vh.tvVipcard.setText(home.MemCard);
            vh.tvTime.setText(home.SignTime);
        }
        return convertView;
    }

    class ViewHolder {
        @Bind(R.id.tv_vipcard)
        TextView tvVipcard;
        @Bind(R.id.tv_jifen)
        TextView tvJifen;
        @Bind(R.id.tv_time)
        TextView tvTime;

        ViewHolder(View view) {
            ButterKnife.bind(this, view);
        }
    }
}
