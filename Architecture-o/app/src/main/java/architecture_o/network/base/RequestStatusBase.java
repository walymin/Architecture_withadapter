package architecture_o.network.base;

import android.os.Parcel;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.concurrent.TimeUnit;

import architecture_o.cache.DataStore;
import okhttp3.OkHttpClient;


public abstract class RequestStatusBase implements IRequest {

    public static String mRequestFailReason;
    protected static final OkHttpClient mOkHttpClient
            = new OkHttpClient.Builder()
            .connectTimeout(30, TimeUnit.SECONDS)
            .readTimeout(60, TimeUnit.SECONDS)
            .writeTimeout(10, TimeUnit.MINUTES)
            .build();


    //	EventHandler mEventHandler = new EventHandler();
    private IRequest.Status mStatus = IRequest.Status.Status_Init;
    private int errorCode;

    private ArrayList<WeakReference<OnResultListener>> mListeners = new ArrayList<>();
    private OnResultListener mStrongListener;

    public static void setRequestFailReason(String reason) {
        mRequestFailReason = reason;
    }

    protected void setErrorCode(int errorCode) {
        this.errorCode = errorCode;
    }

    public int getErrorCode() {
        return errorCode;
    }

    @Override
    public void addOnResultListener(OnResultListener listener) {
        for (WeakReference<OnResultListener> l : mListeners) {
            if (l.get() != null && l.get() == listener) {
                return;
            }
        }
        mListeners.add(new WeakReference<>(listener));
    }

    @Override
    public void removeOnResultListener(OnResultListener listener) {
        for (WeakReference<OnResultListener> l : mListeners) {
            if (l.get() != null && l.get() == listener) {
                mListeners.remove(l);
                break;
            }
        }
    }

    @Override
    public void setOnResultListener(OnResultListener listener) {
        mStrongListener = listener;
    }

    @Override
    public void unsetOnResultListener() {
        mStrongListener = null;
    }

    public ArrayList<WeakReference<OnResultListener>> getListeners() {
        return mListeners;
    }

    protected void onResultSuccess(String reason) {
        setStatus(IRequest.Status.Status_Success);
        for (int i = mListeners.size() - 1; i >= 0; i--) {
            WeakReference<OnResultListener> l = mListeners.get(i);
            if (l.get() != null)
                l.get().onResult(this, StructResult.Success, reason);
        }
        if (mStrongListener != null)
            mStrongListener.onResult(this, StructResult.Success, reason);
    }

    protected void onParseError() {
        setStatus(IRequest.Status.Status_ResultError);

        for (WeakReference<OnResultListener> l : mListeners) {
            if (l.get() != null)
                l.get().onResult(this, StructResult.ParseError, null);
        }
        if (mStrongListener != null)
            mStrongListener.onResult(this, StructResult.ParseError, null);
    }

    protected void onRequestError() {
        setStatus(IRequest.Status.Status_RequestFailure);

        for (WeakReference<OnResultListener> l : mListeners) {
            if (l.get() != null)
                l.get().onResult(this, StructResult.RequestFail, mRequestFailReason);
        }
        if (mStrongListener != null)
            mStrongListener.onResult(this, StructResult.RequestFail, mRequestFailReason);
    }

    protected void onResultError(String reason) {
        setStatus(IRequest.Status.Status_ResultError);

        for (WeakReference<OnResultListener> l : mListeners) {
            if (l.get() != null)
                l.get().onResult(this, StructResult.ResultError, reason);
        }
        if (mStrongListener != null)
            mStrongListener.onResult(this, StructResult.ResultError, reason);
    }

    protected void onResultEmpty(String reason) {
        setStatus(IRequest.Status.Status_Empty);

        for (WeakReference<OnResultListener> l : mListeners) {
            if (l.get() != null)
                l.get().onResult(this, StructResult.Empty, reason);
        }
        if (mStrongListener != null)
            mStrongListener.onResult(this, StructResult.Empty, reason);
    }

    public static String getSessionId() {
        String userSessionId = DataStore.getSessionId();
        if (userSessionId != null)
            return userSessionId;
        return DataStore.getInitSessionId();
    }

    public void reset() {
        mStatus = IRequest.Status.Status_Init;
    }

    public IRequest.Status getStatus() {
        return mStatus;
    }

    protected void setStatus(IRequest.Status status) {
        mStatus = status;
    }

    @Override
    public boolean isNew() {
        return mStatus == IRequest.Status.Status_Success;
    }

    @Override
    public boolean isReturned() {
        return mStatus == IRequest.Status.Status_Success
                || mStatus == IRequest.Status.Status_ResultError
                || mStatus == IRequest.Status.Status_RequestFailure
                || mStatus == IRequest.Status.Status_Empty;
    }

    @Override
    public boolean isFetching() {
        return mStatus == IRequest.Status.Status_Fetching;
    }

    @Override
    public boolean isLazy() {
        return mStatus == IRequest.Status.Status_Init;
    }

    @Override
    public boolean isError() {
        return mStatus == IRequest.Status.Status_RequestFailure
                || mStatus == IRequest.Status.Status_ResultError;
    }

    @Override
    public boolean isRequestFailure() {// ��������ʧ��
        return mStatus == IRequest.Status.Status_RequestFailure;
    }

    @Override
    public boolean isResultError() {
        return mStatus == IRequest.Status.Status_ResultError;
    }

    @Override
    public boolean isNotNew() {
        return mStatus == IRequest.Status.Status_Init || mStatus == IRequest.Status.Status_Old;
    }

    @Override
    public boolean isEmpty() {
        return mStatus == IRequest.Status.Status_Empty;
    }

    @Override
    public boolean needRefresh() {
        return mStatus != IRequest.Status.Status_Success
                && mStatus != IRequest.Status.Status_Fetching;
    }

    public interface OnResultListener {
        void onResult(RequestStatusBase struct, StructResult result, String reason);
    }

    public enum StructResult {
        Success, Empty, RequestFail, ResultError, ParseError
    }

//    public static void initSession() {
//        mStructSession.request();
//    }

    protected void saveToParcel(Parcel parcel) {
        parcel.writeInt(mStatus.ordinal());
        parcel.writeInt(errorCode);
    }

    protected void readFromParcel(Parcel parcel) {
        mStatus = IRequest.Status.values()[parcel.readInt()];
        errorCode = parcel.readInt();
    }
}
