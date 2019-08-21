package demo.httpclients.concurrency.server;

import org.glassfish.grizzly.http.server.HttpHandler;
import org.glassfish.grizzly.http.server.HttpServer;
import org.glassfish.grizzly.http.server.NetworkListener;
import org.glassfish.grizzly.http.server.Request;
import org.glassfish.grizzly.http.server.Response;
import org.glassfish.grizzly.nio.transport.TCPNIOTransport;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicLong;

public class Server {

    public static void main(String[] args) {
        
        HttpServer server = HttpServer.createSimpleServer(null, 8999);
        NetworkListener l = server.getListener("grizzly");
        TCPNIOTransport transport = l.getTransport();
        transport.setWorkerThreadPool(Executors.newFixedThreadPool(11000));
        server.getServerConfiguration().addHttpHandler(
            new HttpHandler() {
                public void service(Request request, Response response) throws Exception {
                    final SimpleDateFormat format = new SimpleDateFormat("EEE, dd MMM yyyy HH:mm:ss zzz", Locale.US);
                    final String date = format.format(new Date(System.currentTimeMillis()));
                    response.setContentType("text/plain");
                    response.setContentLength(date.length());
                    response.getWriter().write(date);
                }
            },
            "/time");
        final Object lock = new Object();
        final AtomicLong counter = new AtomicLong(0);
        server.getServerConfiguration().addHttpHandler(
                new HttpHandler() {
                    public void service(Request request, Response response) throws Exception {
                        synchronized(lock) {
                            counter.addAndGet(1);
                            lock.wait();
                        }
                        final String content = "go.";
                        response.setContentType("text/plain");
                        response.setContentLength(content.length());
                        response.getWriter().write(content);
                        counter.addAndGet(-1);
                    }
                },
                "/wait");
        server.getServerConfiguration().addHttpHandler(
                new HttpHandler() {
                    public void service(Request request, Response response) throws Exception {
                        synchronized(lock) {
                            lock.notifyAll();
                        }
                        final String content = "notified.";
                        response.setContentType("text/plain");
                        response.setContentLength(content.length());
                        response.getWriter().write(content);
                    }
                },
                "/notify");
        server.getServerConfiguration().addHttpHandler(
                new HttpHandler() {
                    public void service(Request request, Response response) throws Exception {
                        final String content = "" + counter.longValue();
                        response.setContentType("text/plain");
                        response.setContentLength(content.length());
                        response.getWriter().write(content);
                    }
                },
                "/counter");
        try {
            server.start();
            System.out.println("Press any key to stop the server...");
            System.in.read();
        } catch (Exception e) {
            System.err.println(e);
        }
    }
}
