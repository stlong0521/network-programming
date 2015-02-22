//************************************************************************************************
// Author: Tianlong Song
// Name: Client.java
// Description: Socket client 
// Date created: 01/05/2015 
//************************************************************************************************

import java.io.*;
import java.net.*;

class Client {
	private static final int PORT = 8888; // Port number
	private static final String HOST = "::1"; // Localhost address, IPV6

	public static void main(String args[]) {
		// Client socket
		Socket client;

		// Create client socket
		try {
			client = new Socket(HOST,PORT); // The construction of Socket automatically tries to connect to server
			System.out.println("Client socket created");
		} catch(IOException e) {
			System.out.println(e);
			return;
		}

		// Talk to server 
		try {
			// Input and output stream for receiving and sending messages
			BufferedReader in = new BufferedReader(new InputStreamReader(client.getInputStream()));
			PrintWriter out = new PrintWriter(client.getOutputStream(),true);
			
			// Input stream for reading from console
			BufferedReader br = new BufferedReader(new InputStreamReader(System.in));

			// Receive and send messages	
			String msg;
			while(true) {
				msg = in.readLine(); // Receive messages from server
				if(msg.equals("Submit your name: ")||msg.equals("Name conflict! Resubmit your name: ")) {
					System.out.print(msg);
				} else {
					System.out.println(msg);
					System.out.print("Message to send: ");
				}
				msg = br.readLine(); // Receive message from console
				out.println(msg); // Send messages to server
				if(msg.equals("q")||msg.equals("Q")) {
					break;
				}
			}
		} catch(IOException e) {
			System.out.println(e);
		} finally {
			try {
				client.close();
				System.out.println("Client socket closed");
			} catch(IOException e) {
				System.out.println(e);
			}
		}
	}
}
