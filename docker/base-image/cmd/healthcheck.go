package main

import (
	"log"
	"net/http"
	"net/url"
	"os"
	"strings"
)

func main() {
	var path string

	if len(os.Args) > 1 {
		path = os.Args[1]
	} else {
		path = "/actuator/info"
	}

	if !startsWithSlash(path) {
		path = "/" + path
	}

	u := "http://127.0.0.1:8080" + path
	if isUrl(u) {
		_, err := http.Get(u)
		if err != nil {
			os.Exit(1)
		}
	} else {
		log.Panicln("Got Malformed URL. Exiting...")
	}
}

func startsWithSlash(s string) bool {
	return strings.HasPrefix(s, "/")
}

func isUrl(str string) bool {
	u, err := url.Parse(str)
	return err == nil && u.Scheme != "" && u.Host != ""
}
