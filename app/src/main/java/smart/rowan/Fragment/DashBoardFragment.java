package smart.rowan.Fragment;

import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.TextView;
import android.widget.Toast;

import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.prolificinteractive.materialcalendarview.CalendarDay;
import com.prolificinteractive.materialcalendarview.MaterialCalendarView;
import com.prolificinteractive.materialcalendarview.OnDateSelectedListener;

import org.json.JSONArray;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;

import smart.rowan.HomeActivity;
import smart.rowan.R;
import smart.rowan.TaskMethod;
import smart.rowan.databinding.FragmentDashboardBinding;


public class DashBoardFragment extends Fragment implements OnDateSelectedListener {

    String mRestId;

    SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
    FragmentDashboardBinding mDashboardBinding;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mDashboardBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_dashboard, container, false);
        View view = mDashboardBinding.getRoot();
        mDashboardBinding.calendarView.setOnDateChangedListener(this);
        mDashboardBinding.calendarView.state().edit().setMaximumDate(Calendar.getInstance().getTime()).commit();
        InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(getContext().INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(mDashboardBinding.calendarView.getWindowToken(), 0);
        //Set title
        ((AppCompatActivity) getActivity()).getSupportActionBar().setTitle("Dashboard");
        ((AppCompatActivity) getActivity()).getSupportActionBar().setDisplayHomeAsUpEnabled(false);
        return view;
    }

    private void initPeakTimeText(TextView peakTimeTextView, TextView peakTimeCountTextView) {
        peakTimeTextView.setVisibility(View.GONE);
        peakTimeCountTextView.setVisibility(View.GONE);
    }

    private void setPeakTimeText(TextView peakTimeTextView, String peakTime, TextView peakTimeCountTextView, int count) {
        peakTimeTextView.setText(peakTime);
        String called = String.valueOf(count) + " called";
        peakTimeCountTextView.setText(called);
        peakTimeTextView.setVisibility(View.VISIBLE);
        peakTimeCountTextView.setVisibility(View.VISIBLE);
    }

    private void setTableCountText(TextView tableNumberTextView, String peakTime, TextView callCountTextView, int count) {
        String called = String.valueOf(count) + "called";
        String peakTimes = "Number " + peakTime;
        tableNumberTextView.setText(peakTimes);
        callCountTextView.setText(called);
        tableNumberTextView.setVisibility(View.VISIBLE);
        callCountTextView.setVisibility(View.VISIBLE);
    }

    @Override
    public void onDateSelected(@NonNull MaterialCalendarView widget, @Nullable CalendarDay date, boolean selected) {
        String time = "";
        ArrayList<Entry> pieEntries = new ArrayList<>();
        String pkg = getActivity().getPackageName();
        ArrayList<String> peakTime = new ArrayList<>();
        ArrayList<Integer> countCall = new ArrayList<>();
        ArrayList<String> tableNum = new ArrayList<>();
        ArrayList<Integer> tableCount = new ArrayList<>();

        int i;
        try {
            mRestId = HomeActivity.sRest.getRestId();
            String[] results = new TaskMethod(getString(R.string.dashboard_php), "rest_id=" + HomeActivity.sRest.getRestId() + "&timestamp=" + getSelectedDatesString(), "UTF-8").execute().get().split("/");

            if (results[0].equals("null") || results[1].equals("null")) {
                Toast.makeText(getActivity(), "No data on " + getSelectedDatesString(), Toast.LENGTH_SHORT).show();
                //Snackbar.make(getActivity().findViewById(R.id.bottom_menu_snack), getSelectedDatesString()+"일의 데이터가 부족합니다.", Snackbar.LENGTH_SHORT).show();
            } else {
                JSONArray delayedTimeArray = new JSONArray(results[0]);
                JSONArray peakTimeArray = new JSONArray(results[1]);

                for (int index = 0; index < delayedTimeArray.length(); index++) {
                    JSONObject jsonObject = delayedTimeArray.getJSONObject(index);
                    time = jsonObject.getString("avg_delayed_time_for_day");
                    tableNum.add(jsonObject.getString("table_number"));
                    tableCount.add(Integer.parseInt(jsonObject.getString("table_served_count")));
                    int tableNumberId = getResources().getIdentifier("dashTableNumberTv" + (index + 1), "id", pkg);
                    int tableCountId = getResources().getIdentifier("dashTableCountTv" + (index + 1), "id", pkg);
                    setTableCountText((TextView) getActivity().findViewById(tableNumberId), tableNum.get(index), (TextView) getActivity().findViewById(tableCountId), tableCount.get(index));
                }
                for (i = 0; i < peakTimeArray.length(); i++) {
                    JSONObject jsonObject = peakTimeArray.getJSONObject(i);
                    peakTime.add(jsonObject.getString("hour_by_30min"));
                    countCall.add(Integer.parseInt(jsonObject.getString("number_of_calls")));//table_number , table_served_count
                    int pickTimeId = getResources().getIdentifier("dashPickTimeTv" + (i + 1), "id", pkg);
                    int countCallId = getResources().getIdentifier("dashPickTimeCountTv" + (i + 1), "id", pkg);
                    setPeakTimeText((TextView) getActivity().findViewById(pickTimeId), peakTime.get(i), (TextView) getActivity().findViewById(countCallId), countCall.get(i));

                    pieEntries.add(new Entry(countCall.get(i), i));
                    Log.d("hour_by_30min ", jsonObject.getString("hour_by_30min"));
                    Log.d("number_of_calls ", jsonObject.getString("number_of_calls"));
                }
                for (int j = i; j < 5; j++) {
                    int pickTimeId = getResources().getIdentifier("dashPickTimeTv" + (j + 1), "id", pkg);
                    int countCallId = getResources().getIdentifier("dashPickTimeCountTv" + (j + 1), "id", pkg);
                    initPeakTimeText((TextView) getActivity().findViewById(pickTimeId), (TextView) getActivity().findViewById(countCallId));
                }

            }
            String sec = time + "sec";
            mDashboardBinding.dashAverageTime.setText(sec);

            PieDataSet pieDataSet = new PieDataSet(pieEntries, "Pick time");
            pieDataSet.setColors(new int[]{R.color.chart1, R.color.chart2, R.color.chart3, R.color.chart4, R.color.chart5}, this.getContext());

            PieData thePieData = new PieData(peakTime, pieDataSet);
            mDashboardBinding.pieChart.setData(thePieData);
            mDashboardBinding.pieChart.animateX(1000);
            mDashboardBinding.pieChart.setDescriptionTextSize(30);
            mDashboardBinding.pieChart.setDescription(getSelectedDatesString());

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private String getSelectedDatesString() {
        CalendarDay date = mDashboardBinding.calendarView.getSelectedDate();
        if (date == null) {
            return "No Selection";
        }
        return format.format(date.getDate());
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }
}
