package main

import (
	"bufio"
	"flag"
	"fmt"
	"log"
	"os"
	"os/exec"
	"strings"
)

const (
	EmptyFile   = "EMPTY_FILE"
	EmptyString = ""
)

var (
	failedTests      []string
	rerunCommandArgs []string
)

func main() {
	failedTestsList := flag.String("file", EmptyFile, "File with failed test")
	mvnProfiles := flag.String("profiles", EmptyString, "Maven Profiles (if any)")
	mvnExtraParams := flag.String("params", EmptyString, "Maven params aka -D flags")
	mvnTargets := flag.String("targets", "test", "Space-separated maven targets")

	flag.Parse()

	if *failedTestsList == EmptyFile {
		log.Fatal("No file with failed test defined. Please use '-file /path/to/file' flag.")
	}
	if strings.Contains(*mvnExtraParams, "-Dtest=") {
		log.Fatal("Conflict: mvn-rr constructs own -Dtest and your version would be overridden. " +
			"This is not what your want. Use plain maven instead.")
	}

	if *mvnTargets == EmptyString {
		log.Fatalf("No maven targets defined. Nothing to do.")
	}

	profiles := strings.TrimSpace(*mvnProfiles)
	params := strings.TrimSpace(*mvnExtraParams)
	targets := strings.TrimSpace(*mvnTargets)

	file, err := os.Open(*failedTestsList)
	if err != nil {
		log.Fatal(err)
	}
	defer file.Close()

	scanner := bufio.NewScanner(file)
	for scanner.Scan() {
		test := scanner.Text()
		if isTestNameValid(test) {
			appendFailedTest(test)
		}
	}

	mvnCmd := "mvn"
	versionArgs := "--version"
	versionCommand := exec.Command(mvnCmd, versionArgs)
	versionOut, err := versionCommand.CombinedOutput()
	if err != nil {
		log.Fatal(err)
	}

	fmt.Printf("Running with \n%s\n", string(versionOut))

	if profiles != EmptyString {
		appendCommandArgs("-P=" + profiles)
	}
	if params != EmptyString {
		appendCommandArgs(params)
	}
	if len(failedTests) > 0 {
		failedTestsList := strings.Join(failedTests, " ")
		failedTestsList = strings.TrimSpace(failedTestsList)
		dTest := []string{"-Dtest=\"", failedTestsList, "\""}
		appendCommandArgs(strings.Join(dTest, ""))
	}
	appendCommandArgs(targets)

	fmt.Printf("%s %s", mvnCmd, rerunCommandArgs)
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

func appendFailedTest(failedTest string) {
	failedTests = append(failedTests, failedTest)
}

func appendCommandArgs(arg string) {
	rerunCommandArgs = append(rerunCommandArgs, arg)
}
