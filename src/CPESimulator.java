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
	public void src(IP datagram) {
		srcDelHeader(datagram);
	}

	public void srcAddHeader(String dName) {
		DNS query = new DNS (0, 0, 1, 1, 0, dName, "", "" ,"");

		UDP segment = new UDP(1001, 53, 42,  query);

		IP datagram = new IP(4, 20, 42, 0, 007, 0, 128, 17, srcIP, localDNSIP, 0, segment);
		System.out.println( datagram.toString());
		
		localDNS(datagram);
		
	}
	
	public void srcDelHeader(IP datagram) {
		UDP segment = datagram.getSegment();
		DNS query = segment.getQuery();
		if (IPfound) {
			System.out.println(query.getAnswers());
		} else {
			System.out.println("IP address not found");
		}
	}
	
	//Local DNS
	public void localDNS(IP datagram) {
		localDelHeader(datagram);
		
		if (IPfound) {
			srcDelHeader(datagram);
		} else {
			root(datagram);
		}
	}
	
	public void localDelHeader(IP datagram) {
		UDP segment = datagram.getSegment();
		DNS query = segment.getQuery();
		
		checkLocalDNSDB(query);
	}
	
	public void checkLocalDNSDB(DNS query) {
		if (query.getQuestions().compareTo("google.com") == 0) {
			IPfound = true;
		} else {
			IPfound = false;
		}
	}
	
	public void root(IP datagram) {
		rootDelHeader(datagram);
		
		if (IPfound){
			localDNS(datagram);
		}else {
			
		}
	}

	public void rootDelHeader(IP datagram) {
		UDP segment = datagram.getSegment();
		DNS query = segment.getQuery();
		
		int length = query.getQuestions().length();
		String topLevel = query.getQuestions().substring(length - 3);
		
		if (!IPfound) {
			if (topLevel.equals("com")) {
				comTLDDelHeader(datagram);
			} else if (topLevel.equals("edu")) {
				eduTLDDelHeader(datagram);
			} else if (topLevel.equals("org")) {
				orgTLDDelHeader(datagram);
			}
		}
	}
	
	public void orgTLDDelHeader(IP datagram) {
		
	}
	
	public void eduTLDDelHeader(IP datagram) {
		
	}
	
	public void comTLDDelHeader(IP datagram) {
		
	}
}
