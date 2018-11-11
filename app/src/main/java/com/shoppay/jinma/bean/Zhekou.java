package com.shoppay.jinma.bean;

/**
 * Created by songxiaotao on 2017/7/4.
 */

public class Zhekou {
    public String GoodsID;//=商品ID
    public String GoodsCode;//=商品编码
    public String GoodsType;//=商品类型 0 服务商品(无库存) 1 普通商品(计库存)
    public String Number;//=现有库存
    public String GoodsPrice;//=商品标准售价
    public String ClassDiscountPercent;//=当前消费会员此商品折扣比例
    public String DiscountPrice;//=折后价格标记C
    public String GoodsPoint;//=商品积分
    public String GoodsClassID;//=商品积分
}
