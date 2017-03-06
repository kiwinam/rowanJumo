package smart.rowan;

import android.app.Activity;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.support.v4.content.ContextCompat;
import android.text.style.ForegroundColorSpan;

import com.prolificinteractive.materialcalendarview.CalendarDay;
import com.prolificinteractive.materialcalendarview.DayViewDecorator;
import com.prolificinteractive.materialcalendarview.DayViewFacade;
import com.prolificinteractive.materialcalendarview.spans.DotSpan;

import java.util.Collection;
import java.util.Date;
import java.util.HashSet;

/**
 * Created by charlie on 2017. 2. 15..
 */

public class TodayDecorator implements DayViewDecorator {

    private Drawable drawable;
    CalendarDay currentDay = CalendarDay.from(new Date());

    /*public TodayDecorator(Activity activity) {
        drawable = ContextCompat.getDrawable(activity,R.drawable.selector_decorator);
    }*/
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