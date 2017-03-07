package smart.rowan;

import android.content.DialogInterface;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.View;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import smart.rowan.databinding.ActivitySignupBinding;

public class SignupActivity extends AppCompatActivity {

    private static final int REQUEST_SIGN_IN = 0;
    ActivitySignupBinding binding;
    String db_first_name, db_last_name, db_email, db_password, db_password_confirm;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_signup);

        getSupportActionBar().hide();

        binding.signUpBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                validate();
            }
        });


        // Adding term text view dialog.
        TextView term = (TextView) findViewById(R.id.term);

        term.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String term_msg = "Lorem ipsum dolor sit amet, consetetur sadipscing elitr, sed diam nonumy eirmod tempor invidunt ut labore et dolore magna aliquyam erat, sed diam voluptua. At vero eos et accusam et justo duo dolores et ea rebum. Stet clita kasd gubergren, no sea takimata sanctus est Lorem ipsum dolor sit amet. Lorem ipsum dolor sit amet, consetetur sadipscing elitr, sed diam nonumy eirmod tempor invidunt ut labore et dolore magna aliquyam erat, sed diam voluptua. At vero eos et accusam et justo duo dolores et ea rebum. Stet clita kasd gubergren, no sea takimata sanctus est Lorem ipsum dolor sit amet. Lorem ipsum dolor sit amet, consetetur sadipscing elitr, sed diam nonumy eirmod tempor invidunt ut labore et dolore magna aliquyamLorem ipsum dolor sit amet, consetetur sadipscing elitr, sed diam nonumy eirmod tempor invidunt ut labore et dolore magna aliquyam erat, sed diam voluptua. At vero eos et accusam et justo duo dolores et ea rebum. Stet clita kasd gubergren, no sea takimata sanctus est Lorem ipsum dolor sit amet. Lorem ipsum dolor sit amet, consetetur sadipscing elitr, sed diam nonumy eirmod tempor invidunt ut labore et dolore magna aliquyam erat, sed diam voluptua. At vero eos et accusam et justo duo dolores et ea rebum. Stet clita kasd gubergren, no sea takimata sanctus est Lorem ipsum dolor sit amet. Lorem ipsum dolor sit amet, consetetur sadipscing elitr, sed diam nonumy eirmod tempor invidunt ut labore et dolore magna aliquyamLorem ipsum dolor sit amet, consetetur sadipscing elitr, sed diam nonumy eirmod tempor invidunt ut labore et dolore magna aliquyam erat, sed diam voluptua. At vero eos et accusam et justo duo dolores et ea rebum. Stet clita kasd gubergren, no sea takimata sanctus est Lorem ipsum dolor sit amet. Lorem ipsum dolor sit amet, consetetur sadipscing elitr, sed diam nonumy eirmod tempor invidunt ut labore et dolore magna aliquyam erat, sed diam voluptua. At vero eos et accusam et justo duo dolores et ea rebum. Stet clita kasd gubergren, no sea takimata sanctus est Lorem ipsum dolor sit amet. Lorem ipsum dolor sit amet, consetetur sadipscing elitr, sed diam nonumy eirmod tempor invidunt ut labore et dolore magna aliquyam";

                AlertDialog.Builder builder = new AlertDialog.Builder(SignupActivity.this);
                builder.setTitle("ROWAN term of registration");
                builder.setMessage(term_msg);

                //auto check if agree
                builder.setPositiveButton("Agree", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        binding.checkAgree.setChecked(true);
                        binding.checkAgree.isChecked();
                    }
                });
                builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        binding.checkAgree.setChecked(false);
                    }
                });
                builder.show();
            }
        });


        // Back to login page
        TextView btnSignIn = (TextView) findViewById(R.id.signIn);
        btnSignIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
                startActivityForResult(intent, REQUEST_SIGN_IN);
            }
        });
    }

    private void validate() {
        // change errorSet() icon
        Drawable customErrorDrawable = getResources().getDrawable(R.drawable.ic_lock);
        customErrorDrawable.setBounds(0, 0, customErrorDrawable.getIntrinsicWidth(), customErrorDrawable.getIntrinsicHeight());

        boolean cancel = false;
        View focusView = null;


        // Reset errors.
        binding.firstName.setError(null);
        binding.lastName.setError(null);
        binding.email.setError(null);
        binding.password.setError(null);
        binding.confirmPassword.setError(null);

        // Store values at the time of the login attempt.
        db_first_name = binding.firstName.getText().toString();
        db_last_name = binding.lastName.getText().toString();
        db_email = binding.email.getText().toString();
        db_password = binding.password.getText().toString();
        db_password_confirm = binding.confirmPassword.getText().toString();

        // Check email.
        if (TextUtils.isEmpty(db_first_name)) {
            binding.firstName.setError(getString(R.string.error_field_required));
            focusView = binding.firstName;
            cancel = true;
        }

        if (TextUtils.isEmpty(db_last_name)) {
            binding.lastName.setError(getString(R.string.error_field_required));
            focusView = binding.lastName;
            cancel = true;
        }

        if (TextUtils.isEmpty(db_email)) {
            binding.email.setError(getString(R.string.error_field_required));
            focusView = binding.email;
            cancel = true;
        } else if (!android.util.Patterns.EMAIL_ADDRESS.matcher(db_email).matches()) {
            binding.email.setError(getString(R.string.error_invalid_email));
            focusView = binding.email;
            cancel = true;
        }

        // Check password
        if (TextUtils.isEmpty(db_password)) {
            binding.password.setError(getString(R.string.error_field_required));
            focusView = binding.password;
            cancel = true;
        } else if (TextUtils.isEmpty(db_password_confirm)) {
            binding.confirmPassword.setError(getString(R.string.error_field_required));
            focusView = binding.confirmPassword;
            cancel = true;
        } else if (!db_password.equals(db_password_confirm)) {
            binding.password.setError("Your passwords do not match.");
            focusView = binding.password;
            cancel = true;
        } else if (!passwordCheck(db_password)) {
            binding.password.setError(getString(R.string.error_invalid_password));
            focusView = binding.password;
            cancel = true;
        }

        if (cancel) {
            focusView.requestFocus();
        } else {
            if (!binding.checkAgree.isChecked()) {
                Toast.makeText(getApplicationContext(), "Please check the term of service.", Toast.LENGTH_LONG).show();
                // go to bottom side to show checkbox (agree the term).
                ScrollView sv = (ScrollView) findViewById(R.id.signupScrollView);
                sv.scrollTo(0, sv.getBottom());
            } else {
                String method = "signup";
                Authentication auth = new Authentication(this, this);
                auth.execute(method, db_first_name, db_last_name, db_email, db_password);
            }
        }
    }

    private boolean passwordCheck(String valid_password) {
        return valid_password.length() >= 6;
    }

    @Override
    public void onBackPressed() {
        startActivity(new Intent(this, LoginActivity.class));
        finish();
    }
}