import java.io.*;
import java.net.*;

class Server {
	public static void main(String argv[]) throws Exception {
		
		// socket for TCP
		ServerSocket welcomeSocket = null;
		
		// datagram for UDP
		DatagramSocket serverSocket = null;
		DatagramPacket receivePacket = null;
		
		String clientSentence;
		String reversedSentence;
		
		int MAX_BYTE = 65507;
		int n_port = 6677;
		int r_port = 0;
		int initNego;

		byte[] receiveData = new byte[MAX_BYTE];
		byte[] sendData = new byte[MAX_BYTE];
		
		try{
			/*
			 *  TCP implementation
			 */
			
			// initialize TCP
			welcomeSocket = new ServerSocket(n_port);	
			// outputs n_port in order to allow client to connect
			System.out.println("n_port is "+n_port);
		
			// accepts the connection
			Socket connectionSocket = welcomeSocket.accept();
			
			// input & ouput streams
			BufferedReader inFromClient = new BufferedReader(
						new	InputStreamReader(connectionSocket.getInputStream()));
			DataOutputStream outToClient =
					new DataOutputStream(connectionSocket.getOutputStream());

			// initiating negotiation
			initNego = inFromClient.read();

			// setting a random port
			welcomeSocket = new ServerSocket(r_port);		
			r_port = welcomeSocket.getLocalPort();
		
			// send r_port to client
			outToClient.writeBytes(Integer.toString(r_port)+'\n');	
		
			
			/*
			 * UDP implementation
			 */
			
			// connect UDP using r_port
			serverSocket = new DatagramSocket(r_port);			
			receivePacket = new DatagramPacket(receiveData, receiveData.length);

			// receive String from client
			serverSocket.receive(receivePacket);		
			clientSentence = new String(receivePacket.getData());		
			InetAddress IPAddress = receivePacket.getAddress();

		
			// get port of sender
			int port = receivePacket.getPort();
			
			// reverse the input sentence
			reversedSentence = new StringBuilder(clientSentence).reverse().toString();
		
			// make datagramPacket for reversed sentence
			sendData = reversedSentence.getBytes();			
			DatagramPacket sendPacket = 
					new DatagramPacket(sendData, sendData.length, IPAddress, port);
			
			// send the reversed sentence
			serverSocket.send(sendPacket);	
		}
		catch(IOException e){
			System.err.println("IOException ERROR: "+e.getMessage());
			System.exit(1);
		}
		catch(Exception e){
			System.err.println("ERROR: "+e.getMessage());
			System.exit(1);
		}
		finally{
			// closes all connections
			serverSocket.close();
			welcomeSocket.close();
		}
	}
}