#!/bin/bash

# Duration of a slice in seconds.
SLICE_DURATION=$2

DESTINATION_DIR=$3

START_TIME=$1

I=0

while :
do
  START_TIME_OFFSET=$(($I * $SLICE_DURATION))
  END_TIME_OFFSET=$(($START_TIME_OFFSET + $SLICE_DURATION))

  CAPTURE_START_TIME=`date --date="$START_TIME $START_TIME_OFFSET seconds" "+%Y-%m-%d %H:%M:%S"`
  CAPTURE_END_TIME=`date --date="$START_TIME $END_TIME_OFFSET seconds" "+%Y-%m-%d %H:%M:%S"`
  sudo python snaffer-sched.py eth0 "$CAPTURE_START_TIME" "$CAPTURE_END_TIME"

  DATE_SUFFIX=`date "+%Y-%m-%d-%H-%M-%S" --date="$CAPTURE_START_TIME"`
  mkdir $DESTINATION_DIR/$DATE_SUFFIX
  mv dump/*${DATE_SUFFIX}.avro $DESTINATION_DIR/$DATE_SUFFIX

  I=$(($I + 1))
done


