package architecture_o.listmodel;

import java.util.List;

/**
 * Created by Herbert on 16/9/28.
 */

public abstract class FixedData extends BaseRequestData {
    protected abstract List getListData();

    @Override
    public boolean isStateUnprepare() {
        return false;
    }

    @Override
    public boolean isStateNeedFetch() {
        return false;
    }

    @Override
    public boolean isStateEmpty() {
        List list = getListData();
        return list == null || list.size() == 0;
    }

    @Override
    public boolean isStateError() {
        return false;
    }

    @Override
    public boolean isStateNew() {
        return true;
    }

    @Override
    public boolean isStateFetching() {
        return false;
    }

    @Override
    public boolean request() {
        return false;
    }

    @Override
    public void init() {

    }

    @Override
    public void uninit() {

    }

    @Override
    public boolean requestNext() {
        return false;
    }
}
