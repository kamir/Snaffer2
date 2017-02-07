mkdir $1

scp -r root@kudu-mk-5.vpc.cloudera.com:/root/Snaffer/dump $1
scp -r root@kudu-mk-4.vpc.cloudera.com:/root/Snaffer/dump $1
scp -r root@kudu-mk-3.vpc.cloudera.com:/root/Snaffer/dump $1
scp -r root@kudu-mk-2.vpc.cloudera.com:/root/Snaffer/dump $1
scp -r root@kudu-mk-1.vpc.cloudera.com:/root/Snaffer/dump $1

ssh root@kudu-mk-5.vpc.cloudera.com rm -rf /root/Snaffer/dump
ssh root@kudu-mk-4.vpc.cloudera.com rm -rf /root/Snaffer/dump
ssh root@kudu-mk-3.vpc.cloudera.com rm -rf /root/Snaffer/dump
ssh root@kudu-mk-2.vpc.cloudera.com rm -rf /root/Snaffer/dump

ssh root@kudu-mk-5.vpc.cloudera.com mkdir /root/Snaffer/dump
ssh root@kudu-mk-4.vpc.cloudera.com mkdir /root/Snaffer/dump
ssh root@kudu-mk-3.vpc.cloudera.com mkdir /root/Snaffer/dump
ssh root@kudu-mk-2.vpc.cloudera.com mkdir /root/Snaffer/dump

rm -rf /root/Snaffer/dump
mkdir /root/Snaffer/dump

hdfs dfs -put $1 /SNAFFBASE/$1

