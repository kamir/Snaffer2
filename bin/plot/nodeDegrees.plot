set terminal png size 900,400
set output "hostInDegree.png"

set datafile separator ","
set title "FOSDEM Demo - Indgree"

set ylabel "degree"
set xlabel "host"

#set xrange [0.01:6000]

set key right top	
set grid

plot "/__A__/y_df41.indegree.csv/part-00000" using 2:1 with points lw 2 lt 1 title 'episode a', \
 "/__A__/y_df42.indegree.csv/part-00000" using 2:1 with points lw 2 lt 2 title 'episode b', \
 "/__A__/y_df43.indegree.csv/part-00000" using 2:1 with points lw 2 lt 3 title 'episode c', \
 "/__A__/y_df44.indegree.csv/part-00000" using 2:1 with points lw 2 lt 4 title 'episode d'

set output "hostOutDegree.png"

set datafile separator ","
set title "FOSDEM Demo - Outdgree"

set ylabel "degree"
set xlabel "host"

#set xrange [0.01:6000]

set key right top	
set grid

plot "/__A__/y_df41.indegree.csv/part-00000" using 2:1 with points lw 2 lt 1 title 'episode a', \
 "/__A__/y_df42.outdegree.csv/part-00000" using 2:1 with points lw 2 lt 2 title 'episode b', \
 "/__A__/y_df43.outdegree.csv/part-00000" using 2:1 with points lw 2 lt 3 title 'episode c', \
 "/__A__/y_df44.outdegree.csv/part-00000" using 2:1 with points lw 2 lt 4 title 'episode d'

