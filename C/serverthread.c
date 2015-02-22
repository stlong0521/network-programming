//******************************************************************************************
// Author: Tianlong Song
// Name: serverthread.c
// Description: Socket server using thread to handle clinets
// Date created: 01/04/2015
//******************************************************************************************

#include <stdio.h>
#include <string.h>
#include <sys/types.h>
#include <sys/socket.h>
#include <netinet/in.h>
#include <arpa/inet.h>

#define PORT 8888	// The port number to be used
#define BACKLOG 10	// Maximum length of connection queue

// Handler declaration
void *handler(void*);

// Struct for multiple arguments passing to handler
struct arg_struct {
	int fd;
	struct sockaddr_in addr;	
};

int main() {
	struct sockaddr_in server,client;
	int sockfd,newfd;
	int c;

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

	// Talking with the client(s)
	c = sizeof(struct sockaddr_in);
	pthread_t thread_id;
	struct arg_struct args;
	// Accept a new connection from a client and assign a handler (thread) for it
	while((newfd = accept(sockfd,(struct sockaddr *)&client,(socklen_t *)&c))) {
		args.fd = newfd;
		args.addr = client;
		if(pthread_create(&thread_id,NULL,handler,(void *)&args)<0) {
			printf("Thread assignment failed!\n");
			continue;
		}
		printf("Connected to %s:%d\n",inet_ntoa(client.sin_addr),client.sin_port);
	}
	
	return 0;
}

void *handler(void* arguments) {
	struct arg_struct *args = (struct arg_struct *)arguments;
	int newfd = args->fd;
	struct sockaddr_in client = args->addr; 
	const char *s = "Welcome to the server. Type something and hit enter.";
	char message[1024];
	
	// Send welcome message	
	memcpy(message,s,strlen(s));
	if(send(newfd,s,(int)strlen(s),0)+1<0) {
		printf("Send failed!\n");
	}
	
	// Receive and send data to client(s)
	while(1) {
		if(recv(newfd,message,1024,0)<0) {
			printf("Receive failed!\n");
		} else if((strcmp(message,"q")==0)||(strcmp(message,"Q")==0)) {
			printf("Client %s:%d is offline\n",inet_ntoa(client.sin_addr),client.sin_port);
			break;
		} else {
			if(send(newfd,message,(int)strlen(message)+1,0)<0) {
				printf("Send failed!\n");
			}
		}
	}
}
