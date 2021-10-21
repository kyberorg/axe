package main

import (
	"log"
	"os"
	"os/exec"
	"strings"
)

var javaOptions strings.Builder

func main() {
	fileEnv("DB_PASSWORD", "")
	fileEnv("TELEGRAM_TOKEN", "")
	fileEnv("BUGSNAG_TOKEN", "")
	fileEnv("DELETE_TOKEN", "")

	//custom JAVA_OPTS support
	javaOptions.WriteString(os.Getenv("JAVA_OPTS"))

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
	appendJavaOpts("--add-opens java.base/java.lang=ALL-UNNAMED")
	appendJavaOpts("-XX:+UseContainerSupport")
	appendJavaOpts("-XX:+AlwaysActAsServerClassMachine")
	appendJavaOpts("-XX:+HeapDumpOnOutOfMemoryError -XX:HeapDumpPath=/opt/dumps")

	//Issue 264 (OpenJ9 tuning). Based on https://yals.ee/dUxHlC
	appendJavaOpts("-Xgcpolicy:gencon")
	appendJavaOpts("-Xquickstart")
	appendJavaOpts("-Xtune:virtualized")
	appendJavaOpts("-XX:+ClassRelationshipVerifier")
	appendJavaOpts("-XX:+TransparentHugePage")

	//Adding J9 Dump Options (#361). Created by https://yls.ee/MVeiwD
	appendJavaOpts("-Xdump:heap:events=user,request=exclusive+prepwalk+serial")

	//Issue 236 (Vaadin Production Mode)
	appendJavaOpts("-Dvaadin.production=true")

	versionCmd := "java --version"
	versionCommand := exec.Command(versionCmd)
	err := versionCommand.Run()

	if err != nil {
		log.Fatal(err)
	}

	cmd := makeJavaCommand()
	command := exec.Command(cmd)
	err = command.Run()

	if err != nil {
		log.Fatal(err)
	}
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
	javaOptions.WriteString(" " + option)
}

func makeJavaCommand() string {
	var cmd strings.Builder
	cmd.WriteString("java")
	cmd.WriteString(" ")
	cmd.WriteString(javaOptions.String())
	cmd.WriteString(" ")
	cmd.WriteString("org.springframework.boot.loader.JarLauncher")
	return cmd.String()
}
