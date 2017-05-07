package architecture_o.base;

import android.content.Context;
import android.os.Bundle;
import android.view.View;

import architecture_o.R;
import architecture_o.listmodel.IPresenter;


/**
 * Created by Herbert on 2015/5/19.
 */

public abstract class BaseListFragment extends BaseFragment implements ObservableScrollView {
    protected IPresenter presenter;
    public BaseListFragment(){
        presenter = createPresenter();
    }

    @Override
    protected int getLayoutId() {
        return R.layout.layout_state_list;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        presenter.create(getActivity(), view, android.R.id.list);
        onShow();
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
    }

    @Override
    protected void onShow() {
        if (isAdded() && !isHidden() && getUserVisibleHint()) {
            presenter.init();
            presenter.update(null);
        }
    }

    @Override
    public View getScrollView() {
        if (isAdded())
            return getView().findViewById(android.R.id.list);
        return null;
    }

    @Override
    public int getHeaderHeight() {
        if (isAdded() && getActivity() instanceof ObservableParentView)
            return ((ObservableParentView) getActivity()).getHeaderHeight();
        return 0;
    }


    @Override
    public void onDestroyView() {
        super.onDestroyView();
        presenter.destroy();
    }

    protected abstract IPresenter createPresenter();
}
