package smart.rowan;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.databinding.DataBindingUtil;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Toast;

import smart.rowan.chatting.EmployeeService;
import smart.rowan.chatting.EmployerService;
import smart.rowan.databinding.ActivityLoginBinding;
import smart.rowan.etc.MethodClass;

public class LoginActivity extends AppCompatActivity implements View.OnClickListener {
    private long mLastTimeBackPressed;
    String email, pass;
    String sessionCheck;
    ActivityLoginBinding loginBinding;
    InputMethodManager imm;
    private static final String BACK_BTN_TOUCH_MSG = "when touched , it turns off.";

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.signInBtn:
                if (MethodClass.isEmail(loginBinding.email.getText().toString())) {
                    validate();
                } else {
                    loginBinding.email.setError(getString(R.string.error_invalid_email));
                }
                break;
            case R.id.signUpBtn:
                Intent intent = new Intent(getApplicationContext(), SignupActivity.class);
                startActivity(intent);
                finish();
                break;
            case R.id.linearLoginForm:
                imm.hideSoftInputFromWindow(loginBinding.linearLoginForm.getWindowToken(), 0);
                break;
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportActionBar().hide();

        loginBinding = DataBindingUtil.setContentView(this, R.layout.activity_login);
        imm = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
        // Check login session
        if (MethodClass.isServiceRunningCheck(this, "smart.rowan.chatting.EmployerService")) {
            stopService(new Intent(this, EmployerService.class));
        }
        stopService(new Intent(this, EmployeeService.class));
        SharedPreferences sp = getSharedPreferences("SharedData", Context.MODE_PRIVATE);
        sessionCheck = sp.getString("id", "empty");
        if (!sessionCheck.equals("empty")) {
            Intent owner = new Intent(this, HomeActivity.class);
            this.startActivity(owner);
            finish();
        } else {
            loginBinding.signInBtn.setOnClickListener(this);
            loginBinding.signUpBtn.setOnClickListener(this);
            loginBinding.linearLoginForm.setOnClickListener(this);
        }
    }

    private void validate() {
        // change errorSet() icon
        Drawable customErrorDrawable = getResources().getDrawable(R.drawable.ic_lock);
        customErrorDrawable.setBounds(0, 0, customErrorDrawable.getIntrinsicWidth(), customErrorDrawable.getIntrinsicHeight());
        View focusView = null;
        boolean cancel = false;

        // Reset errors.
        loginBinding.email.setError(null);
        loginBinding.password.setError(null);

        // Store values at the time of the login attempt.
        email = loginBinding.email.getText().toString();
        pass = loginBinding.password.getText().toString();

        // Check email.
        if (TextUtils.isEmpty(email)) {
            loginBinding.email.setError(getString(R.string.error_field_required));
            focusView = loginBinding.email;
            cancel = true;
        } else if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            loginBinding.email.setError(getString(R.string.error_invalid_email));
            focusView = loginBinding.email;
            cancel = true;
        }

        // Check password
        if (TextUtils.isEmpty(pass)) {
            loginBinding.password.setError(getString(R.string.error_field_required));
            focusView = loginBinding.password;
            cancel = true;
        } else if (!passwordCheck(pass)) {
            loginBinding.password.setError(getString(R.string.error_invalid_password));
            focusView = loginBinding.password;
            cancel = true;
        }

        if (cancel) {
            focusView.requestFocus();
        } else {
            String method = "login";
            Authentication auth = new Authentication(this, this);
            auth.execute(method, email, pass);

        }
    }

    private boolean passwordCheck(String valid_password) {
        return valid_password.length() >= 6;
    }

    public void onBackPressed() {
        if (System.currentTimeMillis() - mLastTimeBackPressed < 1500) {
            finish();
            return;
        }
        mLastTimeBackPressed = System.currentTimeMillis();
        Toast.makeText(this, BACK_BTN_TOUCH_MSG, Toast.LENGTH_SHORT).show();
    }

}