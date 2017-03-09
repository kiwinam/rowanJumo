package smart.rowan.Fragment;

import android.content.Context;
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
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.utils.ColorTemplate;
import com.prolificinteractive.materialcalendarview.CalendarDay;
import com.prolificinteractive.materialcalendarview.MaterialCalendarView;
import com.prolificinteractive.materialcalendarview.OnDateSelectedListener;

import org.json.JSONArray;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import smart.rowan.HomeActivity;
import smart.rowan.R;
import smart.rowan.databinding.FragmentDashboardBinding;
import smart.rowan.etc.MethodClass;
import smart.rowan.etc.TaskMethod;


public class DashBoardFragment extends Fragment implements OnDateSelectedListener {

    String mRestId;
    private MethodClass methodClass;
    SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
    FragmentDashboardBinding mDashboardBinding;
    HashMap<String, Integer> peakTime;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mDashboardBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_dashboard, container, false);
        View view = mDashboardBinding.getRoot();
        methodClass = new MethodClass();
        mDashboardBinding.calendarView.setOnDateChangedListener(this);
        mDashboardBinding.calendarView.state().edit().setMaximumDate(Calendar.getInstance().getTime()).commit();
        InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(mDashboardBinding.calendarView.getWindowToken(), 0);
        //Set title
        ((AppCompatActivity) getActivity()).getSupportActionBar().setTitle("Dashboard");
        ((AppCompatActivity) getActivity()).getSupportActionBar().setDisplayHomeAsUpEnabled(false);
        return view;
    }

    @Override
    public void onDateSelected(@NonNull MaterialCalendarView widget, @Nullable CalendarDay date, boolean selected) {
        String time = "";
        ArrayList<Entry> pieEntries = new ArrayList<>();
        ArrayList<String> labels = new ArrayList<>();
        for(int i = 0 ; i<=24; i++) {
            labels.add(""+i+"");
        }
        String pkg = getActivity().getPackageName();
        ArrayList<String> peakTime = new ArrayList<>();
        ArrayList<Integer> countCall = new ArrayList<>();
        ArrayList<String> tableNum = new ArrayList<>();
        ArrayList<Integer> tableCount = new ArrayList<>();

        int i;
        try {
            mRestId = HomeActivity.sRest.getRestId();
            String result = new TaskMethod(getString(R.string.dashboard_php), "rest_id=" +
                    HomeActivity.sRest.getRestId() + "&timestamp=" + getSelectedDatesString(), "UTF-8").execute().get();
            String results[] = result.split("/");
            int[] count = new int[24];
            HashMap<String, Integer> peakTimes = new HashMap<>();
            TreeMap<String, Integer> topTime = new TreeMap<>(Collections.reverseOrder());
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
                    methodClass.setTableCountText((TextView) getActivity().findViewById(tableNumberId), tableNum.get(index), (TextView) getActivity().findViewById(tableCountId), tableCount.get(index));
                }
                for (i = 0; i < peakTimeArray.length(); i++) {
                    JSONObject jsonObject = peakTimeArray.getJSONObject(i);
                    String fullTime = jsonObject.getString("2hours");
                    Log.d("fullTime", fullTime);
                    peakTime.add(fullTime);
                    String[] splitTime = fullTime.split(" ");
                    Log.d("splitTime", splitTime[1].replace(":", ""));
                    int rangeTime = Integer.parseInt(splitTime[1].replace(":", ""));
                    //countCall.add(Integer.parseInt(jsonObject.getString("number_of_calls")));//table_number , table_served_count
                    //int pickTimeId = getResources().getIdentifier("dashPickTimeTv" + (i + 1), "id", pkg);
                    //int countCallId = getResources().getIdentifier("dashPickTimeCountTv" + (i + 1), "id", pkg);
                    //methodClass.setPeakTimeText((TextView) getActivity().findViewById(pickTimeId), peakTime.get(i), (TextView) getActivity().findViewById(countCallId), countCall.get(i));
                    if (0 <= rangeTime && rangeTime < 10000) {
                        count[0] += 1;
                        peakTimes.put("00",count[0]);
                    } else if (10000 <= rangeTime && rangeTime < 20000) {
                        count[1] += 1;
                        peakTimes.put("01",count[1]);
                    } else if (20000 <= rangeTime && rangeTime < 30000) {
                        count[2] += 1;
                        peakTimes.put("02",count[2]);
                    } else if (30000 <= rangeTime && rangeTime < 40000) {
                        count[3] += 1;
                        peakTimes.put("03",count[3]);
                    } else if (40000 <= rangeTime && rangeTime < 50000) {
                        count[4] += 1;
                        peakTimes.put("04",count[4]);
                    } else if (50000 <= rangeTime && rangeTime < 60000) {
                        count[5] += 1;
                        peakTimes.put("05",count[5]);
                    } else if (60000 <= rangeTime && rangeTime < 70000) {
                        count[6] += 1;
                        peakTimes.put("06",count[6]);
                    } else if (70000 <= rangeTime && rangeTime < 80000) {
                        count[7] += 1;
                        peakTimes.put("07",count[7]);
                    } else if (80000 <= rangeTime && rangeTime < 90000) {
                        count[8] += 1;
                        peakTimes.put("08",count[8]);
                    } else if (90000 <= rangeTime && rangeTime < 100000) {
                        count[9] += 1;
                        peakTimes.put("09",count[9]);
                    } else if (100000 <= rangeTime && rangeTime < 110000) {
                        count[10] += 1;
                        peakTimes.put("10",count[10]);
                    } else if (110000 <= rangeTime && rangeTime < 120000) {
                        count[11] += 1;
                        peakTimes.put("11",count[11]);
                    }else if (120000 <= rangeTime && rangeTime < 130000) {
                        count[12] += 1;
                        peakTimes.put("12",count[12]);
                    } else if (130000 <= rangeTime && rangeTime < 140000) {
                        count[13] += 1;
                        peakTimes.put("13",count[13]);
                    } else if (140000 <= rangeTime && rangeTime < 150000) {
                        count[14] += 1;
                        peakTimes.put("14",count[14]);
                    } else if (150000 <= rangeTime && rangeTime < 160000) {
                        count[15] += 1;
                        peakTimes.put("15",count[15]);
                    } else if (160000 <= rangeTime && rangeTime < 170000) {
                        count[16] += 1;
                        peakTimes.put("16",count[16]);
                    } else if (170000 <= rangeTime && rangeTime < 180000) {
                        count[17] += 1;
                        peakTimes.put("17",count[17]);
                    } else if (180000 <= rangeTime && rangeTime < 190000) {
                        count[18] += 1;
                        peakTimes.put("18",count[18]);
                    } else if (190000 <= rangeTime && rangeTime < 200000) {
                        count[19] += 1;
                        peakTimes.put("19",count[19]);
                    } else if (200000 <= rangeTime && rangeTime < 210000) {
                        count[20] += 1;
                        peakTimes.put("20",count[20]);
                    } else if (210000 <= rangeTime && rangeTime < 220000) {
                        count[21] += 1;
                        peakTimes.put("21",count[21]);
                    } else if (220000 <= rangeTime && rangeTime < 230000) {
                        count[22] += 1;
                        peakTimes.put("22",count[22]);
                    }else if (230000 <= rangeTime && rangeTime < 240000) {
                        count[23] += 1;
                        peakTimes.put("23",count[23]);
                    }
                    //pieEntries.add(new Entry(countCall.get(i), i));
                }

                for (int c = 0; c < count.length; c++) {
                    Log.d(count[c] + "", "" + c);
                    pieEntries.add(new Entry(count[c], c));
                }
                Iterator it = sortByValue(peakTimes).iterator();
                int counts = 0;
                while(it.hasNext()){
                    counts++;
                    if(counts>=6){
                        break;
                    }
                    String temp = (String) it.next();
                    topTime.put(temp, peakTimes.get(temp));

                }
                Iterator<String> treeMapReverseIter = topTime.keySet().iterator();
                int countss = 0;
                while( treeMapReverseIter.hasNext()) {
                    countss++;
                    String key = treeMapReverseIter.next();
                    int value = topTime.get( key );
                    int pickTimeId = getResources().getIdentifier("dashPickTimeTv" + (countss), "id", pkg);
                    int countCallId = getResources().getIdentifier("dashPickTimeCountTv" + (countss), "id", pkg);
                    methodClass.setPeakTimeText((TextView) getActivity().findViewById(pickTimeId), key, (TextView) getActivity().findViewById(countCallId), value);

                }

                for (int j = i; j < 5; j++) {
                    int pickTimeId = getResources().getIdentifier("dashPickTimeTv" + (j + 1), "id", pkg);
                    int countCallId = getResources().getIdentifier("dashPickTimeCountTv" + (j + 1), "id", pkg);
                    methodClass.initPeakTimeText((TextView) getActivity().findViewById(pickTimeId), (TextView) getActivity().findViewById(countCallId));
                }

            }
            String sec = time + "sec";
            mDashboardBinding.dashAverageTime.setText(sec);

            //PieDataSet pieDataSet = new PieDataSet(pieEntries, "Pick time");
            LineDataSet lineDataSet = new LineDataSet(pieEntries, "Peak time");
            //lineDataSet.setColors(new int[]{R.color.chart1, R.color.chart2, R.color.chart3, R.color.chart4, R.color.chart5}, this.getContext());
            lineDataSet.setColors(ColorTemplate.VORDIPLOM_COLORS);
            lineDataSet.setDrawCubic(true);
            lineDataSet.setDrawFilled(true); //선아래로 색상표시
            lineDataSet.setDrawValues(false);
            LineData thePieData = new LineData(labels, lineDataSet);
            //mDashboardBinding.lineChart.setData(thePieData);
            //mDashboardBinding.lineChart.animateX(1000);
            //mDashboardBinding.lineChart.setDescriptionTextSize(30);
            //mDashboardBinding.lineChart.setDescription(getSelectedDatesString());

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

    private List sortByValue(final Map map){
        List<String> list = new ArrayList();
        list.addAll(map.keySet());

        Collections.sort(list,new Comparator(){

            public int compare(Object o1,Object o2){
                Object v1 = map.get(o1);
                Object v2 = map.get(o2);

                return ((Comparable) v1).compareTo(v2);
            }

        });
        Collections.reverse(list); // 주석시 오름차순
        return list;
    }

}
