package cn.liweidan.myfuture.completion;

import static com.sun.tools.javac.util.Constants.format;

/**
 * <p>Desciption:折扣类，包含折扣方式枚举</p>
 * CreateTime : 2017/8/5 下午4:19
 * Author : Weidan
 * Version : V1.0
 */
public class Discount {

    /**
     * 返回计算折扣后的价格
     * @param price
     * @param discountCode
     * @return
     */
    public static String apply(double price, Code discountCode) {
        delay();// 模拟请求折扣的延迟
        return format(price * (100 - discountCode.per) / 100);
    }

    private static void delay() {
        try {
            Thread.sleep(1000L);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    /**
     * 应用一个Quote计算的类
     * @param quote
     * @return
     */
    public static String applyDiscount(Quote quote){
        return quote.getShopName() + " price is " +
                Discount.apply(quote.getPrice(), quote.getDiscountCode());
    }

    public enum Code {
        NONE(0), SILVER(5), GOLD(10), PLATINUM(15), DIAMOND(20);

        /** 折扣百分率 */
        private final int per;

        Code(int per){
            this.per = per;
        }

    }

}
