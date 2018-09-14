package cn.liweidan.mall;

import cn.liweidan.mall.dbo.SaleDo;
import cn.liweidan.mall.service.SaleService;

import java.util.UUID;

/**
 * 启动类
 * @author liweidan
 * @date   2017.12.28 下午4:10
 * @email  toweidan@126.com
 */
public class Application {
    private static SaleService saleService = new SaleService();

    public static void main(String[] args) {
        SaleDo saleDo = new SaleDo(UUID.randomUUID().toString(),
                UUID.randomUUID().toString(), (long) (Math.random() * 1000));
        saleService.pay(saleDo);
    }
}
