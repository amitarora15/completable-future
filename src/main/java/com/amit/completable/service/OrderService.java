package com.amit.completable.service;

import com.amit.completable.entity.Inventory;
import com.amit.completable.entity.Order;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.concurrent.CompletableFuture;

@Service
public class OrderService {

    @Async("asyncExecutor")
    public CompletableFuture<Order> getOrder(){
        System.out.println(Instant.now() + " : " + Thread.currentThread().getName() + " : Started getting Order");
        try {
            Thread.sleep(4000L);
        } catch (InterruptedException e) {

        }
        Order order = Order.builder().name("My Order").build();
        System.out.println(Instant.now() + " : " + Thread.currentThread().getName() + " : Ended getting Order");
        return CompletableFuture.completedFuture(order);
    }
}
