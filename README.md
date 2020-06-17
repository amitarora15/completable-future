Completable Future
--

Important Points:
1. Async programming
1. Extension to Future, but Future has some limitations
    * Cannot be manually completed or cancelled
    * Cannot attach callback to future, get() is blocking call
    * Chanining of Futures
    * Combining Future result 
    * Exception Handling in Future
1. CompletableFuture implements Future and CompletionStage interfaces 
1. Async thread obtained from ForkJoinPool.commonPool()
1. Multiple HTTP service call, can be combined - @EnableSync and @Async annotations can be used
1. CompletableFuture.join() method is similar to the get method, but it throws an unchecked exception in case the Future does not complete normally.
1. Java 9 introduced lot of changes in this like timeout and many others
---

* Future Creation  
`CompletableFuture<String> completableFuture = new CompletableFuture<String>();`  
`String result = completableFuture.get();`  
This will block forever unless you complete or cancel the completableFuture in some other thread.

* Future Completion  
`completableFuture.complete("Future's Result")`  
`completableFuture.completeExceptionally(new Exception())`   --> Return ExecutionException (and in that actual exception is wrapped)

* Future Cancellation - return CancellationException  
`completableFuture.cancel(false)`

*  If you already know the result of a computation, you can use the static completedFuture method with an argument that represents a result of this computation. Then the get method of the Future will never block.  
  `Future<String> completableFuture = CompletableFuture.completedFuture("Hello");`
 
* Run Async and don't return anything  
`CompletableFuture<Void> future = CompletableFuture.runAsync(..runnable..)`  
or  
 `CompletableFuture<Void> future = CompletableFuture.runAsync(..runnable.., executor)`
 
 * Run Async and return something  
`CompletableFuture<String> future = CompletableFuture.supplyAsync(..supplier..)`  
 or  
`CompletableFuture<String> future = CompletableFuture.supplyAsync(..supplier.., executor)`  
Supplier<T> has get() method which return 'T'

* Don't block, instead attach callback to be executed when Async execution is finished. These callbacks can be chained. All these run in same thread as async thread or main thread. If we want to execute callback in different thread, that is possible through two variants - with executor or without executor
  * thenApply -  It takes a Function<T,R> as an argument. Function<T,R> is a simple functional interface representing a function that accepts an argument of type T and produces a result of type R   
  `CompletableFuture<String> welcomeText = CompletableFuture.supplyAsync(()  ...).thenApply(a -> {..}}).thenApply(b -> {..});`    
  `CompletableFuture<String> welcomeText = CompletableFuture.supplyAsync(()  ...).thenApplyAsync(a -> {..}}).thenApplyAsync(b -> {..});` 
  
  * thenAccept (thenAcceptBoth - accept two futures, but does not return anything) - Don't want to return anything value from callback. Takes a Consumer<T> and returns CompletableFuture<Void>. Generally last method of chain  
  `CompletableFuture.supplyAsync(() -> {..}).thenAccept(a -> {..}))`  
  `CompletableFuture.supplyAsync(() -> {..}).thenAcceptAsync(a -> {..}))`  
  `CompletableFuture.supplyAsync(() -> {..}).thenAcceptAsync(a -> {..}), executor)`  
  
  * thenRun - Don't take any parameter and don't return anything. Takes Runnable as argument  
  `CompletableFuture.supplyAsync(() -> {..}).thenRun(() -> {..})`   
  `CompletableFuture.supplyAsync(() -> {..}).thenRunAsync(() -> {..})`  
  `CompletableFuture.supplyAsync(() -> {..}).thenRunAsync(() -> {..}, executor)`

* Combining Completable Future
  * thenCompose - Used to combine dependent completable future. eg:
   `CompletableFuture<User> getUsersDetail(String userId) {....}`  
   
    `CompletableFuture<Double> getCreditRating(User user) {...}`  
     
    `CompletableFuture<Double> result = getUserDetail(userId)
     .thenCompose(user -> getCreditRating(user));`  
    here thenApply() can also be used, but it will return CompletableFuture<CompletableFuture<Double>.  
    _So, Rule of thumb here - If your callback function returns a CompletableFuture, and you want a flattened result from the CompletableFuture chain (which in most cases you would), then use thenCompose()._
    
  * thenCombine - Used to combine independent task and do some work on it  
     `CompletableFuture<Double> combinedFuture = weightInKgFuture`  
       `   .thenCombine(heightInCmFuture, (weightInKg, heightInCm) -> { `   
     `Double heightInMeter = heightInCm/100;`  
      `return weightInKg/(heightInMeter*heightInMeter);`  
      `});`

* Multiple Combining Independent Completable Future   

  * anyOf - takes multiple CompletableFuture and all run in parallel and do something when any of them is complete. Better to have same return type of all CompletableFuture passed to anyOf, otherwise difficult to find return type  
  `CompletableFuture<Object> anyOfFuture = CompletableFuture.anyOf(future1, future2, future3);`
  
  * allOf - takes multiple CompletableFuture and all run in parallel and do something when all are complete  
  The problem with CompletableFuture.allOf() is that it returns CompletableFuture<Void>. But this problem can be solved.
  
* Exception Handling - Exception at any level will suppress next level of call (callbacks or execution).
  * exceptionally -  
  `CompletableFuture<String> maturityFuture = CompletableFuture.supplyAsync(() ->{..}).exceptionally(ex -> {`  
        `System.out.println("Oops! We have an exception - " + ex.getMessage());`  
        `return "Unknown!";`  
    `});` 
    
  * handle -   
     `CompletableFuture<String> maturityFuture = CompletableFuture.supplyAsync(() ->{..})}).handle((res, ex) -> {..});`  
     

_References_
* https://www.callicoder.com/java-8-completablefuture-tutorial/
* https://howtodoinjava.com/spring-boot2/rest/enableasync-async-controller/
* https://www.baeldung.com/java-completablefuture
     
     
     