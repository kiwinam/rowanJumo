package smart.rowan;

import android.app.Activity;

import com.prolificinteractive.materialcalendarview.CalendarDay;
import com.prolificinteractive.materialcalendarview.DayViewDecorator;
import com.prolificinteractive.materialcalendarview.DayViewFacade;


public class AttendanceDayDecorator implements DayViewDecorator {
    CalendarDay currentDay;
    Activity activity;

    public AttendanceDayDecorator(Activity activity, CalendarDay date) {
        this.activity = activity;
        currentDay = date;
    }

    @Override
    public boolean shouldDecorate(CalendarDay day) {
        return day.equals(currentDay);
    }

    @Override
    public void decorate(DayViewFacade view) {
        view.addSpan(new myDotSpan(6, activity.getApplicationContext().getResources().getColor(R.color.workColor)));
    }
}
