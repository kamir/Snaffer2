def profileTEMPNET (df : org.apache.spark.sql.DataFrame, tab: String, res: Int) = {



//--------------------------------------
// This function aggregates communication links by SUBSYSTEM   
// interval ts to get a link strength.
//
//
// Time RESOLUTION has to be provided as a parameter and to be added in the result file
// name for clarity. 
//--------------------------------------
// This is link-type :4:
//--------------------------------------



	df.registerTempTable( tab )



    //
    // We use relative times based on the very first communication event in our network.
    // 
	// TS_MIN for all values, not per group !!!
	//
	val qTSMIN = """SELECT min(ts) as t0 FROM """ + tab 
    val row = sqlContext.sql( qTSMIN ).rdd.take(1)
    val t0=row(0).get(0)
    
    //------------------------------------------------------------------------------------
    // LINK ACTIVITY FROM SOURCE BY SUBSYSTEM 
    // 
    //    Source and Target are combined to define the "connectivity path in a unique way" 
    //    in order to be able to count activity on this path.
    //    We group by LABEL to get the link-strength as activity.
    //
    val qLA10 = """SELECT CONCAT(srcip,':',srcport) as SOURCE, CONCAT(dstip,':',dstport) as TARGET, 1 AS WEIGHT, srcip as LABEL, ts-""" + t0 + """ as `TIME`, ts FROM """ + tab 
    println(qLA10)    
    val r10 = sqlContext.sql( qLA10 )
    r10.registerTempTable( "tmp10" ) 
    
    val qLA33 = """SELECT LABEL, TIME, sum(WEIGHT) as WEIGHT, '43' as TYPE FROM tmp10 GROUP BY LABEL, TIME SORT BY LABEL, TIME""" 
    println(qLA33)
    val r33 = sqlContext.sql( qLA33 )
	r33.show(50)  
	
    r33.coalesce(1).write.format("com.databricks.spark.csv").option("header", "true").save("/__A__/TS_" + tab + "_LABELS_L4_host.csv")
    	  
	
    //------------------------------------------------------------------------------------
	// r20.coalesce(1).write.format("com.databricks.spark.csv").option("header", "true").save("/__A__/net_l2_" + res.toString + "_" + tab + ".link-activity-ports.csv")
    //------------------------------------------------------------------------------------
	
}
