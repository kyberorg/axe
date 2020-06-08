#!/bin/bash

# usage: file_env VAR [DEFAULT]
#    ie: file_env 'XYZ_DB_PASSWORD' 'example'
# (will allow for "$XYZ_DB_PASSWORD_FILE" to fill in the value of
#  "$XYZ_DB_PASSWORD" from a file, especially for Docker's secrets feature)
file_env() {
  local var="$1"
  local fileVar="${var}_FILE"
  local def="${2:-}"
  if [ "${!var:-}" ] && [ "${!fileVar:-}" ]; then
    echo >&2 "error: both $var and $fileVar are set (but are exclusive)"
    exit 1
  fi
  local val="$def"
  if [ "${!var:-}" ]; then
    val="${!var}"
  elif [ "${!fileVar:-}" ]; then
    val="$(<"${!fileVar}")"
  fi
  export "$var"="$val"
  unset "$fileVar"
}

file_env 'YALS_DB_PASSWORD'
file_env 'TELEGRAM_TOKEN'
file_env 'BUGSNAG_TOKEN'

exec java ${JAVA_OPTS} -Djava.security.egd=file:/dev/./urandom \
--add-opens java.base/java.lang=ALL-UNNAMED \
-XX:+UseContainerSupport -XX:+AlwaysActAsServerClassMachine \
-jar /app/yals.jar
