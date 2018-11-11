package com.shoppay.jinma.tools;

import android.util.Log;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by songxiaotao on 2018/1/18.
 */

public class DayinUtils {
    public static byte[] dayin(String msg){
        LogUtils.d("xx",msg);
        try
        {
            byte[] next2Line = ESCUtil.nextLine(2);
            byte[] xiahuaxian = "------------------------------".getBytes("gb2312");

            byte[] boldOn = ESCUtil.boldOn();
            byte[] fontSize2Big = ESCUtil.fontSizeSetBig(3);
            byte[] center = ESCUtil.alignCenter();
            byte[] Focus = "网 507".getBytes("gb2312");
            byte[] boldOff = ESCUtil.boldOff();
            byte[] fontSize2Small = ESCUtil.fontSizeSetSmall(3);
            byte[] left = ESCUtil.alignLeft();
            boldOn = ESCUtil.boldOn();
            byte[] fontSize1Big = ESCUtil.fontSizeSetBig(2);
            boldOff = ESCUtil.boldOff();
            byte[] fontSize1Small = ESCUtil.fontSizeSetSmall(2);
            next2Line = ESCUtil.nextLine(2);
            byte[] nextLine = ESCUtil.nextLine(1);
            nextLine = ESCUtil.nextLine(1);
            byte[] next4Line = ESCUtil.nextLine(4);
            byte[] breakPartial = ESCUtil.feedPaperCutPartial();


            List<byte[]> bytesList = new ArrayList<>();
//            如果使用"."、"|"、"^"等字符做分隔符时，要写成s3.split("\\^")的格式
            for(String s:msg.split("\\|")){
                byte[] dayin=s.getBytes("gb2312");
                byte[][] mm ={ nextLine, left,dayin};
                byte[] headerBytes =ESCUtil. byteMerger(mm);
                bytesList.add(headerBytes);
            }


            return MergeLinearArraysUtil.mergeLinearArrays(bytesList);

            //            bluetoothUtil.send(MergeLinearArraysUtil.mergeLinearArrays(bytesList));

        }
        catch (UnsupportedEncodingException e)
        {
            e.printStackTrace();
            Log.d("xx","UnsupportedEncodingException");
        }
        return null;
    }
}
