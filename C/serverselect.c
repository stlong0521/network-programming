//**************************************************************************************
// Author: Tianlong Song
// Name: serverselect.c
// Description: Socket server using select to handle clinets
// Date created: 01/05/2015
//*************************************************************************************

#include <stdio.h>
#include <string.h>
#include <sys/types.h>
#include <sys/socket.h>
#include <netinet/in.h>
#include <arpa/inet.h>

#define PORT 8888	// The port number to be used
#define BACKLOG 10	// Maximum length of connection queue

int main() {
	struct sockaddr_in server,client;
	int sockfd,newfd;
	char message[1024];
	int c,i;
	const char *s = "Welcome to the server. Type something and hit enter.";
	
	fd_set master; // Master file descriptor list
	fd_set read_fds; // Temp file descriptor list
	int fdmax; // Maximum file descriptor number

	// Clear the master and temp list
	FD_ZERO(&master);
	FD_ZERO(&read_fds);

	// Settings
	server.sin_family = AF_INET; // IPV4
	server.sin_addr.s_addr = INADDR_ANY; // Any address
	server.sin_port = htons(PORT); //Port #, convert byte order to network format
	
	// Create server socket
	sockfd = socket(AF_INET,SOCK_STREAM,0);
	if(sockfd<0) {
		printf("Could not create socket!\n");
	}
	printf("Server socket created\n");

	// Bind socket to local host and port
	if(bind(sockfd,(struct sockaddr *)&server,sizeof(server))<0) {
		printf("Socket bind failed!\n");
	}
	printf("Socket bind complete\n");
	
	// Start listening on socket
	listen(sockfd,BACKLOG);

	// Add the server socket to the master set
	FD_SET(sockfd,&master);

	// Keep track of the biggest file descriptor number
	fdmax = sockfd;

	// Handling incoming connections
	while(1) {
		// Selection
		read_fds = master;
		if(select(fdmax+1,&read_fds,NULL,NULL,NULL)<0) {
			printf("Selection error!\n");
		}

		// Check each file descriptor
		for(i=0;i<=fdmax;i++) {
			if(FD_ISSET(i,&read_fds)) {
				if(i==sockfd) { // Server socket
					c = sizeof(struct sockaddr_in);
					newfd = accept(sockfd,(struct sockaddr *)&client,(socklen_t *)&c);
					if(newfd<0) {
						printf("Socket accept failed!\n");
					}
					printf("Connected to %s:%d\n",inet_ntoa(client.sin_addr),client.sin_port);
					memcpy(message,s,strlen(s));
					if(send(newfd,s,(int)strlen(s),0)+1<0) {
						printf("Send failed!\n");
					}
					// Add the new socket to master set
					FD_SET(newfd,&master);
					if(newfd>fdmax) { // Keep track of the max file descriptor
						fdmax = newfd;
					}
				} else { // Handle data from a client
					if(recv(i,message,1024,0)<0) {
						printf("Receive failed!\n");
					} else if((strcmp(message,"q")==0)||(strcmp(message,"Q")==0)) {
						printf("Client %s:%d is offline\n",inet_ntoa(client.sin_addr),client.sin_port);
						// Remove the quitting socket from master set
						FD_CLR(i,&master);
						if(fdmax==i) {
							fdmax--;
						}
					} else {
						if(send(i,message,(int)strlen(message)+1,0)<0) {
							printf("Send failed!\n");
						}
					}
				}
			}
		}
	}

	return 0;
}
