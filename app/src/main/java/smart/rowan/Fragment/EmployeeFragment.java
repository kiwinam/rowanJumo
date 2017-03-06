package smart.rowan.Fragment;


import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ListView;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Vector;

import smart.rowan.Dialog.EmployeeMemoDialog;
import smart.rowan.HomeActivity;
import smart.rowan.MethodClass;
import smart.rowan.R;
import smart.rowan.TaskMethod;
import smart.rowan.chatting.EmployeeAdapter;
import smart.rowan.chatting.OneOOneActivity;
import smart.rowan.chatting.User;

public class EmployeeFragment extends Fragment {

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
        } catch (Exception e) {
            e.getMessage();
        } finally {
            adapter = new EmployeeAdapter(getContext(), user, getActivity().getLayoutInflater());
            employeeListView.setAdapter(adapter);
            employeeListView.setOnItemClickListener(listener);
            employeeListView.setOnItemLongClickListener(longListener);
            ((AppCompatActivity) getActivity()).getSupportActionBar().setTitle("Employee");
            ((AppCompatActivity) getActivity()).getSupportActionBar().setDisplayHomeAsUpEnabled(false);

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

                        }
                    }
                }
            }).start();
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