package main

import (
	"bufio"
	"flag"
	"log"
	"os"
)

const EmptyFile = "EMPTY_FILE"
const EmptyOpts = ""

func main() {
	failedTestsList := flag.String("ff", EmptyFile, "File with failed test")
	mvnExtraParams := flag.String("mavenOpts", EmptyOpts, "Maven params aka -D flags")

	if *failedTestsList == EmptyFile {
		log.Fatal("No file with failed test defined. Please use '--ff /path/to/file' flag")
	}
	file, err := os.Open(*failedTestsList)
	if err != nil {
		log.Fatal(err)
	}
	defer file.Close()

	var failedTests string
	scanner := bufio.NewScanner(file)
	for scanner.Scan() {
		failedTests += scanner.Text() + " "
	}

	log.Printf("Got extra args: %s, Got tests: %s", *mvnExtraParams, failedTests)
}
