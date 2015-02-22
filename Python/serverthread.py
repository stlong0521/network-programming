########################################################################################
# Author: Tianlong Song
# Name: serverthread.py
# Description: Socket server using threads to handle clients
# Date created: 01/01/2015
########################################################################################

import socket
import sys
from thread import *

HOST = ""   # Symbolic name, meaning all available interfaces
PORT = 8888 # Arbitrary non-privileged port

s = socket.socket(socket.AF_INET,socket.SOCK_STREAM)
print "Socket created"

# Bind socket to local host and port
try:
    	s.bind((HOST,PORT))
except socket.error as msg:
    	print "Bind failed. Error Code: " + str(msg[0]) + " Message " + msg[1]
    	sys.exit()

print "Socket bind complete"

# Start listening on socket
s.listen(10)
print "Socket now listening"

# Function for handling connections. This will be used to create threads
def clientthread(conn,addr):
    	# Sending message to connected client
    	conn.send("Welcome to the server. Type something and hit enter")

    	# Infinite loop so that function does not terminate and thread does not end
    	while True:
        	# Receiving from client
        	data = conn.recv(1024)
        	if not data:
            		break
		elif data=="q" or data=="Q":
	    		print "Client (%s:%s) is offline" % addr
        	else:
            		reply = "You sent: " + data
	    		conn.sendall(reply)

    	# Came out of loop
    	conn.close()

# Talking with the client(s)
while True:
    	# Wait to accept a connection: blocking call
    	conn,addr = s.accept()
    	print "Connected with " + addr[0] + ":" + str(addr[1])

    	# Start a new thread to serve the connected client
    	start_new_thread(clientthread,(conn,addr))

# Close the socket
s.close()
print "Socket closed"
