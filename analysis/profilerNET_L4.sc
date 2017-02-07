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
    
    
    
    val qLA20 = """SELECT SOURCE, df44ne.id, TARGET, WEIGHT, ts/""" + res + """ as TIME_SCALED, ts, TIME, '4' as TYPE, LABEL, mcs, compnum, strongcompnum FROM tmp10 FULL OUTER JOIN df44ne ON SOURCE = df44ne.id """ 
    println(qLA20)
    val r20 = sqlContext.sql( qLA20 )
	r20.show(50)  
	r20.registerTempTable( "tmp20" ) 
    r20.cache()



    val qLA30 = """SELECT mcs, TIME, count(*) as WEIGHT, '40' as TYPE FROM tmp20 GROUP BY mcs, TIME """ 
    println(qLA30)
    val r30 = sqlContext.sql( qLA30 )
	r30.show(50)  
    r30.registerTempTable( "tmp30" ) 
    val qLA30_1 = """SELECT mcs, TIME, first(WEIGHT) as WEIGHT FROM tmp30 GROUP BY mcs, TIME ORDER BY mcs, TIME"""
    println(qLA30_1)
    val r30_1 = sqlContext.sql( qLA30_1 )
	r30_1.show(50)  
    r30_1.coalesce(1).write.format("com.databricks.spark.csv").option("header", "true").save("/__A__/TS_" + tab + "_mcs_L4.csv")



    val qLA31 = """SELECT compnum, TIME, count(*) as WEIGHT, '41' as TYPE FROM tmp20 GROUP BY compnum, TIME """ 
    println(qLA31)
    val r31 = sqlContext.sql( qLA31 )
	r31.show(50)  
    r31.registerTempTable( "tmp31" ) 
    val qLA31_1 = """SELECT compnum, TIME, first(WEIGHT) as WEIGHT FROM tmp31 GROUP BY compnum, TIME ORDER BY compnum, TIME"""
    println(qLA31_1)
    val r31_1 = sqlContext.sql( qLA31_1 )
	r31_1.show(50)  
    r31_1.coalesce(1).write.format("com.databricks.spark.csv").option("header", "true").save("/__A__/TS_" + tab + "_compnum_L4.csv")



    val qLA32 = """SELECT strongcompnum, TIME, count(*) as WEIGHT, '42' as TYPE FROM tmp20 GROUP BY strongcompnum, TIME """ 
    println(qLA32)
    val r32 = sqlContext.sql( qLA32 )
	r32.show(50)  
    r32.registerTempTable( "tmp32" ) 
    val qLA32_1 = """SELECT strongcompnum, TIME, first(WEIGHT) as WEIGHT FROM tmp32 GROUP BY strongcompnum, TIME ORDER BY strongcompnum, TIME"""
    println(qLA32_1)
    val r32_1 = sqlContext.sql( qLA32_1 )
	r32_1.show(50)  
    r32_1.coalesce(1).write.format("com.databricks.spark.csv").option("header", "true").save("/__A__/TS_" + tab + "_strongcompnum_L4.csv")

    
    
    val qLA33 = """SELECT LABEL, TIME, count(*) as WEIGHT, '43' as TYPE FROM tmp20 GROUP BY LABEL, TIME """ 
    println(qLA33)
    val r33 = sqlContext.sql( qLA33 )
	r33.show(50)  
    r33.registerTempTable( "tmp33" ) 
    val qLA33_1 = """SELECT LABEL, TIME, first(WEIGHT) as WEIGHT FROM tmp33 GROUP BY LABEL, TIME ORDER BY Label, TIME"""
    println(qLA33_1)
    val r33_1 = sqlContext.sql( qLA33_1 )
	r33_1.show(50)  
    r33_1.coalesce(1).write.format("com.databricks.spark.csv").option("header", "true").save("/__A__/TS_" + tab + "_LABELS_L4.csv")
    	  
	
    //------------------------------------------------------------------------------------
	// r20.coalesce(1).write.format("com.databricks.spark.csv").option("header", "true").save("/__A__/net_l2_" + res.toString + "_" + tab + ".link-activity-ports.csv")
    //------------------------------------------------------------------------------------
	
}
