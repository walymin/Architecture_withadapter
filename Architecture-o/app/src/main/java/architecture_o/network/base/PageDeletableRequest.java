package architecture_o.network.base;

import architecture_o.network.bean.JavaBean;

public abstract class PageDeletableRequest<T extends JavaBean> extends PageRequestBase<T> {

    private int mDelCount;

    @Override
    public int getCount() {
        return super.getCount() - mDelCount;
    }

    @Override
    public void clear() {
        super.clear();
        mDelCount = 0;
    }

    public void remove(int index) {
        if (index >= 0 && index < mList.size()) {
            mDelCount++;
            mList.remove(index);
        }
    }

    @Override
    public void request() {
        //if (mDelCount > 0)
        super.request();
    }

    public int getDeletedCount() {
        return mDelCount;
    }
}
