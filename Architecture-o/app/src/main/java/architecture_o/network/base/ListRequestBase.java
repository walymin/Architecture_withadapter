package architecture_o.network.base;


import architecture_o.network.bean.JavaBean;

public abstract class ListRequestBase<T extends JavaBean> extends  PageRequestNoTotal<T> {
    @Override
    protected void onResponseSuccess(boolean fromStorage, T bean) {
//        clear();
        super.onResponseSuccess(fromStorage, bean);
        mIsEnd = true;
    }

    @Override
    public void request() {
        requestFirst();
    }
}
