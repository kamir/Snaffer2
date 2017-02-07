# Our goal: 
Packet inspection with Hive and Spark, analysis of the communication graph via GraphX and Gephi.

# Results:
A communication graph shows the hotspots. Time dependent views allow application fine tuning.

Cybersecurity is a broad topic and many commercial products are related to it. We demonstrate a fundamental concept in network analysis: re-construction and visualization of temporal networks. Furthermore, we apply the method to describe operational conditions of a Hadoop cluster. Our experiments provide first results and allow a classification of the cluster state related to current workloads. The temporal networks show significant differences for different operation modes. In reallity we would expect mixed workloads. If such workload parameters are known, we are able to handle a-typical events accordingly - which means, we are able to create alerts based on context information, rather than only the package content. We show an end-to-end example: (1) Data collection is done via python, using the sniffer script; (2) using Apache Hive and Apache Spark we analyze the network traffic data and create the temporary network. Finally, we are able to visualize the results using Gephi in step (3). In a next step, we plan to contribute to the Apache Spot project.

See our presentation @FOSDEM 2017: (https://fosdem.org/2017/schedule/event/graph_traffic_analysis_hadoop_patterns/)

The slides are on Slideshare: (http://www.slideshare.net/mirkokaempf/pcap-graphs-for-cybersecurity-an-system-tuning)

![alt text](doc/snap-4.jpg?raw=true "Links between Hosts, based on packet traffic.")

Simple statistics is done via Hive.

![alt text](doc/snap-2.jpg?raw=true "Packets per host")

More advanced packet content inspection follows soon ... (via Apache Spark).



# Our Tools: 

## Snaffer
A Python based TCP Sniffer which writes AVRO files for analysis in Spark or Impala.

### Preparation: Install Dependencies

The following steps are executed by the bootstrap.sh script.

```sh
sudo yum install python-devel
sudo yum install gcc
sudo yum install gcc-c++
sudo yum install libpcap-devel
sudo easy_install pip
sudo pip install avro
sudo pip install pcapy
wget http://www.coresecurity.com/system/files/pcapy-0.10.8.tar_.gz
tar -xf pcapy-0.10.8.tar_.gz
cd pcapy-0.10.8
sudo python setup.py install
```
More details about pcapy: https://www.coresecurity.com/corelabs-research/open-source-tools/pcapy


### TXT Output Format

```sh
sudo python sniffer.py eth0
```

```{r, engine='bash', count_lines}
srcIP|dstIP|protocolType|headerLength|srcPort|dstPort|seqNumber|ackNumber|data
```

```{r, engine='bash', count_lines}
172.18.13.178|172.28.196.65|TCP|34|54985|7180|1436448910|3130769187|+$QGET /cmf/keepSessionActive?_=1475157432039 HTTP/1.1Host: nightly-agg.vpc.cloudera.com:7180Connection: keep-aliveAccept: application/json, text/javascript, */*; q=0.01X-Requested-With: XMLHttpRequestUser-Agent: Mozilla/5.0 (Macintosh; Intel Mac OS X 10_11_6) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/53.0.2785.116 Safari/537.36Referer: http://nightly-agg.vpc.cloudera.com:7180/cmf/aggregator/peersAccept-Encoding: gzip, deflate, sdchAccept-Language: en-US,en;q=0.8Cookie: __utma=1.585441285.1454243470.1461920899.1462999301.3; __utmz=1.1462999301.3.3.utmcsr=cloudera.com|utmccn=(referral)|utmcmd=referral|utmcct=/training/course-listing.html; __unam=42ca463-1542356ce67-551b5dc8-16; s_fid=5EE00B1F945174D7-1FF04EA171C28908; AMCV_97C7898555F6819F7F000101%40AdobeOrg=-179204249%7CMCIDTS%7C17071%7CMCMID%7C27502997852283248664151525536734913275%7CMCAAMLH-1474973126%7C6%7CMCAAMB-1475562544%7CNRX38WO0n5BH8Th-nqAG_A%7CMCOPTOUT-1474964944s%7CNONE%7CMCAID%7CNONE; mbox=PC#1472472754460-638850.26_24#1476172935|check#true#1474963395|session#1474963328823-319732#1474965195; CLOUDERA_MANAGER_SESSIONID=50k0wbjh8ce116wyabl4uerjk; _ga=GA1.2.585441285.1454243470; __utmt=1; __utma=164720160.585441285.1454243470.1475152783.1475157433.4; __utmb=164720160.1.10.1475157433; __utmc=164720160; __utmz=164720160.1473597270.1.1.utmcsr=(direct)|utmccn|
172.28.196.65|172.18.13.178|TCP|34|7180|54985|3130769187|1436450264|+$R|
172.18.13.178|172.28.196.65|TCP|34|54985|7180|1436450264|3130769187|+$Q=(direct)|utmcmd=(none)|
172.28.196.65|172.18.13.178|TCP|34|7180|54985|3130769187|1436450291|+$R|
172.28.196.65|172.18.13.178|TCP|34|7180|54985|3130769187|1436450291|+$RHTTP/1.1 200 OKContent-Type: application/json; charset=UTF-8Content-Encoding: gzipTransfer-Encoding: chunkedServer: Jetty(6.1.26.cloudera.4)A|
```

### Avro Output Format

```sh
sudo python snaffer.py eth0 1000
```

Collects 1000 packets and writes into an Avro file using the Avro schema in: schema/packet.asvc.
```{r, engine='json', count_lines}
{
    "namespace":"com.cloudera.security.checks",
    "type":"record",
    "doc":"This Schema describes a DATA PACKET",
    "name":"Packet",
    "fields":[
    	{"name":"packet_grab_nr", "type": "long"    },
        {"name":"ts",             "type": "long"    },
        {"name":"srcIP",          "type": "string"  },
        {"name":"dstIP",          "type": "string"  },
	    {"name":"srcPort",        "type": "int"     },
        {"name":"dstPort",        "type": "int"     },
        {"name":"seqNumber",      "type": "int"     },
        {"name":"ackNumber",      "type": "int"     },
        {"name":"dumpHost",       "type": "string"  },
        {"name":"protocolType",   "type": "string"  },
	    {"name":"dumpDevice",     "type": ["null", "string"] },
	    {"name":"data",           "type": ["null", "bytes"] }
    ]
}
```

Output is written to folder dump using the following filename pattern:

packetdump_HOSTNAME_DEVICE_STARTTIME.avro

Example: 

packetdump_quickstart.cloudera_lo_2016-10-28 09:00:04.avro

# EDH based TSB and Network Analysis Workbench

![alt text](https://raw.githubusercontent.com/kamir/Snaffer2/master/doc/Snaffer-and-Apache-Spot%20-%20The%20Cybersecurity-Workbench-for-Apache-Spark-on-CDH.png "Architectural Overview")


## Packet Analysis in Hadoop

1.) Prepare DUMP-Space (only once)

```sh
hadoop fs -mkdir /user/cloudera/TCPDUMP/
hadoop fs -mkdir /user/cloudera/TCPDUMP/raw
hadoop fs -mkdir /user/cloudera/TCPDUMP/META
hadoop fs -put ./schema/* /user/cloudera/TCPDUMP/META
```

You should run bootstrapHDFS.sh as super user to update the schema after the schema was changed.

Create the Hive table:

```SQL
CREATE EXTERNAL TABLE tcpdumps_avro 
ROW FORMAT SERDE 'org.apache.hadoop.hive.serde2.avro.AvroSerDe' 
STORED AS INPUTFORMAT 'org.apache.hadoop.hive.ql.io.avro.AvroContainerInputFormat'
OUTPUTFORMAT 'org.apache.hadoop.hive.ql.io.avro.AvroContainerOutputFormat'
LOCATION '/user/cloudera/TCPDUMP/raw' 
TBLPROPERTIES ('avro.schema.url'='hdfs://127.0.0.1:8020/user/cloudera/TCPDUMP/META/packet.avsc')
```

Table creation hast to be done only once, during setup. If the schema has changed, one has to ensure, that the new schema is compatible to the existing data files if those should be kept. The new schema has to be copied into the METADATA folder and than, after refreshing the metadata it is possible to use the new data format.

2.) Upload new dumps to HDFS

Rename the file (all ":" have to be replaced by "_").

```sh
hadoop fs -put ./dump/*.avro /user/cloudera/TCPDUMP/raw
rm ./dump/*
```

3.) Count Packets in Hive and links between cluster hosts.

