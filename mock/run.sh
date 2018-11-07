#!/bin/sh
ls -la /tmp/sema/db.init

while [ ! -f /tmp/sema/db.init ]; do
    sleep 2;
done
echo "Start------------------------------"
ls -la /tmp/sema/db.init

/usr/bin/start.sh