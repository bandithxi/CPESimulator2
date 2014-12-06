
public class IP {
	private int version;
	private int headerLen;
	private int totalLength;
	private int packetType;
	private int ID;
	private int fragOffset;
	private int TTL;
	private int protocol;
	private String srcIP;
	private String destIP;
	private String options;
	private UDP segment;

	//Not used for this simulation
	private int flags;
	private int checkSum;
	
	public IP(int version, int headerLen, int totalLength, int packetType,
			int iD, int fragOffset, int tTL, int protocol, String srcIP,
			String destIP, String options, UDP segment) {
		super();
		this.version = version;
		this.headerLen = headerLen;
		this.totalLength = totalLength;
		this.packetType = packetType;
		this.ID = iD;
		this.fragOffset = fragOffset;
		this.TTL = tTL;
		this.protocol = protocol;
		this.srcIP = srcIP;
		this.destIP = destIP;
		this.flags = flags;
		this.segment = segment;
		this.checkSum = 0;
	}

	public int getVersion() {
		return version;
	}

	public int getHeaderLen() {
		return headerLen;
	}

	public int getTotalLength() {
		return totalLength;
	}

	public int getPacketType() {
		return packetType;
	}

	public int getID() {
		return ID;
	}

	public int getFragOffset() {
		return fragOffset;
	}

	public int getTTL() {
		return TTL;
	}

	public int getProtocol() {
		return protocol;
	}

	public String getSrcIP() {
		return srcIP;
	}

	public String getDestIP() {
		return destIP;
	}

	public String toString() {
		return "IP [version = " + version + ", headerLen = " + headerLen
				+ ", totalLength = " + totalLength + ", packetType = " + packetType
				+ ", ID = " + ID + ", fragOffset = " + fragOffset + ", TTL = " + TTL
				+ ", protocol = " + protocol + ", srcIP = " + srcIP + ", destIP = "
				+ destIP +  ", flags = " + flags
				+ ", IP checkSum =" + checkSum + ", \nsegment=" + segment.toString() +"]";
	}

	public int getFlags() {
		return flags;
	}

	public int getCheckSum() {
		return checkSum;
	}
	
	public UDP getSegment() {
		return segment;
	}
}
