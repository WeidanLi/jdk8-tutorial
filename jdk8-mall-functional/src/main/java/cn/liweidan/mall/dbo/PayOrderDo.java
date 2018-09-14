package cn.liweidan.mall.dbo;

/**
 * 支付单类
 * @author liweidan
 * @date   2017.12.28 下午2:09
 * @email  toweidan@126.com
 */
public class PayOrderDo {

    /** 数据库唯一标志 */
    private String id;

    /** 订单id */
    private String saleId;

    /** 支付金额 */
    private long payPrice;

    /** 折扣的价格 */
    private long price;

    public PayOrderDo(String id, String saleId, long payPrice, long price) {
        this.id = id;
        this.saleId = saleId;
        this.payPrice = payPrice;
        this.price = price;
    }

    public String getId() {
        return id;
    }

    public String getSaleId() {
        return saleId;
    }

    public long getPayPrice() {
        return payPrice;
    }

    public long getPrice() {
        return price;
    }

    @Override
    public String toString() {
        return "PayOrderDo{" +
                "id='" + id + '\'' +
                ", saleId='" + saleId + '\'' +
                ", payPrice=" + payPrice +
                ", price=" + price +
                '}';
    }
}
