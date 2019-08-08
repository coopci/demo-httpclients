package demo.httpclients.clients.ahc;


import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Dispatcher;

import java.io.IOException;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

public class OkhttpConcurrentClient {
    
    static int concurrency = 100;
    public static void main(String[] args) {
        
        ExecutorService executorService = new ThreadPoolExecutor(1, 100, 0L, TimeUnit.MILLISECONDS, new LinkedBlockingQueue<Runnable>(5));
        
        Dispatcher d = new Dispatcher(Executors.newCachedThreadPool());
        // Dispatcher d = new Dispatcher(Executors.newFixedThreadPool(50));
        // Dispatcher d = new Dispatcher(executorService);
        d.setMaxRequests(100);
        d.setMaxRequestsPerHost(100);
        okhttp3.OkHttpClient okhttpClient = new okhttp3.OkHttpClient.Builder()
                .connectTimeout(1000, TimeUnit.SECONDS)
                .writeTimeout(1000, TimeUnit.SECONDS)
                .readTimeout(3000, TimeUnit.SECONDS)
                .dispatcher(d)
                .build();
        //okhttpClient.setConnectTimeout(1500, TimeUnit.SECONDS); // connect timeout
        // okhttpClient.setReadTimeout(1500, TimeUnit.SECONDS);    // socket timeout
        CompletableFuture<String>[] futures = (CompletableFuture<String>[])new CompletableFuture[concurrency];
        for (int i = 0; i < concurrency; ++i) {
            final okhttp3.Request request = new okhttp3.Request.Builder().url("http://localhost:8999/wait").get().build();
            final CompletableFuture<String> future = new CompletableFuture<String>();
            okhttpClient.newCall(request).enqueue(new Callback() {
                // @Override
                public void onFailure(Call call, IOException e) {
                    future.complete("fail.");
                }

                // @Override
                public void onResponse(final Call call, final okhttp3.Response response) throws IOException {
                    future.complete("complete.");
                }
                
            });
            futures[i] = future;
        }
        System.out.println("All sent.");
        for (int i = 0; i < concurrency; ++i) {
            try {
                String msg = futures[i].get();
                System.out.println("" + i + " " + msg);
            } catch (Exception e) {
                System.out.println("" + i + "throw");
                e.printStackTrace();
            } 
        }
        
        executorService.shutdown();
    }
}
