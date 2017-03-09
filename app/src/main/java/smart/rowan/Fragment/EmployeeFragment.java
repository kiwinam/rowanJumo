package smart.rowan.Fragment;


import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Vector;

import smart.rowan.Dialog.EmployeeMemoDialog;
import smart.rowan.HomeActivity;
import smart.rowan.R;
import smart.rowan.chatting.EmployeeAdapter;
import smart.rowan.chatting.OneOOneActivity;
import smart.rowan.chatting.User;
import smart.rowan.etc.MethodClass;
import smart.rowan.etc.TaskMethod;

public class EmployeeFragment extends Fragment {

    private static final String ERROR_MSG1 = "Occurred Temporary Error, Please Check Network and try again. - EM";
    Vector<User> user = new Vector<>();
    ListView employeeListView;
    String mId;
    SharedPreferences myData;
    String hashString;
    private boolean isEF;
    private JSONArray jsonArrays;
    private EmployeeAdapter adapter;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_employee, container, false);
        try {
            myData = getActivity().getSharedPreferences("SharedData", Context.MODE_PRIVATE);
            hashString = myData.getString("messageMap", null);
            InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
            isEF = true;
            mId = HomeActivity.sUser.getLastName() + " " + HomeActivity.sUser.getFirstName();
            employeeListView = (ListView) view.findViewById(R.id.employeeListView);
            String strArray = new TaskMethod(getActivity().getString(R.string.employee),
                    "res_id=" + HomeActivity.sRest.getRestId(), "UTF-8").execute().get();
            JSONArray jsonArray = new JSONArray(strArray);
            jsonArrays = jsonArray;
            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject jo = jsonArray.getJSONObject(i);
                String fName = jo.getString("first_name");
                String lName = jo.getString("last_name");
                String email = jo.getString("email");
                if (!mId.equals(lName + " " + fName)) {
                    int value = 0;
                    if (hashString != null) {
                        HashMap<String, Integer> messageMap = MethodClass.changeStringToHashMap(hashString);
                        String emails = email.replace(".", "");
                        if (messageMap.containsKey(emails)) {
                            value = messageMap.get(emails);
                        } else {
                            value = 0;
                        }
                    }
                    user.add(new User((lName + " " + fName), email, value));
                }
            }
        } catch (Exception e) {
            e.getMessage();
        } finally {
            adapter = new EmployeeAdapter(getContext(), user, getActivity().getLayoutInflater());
            employeeListView.setAdapter(adapter);
            employeeListView.setOnItemClickListener(listener);
            employeeListView.setOnItemLongClickListener(longListener);
            ((AppCompatActivity) getActivity()).getSupportActionBar().setTitle("Employee");
            ((AppCompatActivity) getActivity()).getSupportActionBar().setDisplayHomeAsUpEnabled(false);
            /*Handler mHandler = new Handler(Looper.getMainLooper());
            mHandler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    // 내용

                }
            }, 0);*/
            try {
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        while (isEF) {
                            try {
                                Thread.sleep(333);
                                user.clear();
                                String hashString = myData.getString("messageMap", null);
                                for (int i = 0; i < jsonArrays.length(); i++) {
                                    JSONObject jo = jsonArrays.getJSONObject(i);
                                    String fname = jo.getString("first_name");
                                    String lname = jo.getString("last_name");
                                    String email = jo.getString("email");
                                    if (!mId.equals(lname + " " + fname)) {
                                        int value = 0;
                                        if (hashString != null) {
                                            HashMap<String, Integer> messageMap = MethodClass.changeStringToHashMap(hashString);
                                            String emails = email.replace(".", "");
                                            if (messageMap.containsKey(emails)) {
                                                value = messageMap.get(emails);
                                            } else {
                                                value = 0;
                                            }
                                        }
                                        user.add(new User((lname + " " + fname), email, value));
                                    }
                                }
                                Collections.sort(user, new MemberComparator());
                                getActivity().runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        adapter.notifyDataSetChanged();
                                    }
                                });

                            } catch (Exception e) {
                                //Log.d("errorMsg", e.getMessage());
                            }
                        }
                    }
                }).start();
            } catch (Exception e) {
                Toast.makeText(getContext(), ERROR_MSG1, Toast.LENGTH_SHORT).show();
            }
        }
        return view;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    AdapterView.OnItemClickListener listener = new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            Intent intent = new Intent(getContext(), OneOOneActivity.class);
            intent.putExtra("youNick", user.get(position).getName());
            HomeActivity.isHome = false;
            intent.putExtra("youEmail", user.get(position).getEmail());
            startActivity(intent);
            getActivity().finish();
        }
    };

    AdapterView.OnItemLongClickListener longListener = new AdapterView.OnItemLongClickListener() {
        @Override
        public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
            EmployeeMemoDialog dialog = new EmployeeMemoDialog(getActivity(), user.get(position).getName());
            dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
            dialog.show();
            return true;
        }
    };

    @Override
    public void onPause() {
        super.onPause();
        isEF = false;
    }
}

class MemberComparator implements Comparator {
    public int compare(Object arg0, Object arg1) {
        return ((User) arg0).getCount() > ((User) arg1).getCount() ? 1 : 0;
    }
}