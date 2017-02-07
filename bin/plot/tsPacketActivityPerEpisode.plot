set terminal png size 900,400
set output "tsPA.png"

set datafile separator ","
set title "FOSDEM Demo - Hadoop Communication Patterns"

set ylabel "Packets per second"
set xlabel "Time"

#set xdata time
set xrange [0:600]

set timefmt "%H:%m:%s-%S"

set key right bottom	
set grid

plot "/__A__/df41.episode-activity.csv/part-00000" using 1:($2+0) with lines lw 2 lt 1 title 'episode a', \
 "/__A__/df42.episode-activity.csv/part-00000" using 1:($2+2000) with lines lw 2 lt 2 title 'episode b', \
 "/__A__/df43.episode-activity.csv/part-00000" using 1:($2+4000) with lines lw 2 lt 3 title 'episode c', \
 "/__A__/df44.episode-activity.csv/part-00000" using 1:($2+6000) with lines lw 2 lt 4 title 'episode d', \
 "/__A__/df45.episode-activity.csv/part-00000" using 1:($2+8000) with lines lw 2 lt 4 title 'episode e'

