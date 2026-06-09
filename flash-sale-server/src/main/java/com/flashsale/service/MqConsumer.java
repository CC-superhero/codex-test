package com.flashsale.service;

import com.flashsale.config.RabbitMQConfig;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

@Component
public class MqConsumer {

    @Resource
    private FlashSaleService flashSaleService;

    @RabbitListener(queues = RabbitMQConfig.FLASH_ORDER_QUEUE)
    public void onFlashOrder(FlashSaleService.FlashOrderMessage message) {
        flashSaleService.processFlashOrder(message);
    }
}
