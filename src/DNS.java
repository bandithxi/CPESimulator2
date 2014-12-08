import java.util.ArrayList;

//DNS packet
public class DNS {
	
	//Fields
	private int ID;
	private int flags;
	private int numQuestion;
	private int numAnswers;
	private int numRR;
	private int numAuthNS;
	
	private String Questions;
	private String answers;
	private String authority;
	private String additional;
	
	public DNS(int iD, int flags, int numQuestion, int numAnswers, int numRR,
			int numAuthNS, String questions,
			String answers, String authority,
			String additional) {
		
		super();
		
		this.ID = iD;
		this.flags = flags;
		this.numQuestion = numQuestion;
		this.numRR = numRR;
		this.numAuthNS = numAuthNS;
		this.Questions = questions;
		this.answers = answers;
		this.authority = authority;
		this.additional = additional;
	}

	public int getID() {
		return ID;
	}

	public int getFlags() {
		return flags;
	}

	public String toString() {
		return "DNS [ID=" + ID + ", flags=" + flags + ", numQuestion="
				+ numQuestion + ", numRR=" + numRR + ", numAuthNS="
				+ numAuthNS + ", Questions=" + Questions + ", answers="
				+ answers + ", authority=" + authority + ", additional="
				+ additional + "]";
	}

	public int getNumQuestion() {
		return numQuestion;
	}

	public int getNumRR() {
		return numRR;
	}

	public int getNumAuthNS() {
		return numAuthNS;
	}

	public String getQuestions() {
		return Questions;
	}

	public String getAnswers() {
		return answers;
	}

	public String getAuthority() {
		return authority;
	}

	public String getAdditonal() {
		return additional;
	}
	
	public void setID(int iD) {
		ID = iD;
	}

	public void setFlags(int flags) {
		this.flags = flags;
	}

	public void setNumQuestion(int numQuestion) {
		this.numQuestion = numQuestion;
	}

	public void setNumRR(int numRR) {
		this.numRR = numRR;
	}

	public void setNumAuthNS(int numAuthNS) {
		this.numAuthNS = numAuthNS;
	}

	public void setQuestions(String questions) {
		Questions = questions;
	}

	public void addAnswer(String ans) {
		this.answers = ans;
	}

	public void setAuthority(String authority) {
		this.authority = authority;
	}

	public void setAdditonal(String additional) {
		this.additional = additional;
	}
	
}
