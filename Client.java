import java.io.*;
import java.net.*;

class Client{
	public static void main(String args[]) throws Exception{
		
		// socket for TCP
		Socket clientSocket = null;
		
		// socket for UDP
		DatagramSocket UDPSocket = null;

		int MAX_BYTE = 65535;
		String r_port;
		int initNego = 13;			// sending request for negotiation
		int n_port = 0;	
		
		String reversedSentence;	// output string (reversed)
		String server_address = "";	// connecting port
		String sentence = "";		// input sentence
		
		byte[] sendData = new byte[MAX_BYTE];
		byte[] receiveData = new byte[MAX_BYTE];
		
		try{			
			// input has two arguments
			if(args.length == 2){
				// set up server address
				server_address = args[0];
				// set up n_port
				n_port = Integer.parseInt(args[1]);
				sentence = "";
			}
			// input has three arguments
			else if(args.length == 3){
				server_address = args[0];
				n_port = Integer.parseInt(args[1]);
				// set up input sentence
				sentence = args[2];
			}
			// else output error and exit
			else{
				System.err.println("ERROR: requires 2 or 3 args. received: "+args.length);
				System.err.println("terminating...");
				System.exit(1);
			}
			
			//  STAGE1 TCP connection
			
			// initialize TCPsocket
			clientSocket = new Socket(server_address, n_port);
		
			// input & output streams
			DataOutputStream outToServer =
					new DataOutputStream(clientSocket.getOutputStream());
			BufferedReader inFromServer =
					new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));

			// requesting r_port by sending integer (initiating negotiation)
			outToServer.write(initNego+'\n');
		
			// receive r_port from server
			r_port = inFromServer.readLine();		
		
			//	STAGE2 UDP CONNECTION
			
			// initialize UDPsocket
			UDPSocket = new DatagramSocket();		
			InetAddress IPAddress = InetAddress.getByName(server_address);	
	
			sendData = sentence.getBytes();		
			DatagramPacket sendPacket = 
				new DatagramPacket(sendData, sendData.length, 
						IPAddress, Integer.parseInt(r_port));

			// send the sentence
			UDPSocket.send(sendPacket);	
		
			DatagramPacket receivePacket = 
				new DatagramPacket(receiveData, receiveData.length);

			// receive the reversed sentence
			UDPSocket.receive(receivePacket);
		
			reversedSentence = new String(receivePacket.getData());
			
			// outputs result
			System.out.println("received sentence: " + reversedSentence);
		}
		catch(NumberFormatException e){
			System.err.println("NumberFormatExcpetion ERROR\n"+
					e.getMessage()+" has to be integer");
			System.err.println("terminating...");
			System.exit(1);			
		}
		catch(IOException e){
			System.err.println("IOException ERROR: "+e.getMessage());
			System.err.println("terminating...");
			System.exit(1);
			
		}
		catch(Exception e){
			System.err.println("ERROR: "+e.getMessage());
			System.err.println("terminating...");
			System.exit(1);
		}
		finally{
			// closes all connections
			UDPSocket.close();
			clientSocket.close();
		}
		
	}
}