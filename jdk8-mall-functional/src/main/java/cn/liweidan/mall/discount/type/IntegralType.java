package cn.liweidan.mall.discount.type;

import cn.liweidan.mall.dbo.PayOrderDo;
import cn.liweidan.mall.dbo.SaleDo;
import cn.liweidan.mall.discount.DiscountFunction;

/**
 * 积分折扣
 * @author liweidan
 * @date   2017.12.28 下午3:16
 * @email  toweidan@126.com
 */
public class IntegralType implements DiscountType {

    /** 积分 */
    private long integral;

    public IntegralType(long integral) {
        this.integral = integral;
    }

    @Override
    public PayOrderDo discount(SaleDo saleDo) {
        System.out.println("积分为：" + integral);
        if (integral > 100) {
            DiscountFunction function = (Long oldPrice) -> oldPrice - 100 + 10;
            return function.getPayOrderDo(saleDo);
        } else if (integral > 50) {
            DiscountFunction function = (Long oldPrice) -> oldPrice - 50 + 5;
            return function.getPayOrderDo(saleDo);
        } else {
            DiscountFunction function = (Long oldPrice) -> oldPrice;
            return function.getPayOrderDo(saleDo);
        }
    }
}
