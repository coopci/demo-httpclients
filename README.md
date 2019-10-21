# demo-httpclients

demo.httpclients.concurrency.clients.AhcConcurrentClient and demo.httpclients.concurrency.clients.OkhttpConcurrentClient are two clients with same "business logic" -- request "/wait" on server. The two classes the behaviour difference of the two libs.


src/main/golang/httpserver.go is a golang http server serving "/wait" and "/release", because the java/grizzly based demo.httpclients.concurrency.server.Server doesn't really support high concurrency. Of course, java with NIO can support higher concurrency than demo.httpclients.concurrency.server.Server but requires a pretty different programming paradigm. While the golang server is still written in a "sync blocking" manner in the perspective of application coding.
