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
	final static String authDNSIP = "129.35.172.0";
	
	boolean IPfound = false;
	boolean searchComplete = false;
	int n=0,m=0;
	
	public static void main(String[] args) {
		System.out.println("Enter the domain name");
		Scanner stream = new Scanner(System.in);
		
		CPESimulator prog = new CPESimulator();
		prog.srcAddHeader(stream.next());
	}
	
	/***********************************************************
	 *	At SOURCE
	 *  Building DNS query and adding UDP and IP headers at source 
	 *  After adding sending IP datagram to local DNS.
	 ************************************************************/
	
	public void srcAddHeader(String dName) {
		int src_ID=0;
		int src_flags=0;
		int src_numQuestion=0;
		int src_numAnswers=0;
		int src_numRR=0;
		int src_numAdditionRR=0;
		String src_Questions=dName;
		String src_answers="";
		String src_authority="";
		String src_additional="";
		
		int srcPort=1001;
		int destPort=53;
		int length=40;

		int version=4;
		int headerLen=20;
		int packetType=0;
		int totalLength=60;
		int ID=1234;
		int fragOffset=0;
		int TTL=50;
		int protocol=17;
		String options="";
		
		src_numQuestion++;
		
		//Building DNS Query at Application layer
		DNS query = new DNS (src_ID, src_flags, src_numQuestion, src_numAnswers, 
							src_numRR, src_numAdditionRR, src_Questions, 
							src_answers, src_authority, src_additional);
		//Building UDP Segment at Transport layer
		UDP segment = new UDP(srcPort, destPort, length,  query);
		
		//Building IP Datagram at Network layer
		IP datagram = new IP(version, headerLen, packetType, totalLength, ID, 
				fragOffset, TTL, protocol, srcIP, localDNSIP, options, segment);
		//Sending IP datagram to Local DNS
		localDNS(datagram);
	}
	
	/*************************************************
	 *	Deleting IP and UDP headers.
	 *  Analyze DNS Query.
	 *  Display output which is in DNS Answers field.
	 *************************************************/
	public void srcDelHeader(IP datagram) {
		//Received IP datagram in Network layer.
		IP datagram1 = datagram;
		//Getting UDP segment from IP datagram at Transport layer.
		UDP segment = datagram1.getSegment();
		//Getting DNS query from UDP segment.
		DNS query = segment.getQuery();
		//Displaying Results in Output.
		System.out.println("");
		System.out.println("Result");
		System.out.println("------");
		System.out.println("Number of Questions : " + query.getNumQuestion());
		System.out.println("Number of Answers : " + query.getNumRR());
		System.out.println("Number of Authoritative answers : " + query.getNumAuthNS());
		System.out.println("IP address : " + query.getAnswers());
		if (query.getNumAuthNS()!=0){
			System.out.println("IP of Authoritative DNS Server : " + query.getAuthority());
		}
	}
	
	//------------------------------------------------------------------------
	//At Local DNS
	//Deleting IP and UDP headers from IP datagram received
	//Analyze Questions field in DNS query.
	//If domain name found in Local DNS database, add IP and UDP headers and 
	//   send IP datagram to Source. Else send IP datagram to Root
	//------------------------------------------------------------------------
	public void localDNS(IP datagram) {
		int srcPort=53;
		int destPort=53;
		int length=40;
		
		int version=4;
		int headerLen=20;
		int packetType=0;
		int totalLength=60;
		int ID=1234;
		int fragOffset=0;
		int TTL=50;
		int protocol=17;
		String options="";
		
		//Received IP datagram in Network layer.
		IP datagram1 = datagram;
		
		//Getting UDP segment from IP datagram at Transport layer.
		UDP segment = datagram1.getSegment();
		
		//Getting DNS query from UDP segment.
		DNS query = segment.getQuery();
		
		String Question = query.getQuestions();
		
		// Check if IP is the local DNS's database
		if (!IPfound && !searchComplete){
			Connection conn = null;
			Statement stmt = null;
			ResultSet rs = null;
			
			try {
				
				//Specify database location on machine
				Class.forName("com.mysql.jdbc.Driver").newInstance();
				String connectionUrl = "jdbc:mysql://localhost:3306/cpe600_dns";
				
				//Privilege information
				String connectionUser = "root";
				String connectionPassword = "ashok";
				
				//Establish database connection
				conn = DriverManager.getConnection(connectionUrl, connectionUser, connectionPassword);
				stmt = conn.createStatement();
				rs = stmt.executeQuery("SELECT * FROM localdns_table");
				
				//Query all items
				while (rs.next()) {
					String name = rs.getString("name");
					String value = rs.getString("value");
					String type = rs.getString("type");
					if (name.compareTo(Question)==0) {
						query.addAnswer(value);
						n++;
						query.setNumRR(n);
						IPfound = true;
						break;
					} else {
						IPfound = false;
					}
				} 
			} catch (Exception e) {
				e.printStackTrace();
				
				//Exit database
			} finally {
				try { if (rs != null) rs.close(); } catch (SQLException e) { e.printStackTrace(); }
				try { if (stmt != null) stmt.close(); } catch (SQLException e) { e.printStackTrace(); }
				try { if (conn != null) conn.close(); } catch (SQLException e) { e.printStackTrace(); }
			}
		}
		
		//Detemine whether search is done at this level
		segment = new UDP(srcPort, destPort, length, query);
		if (IPfound || searchComplete) {
			datagram = new IP(version, headerLen, packetType, totalLength, ID, 
					fragOffset, TTL, protocol, localDNSIP, srcIP, options, segment);
			srcDelHeader(datagram);
		//IP is not found so search root
		}else {
			datagram = new IP(version, headerLen, packetType, totalLength, ID, 
					fragOffset, TTL, protocol, localDNSIP, rootIP, options, segment);
			root(datagram);
		}
	}
	
	/*
	 * Search root server
	 * Deleting IP and UDP headers
	 * Analyze DNS Query
	 * Display output which is in DNS Answers field.
	 * Go to the appropiate TLD server
	 */
	public void root(IP datagram) {
		int srcPort=53;
		int destPort=53;
		int length=40;
		
		int version=4;
		int headerLen=20;
		int packetType=0;
		int totalLength=60;
		int ID=1234;
		int fragOffset=0;
		int TTL=50;
		int protocol=17;
		String options="";
		
		//Received IP datagram in Network layer.
		IP datagram1 = datagram;
		//Getting UDP segment from IP datagram at Transport layer.
		UDP segment = datagram1.getSegment();
		//Getting DNS query from UDP segment.
		DNS query = segment.getQuery();
		
		int len = query.getQuestions().length();
		String topLevel = query.getQuestions().substring(len - 3);
		
		if (IPfound || searchComplete) {
			segment = new UDP(srcPort, destPort, length,  query);
			datagram = new IP(version, headerLen, packetType, totalLength, ID, 
					fragOffset, TTL, protocol, rootIP, localDNSIP, options, segment);
			localDNS(datagram);
		} else {
			if (topLevel.equals("com")) {
				segment = new UDP(srcPort, destPort, length,  query);
				datagram = new IP(version, headerLen, packetType, totalLength, ID, 
						fragOffset, TTL, protocol, rootIP, comTLDIP, options, segment);

				comTLD(datagram);
			} else if (topLevel.equals("edu")) {
				segment = new UDP(srcPort, destPort, length,  query);
				datagram = new IP(version, headerLen, packetType, totalLength, ID, 
						fragOffset, TTL, protocol, rootIP, eduTLDIP, options, segment);

				eduTLD(datagram);
			} else if (topLevel.equals("org")) {
				segment = new UDP(srcPort, destPort, length,  query);
				datagram = new IP(version, headerLen, packetType, totalLength, ID, 
						fragOffset, TTL, protocol, rootIP, orgTLDIP, options, segment);
				orgTLD(datagram);
			} else {
				query.addAnswer("Not found");
				segment = new UDP(srcPort, destPort, length,  query);
				datagram = new IP(version, headerLen, packetType, totalLength, ID, 
						fragOffset, TTL, protocol, rootIP, localDNSIP, options, segment);
				searchComplete = true;
				localDNS(datagram);
			}
		}
	}

	/*
	 * Search TLD server
	 * Deleting IP and UDP headers
	 * Analyze DNS Query
	 * Display output which is in DNS Answers field.
	 * Go back to the root server with appropiate datagram
	 */
	public void orgTLD(IP datagram) {
		int srcPort=53;
		int destPort=53;
		int length=40;
		
		int version=4;
		int headerLen=20;
		int packetType=0;
		int totalLength=60;
		int ID=1234;
		int fragOffset=0;
		int TTL=50;
		int protocol=17;
		String options="";
		String name="";
		String value="";
		String type="";
		
		//Received IP datagram in Network layer.
		IP datagram1 = datagram;
		//Getting UDP segment from IP datagram at Transport layer.
		UDP segment = datagram1.getSegment();
		//Getting DNS query from UDP segment.
		DNS query = segment.getQuery();
		String Question = query.getQuestions();

		if (!IPfound){
			Connection conn = null;
			Statement stmt = null;
			ResultSet rs = null;
			try {
				Class.forName("com.mysql.jdbc.Driver").newInstance();
				String connectionUrl = "jdbc:mysql://localhost:3306/cpe600_dns";
				String connectionUser = "root";
				String connectionPassword = "ashok";
				
				//Establish database connection
				conn = DriverManager.getConnection(connectionUrl, connectionUser, connectionPassword);
				stmt = conn.createStatement();
				rs = stmt.executeQuery("SELECT * FROM orgtld_table"); 
				while (rs.next()) {
					name = rs.getString("name");
					value = rs.getString("value");
					type = rs.getString("type");
					if (name.compareTo(Question)==0) {
						query.addAnswer(value);
						n++;
						query.setNumRR(n);
						IPfound = true;
						searchComplete = true;
						break;
					} else {
						query.addAnswer("Not found in .org TLD");
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

		segment = new UDP(srcPort, destPort, length, query);
		if (IPfound) {
			datagram = new IP(version, headerLen, packetType, totalLength, ID, 
					fragOffset, TTL, protocol, orgTLDIP, rootIP, options, segment);
			root(datagram);
		} else {
			datagram = new IP(version, headerLen, packetType, totalLength, ID, 
					fragOffset, TTL, protocol, orgTLDIP, rootIP, options, segment);
			root(datagram);
		}
	}

	/*
	 * Search TLD server
	 * Deleting IP and UDP headers
	 * Analyze DNS Query
	 * Display output which is in DNS Answers field.
	 * Go back to the root server with appropiate datagram
	 */
	public void eduTLD(IP datagram) {
		int srcPort=53;
		int destPort=53;
		int length=40;
		
		int version=4;
		int headerLen=20;
		int packetType=0;
		int totalLength=60;
		int ID=1234;
		int fragOffset=0;
		int TTL=50;
		int protocol=17;
		String options="";
		
		//Received IP datagram in Network layer.
		IP datagram1 = datagram;
		//Getting UDP segment from IP datagram at Transport layer.
		UDP segment = datagram1.getSegment();
		//Getting DNS query from UDP segment.
		DNS query = segment.getQuery();
		String Question = query.getQuestions();

		if (!IPfound){
			Connection conn = null;
			Statement stmt = null;
			ResultSet rs = null;
			try {
				
				//Specify database location on machine
				Class.forName("com.mysql.jdbc.Driver").newInstance();
				String connectionUrl = "jdbc:mysql://localhost:3306/cpe600_dns";
				String connectionUser = "root";
				String connectionPassword = "ashok";
				
				//Establish database connection
				conn = DriverManager.getConnection(connectionUrl, connectionUser, connectionPassword);
				stmt = conn.createStatement();
				rs = stmt.executeQuery("SELECT * FROM edutld_table"); 
				
				//Query all item
				while (rs.next()) {
					String name = rs.getString("name");
					String value = rs.getString("value");
					String type = rs.getString("type");
					
					if (name.compareTo(Question)==0) {
						query.addAnswer(value);
						n++;
						query.setNumRR(n);
						IPfound = true;
						searchComplete = true;
						break;
					} else {
						query.addAnswer("Not found in .edu TLD");
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

		segment = new UDP(srcPort, destPort, length, query);
		if (IPfound) {
			datagram = new IP(version, headerLen, packetType, totalLength, ID, 
					fragOffset, TTL, protocol, eduTLDIP, rootIP, options, segment);
			root(datagram);
		} else {
			datagram = new IP(version, headerLen, packetType, totalLength, ID, 
					fragOffset, TTL, protocol, eduTLDIP, rootIP, options, segment);
			root(datagram);
		}
	}
	
	/*
	 * Search TLD server
	 * Deleting IP and UDP headers
	 * Analyze DNS Query
	 * Display output which is in DNS Answers field.
	 * Go back to the root server with appropiate datagram 
	 */
	public void comTLD(IP datagram) {
		int srcPort=53;
		int destPort=53;
		int length=40;
		
		int version=4;
		int headerLen=20;
		int packetType=0;
		int totalLength=60;
		int ID=1234;
		int fragOffset=0;
		int TTL=50;
		int protocol=17;
		String options="";
		
		String name="";
		String value="";
		String type="";

		//Received IP datagram in Network layer.
		IP datagram1 = datagram;
		//Getting UDP segment from IP datagram at Transport layer.
		UDP segment = datagram1.getSegment();
		//Getting DNS query from UDP segment.
		DNS query = segment.getQuery();
		String Question = query.getQuestions();
				
		Connection conn = null;
		Statement stmt = null;
		ResultSet rs = null;
		try {
			
			//Specify database location on machine
			Class.forName("com.mysql.jdbc.Driver").newInstance();
			String connectionUrl = "jdbc:mysql://localhost:3306/cpe600_dns";
			
			//Privilege information
			String connectionUser = "root";
			String connectionPassword = "ashok";
			
			//Establish database connection
			conn = DriverManager.getConnection(connectionUrl, connectionUser, connectionPassword);
			stmt = conn.createStatement();
			rs = stmt.executeQuery("SELECT * FROM comtld_table"); 
			

			//Query all items
			while (rs.next()) {
				String newName;
				name = rs.getString("name");
				value = rs.getString("value");
				type = rs.getString("type");
				if (type.compareTo("NS")== 0) {
					newName = name.substring(4);
				} else {
					newName = name;
				}
				if (newName.compareTo(Question)==0) {
					if(type.compareTo("NS")==0){
						query.setAuthority(value);
					} else {
						query.addAnswer(value);
					}
					n++;
					query.setNumRR(n);
					IPfound = true;
					searchComplete = true;
					break;
				} else {
					query.addAnswer("Not found in .com TLD");
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

		segment = new UDP(srcPort, destPort, length, query);
		
		//Depend on resource record type go the root or authorative DNS
		if (IPfound) {
			if (type.compareTo("A")==0) {
				datagram = new IP(version, headerLen, packetType, totalLength, ID, 
					fragOffset, TTL, protocol, comTLDIP, rootIP, options, segment);
					root(datagram);
			} else if (type.compareTo("NS") == 0) {
				datagram = new IP(version, headerLen, packetType, totalLength, ID, 
						fragOffset, TTL, protocol, comTLDIP, authDNSIP, options, segment);
					authDNS(datagram);
			} else if (type.compareTo("CNAME") == 0) {
				datagram = new IP(version, headerLen, packetType, totalLength, ID, 
						fragOffset, TTL, protocol, comTLDIP, rootIP, options, segment);
					root(datagram);
			}
		} else {
			datagram = new IP(version, headerLen, packetType, totalLength, ID, 
					fragOffset, TTL, protocol, comTLDIP, rootIP, options, segment);
			root(datagram);
		}
	}
	
	/*
	 * Search authoriative server
	 * Deleting IP and UDP headers
	 * Analyze DNS Query
	 * Display output which is in DNS Answers field.
	 * Go back to the root server with appropiate datagram 
	 */
	public void authDNS(IP datagram) {
		int srcPort=53;
		int destPort=53;
		int length=40;
		
		int version=4;
		int headerLen=20;
		int packetType=0;
		int totalLength=60;
		int ID=1234;
		int fragOffset=0;
		int TTL=50;
		int protocol=17;
		String options="";

		//Received IP datagram in Network layer.
		IP datagram1 = datagram;
		//Getting UDP segment from IP datagram at Transport layer.
		UDP segment = datagram1.getSegment();
		//Getting DNS query from UDP segment.
		DNS query = segment.getQuery();
		String Question = query.getQuestions();
				
		Connection conn = null;
		Statement stmt = null;
		ResultSet rs = null;
		try {
			
			//Specify database location on machine
			Class.forName("com.mysql.jdbc.Driver").newInstance();
			String connectionUrl = "jdbc:mysql://localhost:3306/cpe600_dns";
			
			//Privilege information
			String connectionUser = "root";
			String connectionPassword = "ashok";
			
			//Establish database connection
			conn = DriverManager.getConnection(connectionUrl, connectionUser, connectionPassword);
			stmt = conn.createStatement();
			
			rs = stmt.executeQuery("SELECT * FROM authdns_table"); 
			
			//Query all items
			while (rs.next()) {
				String name = rs.getString("name");
				String value = rs.getString("value");
				String type = rs.getString("type");
				if (name.compareTo(Question)==0) {
					query.addAnswer(value);
					m++;
					query.setNumAuthNS(m);
					IPfound = true;
					searchComplete = true;
					break;
				} else {
					query.addAnswer("Not found in " + query.getQuestions() + "auth DNS");
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

		segment = new UDP(srcPort, destPort, length, query);
		if (IPfound) {
			datagram = new IP(version, headerLen, packetType, totalLength, ID, 
					fragOffset, TTL, protocol, authDNSIP, comTLDIP, options, segment);
			root(datagram);
		} else {
			datagram = new IP(version, headerLen, packetType, totalLength, ID, 
					fragOffset, TTL, protocol, authDNSIP, comTLDIP, options, segment);
			root(datagram);
		}
	}
}