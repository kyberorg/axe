package main

import (
	"fmt"
	"io/fs"
	"log"
	"path/filepath"
)

func main() {
	err := filepath.Walk("/usr",
		func(path string, info fs.FileInfo, err error) error {
			if err != nil {
				log.Println(err)
			}
			fmt.Println(path)
			return nil
		})
	if err != nil {
		log.Panicln(err)
	}
}
