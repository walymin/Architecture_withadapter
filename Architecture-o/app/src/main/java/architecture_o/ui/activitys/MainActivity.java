package architecture_o.ui.activitys;

import android.Manifest;
import android.os.Bundle;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.widget.FrameLayout;
import android.widget.Toast;

import com.tbruyelle.rxpermissions2.RxPermissions;

import architecture_o.R;
import architecture_o.base.BaseActivity;
import architecture_o.ui.fragments.TabDasFragment;
import architecture_o.ui.fragments.TabHomeFragment;
import architecture_o.ui.fragments.TabNotFragment;
import butterknife.BindView;

public class MainActivity extends BaseActivity {
    private static final String TAG = "MainActivity--";

    @BindView(R.id.content)
    FrameLayout content;
    @BindView(R.id.navigation)
    BottomNavigationView navigation;
    private int lastIndex = 0;
    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = item -> {
        switch (item.getItemId()) {
            case R.id.navigation_home:
                turnToTab(lastIndex, 0);
                lastIndex = 0;
                return true;
            case R.id.navigation_dashboard:
                turnToTab(lastIndex, 1);
                lastIndex = 1;
                return true;
            case R.id.navigation_notifications:
                turnToTab(lastIndex, 2);
                lastIndex = 2;
                return true;
        }
        return false;
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setToolbarTitle("aaa");

        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);
       /* if (savedInstanceState!=null){
            savedInstanceState.putInt("tab",navigation.getSelectedItemId());
            savedInstanceState.getInt("tab",-1);
        }*/
        turnToTab(lastIndex, 0);
        requestPermiss();

    }




    @Override
    protected int getLayoutId() {
        return R.layout.activity_main;
    }

    @Override
    protected boolean originalToolBarEnable() {
        return false;
    }


    void turnToTab(int lastIndex, int index) {
        String nextIndex = Integer.toString(index);
        Fragment fromFragment = getSupportFragmentManager().findFragmentByTag(Integer.toString(lastIndex));
        Fragment toFragment = getSupportFragmentManager().findFragmentByTag(nextIndex);

        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();

        if (fromFragment != null) {
            if (fromFragment != toFragment) {
                ft.hide(fromFragment);
                Log.v(TAG, "hide tab " + fromFragment.getClass().getName());
            }
        }

        if (toFragment == null) {
            try {
                toFragment = getFragmentClass(index).newInstance();
                ft.add(R.id.container, toFragment, nextIndex);
                Log.v(TAG, "add tab " + toFragment.getClass().getName());
            } catch (InstantiationException e) {
                e.printStackTrace();
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }
        } else {
            ft.show(toFragment);
            Log.v(TAG, "show tab " + toFragment.getClass().getName());
        }

        ft.commitAllowingStateLoss();
    }

    Class<? extends Fragment> getFragmentClass(int index) {
        Class cls = null;
        switch (index) {
            case 0:
                cls = TabHomeFragment.class;
                break;
            case 1:
                cls = TabDasFragment.class;
                break;
            case 2:
                cls = TabNotFragment.class;
                break;

            default:
                break;
        }
        return cls;
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putInt("tab", navigation.getSelectedItemId());
    }

    private static final long BACK_PRESSED_INTERVAL = 2000;
    private long mCurrentBackPressedTime;

    @Override
    public void onBackPressed() {
        if (navigation.getSelectedItemId() != R.id.navigation_home) {
            navigation.setSelectedItemId(R.id.navigation_home);
            return;
        }

        long curr = System.currentTimeMillis();
        if (curr - mCurrentBackPressedTime > BACK_PRESSED_INTERVAL) {
            mCurrentBackPressedTime = curr;
            Toast.makeText(this, "再按一次返回键退出应用",
                    Toast.LENGTH_SHORT).show();
            return;
        }
        super.onBackPressed();
    }

    protected void requestPermiss() {
        new RxPermissions(this).request(Manifest.permission.CAMERA)
                .subscribe(granted -> {
                    if (granted) { // Always true pre-M
                        // I can control the camera now
                    } else {
                    }
                });
    }
}
