package architecture_o.network.base;

/**
 * Created by Herbert on 16/9/28.
 */

public interface IRequest {

    enum Status {
        Status_Init, Status_Fetching, Status_Old, Status_Success, Status_ResultError, Status_Empty, Status_RequestFailure
    }

    boolean isNew();

    boolean isReturned();

    boolean isFetching();

    boolean isLazy();

    boolean isError();

    boolean isRequestFailure();

    boolean isResultError();

    boolean isNotNew();

    boolean isEmpty();

    boolean needRefresh();

    boolean isOutOfDate();

    void request();

    void addOnResultListener(RequestStatusBase.OnResultListener listener);
    void removeOnResultListener(RequestStatusBase.OnResultListener listener);
    void setOnResultListener(RequestStatusBase.OnResultListener listener);
    void unsetOnResultListener();
}

