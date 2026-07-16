#!/bin/sh
set -eu

listen_port="${PORT:-8080}"
sed "s/__PORT__/${listen_port}/g" /app/nginx.conf.template > /tmp/nginx.conf
nginx -e /dev/stderr -c /tmp/nginx.conf

exec env SERVER_PORT=8081 java -jar /app/app.jar
