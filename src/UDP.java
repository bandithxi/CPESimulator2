//Test edit, please ignore/delete
public class UDP {
	private int srcPort;
	private int destPort;
	private int length;
	private DNS query;
	
	//Not handled for simulation
	private int checkSum;
	
	public UDP(int srcPort, int destPort, int length, DNS query) {
		this.srcPort = srcPort;
		this.destPort = destPort;
		this.length = length;
		this.query = query;
		this.checkSum = 0;
	}

	
	public String toString() {
		return "UDP [srcPort =" + srcPort + ", destPort =" + destPort
				+ ", length =" + length  + ", UDP checkSum ="
				+ checkSum + ", \nDNS query =" + query.toString() + "]";
	}


	public void genCheckSum() {
		this.checkSum = 0;
	}
	
	public int getSourcePort() {
		return srcPort;
	}
	
	public int getDestPort() {
		return destPort;
	}
	
	public int getLength() {
		return length;
	}
	
	public DNS getQuery() {
		return query;
	}
}
