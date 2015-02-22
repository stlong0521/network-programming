//************************************************************************************************
// Author: Tianlong Song
// Name: ServerThread.java
// Description: Socket server using thread to handle clients 
// Date created: 01/05/2015 
//************************************************************************************************

import java.io.*;
import java.net.*;
import java.util.HashSet;

class ServerThread{
	private static final int PORT = 8888; // Port number
	private static final int BAKCLOG = 10; // Maximum length of listening queue
	private static HashSet<String> names = new HashSet<String>(); // Name records for clients

	public static void main(String args[]) {
		// Server and client socket
		ServerSocket server;
		Socket client;

		// Create server socket
		try {
			server = new ServerSocket(PORT,BAKCLOG); // The bind() is automatically called by socket construction
			System.out.println("Server socket created");
		} catch(IOException e) {
			System.out.println(e);
			return;
		}

		// Handle incoming connections
		try {
			while(true) {
				client = server.accept();
				new Handler(client);
				System.out.println("Connected to " + client.getInetAddress().getHostAddress() + ": " + client.getPort());
			}
		} catch(IOException e) {
			System.out.println(e);
		} finally {
			try {
				server.close();
			} catch(IOException e) {
				System.out.println(e);
			}
		}
	}

	private static class Handler implements Runnable {
		private Thread thrd;
		private String name;
		private Socket socket;
		private BufferedReader in;
		private PrintWriter out;

		Handler(Socket socket) {
			this.socket = socket;
			this.thrd = new Thread(this);
			thrd.start();
		}

		public void run() {
			try {
				// Input and output stream for receiving and sending messages
				in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
				out = new PrintWriter(socket.getOutputStream(),true);
				// Request a name from the client
				out.println("Submit your name: ");
				while(true) {
					name = in.readLine();
					if(name==null) {
						out.println("Empty name!");
						continue;
					}
					// Synchornized block needed here, since "names" is a hash table shared by all threads
					synchronized (names) {
						if(names.contains(name)) {
							out.println("Name conflict! Resubmit your name: ");
							continue;
						} else {
							names.add(name);
							break;
						}
					}
				}
				out.println("Name accepted");

				// Receive messages from client and feedback
				String msg = null;
				while(true) {
					msg = in.readLine();
					if(msg==null) {
						return;
					} else if(msg.equals("q")||msg.equals("Q")) {
						break;
					}
					out.println("Feedback from server: " + msg);
				}
			} catch(IOException e) {
				System.out.println(e);
			} finally {
				if(name!=null) {
					names.remove(name);
					System.out.println("Client " + socket.getInetAddress().getHostAddress() + ": " + socket.getPort() + " is offline");
				}
				try {
					socket.close();
				} catch(IOException e) {
					System.out.println(e);
				}
			}
		}
	}
}
