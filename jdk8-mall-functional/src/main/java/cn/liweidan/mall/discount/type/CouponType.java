package cn.liweidan.mall.discount.type;

import cn.liweidan.mall.dbo.PayOrderDo;
import cn.liweidan.mall.dbo.SaleDo;
import cn.liweidan.mall.discount.DiscountFunction;

/**
 * 满减折扣
 * @author liweidan
 * @date   2017.12.28 下午3:25
 * @email  toweidan@126.com
 */
public class CouponType implements  DiscountType{

    /** 满多少钱 */
    private Long fillPrice;

    /** 减多少钱 */
    private Long discountPrice;

    public CouponType(Long fillPrice, Long discountPrice) {
        this.fillPrice = fillPrice;
        this.discountPrice = discountPrice;
    }

    @Override
    public PayOrderDo discount(SaleDo saleDo) {
        System.out.println("满" + fillPrice + "减" + discountPrice);
        DiscountFunction function = (Long oldPrice) -> oldPrice >= fillPrice ? oldPrice - discountPrice : oldPrice;
        PayOrderDo payOrderDo = function.getPayOrderDo(saleDo);
        return payOrderDo;
    }
}
