package com.amit.completable.service;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.stream.Collectors;

@Service
public class FutureService {

    public static final long SLEEP_TIME = 2000;

    private Executor executor;

    public FutureService() {
        executor = Executors.newFixedThreadPool(3);
    }

    public Future getCompletableFuture(boolean needToCancel, boolean withException) {
        CompletableFuture<String> future = new CompletableFuture<>();
        new Thread(() -> {
            try {
                Thread.sleep(SLEEP_TIME);
                System.out.println("Completable future is going to sleep for some time");
            } catch (Exception e) {

            }
            if (!withException) {
                if (needToCancel)
                    future.cancel(false);
                else
                    future.complete("Hello");
            } else {
                future.completeExceptionally(new RuntimeException("Finished with Exception"));
            }
        }).start();
        return future;
    }

    public void completableFutureRunAsync() {
        CompletableFuture.runAsync(() -> System.out.println(Thread.currentThread().getName() + " : Thread "));
    }

    public CompletableFuture<String> completeFutureSupplyAsync() {
        CompletableFuture<String> future = CompletableFuture.supplyAsync(() -> {
            System.out.println(Thread.currentThread().getName() + " : In Supply Async task ");
            return "Hello";
        });
        return future;
    }

    public CompletableFuture<String> completableFutureSupplyAsyncThenApply() {
        CompletableFuture<String> future = CompletableFuture.supplyAsync(() -> {
            System.out.println(Thread.currentThread().getName() + " : In Supply Async task ");
            return "Hello";
        }).thenApply((value) -> {
            System.out.println(Thread.currentThread().getName() + " : In Then Apply task ");
            return value + " Amit";
        }).thenApply((value) -> {
            System.out.println(Thread.currentThread().getName() + " : In Then Apply task ");
            return value + ", welcome !!!";
        });
        return future;
    }

    public CompletableFuture<String> completableFutureSupplyAsyncThenApplyAsync() {
        CompletableFuture<String> future = CompletableFuture.supplyAsync(() -> {
            System.out.println(Thread.currentThread().getName() + " : In Supply Async task ");
            return "Hello";
        }, executor).thenApplyAsync((value) -> {
            System.out.println(Thread.currentThread().getName() + " : In Then Apply task ");
            return value + " Amit";
        }, executor).thenApplyAsync((value) -> {
            System.out.println(Thread.currentThread().getName() + " : In Then Apply task ");
            return value + ", welcome !!!";
        }, executor);
        return future;
    }

    public CompletableFuture<Void> completableFutureSupplyAsyncThenAcceptAndRunAsync() {
        CompletableFuture<Void> future = CompletableFuture.supplyAsync(() -> {
            System.out.println(Thread.currentThread().getName() + " : In Supply Async task ");
            return "Hello";
        }, executor).thenAcceptAsync((value) -> {
            System.out.println(Thread.currentThread().getName() + " : In Then Accept1 task " + value);
        }, executor).thenAcceptAsync((value) -> {
            System.out.println(Thread.currentThread().getName() + " : In Then Accept2 task " + value);
        }, executor).thenRunAsync(() -> {
            System.out.println(Thread.currentThread().getName() + " : In Then Run task ");
        }, executor);
        return future;
    }

    private CompletableFuture<User> getUser(Long id) {
        CompletableFuture<User> user = CompletableFuture.supplyAsync(() -> {
            System.out.println(Thread.currentThread().getName() + ": Getting user for id :" + id);
            return new User(id);
        }, executor);
        return user;
    }

    private CompletableFuture<Product> getProduct(Long userId) {
        CompletableFuture<Product> completableFuture = CompletableFuture.supplyAsync(() -> {
            System.out.println(Thread.currentThread().getName() + ": Getting product for user - id :" + userId);
            return new Product("Test", userId);
        }, executor);
        return completableFuture;
    }

    private CompletableFuture<Product> getProduct(Long userId, Long price, Long delay) {
        CompletableFuture<Product> completableFuture = CompletableFuture.supplyAsync(() -> {
            System.out.println(Thread.currentThread().getName() + ": Getting product for user - id :" + userId);
            Product pr = new Product("Test", userId);
            pr.setPrice(price);
            pr.setDelay(delay);
            try {
                Thread.sleep(delay);
            } catch (Exception e) {

            }
            return pr;
        }, executor);
        return completableFuture;
    }

    public CompletableFuture<CompletableFuture<Product>> getProductByUserUsingThenApply(Long userId) {
        return getUser(userId).thenApplyAsync((user) -> {
            System.out.println(Thread.currentThread().getName() + ": Applying product by user");
            return getProduct(user.getId());
        }, executor);
    }

    public CompletableFuture<Product> getProductByUserUsingCompose(Long userId) {
        return getUser(userId).thenComposeAsync((user) -> {
            System.out.println(Thread.currentThread().getName() + ": Composing product by user");
            return getProduct(user.getId());
        }, executor);
    }

    public CompletableFuture<Long> getTotalProductPriceByUserUsingCompose(Product pr1, Product pr2) {
        return getProduct(pr1.userId, pr1.price, 1L).thenCombineAsync(getProduct(pr2.userId, pr2.price, 1L), (p1, p2) -> p1.price + p2.price, executor);
    }

    public CompletableFuture<Object> getAnyOfCompletableProduct(List<Product> products) {
        List<CompletableFuture<Product>> completableFutures = products.stream().map(p -> getProduct(p.userId, p.price, p.delay)).collect(Collectors.toList());
        return CompletableFuture.anyOf(completableFutures.toArray(new CompletableFuture[0]));
    }

    public List<CompletableFuture<Product>> getAllOfCompletableFuture(List<Product> products){
        List<CompletableFuture<Product>> completableFutures = products.stream().map(p -> getProduct(p.userId, p.price, p.delay)).collect(Collectors.toList());
        CompletableFuture.allOf(completableFutures.toArray(new CompletableFuture[0])).join();
        return completableFutures;
    }

    public CompletableFuture<String> getNameHandleException(boolean needExceptionInAsyncExecution, boolean needExceptionInCallback){
        CompletableFuture<String> completableFuture = CompletableFuture.supplyAsync(() -> {
            if(needExceptionInAsyncExecution) {
                throw new IllegalArgumentException();
            } else
                return "Hello Amit";
        }).thenApplyAsync((s) -> {
            if(needExceptionInCallback){
                throw new RuntimeException();
            } else {
                return s + ", Welcome";
            }
        }). exceptionally(ex -> {
           return ex.getCause().getClass().getCanonicalName();
        });
        return  completableFuture;
    }

    public CompletableFuture<String> getNameWithHandle(boolean needExceptionInAsyncExecution, boolean needExceptionInCallback){
        CompletableFuture<String> completableFuture = CompletableFuture.supplyAsync(() -> {
            if(needExceptionInAsyncExecution) {
                throw new IllegalArgumentException();
            } else
                return "Hello Amit";
        }).thenApplyAsync((s) -> {
            if(needExceptionInCallback){
                throw new RuntimeException();
            } else {
                return s + ", Welcome";
            }
        }). handle((res,ex) -> {
            if(res != null)
                return res;
            else
                return ex.getCause().getClass().getCanonicalName();
        });
        return  completableFuture;
    }

    @Data
    @AllArgsConstructor
    static class User {
        private Long id;
    }

    @Data
    @RequiredArgsConstructor
    static class Product {
        @NonNull
        private String name;
        @NonNull
        private Long userId;
        private Long price;
        private Long delay;
    }


}
