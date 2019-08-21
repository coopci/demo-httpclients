package demo.httpclients.concurrency.clients;

import static org.asynchttpclient.Dsl.asyncHttpClient;
import static org.asynchttpclient.Dsl.post;

import org.asynchttpclient.AsyncHttpClient;
import org.asynchttpclient.Request;
import org.asynchttpclient.Response;
import static org.asynchttpclient.Dsl.post;

import java.io.IOException;
import java.util.concurrent.Future;

public class AhcConcurrentClient {
    
    static int concurrency = 10000;
    public static void main(String[] args) throws IOException {
        
        AsyncHttpClient asyncHttpClient = asyncHttpClient();
        
        Future<Response>[] futures = (Future<Response>[])new Future[concurrency];
        for (int i = 0; i < concurrency; ++i) {
            Request request = post("http://localhost:8181/wait")
                    .setMethod("GET")
                    .setHeader("Content-type", "text/plain")
                    .setRequestTimeout(-1 )
                    .setReadTimeout(-1 )
                    .build();
            Future<Response> whenResponse = asyncHttpClient.executeRequest(request);
            futures[i] = whenResponse;
        }
        System.out.println("All sent.");
        for (int i = 0; i < concurrency; ++i) {
            try {
                futures[i].get();
                System.out.println("" + i + " done");
            } catch (Exception e) {
                System.out.println("" + i + "throw");
                e.printStackTrace();
            } 
        }
        asyncHttpClient.close();
    }
}
