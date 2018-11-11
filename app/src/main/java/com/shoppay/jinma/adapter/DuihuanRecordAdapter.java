package com.shoppay.jinma.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.shoppay.jinma.R;
import com.shoppay.jinma.bean.DuihuanRecord;
import com.shoppay.jinma.tools.LogUtils;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;

public class DuihuanRecordAdapter extends BaseAdapter {
    private Context context;
    private List<DuihuanRecord> list;
    private LayoutInflater inflater;

    public DuihuanRecordAdapter(Context context, List<DuihuanRecord> list) {
        this.context = context;
        if (list == null) {
            this.list = new ArrayList<DuihuanRecord>();
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
            convertView = inflater.inflate(R.layout.item_duihuanjilu, null);
            vh = new ViewHolder(convertView);
            convertView.setTag(vh);
        } else {
            vh = (ViewHolder) convertView.getTag();
        }
        final DuihuanRecord home = list.get(position);
        LogUtils.d("xxx",home.getMemCard());
        vh.mTvVipcard.setText(home.getMemCard());
        vh.mTvJifen.setText(home.getExchangeAllPoint());
        vh.mTvTime.setText(home.getExchangeTime());
        return convertView;
    }


    class ViewHolder {
        @Bind(R.id.tv_vipcard)
        TextView mTvVipcard;
        @Bind(R.id.tv_num)
        TextView mTvNum;
        @Bind(R.id.tv_jifen)
        TextView mTvJifen;
        @Bind(R.id.tv_time)
        TextView mTvTime;

        ViewHolder(View view) {
            ButterKnife.bind(this, view);
        }
    }
}
