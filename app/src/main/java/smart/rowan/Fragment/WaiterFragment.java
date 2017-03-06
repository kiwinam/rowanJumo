package smart.rowan.Fragment;


import android.content.Context;
import android.content.SharedPreferences;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;

import smart.rowan.R;
import smart.rowan.RestAuthentication;
import smart.rowan.databinding.FragmentWaiterBinding;


/**
 * A simple {@link Fragment} subclass.
 */
public class WaiterFragment extends Fragment implements View.OnClickListener {

    String restId, userId, ss;
    FragmentWaiterBinding waiterBinding;

    public WaiterFragment() {}


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        waiterBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_waiter, container, false);
        View view = waiterBinding.getRoot();
        InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        waiterBinding.joinRest.setOnClickListener(this);
        SharedPreferences sharedPreferences = getActivity().getSharedPreferences("SharedData", Context.MODE_PRIVATE);
        userId = sharedPreferences.getString("id", null);
        return view;
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.joinRest:
                joinRestaurant();
                break;
        }
    }

    private void joinRestaurant() {
        boolean cancel = false;
        View focusView = null;

        // Reset errors.
        waiterBinding.restIDJoin.setError(null);

        restId = waiterBinding.restIDJoin.getText().toString();

        // Check rest ID.
        if (TextUtils.isEmpty(restId)) {
            waiterBinding.restIDJoin.setError(getString(R.string.error_field_required));
            focusView = waiterBinding.restIDJoin;
            cancel = true;
        }
        if (cancel) {
            focusView.requestFocus();
        } else {
            String method = "join";
            RestAuthentication rest_auth = new RestAuthentication(getActivity(), getContext());
            rest_auth.execute(method, restId, userId);
        }
    }
}
