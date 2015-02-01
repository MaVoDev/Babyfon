package babyfon;

import java.util.Calendar;

public class Generator {

	public String getRandomPassword() {
		// Generating a password between 1000 and 10000
		int password = (int) Math.floor(Math.random() * 9000 + 1000);

		return password + "";
	}

	public String getCurrentTime() {
		Calendar mCalendar = Calendar.getInstance();
		int hours = mCalendar.get(Calendar.HOUR_OF_DAY);
		int minutes = mCalendar.get(Calendar.MINUTE);

		String currentTime;

		if (hours < 10) {
			if (minutes < 10) {
				currentTime = ("0" + hours + ":0" + minutes);
			} else {
				currentTime = ("0" + hours + ":" + minutes);
			}
		} else {
			if (minutes < 10) {
				currentTime = (hours + ":0" + minutes);
			} else {
				currentTime = (hours + ":" + minutes);
			}
		}

		return currentTime;
	}

	public String getCurrentDate() {
		Calendar mCalendar = Calendar.getInstance();
		int day = mCalendar.get(Calendar.DAY_OF_MONTH);
		int month = mCalendar.get(Calendar.MONTH) + 1;
		int year = mCalendar.get(Calendar.YEAR);

		String currentDate;

		if (day < 10) {
			if (month < 10) {
				currentDate = ("0" + day + ".0" + month + "." + year);
			} else {
				currentDate = ("0" + day + "." + month + "." + year);
			}
		} else {
			if (month < 10) {
				currentDate = (day + ".0" + month + "." + year);
			} else {
				currentDate = (day + "." + month + "." + year);
			}
		}

		return currentDate;
	}
}
