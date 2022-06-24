package com.jiuzhang.seckill.service;

import com.alibaba.fastjson.JSON;
import com.jiuzhang.seckill.db.dao.OrderDao;
import com.jiuzhang.seckill.db.dao.SeckillActivityDao;
import com.jiuzhang.seckill.db.po.Order;
import com.jiuzhang.seckill.db.po.SeckillActivity;
import com.jiuzhang.seckill.mq.RocketMQService;
import com.jiuzhang.seckill.util.RedisService;
import com.jiuzhang.seckill.util.SnowFlake;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.Date;

@Service
@Slf4j
public class SeckillActivityService {

    @Resource
    private RedisService service;

    @Resource
    private SeckillActivityDao seckillActivityDao;

    @Resource
    private RocketMQService rocketMQService;

    @Resource
    private OrderDao orderDao;

    private SnowFlake snowFlake = new SnowFlake(1, 1);

    public boolean seckillStockValidator(long activityId) {
        String key = "stock:" + activityId;
        return service.stockDeductValidator(key);
    }

    public Order createOrder(long seckillActivityId, long userId) throws Exception {
        SeckillActivity activity = seckillActivityDao.querySeckillActivityById(seckillActivityId);
        Order order = new Order();

        // 采用雪花算法生成订单ID
        order.setOrderNo(String.valueOf(snowFlake.nextId()));
        order.setSeckillActivityId(activity.getId());
        order.setUserId(userId);
        order.setOrderAmount(activity.getSeckillPrice().longValue());

        // 发送创建订单消息
        rocketMQService.sendMessage("seckill_order", JSON.toJSONString(order));


        // 发送订单付款状态校验信息
        rocketMQService.sendDelayMessage("pay_check", JSON.toJSONString(order), 3);

        return order;
    }

    public void payOrderProcess(String orderNo) throws Exception {
        log.info("完成支付订单，订单号：" + orderNo);
        Order order = orderDao.queryOrder(orderNo);

        // 1. 判断订单是否存在
        // 2. 判断订单是否为未支付状态
        if (order == null) {
            log.error("订单号对应的订单不存在：", orderNo);
            return;
        } else if (order.getOrderStatus() != 1) {
            log.error("订单状态无效：" + orderNo);
            return;
        }

        // 2. 订单支付完成
        order.setPayTime(new Date());
            // 0 没有可用库存 无效订单 1 已经创建等待支付 2 完成支付
        order.setOrderStatus(2);
        orderDao.updateOrder(order);


        // 3. 发送订单付款成功消息
        rocketMQService.sendMessage("pay_done", JSON.toJSONString(order));


//        Order order = orderDao.queryOrder(orderNo);
//        boolean deductStockResult = seckillActivityDao.deductStock(order.getSeckillActivityId());
//        if (deductStockResult) {
//            if (deductStockResult) {
//                order.setPayTime(new Date());
//                // 0 没有可用库存 无效订单
//                // 1 已经创建等待支付
//                // 2 完成支付
//                order.setOrderStatus(2);
//                orderDao.updateOrder(order);
//            }
//        }

    }
}