```SQL
SELECT data FROM tcpdumps_avro;
SELECT srcip AS Source, dstip AS Target, count(dstip) AS Weight FROM tcpdumps_avro group by srcip, dstip;

```

# Multi-Scenario Packet Analysis in Hadoop

Now we create a partitioned Hive table using the parquet format:

```SQL
CREATE EXTERNAL TABLE IF NOT EXISTS tcpdumps_exA_avro
(
packet_grab_nr INT, 
ts String, 
srcIP STRING, 
dstIP STRING, 
srcPort INT,
dstPort INT,
seqNumber INT,
ackNumber INT,
dumpHost STRING,
protocolType STRING,
dumpDevice STRING,
data STRING
)
PARTITIONED BY (szenario String)
ROW FORMAT SERDE 'parquet.hive.serde.ParquetHiveSerDe'
STORED AS 
INPUTFORMAT "parquet.hive.DeprecatedParquetInputFormat"
OUTPUTFORMAT "parquet.hive.DeprecatedParquetOutputFormat"
LOCATION '/user/cloudera/TCPDUMP/experimentsA';
```
This procedure has to be repeated step by step: 
- Create a staging table 
- Load  PCAP dump results to staging table.
- Create a new partition
- Add data to the new partition using a SELECT query

```SQL
CREATE EXTERNAL TABLE tcpdumps_avro_a 
ROW FORMAT SERDE 'org.apache.hadoop.hive.serde2.avro.AvroSerDe' 
STORED AS INPUTFORMAT 'org.apache.hadoop.hive.ql.io.avro.AvroContainerInputFormat'
OUTPUTFORMAT 'org.apache.hadoop.hive.ql.io.avro.AvroContainerOutputFormat'
LOCATION '/user/cloudera/TCPDUMP/stage/scenarioA' 
TBLPROPERTIES ('avro.schema.url'='hdfs://127.0.0.1:8020/user/cloudera/TCPDUMP/META/packet.avsc')
```

```SQL
set hive.exec.dynamic.partition=true;  
set hive.exec.dynamic.partition.mode=nonstrict;  

INSERT INTO TABLE tcpdumps_session1_par PARTITION (szenario) 
SELECT  
seqnumber,
packet_grab_nr,
srcip,
dstip,
srcport,
dstport,
ts,
acknumber,
dumphost,
protocoltype,
dumpdevice,
data,
"D" as szenario
FROM tcpdumps_avro_d;
```

Finally we can count the records per "Experiment".
```SQL
SELECT szenario, count(*) FROM tcpdumps_exA_avro GROUP BY szenario;
```

## Limitations
- currently not all of our procedures are documented! 

Please let us know your questions! Just add an doc issue - this allows us to prioritize the docu createion process.



