package cn.liweidan.mall.discount;

import cn.liweidan.mall.dbo.PayOrderDo;
import cn.liweidan.mall.dbo.SaleDo;
import cn.liweidan.mall.discount.type.CouponType;
import cn.liweidan.mall.discount.type.DiscountType;
import cn.liweidan.mall.discount.type.IntegralType;

import java.util.UUID;

/**
 * 折扣服务
 * @author liweidan
 * @date   2017.12.28 下午4:05
 * @email  toweidan@126.com
 */
public class DiscountService {

    /**
     * 根据传入的不同类型进行不同的优惠
     * @param saleDo
     * @param discountType 1表示优惠券 2表示积分 其他无优惠
     * @return
     */
    public PayOrderDo discount(SaleDo saleDo, int discountType) {
        if (discountType == 1) {
            System.out.println("使用优惠券进行优惠");
            CouponType couponType = new CouponType(100L, 50L);
            return discount(saleDo, couponType);
        } else if (discountType == 2) {
            System.out.println("使用积分进行优惠");
            IntegralType integralType = new IntegralType((long) (Math.random() * 200));
            return discount(saleDo, integralType);
        } else {
            System.out.println("原价支付");
            return new PayOrderDo(UUID.randomUUID().toString(), saleDo.getId(), saleDo.getSalePrice(), 0);
        }
    }

    private PayOrderDo discount(SaleDo saleDo, DiscountType discountType) {
        return discountType.discount(saleDo);
    }

}
