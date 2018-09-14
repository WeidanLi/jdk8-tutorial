package cn.liweidan.mall.discount.type;

import cn.liweidan.mall.dbo.PayOrderDo;
import cn.liweidan.mall.dbo.SaleDo;

/**
 * 折扣接口
 * @author liweidan
 * @date   2017.12.28 下午3:15
 * @email  toweidan@126.com
 */
public interface DiscountType {
    PayOrderDo discount(SaleDo saleDo);
}
