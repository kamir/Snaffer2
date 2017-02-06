# One Requires ROOT priviliges to execte this Program
# To Stop The Execution Of The Program Press CTRL + C or Command + C
# Please Specify The Capture Device Name As Command Line Arguments

#
#  This version of Sniffer writes packet Metadata and packet data into a CSV 
#  file using the | symbol as delimitter.
#
#  Furthermore, an Avro file is generated, including all the content, including the raw data.
#
#  1) We want to create a network of all traffic flows from the Metadata. 
#  2) The raw data is used for advanced analysis for identification of unencrypted 
#     network traffic in Hadoop clusters.

#
# This tool supports the cluster-hardening procedure.
#

import pcapy
import socket
import struct 
import time
import sys
import string
import array
import datetime
from avro import schema, datafile, io
import pprint

from time import gmtime, strftime
from impacket import ImpactDecoder

#
# The Avro dumpfile will be in the dump folder and it contains hostname and timestamp
# of the start of the dump.
#
hostname = socket.gethostname()
current_time = strftime("%Y-%m-%d_%H-%M-%S", gmtime())

df_writer = None
LOG_FILE = 'Log'
DATA_DUMP = 'unknown'
Dump_Device = '-'

ETHERNET_HEADER_LENGTH = 14
ETHERNET_UNPACK_FORMAT = '!6s6sH'
IP_UNPACK_FORMAT = '!BBHHHBBH4s4s'
TCP_UNPACK_FORMAT = '!HHLLBBHHH'
UDP_UNPACK_FORMAT = '!HHHH'
ICMP_UNPACK_FORMAT = '!BBH'

def logWrite(Data,Display = True):
	try:
		f = open(LOG_FILE + '.log','a+')
		if Display == True:
			print Data
		f.write(str(Data))
		f.write('\n')
	except Exception as e:
		print 'Exception Occured In Log Write Method'
		print e.__class__
		print str(e)	
	else:
		f.close()

def createNewDump(DATA_DUMP):
	f=open(DATA_DUMP,'w')
	f.close()


def dumpData2Avro(Packet_RECORD,writer,id):
	try:
		df_writer.append( Packet_RECORD )
	except Exception as e:
		logWrite('Exception Occured In Dumping Data Method')
		logWrite(e.__class__)
		logWrite(str(e))	

def dumpData(Packet_Store):
	try:
		f=open(DATA_DUMP,'a+')
		f.write(str(Packet_Store))
	except Exception as e:
		logWrite('Exception Occured In Dumping Data Method')
		logWrite(e.__class__)
		logWrite(str(e))	
	else:
		f.close()

def removeNonPrintable(Text):
	return filter(lambda x: x in string.printable, Text)
		
def Select_Capture_Devices(Capture_Device):
	Devices =  pcapy.findalldevs()
		
	if Capture_Device not in Devices:
		logWrite('Capture Device Name Not Recongnized. Please Try Again')
		Capture_Device = None
			
	return Capture_Device

