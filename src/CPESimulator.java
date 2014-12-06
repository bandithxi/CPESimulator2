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
		System.out.println("in src");
		srcDelHeader(datagram);
	}

	public void srcAddHeader(String dName) {
		System.out.println("in srcAddHeader");
		DNS query = new DNS (0, 0, 1, 1, 0, dName, "", "" ,"");

		UDP segment = new UDP(1001, 53, 42,  query);

		IP datagram = new IP(4, 20, 42, 0, 007, 0, 128, 17, srcIP, localDNSIP, 0, segment);
	//	System.out.println( datagram.toString());
		
		localDNS(datagram);
	}
	
	public void srcDelHeader(IP datagram) {
		System.out.println("in srcDelHeader");
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
		System.out.println("in locaDNS");
		localDelHeader(datagram);
		
		if (IPfound) {
			srcDelHeader(datagram);
		} else {
			root(datagram);
		}
	}
	
	public void localDelHeader(IP datagram) {
		System.out.println("in localDelHeader");
		UDP segment = datagram.getSegment();
		DNS query = segment.getQuery();
		
		if (!IPfound) {
			checkLocalDNSDB(query);
		}
	}
	
	public void checkLocalDNSDB(DNS query) {
		System.out.println("in checkLocalDNSDB");
		if (query.getQuestions().compareTo("google.com") == 0) {
			query.addAnswer("10.20.30.40");
			IPfound = true;
		} else {
			IPfound = false;
		}
	}
	
	public void root(IP datagram) {
		System.out.println("in root");
		rootDelHeader(datagram);
		
		if (IPfound){
			localDNS(datagram);
		}else {
			
		}
	}

	public void rootDelHeader(IP datagram) {
		System.out.println("in rootDelHeader");
		UDP segment = datagram.getSegment();
		DNS query = segment.getQuery();
		
		int length = query.getQuestions().length();
		String topLevel = query.getQuestions().substring(length - 3);
		
		if (!IPfound) {
			if (topLevel.equals("com")) {
				comTLD(datagram);
			} else if (topLevel.equals("edu")) {
				eduTLD(datagram);
			} else if (topLevel.equals("org")) {
				orgTLD(datagram);
			}
		}
	}
	
	public void orgTLD(IP datagram) {
		System.out.println("in orgTLD");
		orgTLDDelHeader(datagram);
		if (IPfound) {
			root(datagram);
		} else {
			System.out.println("not found in org db");
		}
	}
	public void orgTLDDelHeader(IP datagram) {
		System.out.println("in orgTLDDelHeader");
		UDP segment = datagram.getSegment();
		DNS query = segment.getQuery();
		
		checkorgTLDDB(query);
	}
	public void checkorgTLDDB(DNS query) {
		if (query.getQuestions().compareTo("google.org") == 0) {
			query.addAnswer("20.20.30.40");
			IPfound = true;
		} else {
			IPfound = false;
		}
	}
	
	public void eduTLD(IP datagram) {
		System.out.println("in eduTLD");
		eduTLDDelHeader(datagram);
		if (IPfound) {
			root(datagram);
		} else {
			System.out.println("not found in edu db");
		}
	}
	public void eduTLDDelHeader(IP datagram) {
		System.out.println("in eduTLDDelHeader");
	}
	public void comTLD(IP datagram) {
		System.out.println("in comTLD");
		comTLDDelHeader(datagram);
		if (IPfound) {
			root(datagram);
		} else {
			System.out.println("not found in com db");
		}
	}
	public void comTLDDelHeader(IP datagram) {
		System.out.println("in comTLDDelHeader");
	}
}