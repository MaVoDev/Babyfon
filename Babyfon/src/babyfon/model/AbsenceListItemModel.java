package babyfon.model;

public class AbsenceListItemModel {

	private int type;

	private String number;
	private String message;

	public AbsenceListItemModel(int type, String number, String message) {
		this.type = type;
		this.number = number;
		this.message = message;
	}

	public int getType() {
		return this.type;
	}

	public String getNumber() {
		return this.number;
	}

	public String getMessage() {
		return this.message;
	}
}
