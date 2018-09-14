package cn.liweidan.myfuture.streamm;

/**
 * <p>Desciption:实现折扣服务</p>
 * CreateTime : 2017/8/5 下午4:26
 * Author : Weidan
 * Version : V1.0
 */
public class Quote {

    private final String shopName;
    private final double price;
    private final Discount.Code discountCode;

    public Quote(String shopName, double price, Discount.Code discountCode) {
        this.shopName = shopName;
        this.price = price;
        this.discountCode = discountCode;
    }

    /**
     * 解析商店返回的字符串，
     * @param s 字符串
     * @return 该类对象
     */
    public static Quote parse(String s){
        String[] split = s.split(":");
        String shopName = split[0];
        double price = Double.parseDouble(split[1]);
        Discount.Code code = Discount.Code.valueOf(split[2]);
        return new Quote(shopName, price, code);
    }


    public String getShopName() {
        return shopName;
    }

    public double getPrice() {
        return price;
    }

    public Discount.Code getDiscountCode() {
        return discountCode;
    }
}
