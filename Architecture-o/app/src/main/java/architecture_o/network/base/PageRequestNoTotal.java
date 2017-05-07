package architecture_o.network.base;

import android.os.Parcel;

import java.util.ArrayList;
import java.util.List;

import architecture_o.network.bean.JavaBean;


public abstract class PageRequestNoTotal<T extends JavaBean> extends RequestBase<T> implements IPageRequest<T> {
    protected static final int MAX_PAGE_COUNT = 20;

    protected int mCurrentPage = 0;
    protected boolean mIsEnd = false;

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
        return !mIsEnd;
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
        return mIsEnd;
    }

    @Override
    public void requestFirst() {
        super.reset();
        mIsEnd = false;
        mCurrentPage = 0;
        if (isEnd())
            return;
        super.request();
    }

    @Override
    public void clear() {
        mCurrentPage = 0;
        mList.clear();
        mIsEnd = false;
    }

    @Override
    protected void onResultEmpty(String reason) {
        super.onResultEmpty(reason);
        mIsEnd = true;
    }

    @Override
    protected void onResponseSuccess(boolean fromStorage, T bean) {
        if (mCurrentPage == 0 || mIsRestoredFromStorage) {
            clear();
            mReturnData = bean;
        }

        mIsRestoredFromStorage = fromStorage;

        ++mCurrentPage;
        List<?> list = getList(bean);
        if (list != null && list.size() > 0) {
            mList.addAll(list);
            if (list.size() < MAX_PAGE_COUNT)
                mIsEnd = true;
        } else
            mIsEnd = true;
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
        mIsEnd = parcel.readInt() > 0;
        mCurrentPage = parcel.readInt();
        mIsRestoredFromStorage = parcel.readInt() > 0;
    }

    @Override
    protected void saveToParcel(Parcel parcel) {
        super.saveToParcel(parcel);
        parcel.writeInt(mIsEnd ? 1 : 0);
        parcel.writeInt(mCurrentPage);
        parcel.writeInt(mIsRestoredFromStorage ? 1 : 0);
    }
}
