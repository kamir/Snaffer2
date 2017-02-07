##
# Show groups based on strongcompnum
#
#

val sqlA = """SELECT strongcompnum, collect_list(id) as total FROM df44ne GROUP BY strongcompnum ORDER BY total DESC"""
val sqlB = """SELECT strongcompnum, collect_list(id) as total FROM df45ne GROUP BY strongcompnum ORDER BY total DESC"""

val a = sqlContext.sql( sqlA )
val b = sqlContext.sql( sqlB )

a.coalesce(1).write.format("com.databricks.spark.csv").option("header", "true").save("/__A__/groups_44.csv")
b.coalesce(1).write.format("com.databricks.spark.csv").option("header", "true").save("/__A__/groups_45.csv")
