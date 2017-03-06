package smart.rowan.Initialize;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.IntRange;
import android.support.annotation.NonNull;
import android.support.v4.app.FragmentManager;
import android.util.Log;

import com.stepstone.stepper.Step;
import com.stepstone.stepper.adapter.AbstractFragmentStepAdapter;
import com.stepstone.stepper.viewmodel.StepViewModel;

import smart.rowan.Initialize.StepFragment;
import smart.rowan.R;

/**
 * Created by charlie on 2017. 2. 20..
 */

public class FragmentStepAdater extends AbstractFragmentStepAdapter {

    public FragmentStepAdater(@NonNull FragmentManager fm, @NonNull Context context) {
        super(fm, context);
    }

    @NonNull
    @Override
    public StepViewModel getViewModel(@IntRange(from = 0) int position) {
        Log.e("inStepViewModel", position + "");
        switch (position) {
            case 0:
                return new StepViewModel.Builder(context)
                        .setTitle("abcd")
                        .create();
            case 1:
                return new StepViewModel.Builder(context)
                        .setTitle("aaaaaa")
                        .create();
            case 2:
                return new StepViewModel.Builder(context)
                        .setTitle("bdfeda")
                        .create();
            default:
                return new StepViewModel.Builder(context)
                        .setTitle("default")
                        .create();
        }
        /*
        return new StepViewModel.Builder(context)
                .setTitle("Tab title")
                .create();*/
    }


    @Override
    public Step createStep(int position) {
        switch (position) {
            case 0:
                return StepFragment.newInstance(R.layout.fragment_setting_step1);
            case 1:
                return StepFragment.newInstance(R.layout.fragment_setting_step2);
            case 2:
                return StepFragment.newInstance(R.layout.fragment_setting_step3);
            default:
                throw new IllegalArgumentException("Unsupported position: " + position);
        }
    }

    @Override
    public int getCount() {
        return 3;
    }
}
