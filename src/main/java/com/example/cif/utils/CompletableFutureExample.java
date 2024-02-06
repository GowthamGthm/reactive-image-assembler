package com.example.cif.utils;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

public class CompletableFutureExample {

    public static void main(String[] args) throws ExecutionException, InterruptedException {
        // Create a CompletableFuture
        CompletableFuture<String> future = CompletableFuture.supplyAsync(() -> "Hello");

        // Attach a callback to the CompletableFuture when it completes
        CompletableFuture<String> greetingFuture = future.thenApply(s -> s + " World");


        // Print the result of the CompletableFuture
        System.out.println(greetingFuture.get());
//        Flux.from(greetingFuture)
//                .map(s ->  new RuntimeException("abcd"))
//                .onErrorContinue(RuntimeException.class , (e) -> {
//                });

    }
}
