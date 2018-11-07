CREATE KEYSPACE IF NOT EXISTS maf WITH REPLICATION = { 'class' : 'SimpleStrategy', 'replication_factor' : 1 } AND DURABLE_WRITES = true;

--;
-- Data tables;
--;

CREATE TABLE IF NOT EXISTS maf."timeseries_index" (
   "systemid" bigint,
   "bucket" bigint,
   "partition" blob,
   "key" bigint,
   "value" blob,
   PRIMARY KEY (("systemid", "bucket", "partition"), "key")
) WITH COMPACT STORAGE
   AND COMPRESSION = { 'sstable_compression' : 'DeflateCompressor', 'chunk_length_kb' : '128' };

CREATE TABLE IF NOT EXISTS maf."unique_series_index" (
   "systemid" bigint,
   "bucket" bigint,
   "partition" blob,
   "key" timeuuid,
   "value" blob,
   PRIMARY KEY (("systemid", "bucket", "partition"), "key")
) WITH COMPACT STORAGE
   AND COMPRESSION = { 'sstable_compression' : 'DeflateCompressor', 'chunk_length_kb' : '128' };

CREATE TABLE IF NOT EXISTS maf."discrete_index" (
   "systemid" bigint,
   "partition" blob,
   "key" varchar,
   "value" blob,
   PRIMARY KEY (("systemid", "partition"), "key")
) WITH COMPACT STORAGE
   AND COMPRESSION = { 'sstable_compression' : 'DeflateCompressor', 'chunk_length_kb' : '128' };

CREATE TABLE IF NOT EXISTS maf."main_data" (
   "id" blob,
   "systemid" bigint,
   "attributes" map<varchar, blob>,
   PRIMARY KEY (("systemid", "id"))
) WITH COMPRESSION = { 'sstable_compression' : 'DeflateCompressor', 'chunk_length_kb' : '128' };