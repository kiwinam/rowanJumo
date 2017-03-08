package smart.rowan;

import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import smart.rowan.Fragment.OwnerFragment;
import smart.rowan.Fragment.WaiterFragment;
import smart.rowan.databinding.ActivitySelectionBinding;

public class SelectionActivity extends AppCompatActivity {
    private ActivitySelectionBinding bind;
    private static final int CONTENT_VIEW = R.layout.activity_selection;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        bind = DataBindingUtil.setContentView(this,CONTENT_VIEW);
        ViewPagerAdapter viewPagerAdapter = new ViewPagerAdapter(getSupportFragmentManager());
        viewPagerAdapter.addFragments(new OwnerFragment(), "Owner");
        viewPagerAdapter.addFragments(new WaiterFragment(), "Waiter/Waitress");
        bind.tabViewPager.setAdapter(viewPagerAdapter);
        bind.tabLayout.setupWithViewPager(bind.tabViewPager);
    }
}
