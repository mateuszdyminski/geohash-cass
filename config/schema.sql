CREATE KEYSPACE geohash WITH replication = {
  'class': 'NetworkTopologyStrategy',
  'datacenter1': '1'
};

USE geohash;

CREATE TABLE "geohashes" (
  geohash text,
  timestamp bigint,
  geodata blob,
  PRIMARY KEY (geohash, timestamp)
) WITH comment='geohashes CF keeps some metrics';