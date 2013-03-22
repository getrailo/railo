package railo.runtime.net.ntp;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;



/**
 * NtpClient - an NTP client for Java.  This program connects to an NTP server
 */
public final class NtpClient	{
	
	
	private String serverName;
	

	/**
	 * default constructor of the class
	 * @param serverName
	 */
	public NtpClient(String serverName) {
		this.serverName=serverName;
	}
	

	
	/**
	 * returns the offest from the ntp server to local system
	 * @return
	 * @throws IOException
	 */
	public long getOffset() throws IOException {
		/// Send request
		DatagramSocket socket = new DatagramSocket();
		socket.setSoTimeout(10000);
		InetAddress address = InetAddress.getByName(serverName);
		byte[] buf = new NtpMessage().toByteArray();
		DatagramPacket packet = new DatagramPacket(buf, buf.length, address, 123);
		
		// Set the transmit timestamp *just* before sending the packet
		NtpMessage.encodeTimestamp(packet.getData(), 40, (System.currentTimeMillis()/1000.0) + 2208988800.0);
		
		socket.send(packet);
		
		// Get response
		packet = new DatagramPacket(buf, buf.length);
		socket.receive(packet);
		
		// Immediately record the incoming timestamp
		double destinationTimestamp = (System.currentTimeMillis()/1000.0) + 2208988800.0;
		
		
		// Process response
		NtpMessage msg = new NtpMessage(packet.getData());
		//double roundTripDelay = (destinationTimestamp-msg.originateTimestamp) - (msg.receiveTimestamp-msg.transmitTimestamp);
		double localClockOffset = ((msg.receiveTimestamp - msg.originateTimestamp) + (msg.transmitTimestamp - destinationTimestamp)) / 2;
		
		return (long) (localClockOffset*1000);
	}
	
	/**
	 * returns the current time from ntp server in ms from 1970
	 * @return
	 * @throws IOException
	 */
	public long currentTimeMillis() throws IOException {
		return System.currentTimeMillis()+getOffset();
	}
	
	/*
	public static void main(String[] args) throws IOException{
		NtpClient ntp=new NtpClient("time.nist.gov");
		
	}
	public static void main(String[] args) throws IOException
	{
		
		String serverName="time.nist.gov";
		
		
		
		
		/// Send request
		DatagramSocket socket = new DatagramSocket();
		InetAddress address = InetAddress.getByName(serverName);
		byte[] buf = new NtpMessage().toByteArray();
		DatagramPacket packet = new DatagramPacket(buf, buf.length, address, 123);
		
		// Set the transmit timestamp *just* before sending the packet
		// ToDo: Does this improve performance or not?
		NtpMessage.encodeTimestamp(packet.getData(), 40, (System.currentTimeMillis()/1000.0) + 2208988800.0);
		
		socket.send(packet);
		
		// Get response
		packet = new DatagramPacket(buf, buf.length);
		socket.receive(packet);
		
		// Immediately record the incoming timestamp
		double destinationTimestamp = (System.currentTimeMillis()/1000.0) + 2208988800.0;
		
		
		// Process response
		NtpMessage msg = new NtpMessage(packet.getData());
		double roundTripDelay = (destinationTimestamp-msg.originateTimestamp) - (msg.receiveTimestamp-msg.transmitTimestamp);
		double localClockOffset = ((msg.receiveTimestamp - msg.originateTimestamp) + (msg.transmitTimestamp - destinationTimestamp)) / 2;
		
		
		// Display response
		
		socket.close();
	}*/
	
	
}