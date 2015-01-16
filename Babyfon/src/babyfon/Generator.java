package babyfon;

public class Generator {
	
	public String getRandomPassword() {
		// Generating a password between 1000 and 10000
		int password = (int) Math.floor(Math.random() * 9000 + 1000);

		return password + "";
	}
}
