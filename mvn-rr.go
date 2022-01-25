package main

import (
	"bufio"
	"flag"
	"log"
	"os"
	"strings"
)

const EmptyFile = "EMPTY_FILE"
const EmptyOpts = ""

func main() {
	failedTestsList := flag.String("file", EmptyFile, "File with failed test")
	mvnExtraParams := flag.String("mavenOpts", EmptyOpts, "Maven params aka -D flags")

	flag.Parse()

	if *failedTestsList == EmptyFile {
		log.Fatal("No file with failed test defined. Please use '-file /path/to/file' flag")
	}
	file, err := os.Open(*failedTestsList)
	if err != nil {
		log.Fatal(err)
	}
	defer file.Close()

	var failedTests string
	scanner := bufio.NewScanner(file)
	for scanner.Scan() {
		test := scanner.Text()
		if isTestNameValid(test) {
			failedTests += test + " "
		}
	}

	log.Printf("Got extra args: %s, Got tests: %s", *mvnExtraParams, failedTests)
}

func isTestNameValid(testName string) bool {
	if len(testName) == 0 || len(strings.TrimSpace(testName)) == 0 {
		return false
	}
	if strings.Contains(testName, "#") {
		parts := strings.Split(testName, "#")
		if len(parts) != 2 {
			return false
		}

		testSuite := parts[0]
		if len(testSuite) == 0 {
			return false
		}
		testName := parts[1]
		if len(testName) == 0 {
			return false
		}
		return true
	} else {
		return false
	}
}
