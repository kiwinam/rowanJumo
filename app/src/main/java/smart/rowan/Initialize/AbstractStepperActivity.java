package smart.rowan.Initialize;

import android.os.Bundle;
import android.support.annotation.LayoutRes;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Toast;

import com.stepstone.stepper.StepperLayout;
import com.stepstone.stepper.VerificationError;

import smart.rowan.R;

/**
 * Created by charlie on 2017. 2. 20..
 */
public abstract class AbstractStepperActivity extends AppCompatActivity implements StepperLayout.StepperListener,
        OnNavigationBarListener {

    private static final String CURRENT_STEP_POSITION_KEY = "position";

    protected StepperLayout mStepperLayout;

    private long mLastTimeBackPressed;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTitle("Initialize at First Time");

        setContentView(getLayoutResId());
        mStepperLayout = (StepperLayout) findViewById(R.id.stepperLayout);
        int startingStepPosition = savedInstanceState != null ? savedInstanceState.getInt(CURRENT_STEP_POSITION_KEY) : 0;
        mStepperLayout.setAdapter(new FragmentStepAdater(getSupportFragmentManager(), this), startingStepPosition);

        mStepperLayout.setListener(this);
    }


    @LayoutRes
    protected abstract int getLayoutResId();

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        outState.putInt(CURRENT_STEP_POSITION_KEY, mStepperLayout.getCurrentStepPosition());
        super.onSaveInstanceState(outState);
    }

    @Override
    public void onBackPressed() {
        final int currentStepPosition = mStepperLayout.getCurrentStepPosition();
        if (currentStepPosition > 0) {
            mStepperLayout.setCurrentStepPosition(currentStepPosition - 1);
        } else {
            if (System.currentTimeMillis() - mLastTimeBackPressed < 1500) {
                finish();
                return;
            }
            mLastTimeBackPressed = System.currentTimeMillis();
            Snackbar.make(getWindow().getDecorView().getRootView(), "'뒤로' 버튼을 한 번 더 누르면 종료됩니다.", Snackbar.LENGTH_SHORT).show();
            finish();
        }
    }

    @Override
    public void onCompleted(View completeButton) {
        Toast.makeText(this, "onCompleted!", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onError(VerificationError verificationError) {
        Toast.makeText(this, "onError! -> " + verificationError.getErrorMessage(), Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onStepSelected(int newStepPosition) {
        Toast.makeText(this, "onStepSelected! -> " + newStepPosition, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onReturn() {
        finish();
    }

    @Override
    public void onChangeEndButtonsEnabled(boolean enabled) {
        mStepperLayout.setNextButtonVerificationFailed(!enabled);
        mStepperLayout.setCompleteButtonVerificationFailed(!enabled);
    }

}