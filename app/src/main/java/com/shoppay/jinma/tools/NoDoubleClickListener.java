package com.shoppay.jinma.tools;

import android.view.View;

import java.util.Calendar;

/**
 * Created by songxiaotao on 2018/1/31.
 */

public abstract class NoDoubleClickListener implements View.OnClickListener {
    public static final int MIN_CLICK_DELAY_TIME=1500;
    private long lastClickTime=0;
   protected abstract void onNoDoubleClick(View view);
    @Override
    public void onClick(View view) {
        long currentTime= Calendar.getInstance().getTimeInMillis();
        if(currentTime-lastClickTime>MIN_CLICK_DELAY_TIME){
            lastClickTime=currentTime;
            onNoDoubleClick(view);

        }
    }
}
