
// 
// THIS IS LINK TYPE 1
// 
//   srcip:srcprt => dstip:dstport
//
// SCHEMA: SOURCE, TARGET, WEIGHT, TIME, TYPE
//
//    WEIGHT = 1 (conts.)
//    TIME = 1 (const.) 
//

def profileTEMPNET (df : org.apache.spark.sql.DataFrame, tab: String) = {

	df.registerTempTable( tab )
	df.cache

//
// NEXT LIBs TO ADD ...
// 
//   RUN: EtoshaReferenceProfiler
//   LIB: /GITHUB/ETOSHA.WS/etosha/etosha-parent/etosha-network-profiler/target/etosha-network-profiler-0.9.0-SNAPSHOT-jar-with-dependencies.jar
    
    //------------------------------------------------------------------------------------
    // GENERATE A TIME-Dependent-Network 
	//

    //
    // We use relative times based on the very first communication event in our network.
    // 
	// TS_MIN for all values, not per group !!!
	//
	val qTSMIN = """SELECT min(ts) as t0 FROM """ + tab 
    val row = sqlContext.sql( qTSMIN ).rdd.take(1)
    val t0=row(0).get(0)
    
    //
    // COMPOSE AN Communication Event as link in a temporal communication network
    //
    val qTS1 = """SELECT CONCAT(srcip,':',srcport) as SOURCE, CONCAT(dstip,':',dstport) as TARGET, 1 as WEIGHT, ts-""" + t0 + """ as `TIME`, 1 as `TYPE` FROM """ + tab 
    println("#LAYER-1 Link-Query: " + qTS1)
    
    val r1 = sqlContext.sql( qTS1 )
    r1.show( 5 )
    r1.registerTempTable( "temp" )
    val qTS2 = """SELECT TIME, sum(WEIGHT) FROM temp GROUP BY TIME ORDER BY TIME"""  
    
    

    val result = sqlContext.sql( qTS2 )
    result.show( 5 )
    
    
    //------------------------------------------------------------------------------------
	result.coalesce(1).write.format("com.databricks.spark.csv").save("/__A__/net_l1_" + tab + ".episode-activity.csv")
    //------------------------------------------------------------------------------------
    
    //------------------------------------------------------------------------------------
    
    
}


