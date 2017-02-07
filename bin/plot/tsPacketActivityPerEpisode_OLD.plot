set terminal png size 900,400
set output "tsPA.png"

set datafile separator ","
set title "FOSDEM Demo - Hadoop Communication Patterns"

set ylabel "Packets per second"
set xlabel "Time"

#set xdata time
set xrange [0:1200]

set timefmt "%H:%m:%s-%S"

set key right bottom	
set grid

plot "/__A__/net_l1_df44.episode-activity.csv/part-00000" using 1:($2+1000) with lines lw 2 lt 1 title 'exp44', \
 "/__A__/net_l1_df45.episode-activity.csv/part-00000" using 1:($2+3000) with lines lw 2 lt 2 title 'exp45'

