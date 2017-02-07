scp ./../snaffer-sched.py root@kudu-mk-5.vpc.cloudera.com:/root/Snaffer/snaffer-sched.py
scp ./../snaffer-sched.py root@kudu-mk-4.vpc.cloudera.com:/root/Snaffer/snaffer-sched.py
scp ./../snaffer-sched.py root@kudu-mk-3.vpc.cloudera.com:/root/Snaffer/snaffer-sched.py
scp ./../snaffer-sched.py root@kudu-mk-2.vpc.cloudera.com:/root/Snaffer/snaffer-sched.py

scp ./../capture-frames.sh root@kudu-mk-5.vpc.cloudera.com:/root/Snaffer/capture-frames.sh
scp ./../capture-frames.sh root@kudu-mk-4.vpc.cloudera.com:/root/Snaffer/capture-frames.sh
scp ./../capture-frames.sh root@kudu-mk-3.vpc.cloudera.com:/root/Snaffer/capture-frames.sh
scp ./../capture-frames.sh root@kudu-mk-2.vpc.cloudera.com:/root/Snaffer/capture-frames.sh

ssh root@kudu-mk-5.vpc.cloudera.com rm -rf /root/Snaffer/dump
ssh root@kudu-mk-4.vpc.cloudera.com rm -rf /root/Snaffer/dump
ssh root@kudu-mk-3.vpc.cloudera.com rm -rf /root/Snaffer/dump
ssh root@kudu-mk-2.vpc.cloudera.com rm -rf /root/Snaffer/dump

ssh root@kudu-mk-5.vpc.cloudera.com mkdir /root/Snaffer/dump
ssh root@kudu-mk-4.vpc.cloudera.com mkdir /root/Snaffer/dump
ssh root@kudu-mk-3.vpc.cloudera.com mkdir /root/Snaffer/dump
ssh root@kudu-mk-2.vpc.cloudera.com mkdir /root/Snaffer/dump

rm -rf ./../dump
mkdir ./../dump

ls ./../dump

date
echo "./capture-frames.sh '2017-01-13 01:53:00' 60 dump"
echo "./collect.sh NAME"

