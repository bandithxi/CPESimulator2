import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Scanner;


public class CPESimulator {
	final static String srcIP = "172.168.10.1";
	final static String localDNSIP = "121.30.40.10";
	final static String rootIP = "10.11.1.13";
	final static String comTLDIP = "25.13.10.1";
	final static String eduTLDIP = "65.32.34.12";
	final static String orgTLDIP = "72.13.21.93";
	
	boolean IPfound = false;
	boolean searchComplete = false;
	
	public static void main(String[] args) {
		System.out.println("Enter the domain name");
		Scanner stream = new Scanner(System.in);
		
		CPESimulator prog = new CPESimulator();
		prog.srcAddHeader(stream.next());
	}
	//source
	public void srcAddHeader(String dName) {
		System.out.println("in srcAddHeader");
		DNS query = new DNS (0, 0, 1, 1, 0, dName, "", "" ,"");

		UDP segment = new UDP(1001, 53, 42,  query);

		IP datagram = new IP(4, 20, 42, 0, 007, 0, 128, 17, srcIP, localDNSIP, 0, segment);
		
		localDNS(datagram);
	}
	
	public void srcDelHeader(IP datagram) {
		System.out.println("in srcDelHeader");
		UDP segment = datagram.getSegment();
		DNS query = segment.getQuery();
		
		System.out.println(query.getAnswers());
	}
	
	//Local DNS
	public void localDNS(IP datagram) {
		System.out.println("in locaDNS");
		
		System.out.println("in localDelHeader");
		UDP segment = datagram.getSegment();
		DNS query = segment.getQuery();
		String Question = query.getQuestions();
		
		System.out.println("in checkLocalDNSDB");
	//testing with db
		if (!IPfound){
			Connection conn = null;
			Statement stmt = null;
			ResultSet rs = null;
			try {
				Class.forName("com.mysql.jdbc.Driver").newInstance();
				String connectionUrl = "jdbc:mysql://localhost:3306/cpe600_dns";
				String connectionUser = "root";
				String connectionPassword = "ashok";
				conn = DriverManager.getConnection(connectionUrl, connectionUser, connectionPassword);
				stmt = conn.createStatement();
				rs = stmt.executeQuery("SELECT * FROM localdns_table"); 
				while (rs.next()) {
					String name = rs.getString("name");
					String value = rs.getString("value");
					String type = rs.getString("type");
					if (name.compareTo(Question)==0) {
						query.addAnswer(value);
						IPfound = true;
						break;
					} else {
						IPfound = false;
					}
				} 
			} catch (Exception e) {
				e.printStackTrace();
			} finally {
				try { if (rs != null) rs.close(); } catch (SQLException e) { e.printStackTrace(); }
				try { if (stmt != null) stmt.close(); } catch (SQLException e) { e.printStackTrace(); }
				try { if (conn != null) conn.close(); } catch (SQLException e) { e.printStackTrace(); }
			}
		}
		
		System.out.println("in localAddHeader");
		segment = new UDP(53, 53, 42,  query);
		if (IPfound || searchComplete) {
			datagram = new IP(4, 20, 42, 0, 007, 0, 128, 17, localDNSIP, srcIP, 0, segment);
			srcDelHeader(datagram);
		}else {
			datagram = new IP(4, 20, 42, 0, 007, 0, 128, 17, localDNSIP, rootIP, 0, segment);
			root(datagram);
		}
	}
	
	public void root(IP datagram) {
		System.out.println("in root");
		
		System.out.println("in rootDelHeader");
		UDP segment = datagram.getSegment();
		DNS query = segment.getQuery();
		
		int length = query.getQuestions().length();
		String topLevel = query.getQuestions().substring(length - 3);
		
		System.out.println("in rootAddHeader");
		if (IPfound || searchComplete) {
			segment = new UDP(1001, 53, 42,  query);
			datagram = new IP(4, 20, 42, 0, 007, 0, 128, 17, rootIP , localDNSIP, 0, segment);
			localDNS(datagram);
		} else {
			if (topLevel.equals("com")) {
				segment = new UDP(53, 53, 42, query);
				datagram = new IP(4, 20, 42, 0, 007, 0, 128, 17, rootIP , comTLDIP, 0, segment);
				comTLD(datagram);
			} else if (topLevel.equals("edu")) {
				segment = new UDP(53, 53, 42, query);
				datagram = new IP(4, 20, 42, 0, 007, 0, 128, 17, rootIP , eduTLDIP, 0, segment);
				eduTLD(datagram);
			} else if (topLevel.equals("org")) {
				segment = new UDP(53, 53, 42, query);
				datagram = new IP(4, 20, 42, 0, 007, 0, 128, 17, rootIP , orgTLDIP, 0, segment);
				orgTLD(datagram);
			} else
				query.addAnswer("IP address not found");
				segment = new UDP(53, 53, 42, query);
				searchComplete = true;
				localDNS(datagram);			
		}
	}

