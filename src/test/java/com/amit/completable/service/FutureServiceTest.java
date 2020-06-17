package com.amit.completable.service;


import org.assertj.core.util.Arrays;
import org.junit.jupiter.api.Test;

import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CancellationException;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class FutureServiceTest {

    private static final String HELLO = "Hello";
    private FutureService service = new FutureService();

    @Test
    public void testComplete_success() {
        try {
            assertTimeoutPreemptively(Duration.ofMillis(FutureService.SLEEP_TIME + 1000), () -> {
                assertEquals(HELLO, service.getCompletableFuture(false, false).get(), "Not returning Hello");
            }, "Did not complete after sleep time");
        } catch (Exception e) {
            assertFalse(true, " Code should not reach here");
        }
    }

    @Test
    public void testComplete_withException() {

        assertTimeoutPreemptively(Duration.ofMillis(FutureService.SLEEP_TIME + 1000), () -> {
            Exception e = assertThrows(ExecutionException.class, () -> {
                service.getCompletableFuture(true, true).get();
            });
            assertEquals(RuntimeException.class, e.getCause().getClass());
        }, "Did not complete after sleep time");
    }

    @Test
    public void testComplete_cancel() {

        assertTimeoutPreemptively(Duration.ofMillis(FutureService.SLEEP_TIME + 1000), () -> {
            Exception e = assertThrows(CancellationException.class, () -> {
                service.getCompletableFuture(true, false).get();
            });
        }, "Did not complete after sleep time");

    }

    @Test
    public void testComplete_immediately() {
        Future future = CompletableFuture.completedFuture(HELLO);
        try {
            assertEquals(HELLO, future.get(), "Not returning Hello");
        } catch (Exception e) {
            assertFalse(true, " Code should not reach here");
        }
    }

    @Test
    public void test_RunAsync() {
        System.out.println(Thread.currentThread().getName() + ": Going to run in async manner");
        service.completableFutureRunAsync();
        System.out.println(Thread.currentThread().getName() + ": Completed Main method");
    }

    @Test
    public void test_SupplyAsync() {
        System.out.println(Thread.currentThread().getName() + ": Going to run in async manner");
        CompletableFuture<String> future = service.completeFutureSupplyAsync();
        System.out.println(Thread.currentThread().getName() + ": Before printing value");
        try {
            assertEquals(HELLO, future.get());
        } catch (Exception e) {
            assertFalse(true, " Code should not reach here");
        }
    }

    @Test
    public void test_SupplyAsync_thenApply() {
        System.out.println(Thread.currentThread().getName() + ": Going to run in async manner with callback");
        CompletableFuture<String> future = service.completableFutureSupplyAsyncThenApply();
        try {
            assertEquals(HELLO + " Amit, welcome !!!", future.get());
        } catch (Exception e) {
            assertFalse(true, " Code should not reach here");
        }
    }

    @Test
    public void test_SupplyAsync_thenApplyAsync() {
        System.out.println(Thread.currentThread().getName() + ": Going to run in async manner with callback");
        CompletableFuture<String> future = service.completableFutureSupplyAsyncThenApplyAsync();
        try {
            assertEquals(HELLO + " Amit, welcome !!!", future.get());
        } catch (Exception e) {
            assertFalse(true, " Code should not reach here");
        }
    }

    @Test
    public void test_SupplyAsync_thenAcceptAndRunAsync() {
        System.out.println(Thread.currentThread().getName() + ": Going to run in async manner with callback");
        service.completableFutureSupplyAsyncThenAcceptAndRunAsync();
    }

    @Test
    public void test_compose(){
        System.out.println(Thread.currentThread().getName() + ": Compose test main thread");
       try{
           FutureService.Product product = service.getProductByUserUsingCompose(10L).get();
           assertEquals(10L, product.getUserId());
       } catch (Exception e){
           assertFalse(true, " Code should not reach here");
       }
    }

    @Test
    public void test_compose_accept(){
        System.out.println(Thread.currentThread().getName() + ": Compose test main thread");
        try{
            FutureService.Product product = service.getProductByUserUsingThenApply(10L).get().get();
            assertEquals(10L, product.getUserId());
        } catch (Exception e){
            assertFalse(true, " Code should not reach here");
        }
    }

    @Test
    public void test_combine(){
        System.out.println(Thread.currentThread().getName() + ": Compose test main thread");
        try{
            FutureService.Product pr1 = new FutureService.Product("Test-1", 10L);
            pr1.setPrice(10L);
            FutureService.Product pr2 = new FutureService.Product("Test-2", 20L);
            pr2.setPrice(20L);
            Long total = service.getTotalProductPriceByUserUsingCompose(pr1, pr2).get();
            assertEquals(30L, total);
        } catch (Exception e){
            e.printStackTrace();
            assertFalse(true, " Code should not reach here");
        }
    }

    @Test
    public void test_anyOff(){
        System.out.println(Thread.currentThread().getName() + ": AnyOff test main thread");
        try{
            FutureService.Product pr1 = new FutureService.Product("Test-1", 10L);
            pr1.setPrice(10L);
            pr1.setDelay(1000L);
            FutureService.Product pr2 = new FutureService.Product("Test-2", 20L);
            pr2.setPrice(20L);
            pr2.setDelay(2000L);
            FutureService.Product pr3 = new FutureService.Product("Test-3", 30L);
            pr3.setPrice(30L);
            pr3.setDelay(500L);
            List<FutureService.Product> list = new ArrayList<>();
            list.add(pr1); list.add(pr2); list.add(pr3);
            FutureService.Product anyProduct = (FutureService.Product) service.getAnyOfCompletableProduct(list).get();
            System.out.println("Any product " + anyProduct);
            assertEquals(30L, anyProduct.getPrice());
        } catch (Exception e){
             assertFalse(true, " Code should not reach here");
        }
    }

    @Test
    public void test_allOff(){
        System.out.println(Thread.currentThread().getName() + ": AllOf test main thread");
        try{
            FutureService.Product pr1 = new FutureService.Product("Test-1", 10L);
            pr1.setPrice(10L);
            pr1.setDelay(1000L);
            FutureService.Product pr2 = new FutureService.Product("Test-2", 20L);
            pr2.setPrice(20L);
            pr2.setDelay(2000L);
            FutureService.Product pr3 = new FutureService.Product("Test-3", 30L);
            pr3.setPrice(30L);
            pr3.setDelay(500L);
            List<FutureService.Product> list = new ArrayList<>();
            list.add(pr1); list.add(pr2); list.add(pr3);
            List<CompletableFuture<FutureService.Product>> completedProducts  = service.getAllOfCompletableFuture(list);
            completedProducts.stream().forEach(p -> {
                try {
                    assertEquals("Test", p.get().getName());
                } catch (Exception e) {
                    assertFalse(true, " Code should not reach here");
                }
            });
        } catch (Exception e){
            assertFalse(true, " Code should not reach here");
        }
    }

    @Test
    public void testExceptionally_withoutException(){
        try{
            assertEquals("Hello Amit, Welcome", service.getNameHandleException(false, false).get());
        } catch (Exception e){
            assertFalse(true, " Code should not reach here");
        }
    }

    @Test
    public void testExceptionally_withException_inSupplyAsync(){
        try{
            assertEquals(IllegalArgumentException.class.getCanonicalName(), service.getNameHandleException(true, false).get());
        } catch (Exception e){
            assertFalse(true, " Code should not reach here");
        }
    }

    @Test
    public void testExceptionally_withException_inApplyAsync(){
        try{
            assertEquals(RuntimeException.class.getCanonicalName(), service.getNameHandleException(false, true).get());
        } catch (Exception e){
            assertFalse(true, " Code should not reach here");
        }
    }

    @Test
    public void testExceptionally_withException_inSupplyAndApplyAsync(){
        try{
            assertEquals(IllegalArgumentException.class.getCanonicalName(), service.getNameHandleException(true, true).get());
        } catch (Exception e){
            assertFalse(true, " Code should not reach here");
        }
    }

    @Test
    public void testHandle_withoutException(){
        try{
            assertEquals("Hello Amit, Welcome", service.getNameWithHandle(false, false).get());
        } catch (Exception e){
            assertFalse(true, " Code should not reach here");
        }
    }

    @Test
    public void testHandle_withException_inSupplyAsync(){
        try{
            assertEquals(IllegalArgumentException.class.getCanonicalName(), service.getNameWithHandle(true, false).get());
        } catch (Exception e){
            assertFalse(true, " Code should not reach here");
        }
    }

    @Test
    public void testHandle_withException_inApplyAsync(){
        try{
            assertEquals(RuntimeException.class.getCanonicalName(), service.getNameWithHandle(false, true).get());
        } catch (Exception e){
            assertFalse(true, " Code should not reach here");
        }
    }

    @Test
    public void testHandle_withException_inSupplyAndApplyAsync(){
        try{
            assertEquals(IllegalArgumentException.class.getCanonicalName(), service.getNameWithHandle(true, true).get());
        } catch (Exception e){
            assertFalse(true, " Code should not reach here");
        }
    }



}
