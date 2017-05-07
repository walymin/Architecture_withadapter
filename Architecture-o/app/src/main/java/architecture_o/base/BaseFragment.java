package architecture_o.base;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.trello.rxlifecycle2.components.support.RxFragment;

import butterknife.ButterKnife;
import butterknife.Unbinder;

public class BaseFragment extends RxFragment {

    private Unbinder unbinder;

    private boolean pageStarted;

    protected String getUserTag() {
        return getClass().getName();
    }

    protected boolean isInViewPager() {
        return false;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        unbinder = ButterKnife.bind(this, view);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }

    @Override
    public void onPause() {
        super.onPause();
        if (!isInViewPager())
            recordPage(false);
    }

    @Override
    public void onResume() {
        super.onResume();
        if (!isInViewPager())
            recordPage(true);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(getLayoutId(), container, false);
        //initInject();
        ButterKnife.bind(this, view);
        return view;
    }

    protected int getLayoutId() {
        return 0;
    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        Log.v(getUserTag(), "setUserVisibleHint " + isVisibleToUser);

        recordPage(isVisibleToUser);

        if (isVisibleToUser)
            onShow();
        else
            onHide();
    }

    @Override
    public void onHiddenChanged(boolean hidden) {
        setUserVisibleHint(!hidden);
    }

    protected void onShow() {

    }

    protected void onHide() {

    }

    protected boolean ifRecordPage() {
        return false;
    }

    private void recordPage(boolean start) {
        if (ifRecordPage() && pageStarted != start) {
            if (start) {
                //StatisticHelper.onPageStart(getUserTag());
            } else {
                //StatisticHelper.onPageEnd(getUserTag());
            }
            pageStarted = start;
        }
    }

   /* protected FragmentComponent getFragmentComponent() {
        return DaggerFragmentComponent.builder()
                .appComponent(AppComponent.Instance.get())
                .fragmentModule(getFragmentModule())
                .build();
    }

    protected FragmentModule getFragmentModule(){
        return new FragmentModule(this);
    }
    protected void initInject() {
    }*/
}
