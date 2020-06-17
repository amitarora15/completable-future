package com.amit.completable.service;

import com.amit.completable.entity.Inventory;
import com.amit.completable.entity.Order;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

@Service
@RequiredArgsConstructor
public class DataService {

    private final OrderService orderService;

    private final InventoryService inventoryService;

    public void executeOrderFromInventory(){
        System.out.println(Instant.now() + " : " + Thread.currentThread().getName() + " : Started getting data");
        CompletableFuture<Order> orderCompletableFuture = orderService.getOrder();
        CompletableFuture<Inventory> inventoryCompletableFuture = inventoryService.getInventory();
        CompletableFuture.allOf(orderCompletableFuture,inventoryCompletableFuture).join();
        try {
            System.out.println(Instant.now() + " : " + Thread.currentThread().getName() + " : Order information " + orderCompletableFuture.get());
            System.out.println(Instant.now() + " : " + Thread.currentThread().getName() + " : Inventory information " + inventoryCompletableFuture.get());
        } catch (Exception e) {
            e.printStackTrace();
        }
        System.out.println(Instant.now() + " : " + Thread.currentThread().getName() + " : Ended getting data");
    }

}
