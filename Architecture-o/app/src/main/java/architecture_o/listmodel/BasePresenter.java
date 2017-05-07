package architecture_o.listmodel;

import android.content.Context;
import android.support.v4.widget.SwipeRefreshLayout;

import architecture_o.adapter.BaseQuickAdapter;
import architecture_o.utils.ImageHelper;


public class BasePresenter implements IPresenter, SwipeRefreshLayout.OnRefreshListener, RequestData.OnDataListener, BaseQuickAdapter.RequestLoadMoreListener {

    IRequestView view;
    IData data;

    public BasePresenter(IRequestView view, IData data) {
        this.view = view;
        this.data = data;
        data.setDataListener(this);
    }

    private void doStateUnprepare() {

    }

    private void doStateNeedFetch() {
        view.hideEmpty();
        view.hideError();
        view.showLoading();
    }

    private void doStateEmpty(String error) {
        view.hideLoading();
        view.hideError();
        view.showEmpty(error);
    }

    private void doStateError(String error) {
        view.hideLoading();
        view.hideEmpty();
        if (!data.needShowLoading()){
            view.loadMoreFail();
        }else{
            view.showError(error);
        }
    }

    private void doStateNew() {
        view.hideError();
        view.hideLoading();
        view.hideEmpty();
        view.loadMoreComplete();

    }

    private void doStateFetching() {
        if (data.needShowLoading()) {
            view.hideEmpty();
            view.hideError();
            view.showLoading();
        }
    }

    @Override
    public void update(String error) {
        IData.Status state = data.getState();
        switch (state) {
            case StateUnprepare:
                doStateUnprepare();
                break;
            case StateNeedFetch:
                data.request();
                doStateNeedFetch();
                break;
            case StateEmpty:
                doStateEmpty(error);
                break;
            case StateError:
                doStateError(error);
                break;
            case StateNew:
                doStateNew();
                break;
            case StateFetching:
                doStateFetching();
                break;
            case StateUnkown:
                break;
        }
        view.refreshView();
    }

    @Override
    public void create(Context context, Object object, int listResId) {
        view.create(context, object, listResId);
        view.setOnRefreshListener(this);
        view.setOnScrollListener(new ImageHelper.PauseOnScrollListener(context));
        view.setOnLoadMoreListener(this);
        if (data.needShowLoading()) {
            view.enableLoadingMore(true);
        } else {
            view.setRefreshing(false);
        }
    }

    @Override
    public void init() {
        data.init();
    }

    @Override
    public void destroy() {
        data.uninit();
        view.destroy();
    }


    @Override
    public void refresh() {
        view.setRefreshing(true);
        onRefresh();
    }

    @Override
    public void onRefresh() {
        if (!data.request())
            view.setRefreshing(false);
    }

    @Override
    public void onDataReady(String reaseon) {
        update(reaseon);
        view.setRefreshing(false);

    }

    @Override
    public void onLoadMoreRequested() {
        if (data.requestNext()) {

        } else {
            view.loadMoreEnd();
        }
    }
}
