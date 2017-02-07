
//
// This script defines the datasets, used for the snaffer-analysis procedure.
//
// ------------------------------------------------------------------------------

//
// You have to adjust the paths !!!
// 
// Not all datasets are needed, typically, you would need at least one "dump-folder"
// containing the Avro files for one experiment from all relevant hosts.
// This script prepares your Spark-shell session and allows us to repeat the procedures.
//

import com.databricks.spark.avro._

// -------
//
// ROUND 2
//
// -------

//val df1a = sqlContext.read.avro("/GITHUB.cloudera.internal/SNAFFBASE/exp1/dump/2017-01-11-11-20-00/*.avro")
//val df1b = sqlContext.read.avro("/GITHUB.cloudera.internal/SNAFFBASE/exp1/dump/2017-01-11-11-26-00/*.avro")
//val df1c = sqlContext.read.avro("/GITHUB.cloudera.internal/SNAFFBASE/exp1/dump/2017-01-11-11-32-00/*.avro")
//val df1d = sqlContext.read.avro("/GITHUB.cloudera.internal/SNAFFBASE/exp1/dump/2017-01-11-11-38-00/*.avro")
//val df1e = sqlContext.read.avro("/GITHUB.cloudera.internal/SNAFFBASE/exp1/dump/2017-01-11-11-44-00/*.avro")
//val df1f = sqlContext.read.avro("/GITHUB.cloudera.internal/SNAFFBASE/exp1/dump/2017-01-11-11-50-00/*.avro")
//val df1g = sqlContext.read.avro("/GITHUB.cloudera.internal/SNAFFBASE/exp1/dump/2017-01-11-11-56-00/*.avro")

//val df41 = sqlContext.read.avro("/GITHUB.cloudera.internal/SNAFFBASE/exp41/dump/*/*.avro")
//val df42 = sqlContext.read.avro("/GITHUB.cloudera.internal/SNAFFBASE/exp42/dump/*/*.avro")
//val df43 = sqlContext.read.avro("/GITHUB.cloudera.internal/SNAFFBASE/exp43/dump/*/*.avro")


val df44 = sqlContext.read.avro("/__A__/exp44/dump/*/*.avro")
val df45 = sqlContext.read.avro("/__A__/exp45/dump/*/*.avro")

df44.cache()
df44.count()

df45.cache()
df45.count()

    
//-----------
// Masterdata for Service => PORT => Mappings
//
//     https://docs.google.com/spreadsheets/d/1pdHX94YoHG29M2CT5JF2_V7AvdrBWeLUv7Emosy8M8k/edit#gid=0
//
//     Please copy this document into your own google docs folder.
//     Now you have to create the credentials. 
//     Adjust the following three variables:
//
//
val account_id = "..."
val keyfile_path = "..."
val doc_id = "..."
//
// 
// TODO: Read this from a properties file ...
// 


//-----------
// READ portlist
//
val portList = sqlContext.read.
    format("com.github.potix2.spark.google.spreadsheets").
    option("serviceAccountId", account_id ).
    option("credentialPath", keyfile_path ).
    load( doc_id + "/masterdata")

portList.printSchema()
portList.count
 
 
 
//-----------
// READ layers
//
val layers = sqlContext.read.
    format("com.github.potix2.spark.google.spreadsheets").
    option("serviceAccountId", account_id ).
    option("credentialPath", keyfile_path ).
    load( doc_id + "/layers")

layers.printSchema()
layers.count

portList.registerTempTable("portlist")
layers.registerTempTable("layers")

 
//-----------
// JOIN Portlist and Layers
//
val servicelayers = sqlContext.sql(
  "SELECT Port, Role, portlist.Service, z FROM portlist, layers " +
  "WHERE portlist.Service = layers.Service")
  
servicelayers.show()
servicelayers.registerTempTable("servicelayers")
servicelayers.printSchema()



// -------
//
// ROUND 2
//
// -------

//
//  This dataset is generated via Gephi and introduces "topology based data enrichment".
//
val df44nodesEnrich = sqlContext.read.format("com.databricks.spark.csv").option("header", "true").option("inferSchema", "true").load("/__A__/df44_nodes.csv")
df44nodesEnrich.registerTempTable("df44ne")

val df45nodesEnrich = sqlContext.read.format("com.databricks.spark.csv").option("header", "true").option("inferSchema", "true").load("/__A__/df45_nodes.csv")
df45nodesEnrich.registerTempTable("df45ne")
	

