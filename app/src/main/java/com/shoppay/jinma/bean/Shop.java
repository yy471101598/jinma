package com.shoppay.jinma.bean;

import java.io.Serializable;

/**
 * Created by songxiaotao on 2017/7/4.
 */

public class Shop implements Serializable{
    public String GoodsID;//=商品ID
    public String GoodsClassID;//=商品所属分类ID
    public String GoodsCode;//=商品编码
    public String GoodsType;//=商品类型 0 服务商品(无库存) 1 普通商品(计库存)
    public String Number;//=现有库存
    public String GoodsPrice;//商品标准售价
    public String GoodsName;
    public String allnum;
    public String allmoney;

}
