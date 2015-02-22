########################################################################################
# Author: Tianlong Song
# Name: client.py
# Description: Socket client
# Date created: 01/03/2015
########################################################################################

import socket
import sys

HOST = socket.gethostname() # Get localhost
PORT = 8888 # Arbitrary non-privileged port

s = socket.socket(socket.AF_INET,socket.SOCK_STREAM)
print "Socket created"

# Connect to server
s.connect((HOST,PORT))

# Talking with the server
data = ""
while data!="q" and data!="Q":
    	# Receiving from server
	data = s.recv(1024)
    	print "Feedback from server: " + data
    	# Sending to server
    	data = raw_input("Sent: ")
    	if not data:
        	break
    	s.sendall(data)

# Close the socket
s.close()
print "Socket closed"