def Analyze_Packet_AVRO(Packet, hdr, ZP):
	
	Packet_Store = None
	
	Ethernet_Header_Length = 14
	
	Ethernet_Header = Packet[:Ethernet_Header_Length]
	
        TS = Packet[Ethernet_Header_Length:4+Ethernet_Header_Length]

	decoder = ImpactDecoder.EthDecoder()
	ethernet_pck = decoder.decode(Packet)
	
	#
	# http://securityblog.gr/1032/simple-python-sniffer-with-pcapy-and-impacket/
	#
	
	try:
	    ts = hdr.getts()[0]
	except Exception, e:
	    logging.error("Snaffer:receive_packets : failed getting timestamp from header. Exception: %s" % str(e))
	                              
	print '***************'
	print ts

	print(
	    datetime.datetime.fromtimestamp(
	            int("1284101485")
	                ).strftime('%Y-%m-%d %H:%M:%S.%f')
	                )
	                
	
	
	print '***************'

	
	Ethernet_Header = struct.unpack(ETHERNET_UNPACK_FORMAT,Ethernet_Header)
	Ethernet_Protocol = socket.ntohs(Ethernet_Header[2])



	
	if Ethernet_Protocol == 8:
		IP_Header = Packet[Ethernet_Header_Length : Ethernet_Header_Length + 20]
		IP_Header = struct.unpack(IP_UNPACK_FORMAT,IP_Header)
		
		IP_Header_Length = IP_Header[0] & 0xF
		IP_Header_Length = IP_Header_Length * 4
		Total_Header_Length = IP_Header_Length + Ethernet_Header_Length
		
		IP_Protocol = IP_Header[6]
		
		Source_IP = socket.inet_ntoa(IP_Header[8])
		Destination_IP = socket.inet_ntoa(IP_Header[9])


		
		Protocol_Type = 'Unknown'
		if IP_Protocol == 6 :
			Protocol_Type = 'TCP'
			Packet_Dump = Analyze_Protocol(Packet,Total_Header_Length,20,TCP_UNPACK_FORMAT)

			Ports_Dump = Analyze_Ports(Packet,Total_Header_Length,20,TCP_UNPACK_FORMAT)

		elif IP_Protocol == 17:	
			Protocol_Type = 'UDP'
			Packet_Dump = Analyze_Protocol(Packet,Total_Header_Length,8,UDP_UNPACK_FORMAT)
		elif IP_Protocol == 1:	
			Protocol_Type = 'ICMP'	
			Packet_Dump = Analyze_Protocol(Packet,Total_Header_Length,4,ICMP_UNPACK_FORMAT)
			
		Packet_Store = '%s|%s|%s|%s|%s|'%(Source_IP, Destination_IP,Protocol_Type,Total_Header_Length,Packet_Dump)
		
		record = {      "packet_grab_nr": ZP,
			"srcIP":        Source_IP,
			"dstIP":        Destination_IP,
			"srcPort":      Ports_Dump[0],
			"dstPort":      Ports_Dump[1],
			"seqNumber":    0,
			"ackNumber":    0,
			"data":         Packet_Dump,
			"dumpHost":     hostname,
			"protocolType": Protocol_Type,
			"dumpDevice":   Dump_Device,
			"ts":		ts }		
  
	return record	

def Analyze_Packet(Packet):
	
	Packet_Store = None
	
	Ethernet_Header_Length = 14
	
	Ethernet_Header = Packet[:Ethernet_Header_Length]
	Ethernet_Header = struct.unpack(ETHERNET_UNPACK_FORMAT,Ethernet_Header)
	Ethernet_Protocol = socket.ntohs(Ethernet_Header[2])
	
	if Ethernet_Protocol == 8:
		IP_Header = Packet[Ethernet_Header_Length : Ethernet_Header_Length + 20]
		IP_Header = struct.unpack(IP_UNPACK_FORMAT,IP_Header)
		
		IP_Header_Length = IP_Header[0] & 0xF
		IP_Header_Length = IP_Header_Length * 4
		Total_Header_Length = IP_Header_Length + Ethernet_Header_Length
		
		IP_Protocol = IP_Header[6]
		
		Source_IP = socket.inet_ntoa(IP_Header[8])
		Destination_IP = socket.inet_ntoa(IP_Header[9])
		
		Protocol_Type = 'Unknown'
		if IP_Protocol == 6 :
			Protocol_Type = 'TCP'
			Packet_Dump = Analyze_Protocol(Packet,Total_Header_Length,20,TCP_UNPACK_FORMAT)
		elif IP_Protocol == 17:	
			Protocol_Type = 'UDP'
			Packet_Dump = Analyze_Protocol(Packet,Total_Header_Length,8,UDP_UNPACK_FORMAT)
		elif IP_Protocol == 1:	
			Protocol_Type = 'ICMP'	
			Packet_Dump = Analyze_Protocol(Packet,Total_Header_Length,4,ICMP_UNPACK_FORMAT)
			
		Packet_Store = '%s|%s|%s|%s|%s|'%(Source_IP, Destination_IP,Protocol_Type,Total_Header_Length,Packet_Dump)
		
	return Packet_Store		

def Analyze_Ports(Packet,Total_Header_Length,Protocol_Length,Unpack_Format):
	
	'''
	For TCP/UDP, Header[0] and Header[1] are Source And Destination Ports recpectively
	For ICMP, Header[0] and Header[1] are ICMP Type And Code respectively
	'''
	
	Header = Packet[Total_Header_Length : Total_Header_Length + Protocol_Length]
	Header = struct.unpack(Unpack_Format,Header)
	Data = Packet[Total_Header_Length + Protocol_Length :  ]
	Data = removeNonPrintable(Data)
	Data = Data.replace('\n','').replace('\r','').strip()
	
	Ports_Dump = array.array('i',[0,0])

	if Protocol_Length == 4:
		Ports_Dump[0] = Header[0]
		Ports_Dump[1] = Header[1]
	else:
		Ports_Dump[0] = Header[0]
		Ports_Dump[1] = Header[1]
	
	return Ports_Dump 

