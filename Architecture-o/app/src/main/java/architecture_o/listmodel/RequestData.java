package architecture_o.listmodel;


import architecture_o.network.base.IPageRequest;
import architecture_o.network.base.IRequest;
import architecture_o.network.base.RequestStatusBase;

/**
 * Created by Herbert on 16/9/28.
 */

public class RequestData extends BaseRequestData implements RequestStatusBase.OnResultListener{

    private IRequest mRequest;
    public RequestData(IRequest request){
        mRequest = request;
    }

    @Override
    public boolean isStateUnprepare() {
        return mRequest == null;
    }

    @Override
    public boolean isStateNeedFetch() {
        return mRequest.isOutOfDate() || mRequest.isLazy();
    }

    @Override
    public boolean isStateEmpty() {
        return mRequest.isEmpty();
    }

    @Override
    public boolean isStateError() {
        return mRequest.isError();
    }

    @Override
    public boolean isStateNew() {
        return mRequest.isNew();
    }

    @Override
    public boolean isStateFetching() {
        return mRequest.isFetching();
    }

    @Override
    public boolean request() {
        if(mRequest == null) return false;
        if (mRequest instanceof IPageRequest/* && mRequest.isOutOfDate()*/) {
            ((IPageRequest) mRequest).requestFirst();
        } else
            mRequest.request();

        return true;
    }

    @Override
    public void init() {
        if (mRequest != null)
            mRequest.addOnResultListener(this);
    }

    @Override
    public void uninit() {
        if (mRequest != null)
            mRequest.removeOnResultListener(this);
    }

    @Override
    public boolean needShowLoading() {
        return !(mRequest instanceof IPageRequest && ((IPageRequest) mRequest).currentPage() > 0);
    }

    @Override
    public boolean requestNext() {
        boolean isEnd = true;
        if (mRequest == null) return false;
        if (mRequest instanceof IPageRequest) isEnd = ((IPageRequest) mRequest).isEnd();
        if (/*mRequest.isNew()&&*/  !isEnd) {
            mRequest.request();
            return true;
        }
        return false;
    }



    @Override
    public void onResult(RequestStatusBase request, RequestStatusBase.StructResult result, String reason) {
        if(request == mRequest && onDataListener != null){
            onDataListener.onDataReady(reason);
        }
    }
}
