########################################################################################
# Author: Tianlong Song
# Name: serverselect.py
# Description: Socket server using select() to handle clients instead of threads
# Date created: 01/01/2015
########################################################################################

import socket,select

HOST = ""   # Symbolic name, meaning all available interfaces
PORT = 8888 # Arbitrary non-privileged port
CONNECTION_LIST = [] # List of sockets
DICT = {} # Use a dictionary to store active (socket,addr) pairs

server_socket = socket.socket(socket.AF_INET,socket.SOCK_STREAM)
server_socket.setsockopt(socket.SOL_SOCKET, socket.SO_REUSEADDR, 1)
print "Server socket created"

# Bind socket to local host and port
try:
	server_socket.bind((HOST,PORT))
except socket.error as msg:
    	print "Bind failed. Error Code: " + str(msg[0]) + " Message " + msg[1]
    	sys.exit()
print "Socket bind complete"

# Start listening on socket
server_socket.listen(10)
print "Socket now listening"

# Add server socket to the list of readable connections
CONNECTION_LIST.append(server_socket);
print "Server started on port " + str(PORT)

# Talking with the client(s)
while True:
    	# Get the list sockets which are ready to be read through select
    	read_sockets,write_sockets,error_sockets = select.select(CONNECTION_LIST,[],[])

    	for sock in read_sockets:
        	# Deal with new connection
        	if sock==server_socket:
            		sockfd, addr = server_socket.accept()
            		CONNECTION_LIST.append(sockfd)
	    		DICT[sockfd] = addr
	    		print "Client (%s:%s) connected" % addr
	    		# Sending message to connected client
            		sockfd.send("Welcome to the server. Type something and hit enter")
        	# Some incoming message from a client
        	else:
            		try:
				data = sock.recv(1024)
				if data=="q" or data=="Q": 
					print "Client (%s:%s) is offline" % DICT[sock]
					sock.close()
					CONNECTION_LIST.remove(sock)
					del DICT[sock]
				elif data:
		    			reply = "You sent: " + data
		    			sock.sendall(reply)
            		except:
                		sock.close()
                		CONNECTION_LIST.remove(sock)
				del DICT[sock]

# Close the socket
server_socket.close()
print "Server socket closed"
