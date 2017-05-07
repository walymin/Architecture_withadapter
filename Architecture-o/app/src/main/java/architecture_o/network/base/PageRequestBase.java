package architecture_o.network.base;

import android.os.Parcel;

import java.util.ArrayList;
import java.util.List;

import architecture_o.network.bean.JavaBean;


public abstract class PageRequestBase<T extends JavaBean> extends RequestBase<T> implements IPageRequest<T> {
    public static final int MAX_PAGE_COUNT = 20;
    private int mPageCount = 0;
    private int mTotalCount = 0;
    private int mCurrentPage = 0;

    public ArrayList<Object> mList = new ArrayList<Object>();
    private boolean mIsRestoredFromStorage;

    @Override
    public int getCurrentTotalCount() {
        return mList.size();
    }

    @Override
    public int getCountWithLoading() {
        int count = getCurrentTotalCount();
        return count + (isEnd() || count == 0 ? 0 : 1);
    }

    public int getCount() {
        return mTotalCount;
    }

    @Override
    public boolean isItemLoading(int position) {
        return getCurrentTotalCount() != 0 && !isEnd() && position == getCountWithLoading() - 1;
    }

    @Override
    public boolean isEmpty() {
        return super.isEmpty() || isNew() && getCurrentTotalCount() == 0;
    }

    @Override
    public Object getItem(int position) {
        if (position < 0) return null;
        if (position < mList.size())
            return mList.get(position);
        return null;
    }

    @Override
    protected RequestParams getParams() {
       RequestParams params = super.getParams();
        params.put("pageindex", currentPage() + 1);
        params.put("pagesize", MAX_PAGE_COUNT);
        return params;
    }

    @Override
    public int currentPage() {
        return mCurrentPage;
    }

    @Override
    public boolean hasNextPage() {
        return mPageCount == 0 ? !isEmpty() : mCurrentPage < mPageCount;
    }

    protected abstract int getTotalPage(T baseBean);

    protected abstract int getTotalCount(T baseBean);

    protected void resetPageInfo(int totalCount, int pageSize,
                                 int newPageContent) {
        mTotalCount = totalCount;
        mPageCount = pageSize;
        ++mCurrentPage;
    }

    @Override
    public void reset() {
        super.reset();

        clear();
    }

    @Override
    public void request() {
        if (isEnd())
            return;
        super.request();
    }

    @Override
    public boolean isEnd() {
        if (isLazy() || isFetching())
            return false;
        if (currentPage() >= mPageCount)
            return true;
        return false;
    }

    @Override
    public void requestFirst() {
        super.reset();
        mCurrentPage = 0;
        if (isEnd())
            return;
        super.request();
    }

    @Override
    public void clear() {
        mCurrentPage = 0;
        mPageCount = 0;
        mTotalCount = 0;
        mList.clear();
    }

    @Override
    protected void onResponseSuccess(boolean fromStorage, T bean) {
        if (mCurrentPage == 0 || mIsRestoredFromStorage) {
            clear();
            mReturnData = bean;
        }

        mIsRestoredFromStorage = fromStorage;

        List<?> list = getList(bean);
        resetPageInfo(getTotalCount(bean), getTotalPage(bean), list == null ? 0
                : list.size());
        if (list != null)
            mList.addAll(list);
    }

    @Override
    public void setOutOfDate() {
        if (getListeners().size() > 0) {
            requestFirst();
        } else
            super.setOutOfDate();
    }

    @Override
    protected void readFromParcel(Parcel parcel) {
        super.readFromParcel(parcel);
        mPageCount = parcel.readInt();
        mTotalCount = parcel.readInt();
        mCurrentPage = parcel.readInt();
        mIsRestoredFromStorage = parcel.readInt() > 0;
    }

    @Override
    protected void saveToParcel(Parcel parcel) {
        super.saveToParcel(parcel);
        parcel.writeInt(mPageCount);
        parcel.writeInt(mTotalCount);
        parcel.writeInt(mCurrentPage);
        parcel.writeInt(mIsRestoredFromStorage ? 1 : 0);
    }
}
