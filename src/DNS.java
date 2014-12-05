import java.util.ArrayList;

public class DNS {
	private int ID;
	private int flags;
	private int numQuestion;
	private int numAnswers;
	private int numRR;
	private int numAdditionRR;
	
	private String Questions;
	private ArrayList<String> answers;
	private ArrayList<String> authority;
	private ArrayList<String> additonal;
	
	public DNS(int iD, int flags, int numQuestion, int numRR,
			int numAdditionRR, String questions,
			ArrayList<String> answers, ArrayList<String> authority,
			ArrayList<String> additonal) {
		
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

	@Override
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

	public ArrayList<String> getAnswers() {
		return answers;
	}

	public ArrayList<String> getAuthority() {
		return authority;
	}

	public ArrayList<String> getAdditonal() {
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
		this.answers.add(ans);
	}

	public void setAuthority(ArrayList<String> authority) {
		this.authority = authority;
	}

	public void setAdditonal(ArrayList<String> additonal) {
		this.additonal = additonal;
	}
	
}
