package architecture_o.listmodel;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.support.annotation.Nullable;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import architecture_o.R;
import architecture_o.adapter.BaseQuickAdapter;
import architecture_o.ui.widget.DecorationUtil;
import architecture_o.utils.ImageHelper;
import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by Herbert on 16/9/28.
 */

public class BaseRequestView implements IRequestView {
    BaseQuickAdapter mAdapter;

    protected RecyclerView mPtrList;
    @Nullable
    @BindView(R.id.swipe_refresh_layout)
    protected SwipeRefreshLayout mSwipeRefreshLayout;
    @Nullable
    @BindView(android.R.id.empty)
    protected View mEmpty;
    @Nullable
    @BindView(R.id.retry)
    protected View mError;
    @Nullable
    @BindView(R.id.wait)
    protected View mWait;

    @Override
    public void enableRefresh(boolean enable) {
        if (mSwipeRefreshLayout != null)
            mSwipeRefreshLayout.setEnabled(enable);
    }

    @Override
    public void showLoading() {
        if (mWait != null)
            mWait.setVisibility(View.VISIBLE);
    }

    @Override
    public void hideLoading() {
        if (mWait != null)
            mWait.setVisibility(View.GONE);
    }

    @Override
    public void showEmpty(String reason) {
        if (mEmpty != null)
            mEmpty.setVisibility(View.VISIBLE);
    }

    @Override
    public void hideEmpty() {
        if (mEmpty != null)
            mEmpty.setVisibility(View.GONE);
    }

    @Override
    public void showError(String reason) {
        if (mError != null)
            mError.setVisibility(View.VISIBLE);
    }

    @Override
    public void hideError() {
        if (mError != null)
            mError.setVisibility(View.GONE);
    }

    @Override
    public void enableLoadingMore(boolean enable) {
        if (mAdapter != null)
            mAdapter.setEnableLoadMore(enable);

    }

    @Override
    public void loadMoreFail() {
        if (mAdapter != null)
            mAdapter.loadMoreFail();
    }

    @Override
    public void loadMoreComplete() {
        if (mAdapter != null)
            mAdapter.loadMoreComplete();
    }

    @Override
    public void loadMoreEnd() {
        if (mAdapter != null)
            mAdapter.loadMoreEnd();
    }


    @Override
    public void create(Context context, Object object, int listResId) {
        if (object instanceof Activity) {
            ButterKnife.bind(this, (Activity) object);
            mPtrList = ButterKnife.findById((Activity) object, listResId);
        } else if (object instanceof View) {
            ButterKnife.bind(this, (View) object);
            mPtrList = ButterKnife.findById((View) object, listResId);
        } else if (object instanceof Dialog) {
            ButterKnife.bind(this, (Dialog) object);
            mPtrList = ButterKnife.findById((Dialog) object, listResId);
        }

        mAdapter = createAdapter();
        mPtrList.setHasFixedSize(true);
        mPtrList.setLayoutManager(createLayoutManager(context));
        mPtrList.setAdapter(mAdapter);
        mAdapter.disableLoadMoreIfNotFullPage(mPtrList);
        mPtrList.setItemAnimator(null);
        mPtrList.addOnScrollListener(new ImageHelper.PauseOnScrollListener(context));
        RecyclerView.ItemDecoration itemDecoration = createItemDecoration();
        if (itemDecoration != null)
            mPtrList.addItemDecoration(itemDecoration);

        if (mSwipeRefreshLayout != null) {
            mSwipeRefreshLayout.setColorSchemeResources(R.color.colorAccent);
        }
    }

    @Override
    public void destroy() {
        if (mPtrList != null)
            mPtrList.clearOnScrollListeners();
    }

    @Override
    public void setOnRefreshListener(SwipeRefreshLayout.OnRefreshListener listener) {
        if (mSwipeRefreshLayout != null) {
            mSwipeRefreshLayout.setOnRefreshListener(listener);
        }
    }

    @Override
    public void setOnScrollListener(RecyclerView.OnScrollListener listener) {
        if (mPtrList != null)
            mPtrList.addOnScrollListener(listener);
    }

    @Override
    public void setOnLoadMoreListener(BaseQuickAdapter.RequestLoadMoreListener listener) {
        if (mAdapter != null && mPtrList != null)
            mAdapter.setOnLoadMoreListener(listener, mPtrList);
    }

    @Override
    public RecyclerView getRecyclerView() {
        return mPtrList;
    }

    @Override
    public BaseQuickAdapter createAdapter() {
        return null;
    }


    @Override
    public RecyclerView.LayoutManager createLayoutManager(Context context) {
        return new LinearLayoutManager(context);
    }

    @Override
    public RecyclerView.ItemDecoration createItemDecoration() {
        return new DecorationUtil.LineDecoration();
    }

    public RecyclerView.Adapter getAdapter() {
        return mAdapter;
    }

    @Override
    public void refreshView() {
        mAdapter.notifyDataSetChanged();
    }


    @Override
    public void setRefreshing(boolean isRefreshing) {
        if (mSwipeRefreshLayout != null)
            mSwipeRefreshLayout.setRefreshing(isRefreshing);
    }

}
