def profileTEMPNET (df : org.apache.spark.sql.DataFrame, tab: String) = {

	df.registerTempTable( tab )
	df.cache


    
    //------------------------------------------------------------------------------------
    // GENERATE A TIME SERIES with overall activity for all available packets from / to 
	// all nodes on which Snaffer was working.
	//

	// TS_MIN for all values, not per group !!!
	val qTSMIN = """SELECT min(ts) as t0 FROM """ + tab 
    val row = sqlContext.sql( qTSMIN ).rdd.take(1)
    val t0=row(0).get(0)
    
    val qTS1 = """SELECT ts-""" + t0 + """ as `t`, count(*) as activity FROM """ + tab + """ GROUP BY ts ORDER BY ts ASC"""
    println(qTS1)
    val result = sqlContext.sql( qTS1 )
    result.show( 360 )
    //------------------------------------------------------------------------------------
	result.coalesce(1).write.format("com.databricks.spark.csv").save("/__A__/" + tab + ".episode-activity.csv")
    //------------------------------------------------------------------------------------
    
	
	
    //------------------------------------------------------------------------------------
    
    
}
