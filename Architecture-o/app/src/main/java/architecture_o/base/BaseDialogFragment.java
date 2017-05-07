package architecture_o.base;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.trello.rxlifecycle2.components.support.RxDialogFragment;

import butterknife.ButterKnife;
import butterknife.Unbinder;


/**
 * Created by Herbert on 2015/1/24.
 */
public class BaseDialogFragment extends RxDialogFragment {

    private Unbinder unbinder;

    String getUserTag() {
        return getClass().getName();
    }


    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        unbinder = ButterKnife.bind(this, view);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(getLayoutId(), container, false);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }

    protected int getLayoutId() {
        return 0;
    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        Log.v(getUserTag(), "setUserVisibleHint " + isVisibleToUser);
        if (recordPage()) {
            if (isVisibleToUser) {
                //StatisticHelper.onPageStart(getUserTag());
            } else {
                //StatisticHelper.onPageEnd(getUserTag());

            }
        }
        if (isVisibleToUser)
            onShow();
        else
            onHide();
    }

    protected void onShow() {

    }

    protected void onHide() {

    }

    protected boolean recordPage() {
        return false;
    }
}
