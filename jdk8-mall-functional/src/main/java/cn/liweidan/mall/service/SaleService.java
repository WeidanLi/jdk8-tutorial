package cn.liweidan.mall.service;

import cn.liweidan.mall.dbo.PayOrderDo;
import cn.liweidan.mall.dbo.SaleDo;
import cn.liweidan.mall.discount.DiscountService;

/**
 * 订单业务层
 * @author liweidan
 * @date   2017.12.28 下午2:17
 * @email  toweidan@126.com
 */
public class SaleService {

    private DiscountService discountService = new DiscountService();

    public void pay(SaleDo saleDo) {
        System.out.println("saleDo: " + saleDo);
        int type = (int) (Math.random()*3);
        PayOrderDo payOrder = discountService.discount(saleDo, type);
        System.out.println("payOrder: " + payOrder);
    }

}