	public void orgTLD(IP datagram) {
		System.out.println("in orgTLD");

		System.out.println("in orgTLDDelHeader");
		UDP segment = datagram.getSegment();
		DNS query = segment.getQuery();
		String Question = query.getQuestions();
	//testing with db
		if (!IPfound){
			Connection conn = null;
			Statement stmt = null;
			ResultSet rs = null;
			try {
				System.out.println("in try block");
				Class.forName("com.mysql.jdbc.Driver").newInstance();
				String connectionUrl = "jdbc:mysql://localhost:3306/cpe600_dns";
				String connectionUser = "root";
				String connectionPassword = "ashok";
				conn = DriverManager.getConnection(connectionUrl, connectionUser, connectionPassword);
				stmt = conn.createStatement();
				rs = stmt.executeQuery("SELECT * FROM orgtld_table"); 
				while (rs.next()) {
					String name = rs.getString("name");
					String value = rs.getString("value");
					String type = rs.getString("type");
					if (name.compareTo(Question)==0) {
						query.addAnswer(value);
						IPfound = true;
						searchComplete = true;
						break;
					} else {
						query.addAnswer("IP address not found on .org TLD");
						IPfound = false;
						searchComplete = true;
					}
				} 
			} catch (Exception e) {
				e.printStackTrace();
			} finally {
				try { if (rs != null) rs.close(); } catch (SQLException e) { e.printStackTrace(); }
				try { if (stmt != null) stmt.close(); } catch (SQLException e) { e.printStackTrace(); }
				try { if (conn != null) conn.close(); } catch (SQLException e) { e.printStackTrace(); }
			}
		}

		System.out.println("in orgTLDAddHeader");
		segment = new UDP(53, 53, 42,  query);
		if (IPfound) {
			datagram = new IP(4, 20, 42, 0, 007, 0, 128, 17, orgTLDIP, rootIP, 0, segment);
			root(datagram);
		} else {
			datagram = new IP(4, 20, 42, 0, 007, 0, 128, 17, orgTLDIP, rootIP, 0, segment);
			root(datagram);
		}
	}

	public void eduTLD(IP datagram) {
		System.out.println("in eduTLD");

		System.out.println("in eduTLDDelHeader");
		UDP segment = datagram.getSegment();
		DNS query = segment.getQuery();
		String Question = query.getQuestions();
		
		System.out.println("in checkeduTLDDB");
	//testing with db
		if (!IPfound){
			Connection conn = null;
			Statement stmt = null;
			ResultSet rs = null;
			try {
				System.out.println("in try block");
				Class.forName("com.mysql.jdbc.Driver").newInstance();
				String connectionUrl = "jdbc:mysql://localhost:3306/cpe600_dns";
				String connectionUser = "root";
				String connectionPassword = "ashok";
				conn = DriverManager.getConnection(connectionUrl, connectionUser, connectionPassword);
				stmt = conn.createStatement();
				rs = stmt.executeQuery("SELECT * FROM edutld_table"); 
				while (rs.next()) {
					String name = rs.getString("name");
					String value = rs.getString("value");
					String type = rs.getString("type");
					if (name.compareTo(Question)==0) {
						query.addAnswer(value);
						IPfound = true;
						searchComplete = true;
						break;
					} else {
						query.addAnswer("IP address not found on .edu TLD");
						IPfound = false;
						searchComplete = true;
					}
				} 
			} catch (Exception e) {
				e.printStackTrace();
			} finally {
				try { if (rs != null) rs.close(); } catch (SQLException e) { e.printStackTrace(); }
				try { if (stmt != null) stmt.close(); } catch (SQLException e) { e.printStackTrace(); }
				try { if (conn != null) conn.close(); } catch (SQLException e) { e.printStackTrace(); }
			}
		}

		System.out.println("in eduTLDAddHeader");
		segment = new UDP(53, 53, 42,  query);
		if (IPfound) {
			datagram = new IP(4, 20, 42, 0, 007, 0, 128, 17, eduTLDIP, rootIP, 0, segment);
			root(datagram);
		} else {
			datagram = new IP(4, 20, 42, 0, 007, 0, 128, 17, eduTLDIP, rootIP, 0, segment);
			root(datagram);
		}
	}
	
	public void comTLD(IP datagram) {
		System.out.println("in comTLD");
		
		System.out.println("in comTLDDelHeader");
		UDP segment = datagram.getSegment();
		DNS query = segment.getQuery();
		String Question = query.getQuestions();
		
		System.out.println("in checkcomTLDDB");
	//testing with db
		Connection conn = null;
		Statement stmt = null;
		ResultSet rs = null;
		try {
			System.out.println("in try block");
			Class.forName("com.mysql.jdbc.Driver").newInstance();
			String connectionUrl = "jdbc:mysql://localhost:3306/cpe600_dns";
			String connectionUser = "root";
			String connectionPassword = "ashok";
			conn = DriverManager.getConnection(connectionUrl, connectionUser, connectionPassword);
			stmt = conn.createStatement();
			rs = stmt.executeQuery("SELECT * FROM comtld_table"); 
			while (rs.next()) {
				String name = rs.getString("name");
				String value = rs.getString("value");
				String type = rs.getString("type");
				if (name.compareTo(Question)==0) {
					query.addAnswer(value);
					IPfound = true;
					searchComplete = true;
					break;
				} else {
					query.addAnswer("IP address not found on .com TLD");
					IPfound = false;
					searchComplete = true;
				}
			} 
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try { if (rs != null) rs.close(); } catch (SQLException e) { e.printStackTrace(); }
			try { if (stmt != null) stmt.close(); } catch (SQLException e) { e.printStackTrace(); }
			try { if (conn != null) conn.close(); } catch (SQLException e) { e.printStackTrace(); }
		}

		System.out.println("in comTLDAddHeader");
		segment = new UDP(53, 53, 42,  query);
		if (IPfound) {
			datagram = new IP(4, 20, 42, 0, 007, 0, 128, 17, comTLDIP, rootIP, 0, segment);
			root(datagram);
		} else {
			datagram = new IP(4, 20, 42, 0, 007, 0, 128, 17, comTLDIP, rootIP, 0, segment);
			root(datagram);
		}
	}
}