package architecture_o.listmodel;

/**
 * Created by Herbert on 16/9/28.
 */

public abstract class BaseRequestData implements IData {
    OnDataListener onDataListener;

    @Override
    public void setDataListener(OnDataListener onDataListener) {
        this.onDataListener = onDataListener;
    }

    @Override
    public Status getState() {
        if (isStateUnprepare())
            return Status.StateUnprepare;
        if (isStateNeedFetch())
            return Status.StateNeedFetch;
        if (isStateEmpty())
            return Status.StateEmpty;
        if (isStateError())
            return Status.StateError;
        if (isStateNew())
            return Status.StateNew;
        if (isStateFetching())
            return Status.StateFetching;
        return Status.StateUnkown;
    }

    @Override
    public boolean needShowLoading() {
        return true;
    }
}
