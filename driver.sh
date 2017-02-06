yarn jar /opt/cloudera/parcels/CDH/lib/hadoop-mapreduce/hadoop-mapreduce-examples.jar teragen -Dmapred.map.tasks=30 250000000 /user/zkiss/TS_input1

yarn jar /opt/cloudera/parcels/CDH/lib/hadoop-mapreduce/hadoop-mapreduce-examples.jar terasort /user/zkiss/TS_input1 /user/zkiss/TS_output1

yarn jar /opt/cloudera/parcels/CDH/lib/hadoop-mapreduce/hadoop-mapreduce-examples.jar teravalidate /user/zkiss/TS_output1 /user/zkiss/TS_validate1

hdfs dfs -rm -R -skipTrash /user/zkiss/TS_input1
hdfs dfs -rm -R -skipTrash /user/zkiss/TS_output1
hdfs dfs -rm -R -skipTrash /user/zkiss/TS_validate1
