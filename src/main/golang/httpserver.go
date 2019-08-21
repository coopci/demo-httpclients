package main

import (
	"fmt"
	"net/http"
 	"sync"
)

func main() {

m := sync.Mutex{}
    c := sync.NewCond(&m)

http.HandleFunc("/", func (w http.ResponseWriter, r *http.Request) {
        fmt.Fprintf(w, "Welcome to my website!")
    })
http.HandleFunc("/wait", func (w http.ResponseWriter, r *http.Request) {
c.L.Lock()
	c.Wait()
c.L.Unlock()
        fmt.Fprintf(w, "Go!")
    })

http.HandleFunc("/release", func (w http.ResponseWriter, r *http.Request) {
        c.Broadcast()
        fmt.Fprintf(w, "Released")
    })

    http.ListenAndServe(":8181", nil)


}


