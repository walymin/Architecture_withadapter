package architecture_o.base;

import android.content.Intent;
import android.os.Bundle;

import architecture_o.R;
import architecture_o.listmodel.IPresenter;


/**
 * Created by Herbert on 2015/5/19.
 */

public abstract class BaseListActivity extends BaseActivity{
    protected IPresenter presenter;
    public BaseListActivity(){
        presenter = createPresenter();
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        presenter.create(this, this, android.R.id.list);
        presenter.init();
        presenter.update(null);
    }

    @Override
    protected int getLayoutId() {
        return R.layout.layout_state_list;
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        setIntent(intent);

        presenter.init();
        presenter.update(null);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        presenter.destroy();
    }

    protected abstract IPresenter createPresenter();
}
