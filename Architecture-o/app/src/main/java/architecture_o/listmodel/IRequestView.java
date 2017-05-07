package architecture_o.listmodel;

import android.content.Context;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.RecyclerView;

import architecture_o.adapter.BaseQuickAdapter;

/**
 * Created by Herbert on 16/9/28.
 */

public interface IRequestView {

    void showEmpty(String reason);

    void hideEmpty();

    void showLoading();

    void hideLoading();

    void showError(String reason);

    void hideError();

    void enableLoadingMore(boolean enable);

    void loadMoreFail();

    void loadMoreComplete();

    void loadMoreEnd();

    // 启用/禁用下拉刷新
    void enableRefresh(boolean enable);

    // 初始化控件，object为Activity或View或Dialog，同ButterKnife
    void create(Context context, Object object, int listResId);

    // 创建列表相关
    RecyclerView getRecyclerView();

    RecyclerView.Adapter createAdapter();

    RecyclerView.LayoutManager createLayoutManager(Context context);

    RecyclerView.ItemDecoration createItemDecoration();

    void refreshView();

    void setOnRefreshListener(SwipeRefreshLayout.OnRefreshListener listener);

    void setOnScrollListener(RecyclerView.OnScrollListener listener);

    void setOnLoadMoreListener(BaseQuickAdapter.RequestLoadMoreListener listener);

    void destroy();

    void setRefreshing(boolean isRefreshing);

    RecyclerView.Adapter getAdapter();
}
