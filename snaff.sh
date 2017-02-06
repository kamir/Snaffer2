sudo python snaffer.py $1 $2 
sudo hadoop fs -put ./dump/*.avro /user/cloudera/TCPDUMP/raw
rm -f ./dump/*
sudo hdfs dfs -ls /user/cloudera/TCPDUMP/raw


