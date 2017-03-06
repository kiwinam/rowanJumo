package smart.rowan.Fragment;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.databinding.DataBindingUtil;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.telephony.PhoneNumberFormattingTextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.RadioGroup;
import android.widget.TextView;

import java.util.HashMap;
import java.util.Map;

import smart.rowan.DatePickerFragmentBday;
import smart.rowan.HomeActivity;
import smart.rowan.R;
import smart.rowan.apiroute.ApiController;
import smart.rowan.databinding.FragmentMyEditBinding;

public class MyEditFragment extends Fragment {

    String mRestId, mRestName, mRestAddress, mRestPhone, mId, mFirstName, mLastName, mBirthDay, mGender, mPhone, mEmail, mAddress;
    String[] restInfo;
    SharedPreferences mydata;
    FragmentMyEditBinding mMy;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mMy = DataBindingUtil.inflate(inflater, R.layout.fragment_my_edit, container, false);
        View view = mMy.getRoot();
        InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        ((AppCompatActivity) getActivity()).getSupportActionBar().setTitle("");
        ((AppCompatActivity) getActivity()).getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        initData();
        if (HomeActivity.sUser.getPosition().equals("owner")) {
            setVisible(true, new EditText[]{mMy.restNameEditText, mMy.restAddressEditText, mMy.restPhoneEditText}, new TextView[]{mMy.restNameTextView, mMy.restAddressTextView, mMy.restPhoneTextView});
        } else {
            setVisible(false, new EditText[]{mMy.restNameEditText, mMy.restAddressEditText, mMy.restPhoneEditText}, new TextView[]{mMy.restNameTextView, mMy.restAddressTextView, mMy.restPhoneTextView});
        }
        setData();
        mMy.birthdayTextView.setOnClickListener(mButtonClickListenerBday);

        // set data

//        my_startdate.setText(m_startdate);
//        my_enddate.setText(m_enddate);
//        my_detail.setText(mDetail);

