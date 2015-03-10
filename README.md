# Cassandra Tests

This project contains Cassandra queries performance tests.

# Setup

## Requirements

docker

cassandra (we only need cqlsh client)

## Pre steps

Run single-node local Cassandra:

```
docker run -d --name cass1 abh1nav/cassandra:latest
```

Check IP of Cassandra:

```
docker inspect -f '{{ .NetworkSettings.IPAddress }}' cass1
```

Create schema:

```
cqlsh <cassandra-ip-here> -f config/schema.sql
```

Set cassandra IP in config/geo.properties in 'seed_nodes'

To insert sample data, change flag 'insertSamples' to true in config/geo.properties  :

## Run

To run performance tests:
```
mvn clean install -Pperf
```


# Tests Results

Single-node local Cassandra:

Biggest rectangle 12412 geohashes:

GeohashDaoPerfTest.big_manyGeoHash_smaller_batches: 		time.bench: 10.75
GeohashDaoPerfTest.big_manyGeoHashes_allInBatch: 		    time.bench: 9.64
GeohashDaoPerfTest.big_manyGeoHashes_rx: 			        time.bench: 9.85
GeohashDaoPerfTest.big_singleGeoHash: 				        time.bench: 28.40
GeohashDaoPerfTest.big_singleGeoHash_parallel: 			    time.bench: 10.42
GeohashDaoPerfTest.big_singleGeoHash_rx: 			        time.bench: 9.44

Average rectangle 3894 geohashes:

GeohashDaoPerfTest.mid_manyGeoHash_smaller_batches: 		time.bench: 3.06
GeohashDaoPerfTest.mid_manyGeoHashes_allInBatch: 		    time.bench: 3.04
GeohashDaoPerfTest.mid_manyGeoHashes_rx: 			        time.bench: 3.05
GeohashDaoPerfTest.mid_singleGeoHash:				        time.bench: 8.65
GeohashDaoPerfTest.mid_singleGeoHash_parallel: 			    time.bench: 2.94
GeohashDaoPerfTest.mid_singleGeoHash_rx: 			        time.bench: 0.93

Small rectangle 273 geohashes:

GeohashDaoPerfTest.sm_manyGeoHash_smaller_batches: 		    time.bench: 0.21
GeohashDaoPerfTest.sm_manyGeoHashes_allInBatch: 		    time.bench: 0.22
GeohashDaoPerfTest.sm_manyGeoHashes_rx: 			        time.bench: 0.24
GeohashDaoPerfTest.sm_singleGeoHash: 				        time.bench: 0.58
GeohashDaoPerfTest.sm_singleGeoHash_parallel: 			    time.bench: 0.22
GeohashDaoPerfTest.sm_singleGeoHash_rx: 			        time.bench: 0.16

3-node remote Cassandra:

TDB