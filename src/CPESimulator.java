import java.util.ArrayList;
import java.util.Scanner;


public class CPESimulator {
	final static String srcIP = "172.168.10.1";
	final static String localDNSIP = "121.30.40.10";
	final static String rootIP = "10.11.1.13";
	final static String comTLDIP = "25.13.10.1";
	final static String eduTLDIP = "65.32.34.12";
	final static String orgTLDIP = "72.13.21.93";
	
	boolean IPfound = false;
	
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		System.out.println("Enter the domain name");
		Scanner stream = new Scanner(System.in);
		
		CPESimulator prog = new CPESimulator();
		prog.srcAddHeader(stream.next());
		
	}
	//source
	public void srcAddHeader(String dName) {
		DNS query = new DNS (0, 0, 1, 1, 0, dName, 
				new ArrayList<String>(), 
				new ArrayList<String>(), 
				new ArrayList<String>());

		UDP segment = new UDP(1001, 53, 42,  query);

		IP datagram = new IP(4, 20, 42, 0, 007, 0, 128, 17, srcIP, localDNSIP, 0, segment);
		System.out.println( datagram.toString());
		
		localDNS(datagram);
		
	}
	public void srcDelHeader() {
		
	}
	
	//Local DNS
	public void localDNS(IP datagram) {
		localDelHeader(datagram);
		
	}
	
	public void localDelHeader(IP datagram) {
		UDP segment = datagram.getSegment();
	//	System.out.println("in local DNS");
	//	System.out.println(segment);
		DNS query = segment.getQuery();
		if (query.getQuestions().compareTo("google.com") == 0) {
			
		}
	}

}