        // Set gender.
        mMy.gender.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                if (checkedId == R.id.male) {
                    mGender = "m";
                } else if (checkedId == R.id.female) {
                    mGender = "f";
                }
            }
        });

        return view;
    }

    private void initData() {
        restInfo = new String[]{HomeActivity.sRest.getRestName(), HomeActivity.sRest.getRestAddress(), HomeActivity.sRest.getRestPhone()};
        mRestId = HomeActivity.sRest.getRestId();
        mId = HomeActivity.sUser.getId();
        mFirstName = HomeActivity.sUser.getFirstName();
        mLastName = HomeActivity.sUser.getLastName();
        mBirthDay = HomeActivity.sUser.getBirthday();
        mGender = HomeActivity.sUser.getGender();
        mPhone = HomeActivity.sUser.getPhone();
        mEmail = HomeActivity.sUser.getEmail();
        mAddress = HomeActivity.sUser.getAddress();
    }

    private void setData() {
       /* mMy.restNameEditText.setText(mRestName);
        mMy.restAddressEditText.setText(mRestAddress);
        mMy.restPhoneEditText.setText(mRestPhone);*/
        mMy.restPhoneEditText.addTextChangedListener(new PhoneNumberFormattingTextWatcher());
        mMy.firstNameEditText.setText(mFirstName);
        mMy.lastNameEditText.setText(mLastName);
        mMy.birthdayTextView.setText(mBirthDay);
        mMy.phoneEditText.setText(mPhone);
        mMy.phoneEditText.addTextChangedListener(new PhoneNumberFormattingTextWatcher());
        mMy.emailEditText.setText(mEmail);
        mMy.addressEditText.setText(mAddress);
        if (mGender.equals("m")) {
            mMy.gender.check(R.id.male);
        } else if (mGender.equals("f")) {
            mMy.gender.check(R.id.female);
        } else {
            System.out.println("Gender not found!");
        }
    }

    private void setVisible(boolean isOwner, EditText[] edit, TextView[] text) {
        if (isOwner) {
            for (int i = 0; i < edit.length; i++) {
                edit[i].setVisibility(View.VISIBLE);
                edit[i].setText(restInfo[i]);
                text[i].setVisibility(View.GONE);
            }
            mMy.waiterRestEditLayout.setVisibility(View.GONE);
            mMy.restNameSmallTv.setVisibility(View.GONE);
            mMy.restNameSmallEd.setVisibility(View.VISIBLE);
            mMy.restAddressSmallTv.setVisibility(View.GONE);
            mMy.restAddressSmallEd.setVisibility(View.VISIBLE);
            mMy.restPhoneSmallTv.setVisibility(View.GONE);
            mMy.restPhoneSmallEd.setVisibility(View.VISIBLE);
        } else {
            for (int i = 0; i < text.length; i++) {
                Log.d("restInfo", restInfo[i] + "..");
                text[i].setVisibility(View.VISIBLE);
                text[i].setText(restInfo[i]);
                edit[i].setText(restInfo[i]);
                edit[i].setVisibility(View.GONE);
            }
            mMy.waiterRestEditLayout.setVisibility(View.VISIBLE);
            mMy.restNameSmallTv.setVisibility(View.VISIBLE);
            mMy.restNameSmallEd.setVisibility(View.GONE);
            mMy.restAddressSmallTv.setVisibility(View.VISIBLE);
            mMy.restAddressSmallEd.setVisibility(View.GONE);
            mMy.restPhoneSmallTv.setVisibility(View.VISIBLE);
            mMy.restPhoneSmallEd.setVisibility(View.GONE);
        }

    }

    private String[] getData() {
        EditText[] editList = {mMy.restNumEditText, mMy.restNameEditText, mMy.restAddressEditText, mMy.restPhoneEditText,
                mMy.firstNameEditText, mMy.lastNameEditText, mMy.phoneEditText, mMy.addressEditText};
        String[] editTextData = new String[10];
        for (int i = 0; i < editList.length; i++) {
            editTextData[i] = editList[i].getText().toString();
        }
        editTextData[8] = mMy.birthdayTextView.getText().toString();
        editTextData[9] = mGender;
        return editTextData;
    }

    private View.OnClickListener mButtonClickListenerBday = new View.OnClickListener() {
        public void onClick(View v) {
            showDatePickerDialogBday(v);
        }
    };

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.save, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int id = item.getItemId();
        if (id == android.R.id.home) {
            getFragmentManager().popBackStack();
            ((AppCompatActivity) getActivity()).getSupportActionBar().setDisplayHomeAsUpEnabled(false);
            return true;
        } else if (id == R.id.save_icon) {
            dataUpdate update = new dataUpdate(getContext());
            update.execute(getData());
        }
        return super.onOptionsItemSelected(item);
    }

    public void showDatePickerDialogBday(View v) {
        DialogFragment bday = new DatePickerFragmentBday();
        bday.show(getFragmentManager(), Integer.toString(v.getId()));
    }


    public class dataUpdate extends AsyncTask<String[], Void, String> {
        String result = "success";
        Context context;
        private ProgressDialog progressBar;

        dataUpdate(Context ctx) {
            this.context = ctx;
        }

        @Override
        protected void onPreExecute() {
            System.out.println("1 called");
            progressBar = new ProgressDialog(context);
            progressBar.setCancelable(true);
            progressBar.setMessage("Saving...");
            progressBar.setProgressStyle(ProgressDialog.STYLE_SPINNER);
            progressBar.setProgress(4);
            progressBar.setMax(100);
            progressBar.show();
        }

        protected String doInBackground(String[]... param) {
            String[] data = param[0];
            ApiController apiController = new ApiController();
            Map input = new HashMap<String, String>();
            input.put("userId", mId);
            input.put("restId", HomeActivity.sRest.getRestId());
            Log.d("restId on edit", HomeActivity.sRest.getRestId());
            input.put("restName", data[1]);
            input.put("restAddress", data[2]);
            input.put("restPhone", data[3]);
            input.put("fName", data[4]);
            input.put("lName", data[5]);
            input.put("phone", data[6]);
            input.put("addr", data[7]);
            input.put("birthday", data[8]);
            input.put("gender", data[9]);
            Log.d("data[9](gen)", data[9] + "..");
            apiController.start(input);
            if (!data[0].equals("")) {
                Log.d("data[0] is", "not null!" + data[0]);
                Map join = new HashMap<String, String>();
                join.put("joinrestid", data[0]);
                join.put("joinuserid", mId);
                apiController.joinStart(join);
            }
            HomeActivity.sRest.setRestName(data[1]);
            HomeActivity.sRest.setRestAddress(data[2]);
            HomeActivity.sRest.setRestPhone(data[3]);
            HomeActivity.sUser.setFirstName(data[4]);
            HomeActivity.sUser.setLastName(data[5]);
            HomeActivity.sUser.setPhone(data[6]);
            HomeActivity.sUser.setAddress(data[7]);
            HomeActivity.sUser.setBirthday(data[8]);
            HomeActivity.sUser.setGender(data[9]);
            try {
                return result;
            } catch (Exception e) {
                return e.getMessage();
            }
        }

        @Override
        protected void onProgressUpdate(Void... values) {
            super.onProgressUpdate(values);
            System.out.println("10 called ");
            //MyFragment myData = new MyFragment();
            // myData.setMyData(firstName, lastName, phone, email, mAddress);
        }

        protected void onPostExecute(String res) {
            getFragmentManager().popBackStack();
            ((AppCompatActivity) getActivity()).getSupportActionBar().setDisplayHomeAsUpEnabled(false);
            System.out.println("11 called ");
            progressBar.dismiss();
        }
    }
}