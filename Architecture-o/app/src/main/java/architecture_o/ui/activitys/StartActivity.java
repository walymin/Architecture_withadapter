package architecture_o.ui.activitys;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;

import architecture_o.R;
import architecture_o.base.BaseActivity;


public class StartActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setStateBarColor(Color.RED);
        startActivity(new Intent(this, MainActivity.class));
        finish();
    }

    @Override
    public int getLayoutId() {
        return R.layout.activity_start;
    }

    @Override
    protected boolean originalToolBarEnable() {
        return false;
    }


}
