#
# Prepare some data directories ...
#

hadoop fs -mkdir /SNAFFBASE
hadoop fs -mkdir /user/cloudera
hadoop fs -mkdir /user/cloudera/TCPDUMP/
hadoop fs -mkdir /user/cloudera/TCPDUMP/raw
hadoop fs -mkdir /user/cloudera/TCPDUMP/META
hadoop fs -put ./schema/* /user/cloudera/TCPDUMP/META


