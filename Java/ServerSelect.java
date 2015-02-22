//************************************************************************************************
// Author: Tianlong Song
// Name: ServerSelect.java
// Description: Socket server using selector to handle clients 
// Date created: 01/05/2015 
//************************************************************************************************

import java.io.*;
import java.net.*;
import java.util.*;
import java.nio.channels.*;
import java.nio.charset.Charset;
import java.nio.ByteBuffer;
import java.nio.CharBuffer;

class ServerSelect{
	private static final int PORT = 8888; // Port number
	private static final int BAKCLOG = 10; // Maximum length of listening queue

	public static void main(String args[]) {
		// Server socket channel
		ServerSocketChannel server;
		try {
			server = ServerSocketChannel.open();
			server.bind(new InetSocketAddress(PORT));
			System.out.println("Server socket created");
		} catch(IOException e) {
			System.out.println(e);
			return;
		}

		// Selector configuration and register the server SocketChannel
		Selector selector;
		try {
			selector = Selector.open();
			server.configureBlocking(false); // Can be non-blocking only
			server.register(selector,SelectionKey.OP_ACCEPT);
		} catch(IOException e) {
			System.out.println(e);
			return;
		}

		// Handle incoming connections
		try {
			while(true) {
				int readyChannels = selector.select();
				if(readyChannels==0) {
					continue;
				}
				Set<SelectionKey> selectedKeys = selector.selectedKeys();
				Iterator<SelectionKey> keyIterator = selectedKeys.iterator();

				while(keyIterator.hasNext()) {
					SelectionKey key = keyIterator.next();
					if(key.isAcceptable()) {
						try {
							// Accept the incoming connection
							ServerSocketChannel serverSocketChannel= (ServerSocketChannel) key.channel();
							SocketChannel clientSocketChannel = serverSocketChannel.accept();
							System.out.println("Connected to " + clientSocketChannel.socket().getInetAddress().getHostAddress() + ": " +clientSocketChannel.socket().getPort());

							// Send welcome message 
							PrintWriter out = new PrintWriter(clientSocketChannel.socket().getOutputStream(),true);
							out.println("Welcome to the server. Type something and hit enter.");
							
							// Register the new client SocketChannel to the selector
							clientSocketChannel.configureBlocking(false); // Can be non-blocking only
							clientSocketChannel.register(selector,SelectionKey.OP_READ);
						} catch(IOException e) {
							System.out.println(e);
						}
					} else if(key.isReadable()) {
						// Non-blocking mode: receiving and sending messages
						SocketChannel clientSocketChannel= (SocketChannel) key.channel();
						ByteBuffer byteBuffer = ByteBuffer.allocate(1024);
						int byteRead = 0;
						String msg;
						
						// Receive messages from client
						if((byteRead = clientSocketChannel.read(byteBuffer))>=0) {
							byteBuffer.flip();
							msg = Charset.defaultCharset().decode(byteBuffer).toString();
							byteBuffer.clear();

							msg = msg.replaceAll("\r\n",""); // Remove end-of-line characters
							
							if(msg.equals("q")||msg.equals("Q")) {
								key.cancel();
								clientSocketChannel.close();
								System.out.println("Client " + clientSocketChannel.socket().getInetAddress().getHostAddress() + ": " + clientSocketChannel.socket().getPort() + " is offline");
								continue;
							}
						} else {
							key.cancel();
							clientSocketChannel.close();
							continue;
						}

						// Feedback to client
						CharBuffer charBuffer = CharBuffer.wrap("Feedback from server: " + msg + "\r\n");
						while(charBuffer.hasRemaining()) {
							clientSocketChannel.write(Charset.defaultCharset().encode(charBuffer));
						}
					}

					// Remove the key once it is processed
					keyIterator.remove();
				}
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
}
