def profileTEMPNET (df : org.apache.spark.sql.DataFrame, tab: String, res: Int) = {



//--------------------------------------
// This function extracts communication links links between ports and groups per time  
// interval ts to get a link strength.
//
//
// Time RESOLUTION has to be provided as a parameter and to be added in the result file
// name for clarity. 
//--------------------------------------
// This is link-type :2:
//--------------------------------------



	df.registerTempTable( tab )


    
    //------------------------------------------------------------------------------------
    // LINK ACTIVITY BETWEEN PORTS
    // 
    //    Source and Target are combined to define the "connectivity path in a unique way" 
    //    in order to be able to count activity on this path.
    //    We group by LABEL to get the link-strength as activity.
    //
    val qLA10 = """SELECT CONCAT(srcip,':',srcport) as SOURCE, CONCAT(dstip,':',dstport) as TARGET, CONCAT( CONCAT(srcip,':',srcport), '->', CONCAT(dstip,':',dstport)) as LABEL, ROUND(ts/""" + res + """,0) as time, ts FROM """ + tab 
    println(qLA10)    
    val r10 = sqlContext.sql( qLA10 )
    r10.registerTempTable( "tmp10" ) 
    
    
    
    val qLA20 = """SELECT first(SOURCE) as SOURCE, first(TARGET) as TARGET, COUNT(*) as WEIGHT, LABEL, ts % """ + res + """, first(ts), '2' as TYPE FROM tmp10 GROUP BY LABEL,ts ORDER BY ts"""			
    println(qLA20)
    val r20 = sqlContext.sql( qLA20 )
	r20.show(50)    
	
    //------------------------------------------------------------------------------------
	r20.coalesce(1).write.format("com.databricks.spark.csv").option("header", "true").save("/__A__/net_l2_" + res.toString + "_" + tab + ".link-activity-ports.csv")
    //------------------------------------------------------------------------------------
	
}
