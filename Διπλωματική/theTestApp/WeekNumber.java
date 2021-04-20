package theTestApp;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;


public class WeekNumber {
	public int getWeekNum(String date) throws ParseException {
		String format = "ddMMyyyy";

		SimpleDateFormat df = new SimpleDateFormat(format);
		Date datee = df.parse(date);

		Calendar cal = Calendar.getInstance();
		cal.setFirstDayOfWeek(Calendar.MONDAY);
		cal.setTime(datee);
		int week = cal.get(Calendar.WEEK_OF_YEAR);
		return week;
	}
}
