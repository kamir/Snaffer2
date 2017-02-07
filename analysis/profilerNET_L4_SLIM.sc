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
    
    
    
    val qLA20 = """SELECT SOURCE, """ + tab + """ne.id, TARGET, WEIGHT, ts/""" + res + """ as TIME_SCALED, ts, TIME, '4' as TYPE, LABEL, componentnumber as compnum, strongcompnum FROM tmp10 FULL OUTER JOIN """ + tab + """ne ON SOURCE = """ + tab + """ne.id """ 
    println(qLA20)
    val r20 = sqlContext.sql( qLA20 )
	r20.show(50)  
	r20.registerTempTable( "tmp20" ) 
    r20.cache()




//    val qLA31 = """SELECT compnum, TIME, count(*) as WEIGHT, '41' as TYPE FROM tmp20 GROUP BY compnum, TIME """ 
 //   println(qLA31)
  // val r31 = sqlContext.sql( qLA31 )
//	r31.show(50)  
  //  r31.registerTempTable( "tmp31" ) 
  //  val qLA31_1 = """SELECT compnum, TIME, first(WEIGHT) as WEIGHT FROM tmp31 GROUP BY compnum, TIME ORDER BY compnum, TIME"""
 //   println(qLA31_1)
//    val r31_1 = sqlContext.sql( qLA31_1 )
//	r31_1.show(50)  
//    r31_1.coalesce(1).write.format("com.databricks.spark.csv").option("header", "true").save("/__A__/TS_" + tab + "_compnum_L4.csv")



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

     
    	  
	
    //------------------------------------------------------------------------------------
	// r20.coalesce(1).write.format("com.databricks.spark.csv").option("header", "true").save("/__A__/net_l2_" + res.toString + "_" + tab + ".link-activity-ports.csv")
    //------------------------------------------------------------------------------------
	
}






