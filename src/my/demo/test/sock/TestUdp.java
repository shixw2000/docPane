package my.demo.test.sock;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.MulticastSocket;
import java.net.NetworkInterface;
import java.net.SocketAddress;
import java.net.SocketException;
import java.util.Arrays;
import java.util.Scanner;

public class TestUdp {
	public static void main(String[] args) {
		int opt = 0;
		String local = null;
		String other = null;
		
		try { 
			if (3 == args.length) {
				opt = Integer.parseInt(args[0]);
				local = args[1];
				other = args[2];
			} else {
				Scanner scan = new Scanner(System.in);
				
				System.out.println("Enter option(1-rcv, other-snd): "); 
				if (scan.hasNext()) {
					System.out.println("==========");
					
					opt = scan.nextInt(); 
				}
				
				if (scan.hasNext()) {
					System.out.println("-----------");
					
					local = scan.next(); 
				}
				
				if (scan.hasNext()) {
					System.out.println("-----------");
					
					other = scan.next(); 
				}
				
				scan.close();
			}
			
			if (1 == opt) {
				testRcv(local, other);
			} else {
				testSnd(local, other);
			}
			
			System.out.println("opt:" + opt);
		} catch(Exception e) {
			e.printStackTrace();
		} finally {
			System.out.println("==========");
		}
	}
	
	static void testSnd(String ip, String remote) throws Exception {
		int localPort = 9999;
		int remotePort = 9998;
		InetAddress local = null;
		InetAddress peer = null;
		Udp udp = null;
		String msg = "Helo, world.";
		
		local = InetAddress.getByName(ip);
		peer = InetAddress.getByName(remote);
		
		udp = new Udp(local, localPort);
		udp.init();
		udp.sendPacket(msg.getBytes(), peer, remotePort);
		udp.close();
	}
	
	static void testRcv(String ip, String mcast) throws Exception {
		int port = 9998;
		InetAddress local = null;
		InetAddress group = null;
		MultiUdp udp = null;
		byte buf[] = null;
		DatagramPacket packet;
		String appStr = " >> reply\n";
		
		buf = new byte[1024];
		local = InetAddress.getByName(ip);
		System.out.printf("local_ip=%s| type=%s|\n", ip, local.getHostAddress());
		group = InetAddress.getByName(mcast);
		System.out.printf("group_ip=%s| type=%s|\n", mcast, group.getHostAddress());
		
		udp = new MultiUdp(local, port, group);
		udp.init();
		
		packet = udp.recvPacket(buf); 
		int len = packet.getLength();
		int offset = packet.getOffset();
		
		System.out.printf("peer_ip=%s| peer_port=%d| msg_len=%d:%d| msg=%s|\n", 
				packet.getAddress().getHostAddress(), packet.getPort(), 
				len, offset,
				new String(buf, offset, len));
		
		System.arraycopy(appStr.getBytes(), 0, buf, offset + len, appStr.length());
		len += offset + appStr.length();
		offset = 0;
		
		packet.setData(buf, offset, len);
		System.out.printf("send msg| len=%d| msg=%s|\n", len, new String(buf, offset, len));
		udp.send(packet);
	}
}

class Udp {
	protected DatagramSocket sock;
	protected InetAddress local;
	protected int port;
 
	Udp(InetAddress addr, int port) {
		this.local = addr;
		this.port = port; 
	}
	
	void init() throws Exception {
		sock = UdpTool.createUdp();
		UdpTool.bindLocal(sock, local, port);
	}
	
	void close() {
		if (null != sock) {
			sock.close();
		}
	}
	
	void send(DatagramPacket packet) throws Exception {
		sock.send(packet);
	}
	
	void sendPacket(byte buf[], InetAddress addr, int port) throws Exception {
		UdpTool.sendPacket(sock, buf, addr, port);
	}
	
	DatagramPacket recvPacket(byte buf[]) throws Exception {
		DatagramPacket packet = null;
		
		packet = UdpTool.recvPacket(sock, buf);
		return packet;
	}
	
	DatagramPacket recvPacket(byte buf[], int offset, int len) throws Exception {
		DatagramPacket packet = null;
		
		packet = UdpTool.recvPacket(sock, buf, offset, len);
		return packet;
	}
}

class MultiUdp extends Udp {
	private InetAddress group;
	private MulticastSocket multiSock = null;
	
	MultiUdp(InetAddress addr, int port, InetAddress group) { 
		super(addr, port);
		
		this.group = group; 
	}
	
	void init() throws Exception {
		multiSock = UdpTool.createUdpMulti(); 
 
		multiSock.setTimeToLive(1);
		UdpTool.bindLocal(multiSock, group, port); 
		UdpTool.joinGroup(multiSock, local, group);
		
		sock = multiSock;
	}
	
	void close() {
		try {
			if (null != multiSock) {
				UdpTool.leaveGroup(multiSock, group);
				multiSock.close();
			}
		} catch (Exception e) {
			
		} finally {
			multiSock = null;
			sock = null;
		}
	}
}

class UdpTool { 
	static DatagramSocket createUdp() throws Exception { 
		try {
			DatagramSocket sock = new DatagramSocket(null);
		
			return sock;
		} catch (Exception e) {
			throw e;
		}
	}
	
	static MulticastSocket createUdpMulti() throws Exception { 
		try {
			MulticastSocket sock = new MulticastSocket(null);
		
			return sock;
		} catch (Exception e) {
			throw e;
		} 
	}
	
	static void close(DatagramSocket sock) {
		if (null != sock) {
			sock.close();
		}
	}
	
	static void bindLocal(DatagramSocket sock, InetAddress addr, int port) throws SocketException {
		SocketAddress local = new InetSocketAddress(addr, port);
		
		try {
			sock.bind(local);
		} catch (Exception e) {
			System.out.printf("bind error: ip=%s| port=%d|\n",
					addr.getHostAddress(), port);
			throw e;
		}
	}
	
	static void joinGroup(MulticastSocket multiSock, InetAddress local, InetAddress group) throws Exception { 
		NetworkInterface dev = NetworkInterface.getByInetAddress(local);
		
		multiSock.setNetworkInterface(dev); 
//		multiSock.setInterface(local);
		multiSock.joinGroup(group); 
		
		InetAddress addr = multiSock.getInterface();
		System.out.printf("GetInf| ip=%s|\n", addr.getHostAddress());
	}
	
	static void leaveGroup(MulticastSocket multiSock, InetAddress group) throws Exception {
		multiSock.leaveGroup(group);
	}
	
	static void sendPacket(DatagramSocket sock, byte buf[], InetAddress addr, int port) throws Exception {
		sendPacket(sock, buf, 0, buf.length, addr, port);
	}
	
	static void sendPacket(DatagramSocket sock, byte buf[], int offset, int len,
			InetAddress addr, int port) throws Exception {
		InetSocketAddress peer = new InetSocketAddress(addr, port);
		
		DatagramPacket packet = new DatagramPacket(buf, offset, len, peer);
		sock.send(packet);
	}
	
	static DatagramPacket recvPacket(DatagramSocket sock, byte buf[]) throws Exception {
		return recvPacket(sock, buf, 0, buf.length);
	}
	
	static DatagramPacket recvPacket(DatagramSocket sock, byte buf[], int offset, int len) throws Exception {
		DatagramPacket packet = new DatagramPacket(buf, offset, len);
		
		sock.receive(packet); 
		return packet; 
	}
}

