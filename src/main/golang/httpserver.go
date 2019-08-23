package main

import (
	"fmt"
	"net/http"
	"sync"
	"sync/atomic"
)

func main() {
	var ops int64
	m := sync.Mutex{}
	c := sync.NewCond(&m)

	http.HandleFunc("/", func(w http.ResponseWriter, r *http.Request) {
		fmt.Fprintf(w, "Welcome to my website!")
	})
	http.HandleFunc("/wait", func(w http.ResponseWriter, r *http.Request) {
		
		c.L.Lock()
		atomic.AddInt64(&ops, 1)
		c.Wait()
		atomic.AddInt64(&ops, -1)
		c.L.Unlock()
		fmt.Fprintf(w, "Go!")
	})

	http.HandleFunc("/release", func(w http.ResponseWriter, r *http.Request) {
		c.Broadcast()
		fmt.Fprintf(w, "Released")
	})

	http.HandleFunc("/count", func(w http.ResponseWriter, r *http.Request) {
		var c = atomic.LoadInt64(&ops);
		output := fmt.Sprintf("%s%d", "Count:", c)
                fmt.Fprintf(w, output)
        })
	http.ListenAndServe(":8181", nil)

}
