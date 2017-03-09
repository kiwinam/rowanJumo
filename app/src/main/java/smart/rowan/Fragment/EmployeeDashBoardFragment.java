package smart.rowan.Fragment;

import android.content.Context;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Toast;

import com.prolificinteractive.materialcalendarview.CalendarDay;
import com.prolificinteractive.materialcalendarview.MaterialCalendarView;
import com.prolificinteractive.materialcalendarview.OnDateSelectedListener;
import com.prolificinteractive.materialcalendarview.OnMonthChangedListener;

import org.jetbrains.annotations.NotNull;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import smart.rowan.AttendanceDayDecorator;
import smart.rowan.HomeActivity;
import smart.rowan.R;
import smart.rowan.TodayDecorator;
import smart.rowan.databinding.FragmentEmployeeDashboardBinding;
import smart.rowan.etc.TaskMethod;

public class EmployeeDashBoardFragment extends Fragment implements OnDateSelectedListener, View.OnClickListener, OnMonthChangedListener {
    SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
    FragmentEmployeeDashboardBinding mEmployeeDashBinding;
    private static final String DID_NOT_WORK = "didn't work.";
    private static final String DIDNT_WORK_MSG = "You didn't work that day";
    Date date;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mEmployeeDashBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_employee_dashboard, container, false);
        View view = mEmployeeDashBinding.getRoot();
        mEmployeeDashBinding.calendarView.state().edit().setMaximumDate(Calendar.getInstance()).commit();
        mEmployeeDashBinding.calendarView.setOnDateChangedListener(this);
        mEmployeeDashBinding.calendarView.setOnMonthChangedListener(this);
        mEmployeeDashBinding.calendarView.addDecorator(new TodayDecorator());
        getTotalDays(mEmployeeDashBinding.calendarView.getCurrentDate());
        InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(mEmployeeDashBinding.calendarView.getWindowToken(), 0);
        return view;
    }

    @Override
    public void onDateSelected(@NotNull MaterialCalendarView widget, @Nullable CalendarDay date, boolean selected) {
        try {
            String result = new TaskMethod(getString(R.string.employee_dashboard_php), "restId=" + HomeActivity.sRest.getRestId() + "&userId=" + HomeActivity.sUser.getId() + "&date=" + getSelectedDatesString(), "UTF-8").execute().get();
            if (result.equals("NO DATA FOUND.")) {
                Toast.makeText(getActivity(), DIDNT_WORK_MSG, Toast.LENGTH_SHORT).show();
                mEmployeeDashBinding.hoursTextView.setText(DID_NOT_WORK);
                mEmployeeDashBinding.totalCallsTextView.setText(DID_NOT_WORK);
                mEmployeeDashBinding.averageTextView.setText(DID_NOT_WORK);
                //Snackbar.make(getActivity().findViewById(R.id.bottom_menu_snack),"You didn't work that day",Snackbar.LENGTH_SHORT).show();
            } else {
                JSONObject jsonResult = new JSONObject(result);
                mEmployeeDashBinding.hoursTextView.setText(jsonResult.getString("HOURS"));
                String calls = jsonResult.getString("COUNT") + " calls";
                mEmployeeDashBinding.totalCallsTextView.setText(calls);
                String sec = jsonResult.getString("AVG") + " sec";
                mEmployeeDashBinding.averageTextView.setText(sec);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        // get hours and average time
    }

    @Override
    public void onMonthChanged(MaterialCalendarView widget, CalendarDay date) {
        getTotalDays(date);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.totalDaysLayout:
                break;
            case R.id.hoursLayout:
                break;
            case R.id.totalCallsLayout:
                break;
            case R.id.averageServingLayout:
                break;
        }
    }

    private String getSelectedDatesString() {
        CalendarDay date = mEmployeeDashBinding.calendarView.getSelectedDate();
        if (date == null) {
            return "No Selection";
        }
        return format.format(date.getDate());
    }

    private void getTotalDays(CalendarDay date) {
        int j = 0;
        try {
            String[] result = new TaskMethod("http://165.132.110.130/rowan/monthly_dashboard.php", "userId=" + HomeActivity.sUser.getId() + "&date=" + format.format(date.getDate()), "UTF-8").execute().get().split("/");
            if (result.length == 1) {
                j = -1;
            } else {
                for (int i = 0; i < result.length - 1; i++) {
                    JSONObject jsonObjectResult = new JSONObject(result[i]);

                    this.date = format.parse(jsonObjectResult.getString("date"));
                    mEmployeeDashBinding.calendarView.addDecorator(new AttendanceDayDecorator(getActivity(), CalendarDay.from(this.date)));
                    //dates.add(this.date);
                    j = i;
                }
            }
            JSONObject jsonObjectResult = new JSONObject(result[j + 1]);
            String days = jsonObjectResult.getString("days") + " days";
            mEmployeeDashBinding.totalDaysTextView.setText(days);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}