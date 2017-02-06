# One Requires ROOT priviliges to execte this Program
# To Stop The Execution Of The Program Press CTRL + C or Command + C
# Please Specify The Capture Device Name As Command Line Arguments

import pcapy
import socket
import struct 
import time
import sys
import string

ETHERNET_HEADER_LENGTH = 14
ETHERNET_UNPACK_FORMAT = '!6s6sH'
IP_UNPACK_FORMAT = '!BBHHHBBH4s4s'
TCP_UNPACK_FORMAT = '!HHLLBBHHH'
UDP_UNPACK_FORMAT = '!HHHH'
ICMP_UNPACK_FORMAT = '!BBH'

LOG_FILE = 'Log'
DATA_DUMP = 'Dump'

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

def createNewDump():
	f=open(DATA_DUMP + '.txt','w')
	f.close()

def dumpData(Packet_Store):
	try:
		f=open(DATA_DUMP + '.txt','a+')
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
	
	if len(sys.argv) < 2 or len(sys.argv) > 2:
		logWrite('Please Specify The Capture Device Name.')
		logWrite('sudo python Sniffer.py <Capture_Device_Name>')
		logWrite('Example: sudo python sniffer.py eth0')
		return
		
	Capture_Device = sys.argv[1]		
	Capture_Device = Select_Capture_Devices(Capture_Device)
	if Capture_Device is None:
		return
	
	logWrite('Sniffing Device Selected %s'%(Capture_Device))
	Capture_Device = pcapy.open_live(Capture_Device , 65536 , 1 , 0)
	
	logWrite('Program Execution Started At ' + time.strftime("%d-%m-%Y %H:%M:%S"))
	createNewDump()
	
	while True:
		try:
			Packet_Header,Packet = Capture_Device.next()
			Packet_Store = Analyze_Packet(Packet)
			if Packet_Store is not None:
				logWrite(Packet_Store)
				dumpData(Packet_Store)
		except KeyboardInterrupt as e:
			logWrite('Network Sniffing Stopped')
			break
		except Exception as e:
			logWrite('Exception Occured In Analyze Packet Loop')
			logWrite(e.__class__)
			logWrite(str(e))
			pass

if __name__ == '__main__':
	main()

