import java.util.ArrayList;

public class DNS {
	private int ID;
	private int flags;
	private int numQuestion;
	private int numAnswers;
	private int numRR;
	private int numAdditionRR;
	
	private String Questions;
	private String answers;
	private String authority;
	private String additonal;
	
	public DNS(int iD, int flags, int numQuestion, int numRR,
			int numAdditionRR, String questions,
			String answers, String authority,
			String additonal) {
		
		super();
		
		this.ID = iD;
		this.flags = flags;
		this.numQuestion = numQuestion;
		this.numRR = numRR;
		this.numAdditionRR = numAdditionRR;
		this.Questions = questions;
		this.answers = answers;
		this.authority = authority;
		this.additonal = additonal;
	}

	public int getID() {
		return ID;
	}

	public int getFlags() {
		return flags;
	}

	public String toString() {
		return "DNS [ID=" + ID + ", flags=" + flags + ", numQuestion="
				+ numQuestion + ", numRR=" + numRR + ", numAdditionRR="
				+ numAdditionRR + ", Questions=" + Questions + ", answers="
				+ answers + ", authority=" + authority + ", additonal="
				+ additonal + "]";
	}

	public int getNumQuestion() {
		return numQuestion;
	}

	public int getNumRR() {
		return numRR;
	}

	public int getNumAdditionRR() {
		return numAdditionRR;
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
		return additonal;
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

	public void setNumAdditionRR(int numAdditionRR) {
		this.numAdditionRR = numAdditionRR;
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

	public void setAdditonal(String additonal) {
		this.additonal = additonal;
	}
	
}
