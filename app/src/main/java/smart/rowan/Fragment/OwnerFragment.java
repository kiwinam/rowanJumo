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
import smart.rowan.databinding.FragmentOwnerBinding;


public class OwnerFragment extends Fragment implements View.OnClickListener {

    String restId, restName, restPhone, restAddress, userId;
    FragmentOwnerBinding ownerBinding;

    public OwnerFragment() {}

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        ownerBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_owner, container, false);
        View view = ownerBinding.getRoot();

        ownerBinding.createRestBtn.setOnClickListener(this);
        InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(getContext().INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        SharedPreferences sharedPreferences = getActivity().getSharedPreferences("SharedData", Context.MODE_PRIVATE);
        userId = sharedPreferences.getString("id", null);

        return view;
    }

    public void onClick(View view) {
        if (view == ownerBinding.createRestBtn) {
            checkRestaurant();
        }
    }

    private void checkRestaurant() {
        boolean cancel = false;
        View focusView = null;

        // Reset errors.
        ownerBinding.restID.setError(null);
        ownerBinding.restName.setError(null);

        restId = ownerBinding.restID.getText().toString();
        restName = ownerBinding.restName.getText().toString();
        restPhone = ownerBinding.restPhone.getText().toString();
        restAddress = ownerBinding.restAddress.getText().toString();

        // Check Rest ID.
        if (TextUtils.isEmpty(restId)) {
            ownerBinding.restID.setError(getString(R.string.error_field_required));
            focusView = ownerBinding.restID;
            cancel = true;
        } else if (TextUtils.isEmpty(restName)) {
            ownerBinding.restName.setError(getString(R.string.error_field_required));
            focusView = ownerBinding.restName;
            cancel = true;
        }

        if (cancel) {
            focusView.requestFocus();
        } else {
            String method = "create";
            RestAuthentication restAuthentication = new RestAuthentication(getContext());
            restAuthentication.execute(method, restId, restName, restPhone, restAddress, userId);
        }
    }
}