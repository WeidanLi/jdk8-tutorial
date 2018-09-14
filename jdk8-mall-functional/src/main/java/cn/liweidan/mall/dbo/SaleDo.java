package cn.liweidan.mall.dbo;

/**
 * 订单类
 * @author liweidan
 * @date   2017.12.28 下午2:08
 * @email  toweidan@126.com
 */
public class SaleDo {

    /** 数据库唯一标志 */
    private String id;

    /** 订单编号 */
    private String saleNo;

    /** 订单的金额 */
    private long salePrice;

    /**
     * 构建一个订单的时候一定需要这三个数值
     * @param id
     * @param saleNo
     * @param salePrice
     */
    public SaleDo(String id, String saleNo, long salePrice) {
        this.id = id;
        this.saleNo = saleNo;
        this.salePrice = salePrice;
    }

    /** 构建完成以后，只能获取其中的数值不能做修改 */

    public String getId() {
        return id;
    }

    public String getSaleNo() {
        return saleNo;
    }

    public long getSalePrice() {
        return salePrice;
    }

    @Override
    public String toString() {
        return "SaleDo{" +
                "id='" + id + '\'' +
                ", saleNo='" + saleNo + '\'' +
                ", salePrice=" + salePrice +
                '}';
    }
}
