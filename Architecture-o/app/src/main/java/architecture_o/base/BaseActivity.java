package architecture_o.base;

import android.os.Bundle;
import android.support.annotation.LayoutRes;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.jaeger.library.StatusBarUtil;
import com.trello.rxlifecycle2.components.support.RxAppCompatActivity;

import java.lang.ref.WeakReference;

import architecture_o.R;
import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by ZJW on 2017/4/19.
 */

public class BaseActivity extends RxAppCompatActivity {
    private LinearLayout mRootLayout;

    @Nullable
    @BindView(R.id.toolbar_id)
    Toolbar mToolbar;

    @Nullable
    @BindView(R.id.title)
    TextView mTitleView;
    public static WeakReference<AppCompatActivity> topActivityRef;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        super.setContentView(R.layout.layout_root);
        setContentView(getLayoutId());
        ButterKnife.bind(this);
        setUpToolbar();
       // setStateBarColor(getResources().getColor(R.color.colorPrimary));
    }

    protected void setStateBarColor(int color) {
        StatusBarUtil.setColor(this, color);
    }

    @Override
    public void setContentView(@LayoutRes int layoutResID) {
        setContentView(View.inflate(this, layoutResID, null));
    }

    @Override
    public void setContentView(View view) {
        mRootLayout = (LinearLayout) findViewById(R.id.root_layout);
        if (mRootLayout != null) {
            mRootLayout.addView(view, new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        topActivityRef = new WeakReference<AppCompatActivity>(this);
    }

    protected void setUpToolbar() {
        if (mToolbar != null && originalToolBarEnable()) {
            setSupportActionBar(mToolbar);
            getSupportActionBar().setTitle("");
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setHomeButtonEnabled(true);
        } else {
            mToolbar.setVisibility(View.GONE);
        }
    }

    protected boolean originalToolBarEnable() {
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            supportFinishAfterTransition();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }


    protected void setToolbarTitle(String title) {
        if (mTitleView != null) {
            mTitleView.setText(title);
        }
    }

    protected int getLayoutId() {
        return 0;
    }
}
