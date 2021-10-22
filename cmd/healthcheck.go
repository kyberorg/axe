package main

import (
	"net/http"
	"os"
)

func main() {
	var path string

	if len(os.Args) > 1 {
		path = os.Args[1]
	} else {
		path = "/actuator/info"
	}

	_, err := http.Get("http://127.0.0.1:8080" + path)
	if err != nil {
		os.Exit(1)
	}
}
