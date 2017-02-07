#
# Install some dependencies ...
#

sudo yum install python-devel
sudo yum install gcc
sudo yum install gcc-c++
sudo yum install libpcap-devel
sudo easy_install pip
sudo pip install avro
sudo pip install pcapy

#wget http://www.coresecurity.com/system/files/pcapy-0.10.8.tar_.gz
tar -xf pcapy-0.10.8.tar_.gz

cd pcapy-0.10.8
sudo python setup.py install
cd ..

mkdir dump

