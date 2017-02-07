# This script starts a local spark-shell on Mirko's MacBook
#
# Relevant JAR files from related projects are added to the Spark-Context
#

#
# We develop the Metadata capturing module for an advances data governance system
# using Cloudera Navigator, and an additional SOLR collection.
#

export SPARK_LOCAL_IP="127.0.0.1"

#cd ../scripts

#
# ADD JAR File for dspm-toolkit to the Spark-Shell
# 
#  Hadoop.TS: 						hts-training-exercises-0.3-jar-with-dependencies.jar
#  Spark convenience tools:  		SparkShellUtilities.jar
#  Gephi-Toolkit for local raphs:  	gephi-toolkit-0.9.2-20170113.202843-77-all.jar"
#  ETOSHA-Network-Profile:      
export JARS="--jars /GITHUB/SparkShellUtilities/dist/SparkShellUtilities.jar,/sparkws/bin/gephi-toolkit-0.9.2-20170113.202843-77-all.jar,/GITHUB/ETOSHA.WS/etosha/etosha-parent/etosha-network-profiler/target/etosha-network-profiler-0.9.0-SNAPSHOT-jar-with-dependencies.jar" 

#
#  This caused a problem with Hive-Metastore ... !!!
#  
#/GITHUB/HadoopTS.3/GITHUB/hts-training-exercises/target/hts-training-exercises-0.3-jar-with-dependencies.jar,




#
# Where did we start the shell?
#
pwd
ls



#
# You can use * for import all jars into a folder when adding in conf/spark-defaults.conf .
#
#spark.driver.extraClassPath /fullpath/*
#spark.executor.extraClassPath /fullpath/*



/Users/kamir/Downloads/spark-1.6.3-bin-hadoop2.6/bin/spark-shell --master local[6] --conf spark.executor.memory=14g $JARS --conf spark.jars.packages=tdebatty:spark-knn-graphs:0.13,com.databricks:spark-avro_2.10:2.0.1,com.github.potix2:spark-google-spreadsheets_2.10:0.4.0,com.databricks:spark-csv_2.10:1.4.0


#
# Goto Spark 2 ...
#
#/Users/kamir/Downloads/spark-2.0.2-bin-hadoop2.6/bin/spark-shell --master local[2] --conf spark.ui.port=4044 
#
#



