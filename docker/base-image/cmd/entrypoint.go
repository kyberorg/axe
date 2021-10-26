package main

import (
	"bytes"
	"fmt"
	"io"
	"log"
	"os"
	"os/exec"
)

var javaOptions []string

func main() {
	fileEnv("DB_PASSWORD", "")
	fileEnv("TELEGRAM_TOKEN", "")
	fileEnv("BUGSNAG_TOKEN", "")
	fileEnv("DELETE_TOKEN", "")

	//Remote Debug Support
	debugPort, debugPortExists := os.LookupEnv("JAVA_DEBUG_PORT")
	if debugPortExists {
		appendJavaOpts("-agentlib:jdwp=transport=dt_socket,server=y,suspend=n,address=0.0.0.0:" + debugPort)
	}

	//JMX (#361)
	jmxPort, jmxPortExists := os.LookupEnv("JAVA_JMX_PORT")
	if jmxPortExists {
		appendJavaOpts("-Dcom.sun.management.jmxremote")
		appendJavaOpts("-Djava.rmi.server.hostname=127.0.0.1")
		appendJavaOpts("-Dcom.sun.management.jmxremote.port=" + jmxPort)
		appendJavaOpts("-Dcom.sun.management.jmxremote.rmi.port=" + jmxPort)
		appendJavaOpts("-Dcom.sun.management.jmxremote.authenticate=false")
		appendJavaOpts("-Dcom.sun.management.jmxremote.ssl=false")
		appendJavaOpts("-Dcom.sun.management.jmxremote.local.only=false")
	}

	appendJavaOpts("-Djava.security.egd=file:/dev/./urandom")
	appendJavaOpts("-Dhttps.protocols=TLSv1.2,TLSv1.3")
	appendJavaOpts("-Djdk.tls.client.protocols=TLSv1.2,TLSv1.3")
	appendJavaOpts("-XX:+UseContainerSupport")
	appendJavaOpts("-XX:+AlwaysActAsServerClassMachine")
	appendJavaOpts("-XX:+HeapDumpOnOutOfMemoryError")
	appendJavaOpts("-XX:HeapDumpPath=/opt/dumps")

	//Issue 236 (Vaadin Production Mode)
	appendJavaOpts("-Dvaadin.production=true")

	javaCmd := "/usr/jre/bin/java"
	versionArgs := "--version"
	versionCommand := exec.Command(javaCmd, versionArgs)
	versionOut, err := versionCommand.CombinedOutput()
	//err = versionCommand.Run()
	fmt.Printf("Java Version \n%s\n", string(versionOut))

	if err != nil {
		log.Fatal(err)
	}

	springLauncher := "org.springframework.boot.loader.JarLauncher"
	javaOptions = append(javaOptions, springLauncher)
	fmt.Printf("%s %s", javaCmd, javaOptions)

	cmd := exec.Command(javaCmd, javaOptions...)
	var stdBuffer bytes.Buffer
	mw := io.MultiWriter(os.Stdout, &stdBuffer)

	cmd.Stdout = mw
	cmd.Stderr = mw

	err = cmd.Run()
	if err != nil {
		_, _ = fmt.Fprintf(os.Stderr, "Failed to start err=%v\n", err)
		fmt.Println(stdBuffer.String())
		os.Exit(1)
	}

	fmt.Printf("Output \n%s\n", stdBuffer.String())
}

//fileEnv VAR [DEFAULT]
func fileEnv(envName string, defaultValue string) {
	fileVarName := envName + "_FILE"

	envValue, envVarExists := os.LookupEnv(envName)
	fileVarValue, fileVarExists := os.LookupEnv(fileVarName)

	if envVarExists && fileVarExists {
		log.Fatalf(" Error both '%s' and '%s' are set (but are exclusive) \n", envName, fileVarName)
	}
	var value string
	if envVarExists {
		value = envValue
	} else if fileVarExists {
		value = fileVarValue
	} else {
		value = defaultValue
	}
	_ = os.Setenv(envValue, value)
}

func appendJavaOpts(option string) {
	javaOptions = append(javaOptions, option)
}
