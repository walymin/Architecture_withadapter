package architecture_o.base;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.util.Pair;

import java.util.LinkedHashMap;
import java.util.Map;

import architecture_o.R;
import architecture_o.listmodel.IPresenter;


/**
 * Created by Herbert on 2015/5/19.
 */

public abstract class MultiListActivity extends BaseActivity {
    // keep the order when one item is inserted, so that when multi listmodels use the same datasource, only the first one need to init the datasource.
    protected Map<IPresenter, Pair<Integer, Integer>> mListImpls = new LinkedHashMap<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        for (Map.Entry<IPresenter, Pair<Integer, Integer>> listModel : mListImpls.entrySet()) {
            if (listModel.getValue() != null && listModel.getValue().first > 0)
                listModel.getKey().create(this, findViewById(listModel.getValue().first), listModel.getValue().second);
            else
                listModel.getKey().create(this, this, listModel.getValue().second);
            listModel.getKey().init();
            listModel.getKey().update(null);
        }
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        setIntent(intent);

        for (Map.Entry<IPresenter, Pair<Integer, Integer>> listModel : mListImpls.entrySet()) {
            listModel.getKey().init();
            listModel.getKey().update(null);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        for (Map.Entry<IPresenter, Pair<Integer, Integer>> listModel : mListImpls.entrySet()) {
            listModel.getKey().destroy();
        }
    }



    @Override
    protected int getLayoutId() {
        return R.layout.layout_state_list;
    }

    public void addPresenter(IPresenter listModel) {
        addPresenter(listModel, 0, android.R.id.list);
    }

    public void addPresenter(IPresenter listModel, int resId, int listResId) {
        mListImpls.put(listModel, new Pair<>(resId, listResId));
    }
}
