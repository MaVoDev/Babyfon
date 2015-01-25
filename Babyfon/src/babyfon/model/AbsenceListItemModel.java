package babyfon.model;

public class AbsenceListItemModel {

	private int type;

	private String number;
	private String message;
	private String date;
	private String time;

	public AbsenceListItemModel(int type, String number, String message, String date, String time) {
		this.type = type;
		this.number = number;
		this.message = message;
		this.date = date;
		this.time = time;
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

	public String getDate() {
		return this.date;
	}

	public String getTime() {
		return this.time;
	}
}
