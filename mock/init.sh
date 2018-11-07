#! /bin/bash

rm -rf /tmp/sema/db.init

while ! cqlsh -e 'describe cluster' cassandra; do
    sleep 2
done

echo "=============== Cassandra is ready =============="
cqlsh -f /init.cql cassandra

touch /tmp/sema/db.init