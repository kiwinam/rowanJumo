package smart.rowan;

import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.text.style.ForegroundColorSpan;

import com.prolificinteractive.materialcalendarview.CalendarDay;
import com.prolificinteractive.materialcalendarview.DayViewDecorator;
import com.prolificinteractive.materialcalendarview.DayViewFacade;

import java.util.Date;

public class TodayDecorator implements DayViewDecorator {

    private Drawable drawable;
    private CalendarDay currentDay = CalendarDay.from(new Date());

    public TodayDecorator() {
        currentDay = CalendarDay.today();
    }

    @Override
    public boolean shouldDecorate(CalendarDay day) {
        return currentDay != null && day.equals(currentDay);
    }

    @Override
    public void decorate(DayViewFacade view) {
        view.addSpan(new ForegroundColorSpan(Color.RED));
    }

    public void setDate(Date date) {
        currentDay = CalendarDay.from(date);
    }
}