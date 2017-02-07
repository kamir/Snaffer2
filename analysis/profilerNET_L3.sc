def profileTEMPNET (df : org.apache.spark.sql.DataFrame, tab: String) = {



//--------------------------------------
// This function extracts the service backbone structure and links ports to hosts.
//
//
// Time RESOLUTION is not needed here.
//--------------------------------------
// This is link-type :3:
//--------------------------------------



	df.registerTempTable( tab )


    
    //------------------------------------------------------------------------------------
    // INFRASTRUCTURE LINKS IN THE BACKGROUND BEHIND BETWEEN PORTS
    //
    val qLA10 = """SELECT srcip as SOURCE, CONCAT(srcip,':',srcport) as TARGET, 1 as WEIGHT, srcport as PORT, '0' as SOURCE_IS_HOST, '1' as TARGET_IS_HOST FROM """ + tab 
    val qLA11 = """SELECT CONCAT(dstip,':',dstport) as SOURCE, dstip as TARGET, 1 as WEIGHT, dstport as PORT, '1' as SOURCE_IS_HOST, '0' as TARGET_IS_HOST FROM """ + tab 

    println(qLA10)    
    val r10 = sqlContext.sql( qLA10 )
    val r11 = sqlContext.sql( qLA11 )
    
    r10.registerTempTable( "tmp10" ) 
    r11.registerTempTable( "tmp11" ) 
    
        

    val qLA20 = """SELECT SOURCE, TARGET, WEIGHT, '3' as TYPE, CONCAT(SOURCE,':',TARGET) AS LABEL, PORT, SOURCE_IS_HOST, TARGET_IS_HOST FROM tmp10 UNION ALL SELECT SOURCE, TARGET, WEIGHT, '3' as TYPE, CONCAT(SOURCE,':',TARGET) AS LABEL, PORT, SOURCE_IS_HOST, TARGET_IS_HOST FROM tmp11"""
    println(qLA20)
    val r20 = sqlContext.sql( qLA20 )
    r20.registerTempTable( "tmp20" ) 

    //------------------------------------------------------------------------------------
	//
	// A table with service layer data allows grouping by service layer.
	//
	//    !!! BE CAREFUL !!! 
	//    Since service layers are configurable, this may vary in real setups. 
	//     
    // In EXP44 we get a reduction from 285097 to 2997
    // 285097 => 2997
    //
    val qLA30 = """SELECT DISTINCT SOURCE, TARGET, WEIGHT, TYPE, LABEL, servicelayers.PORT, Service, Role, SOURCE_IS_HOST, TARGET_IS_HOST, z FROM tmp20 FULL OUTER JOIN servicelayers ON tmp20.PORT = servicelayers.PORT"""
    println(qLA30)

    val r30 = sqlContext.sql( qLA30 )

	//------------------------------------------------------------------------------------
	r30.coalesce(1).write.format("com.databricks.spark.csv").option("header", "true").save("/__A__/net_l3_distinct_enriched_" + tab + ".link-backbone.csv")
    //------------------------------------------------------------------------------------



   // val qLA40 = """SELECT DISTINCT SOURCE, TARGET, WEIGHT, TYPE, LABEL, df44ne.id, mcs, compnum, strongcompnum FROM tmp20 FULL OUTER JOIN df44ne ON SOURCE = df44ne.id"""
   // println(qLA40)

   // val r40 = sqlContext.sql( qLA40 )

   //------------------------------------------------------------------------------------
   // 	r40.coalesce(1).write.format("com.databricks.spark.csv").option("header", "true").save("/__A__/ABC_net_l3_distinct_node_enriched_" + tab + ".csv")
   //------------------------------------------------------------------------------------
	
}