def Analyze_Protocol(Packet,Total_Header_Length,Protocol_Length,Unpack_Format):
	
	'''
	For TCP/UDP, Header[0] and Header[1] are Source And Destination Ports recpectively
	For ICMP, Header[0] and Header[1] are ICMP Type And Code respectively
	'''
	
	Header = Packet[Total_Header_Length : Total_Header_Length + Protocol_Length]
	Header = struct.unpack(Unpack_Format,Header)
	Data = Packet[Total_Header_Length + Protocol_Length :  ]
	Data = removeNonPrintable(Data)
	Data = Data.replace('\n','').replace('\r','').strip()
	
	if Protocol_Length == 4:
		Packet_Dump = '%s|%s|%s|%s'%(Header[0],Header[1],Header[2],Data)
	else:
		Packet_Dump = '%s|%s|%s|%s|%s'%(Header[0],Header[1],Header[2],Header[3],Data)
	
	return Packet_Dump 
	
def main():

	global df_writer	
	global DATA_DUMP
	global Dump_Device

	if len(sys.argv) < 3 or len(sys.argv) > 3:
		logWrite('Please Specify The Capture Device Name and the number of packages to collect.')
		logWrite('sudo python snaffer.py <Capture_Device_Name> <NR_OF_PACKETS>')
		logWrite('Example: sudo python snaffer.py eth0 1000')
		return
		
	Capture_Device = sys.argv[1]		
	ZPMAX = int(sys.argv[2])		

	Dump_Device = Capture_Device
	
	OUTFILE_NAME = 'dump/packetdump_' + hostname + '_' + Capture_Device + '_' + current_time
	OUTFILE_NAME_AVRO = OUTFILE_NAME + '.avro'
	OUTFILE_NAME_TXT = OUTFILE_NAME + '.txt'

	DATA_DUMP = OUTFILE_NAME_TXT

	INPUT_SCHEMA_NAME = 'schema/packet.avsc'

	# Open a file
	fo = open(INPUT_SCHEMA_NAME, "r+")
	SCHEMA_STR = fo.read();
	print "Read String is : ", SCHEMA_STR
	# Close opend file
	fo.close()

	SCHEMA = schema.parse(SCHEMA_STR)

	# Create a 'record' (datum) writer
	rec_writer = io.DatumWriter(SCHEMA)

	# Create a 'data file' (avro file) writer
	df_writer = datafile.DataFileWriter(open(OUTFILE_NAME_AVRO, 'wb'),rec_writer,writers_schema = SCHEMA)




	
	Capture_Device = Select_Capture_Devices(Capture_Device)
	if Capture_Device is None:
		return
	
	logWrite('Sniffing Device Selected %s'%(Capture_Device))
	Capture_Device = pcapy.open_live(Capture_Device , 65536 , 1 , 0)
	
	logWrite('Program Execution Started At ' + time.strftime("%d-%m-%Y %H:%M:%S"))
	logWrite('Nr of packets to dump : ' + str(ZPMAX) )

	createNewDump(DATA_DUMP)

	ZP = 0
	while True :
		try:
			if ZP > ZPMAX: 
				break

			Packet_Header,Packet = Capture_Device.next()
			Packet_Store = Analyze_Packet(Packet)

			if Packet_Store is not None:
				Packet_RECORD = Analyze_Packet_AVRO(Packet,Packet_Header,ZP)
				logWrite("(" + str(ZP) + " : " + str(ZPMAX) + ") ===> " + Packet_Store)
				#logWrite(Packet_Store)
				#dumpData(Packet_Store)
				dumpData2Avro(Packet_RECORD,df_writer,ZP)
				ZP = ZP + 1
		except KeyboardInterrupt as e:
			logWrite('Network Sniffing Stopped')
			break
		except Exception as e:
			logWrite('!!! Warning !!! Exception Occured In Analyze Packet Loop')
			logWrite(e.__class__)
			logWrite(str(e))
			pass

	df_writer.close()


if __name__ == '__main__':
	main()

