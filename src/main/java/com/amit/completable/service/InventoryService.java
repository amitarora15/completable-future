package com.amit.completable.service;

import com.amit.completable.entity.Inventory;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.concurrent.CompletableFuture;

@Service
public class InventoryService {

    @Async("asyncExecutor")
    public CompletableFuture<Inventory> getInventory(){
        System.out.println(Instant.now() + " : " + Thread.currentThread().getName() + " : Started getting Inventory");
        try {
            Thread.sleep(3000L);
        } catch (InterruptedException e) {

        }
        Inventory inv = Inventory.builder().name("Amazon").build();
        System.out.println(Instant.now() + " : " + Thread.currentThread().getName() + " : Ended getting Inventory");
        return CompletableFuture.completedFuture(inv);
    }

}
