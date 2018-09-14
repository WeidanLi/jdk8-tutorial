package cn.liweidan.mall.discount;

import cn.liweidan.mall.dbo.PayOrderDo;
import cn.liweidan.mall.dbo.SaleDo;

import java.util.UUID;
import java.util.function.Function;

/**
 * 折扣减价计算函数
 * @author liweidan
 */
@FunctionalInterface
public interface DiscountFunction extends Function<Long, Long> {
    default PayOrderDo getPayOrderDo(SaleDo saleDo) {
        Long newPrice = apply(saleDo.getSalePrice());
        return new PayOrderDo(UUID.randomUUID().toString(),
                saleDo.getId(), newPrice, saleDo.getSalePrice() - newPrice);
    }
}
