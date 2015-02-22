//*****************************************************************************************
// Author: Tianlong Song
// Name: client.c
// Description: Socket client 
// Date created: 01/04/2015
//*****************************************************************************************

#include <stdio.h>
#include <string.h>
#include <sys/types.h>
#include <sys/socket.h>
#include <netinet/in.h>

#define PORT 8888	// The port number to be used

int main() {
	struct sockaddr_in server;
	int sockfd;
	char message[1024];

	// Settings
	server.sin_family = AF_INET; // IPV4
	server.sin_addr.s_addr = inet_addr("127.0.0.1"); 
	server.sin_port = htons(PORT); //Port #, convert byte order to network format

	// Create client socket 
	sockfd = socket(AF_INET,SOCK_STREAM,0);
	if(sockfd<0) {
		printf("Could not create socket!\n");
	}
	printf("Socket created\n");

	// Connect to server
	if(connect(sockfd,(struct sockaddr *)&server,sizeof(server))<0) {
		printf("Connection failed!\n");
	}
	printf("Connected to server\n");
	
	// Talking with the server 
	do {
		// Receiving from receiver
		if(recv(sockfd,message,1024,0)<0) {
			printf("Receive failed!\n");
			break;
		}
		printf("Feedback from server: %s\n",message);
		// Sending to server
		printf("Text to send: ");
		scanf("%s",message);
		if(send(sockfd,message,(int)strlen(message)+1,0)<0) {
			printf("Send failed!\n");
			break;
		}
	}while((strcmp(message,"q")!=0)&&(strcmp(message,"Q")!=0));

	// Close the socket
	close(sockfd);
	printf("Socket closed\n");

	return 0;
}
