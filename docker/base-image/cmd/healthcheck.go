package main

import (
	"log"
	"net/http"
	"net/url"
	"os"
	"path"
	"strings"
)

func main() {
	var checkPath string

	if len(os.Args) > 1 {
		checkPath = os.Args[1]
	} else {
		checkPath = "/actuator/info"
	}

	if !startsWithSlash(checkPath) {
		checkPath = "/" + checkPath
	}

	u := "http://127.0.0.1:8080" + path.Clean(checkPath)
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
