package babyfon.model;

public class AbsenceListItemModel {

	private String number;
	private String message;

	public AbsenceListItemModel(String number, String message) {
		this.number = number;
		this.message = message;
	}

	public String getNumber() {
		return this.number;
	}

	public String getMessage() {
		return this.message;
	}
}
