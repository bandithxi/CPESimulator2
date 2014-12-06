
public class RR {
	private String name;
	private String value;
	private String type;
	private int TTL;
	
	
	public RR(String name, String value, String type, int tTL) {
		super();
		this.name = name;
		this.value = value;
		this.type = type;
		TTL = tTL;
	}

	public String toString() {
		return "RR [name=" + name + ", value=" + value + ", type=" + type
				+ ", TTL=" + TTL + "]";
	}

	public String getName() {
		return name;
	}

	public String getValue() {
		return value;
	}

	public String getType() {
		return type;
	}

	public int getTTL() {
		return TTL;
	}
	
	
}
