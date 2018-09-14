package cn.liweidan.myfuture.completion;

import java.util.Random;

/**
 * <p>Desciption:参与折扣商店类</p>
 * CreateTime : 2017/8/5 下午4:22
 * Author : Weidan
 * Version : V1.1
 */
public class Shop {


    private String shopName;

    public Shop(String shopName) {
        this.shopName = shopName;
    }

    public Shop() {
    }

    /**
     * 获取产品价格
     * @param prodName 产品名
     * @return
     */
    public String getPrice(String prodName){
        double price = caculatePrice(prodName);
        Discount.Code code = Discount.Code.values()[new Random().nextInt(Discount.Code.values().length)];
        return String.format("%s:%.2f:%s", shopName, price, code);
    }

    /**
     * 模拟网络延迟
     */
    private static void delay(){
        int delay = new Random().nextInt(2000) + 500;
        try {
            Thread.sleep(delay);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * 计算产品价格
     * @param prodName
     * @return
     */
    private double caculatePrice(String prodName){
        delay();
        return new Random().nextDouble() * prodName.charAt(0) + prodName.charAt(1);
    }

    public String getShopName() {
        return shopName;
    }

    public void setShopName(String shopName) {
        this.shopName = shopName;
    }

}
