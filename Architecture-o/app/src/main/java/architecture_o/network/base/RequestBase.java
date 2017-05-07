package architecture_o.network.base;

import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.os.Parcel;
import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonSyntaxException;
import com.google.gson.TypeAdapter;
import com.google.gson.internal.bind.util.ISO8601Utils;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonToken;
import com.google.gson.stream.JsonWriter;

import java.io.IOException;
import java.lang.ref.WeakReference;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.ParsePosition;
import java.text.SimpleDateFormat;
import java.util.ConcurrentModificationException;
import java.util.Date;
import java.util.Locale;

import architecture_o.cache.DataStore;
import architecture_o.network.bean.BaseBean;
import architecture_o.network.bean.JavaBean;
import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

import static architecture_o.cache.DataStore.getVersion;


public abstract class RequestBase<T extends JavaBean> extends RequestStatusBase {
    private static final String TAG = RequestBase.class.getName();
    private static final int MSG_SUCCESS = 0;
    private static final int MSG_REQUEST_FAILURE = 1;
    private static final int MSG_RESULT_FAILURE = 2;

    protected static Gson g = new GsonBuilder()
            .serializeNulls()
            .registerTypeAdapter(int.class, new IntTypeAdapter())
            .registerTypeAdapter(Integer.class, new IntTypeAdapter())
            .registerTypeAdapter(long.class, new LongTypeAdapter())
            .registerTypeAdapter(Long.class, new LongTypeAdapter())
            .registerTypeAdapter(Date.class, new DateTypeAdapter())
            .create();

    private String mLastSession;
    private boolean mSync = false;
    private boolean mIsOutOfDate = false;
    protected boolean mHasRequestFromStorage;
    private ResponseHandler mHandler;
    private Call mCall;
    private Callback mCallback = new MyCallback(this);
    private ProgressRequestBody.ProgressListener mProgressListener;
    protected T mReturnData;

    private static RequestObservableFactory mRequestObservableFactory;

    public T getReturnData() {
        return mReturnData;
    }

    public void setOnProgressListener(ProgressRequestBody.ProgressListener listener) {
        mProgressListener = listener;
    }

    @Override
    public void request() {
        request(false);
    }

    public void request(boolean sync) {
        if (getStatus() == Status.Status_Fetching)
            return;

        if (mHandler == null) {
            Looper looper = Looper.myLooper();
            if (!sync) {
                if (looper != null)
                    mHandler = new ResponseHandler(this);
                else {
                    Log.e(TAG, "Current thread has not called Looper.prepare(). Forcing synchronous mode.");
                    sync = true;
                }
            }
        }

        mSync = sync;
        setStatus(Status.Status_Fetching);
        mLastSession = getSessionId();
        doRequest();
        mIsOutOfDate = false;

        if (isStorageEnabled() && !mHasRequestFromStorage) {
            fromStorage();
            mHasRequestFromStorage = true;
        }
    }

    public static <T extends JavaBean, E extends RequestBase<T>>
    Observable<E> requestAsync(E request) {
        request.request(false);
        return asObservable(request)
                .subscribeOn(AndroidSchedulers.mainThread())
                .observeOn(AndroidSchedulers.mainThread())
                .unsubscribeOn(AndroidSchedulers.mainThread());
    }

    protected RequestParams getParams() {
        return getSuffix();
    }

    public static RequestParams getSuffix() {
        RequestParams builder = new RequestParams();
        builder.put("version", getVersion());
        builder.put("platform", "android");
        String sessionId = getSessionId();
        if (sessionId != null)
            builder.put("PHPSESSID", sessionId);
        return builder;
    }

    protected boolean isGet() {
        return false;
    }

    protected Request.Builder buildRequest() {
        String url = getRequestUrl();
        RequestParams params = getParams();
        Request.Builder requestBuilder = new Request.Builder();
        if (isGet())
            requestBuilder.url(url + (params == null ? "" : "&" + params.toString())).get();
        else {
            requestBuilder.url(url);
            if (params != null) {
                RequestBody body = params.createMultipartEntity().build();
                if (mProgressListener != null)
                    requestBuilder.post(new ProgressRequestBody(body, mProgressListener));
                else
                    requestBuilder.post(body);
                Log.v(TAG, "http request: " + url + "&" + params.toString());
            } else {
                Log.v(TAG, "http request: " + url);
            }
        }
        return requestBuilder;
    }

    protected void doRequest() {
        Request request = buildRequest().build();
        Log.v(TAG, "do Request " + mSync);
        if (mSync) {
            try {
                mCall = mOkHttpClient.newCall(request);
                Response response = mCall.execute();
                onRequestResult(response);
            } catch (IOException e) {
                onRequestFail();
            }
        } else {
            mCall = mOkHttpClient.newCall(request);
            mCall.enqueue(mCallback);
        }
    }

    private void onRequestFail() {
        if (!isStorageEnabled() || getStorage() == null) {
            onRequestError();
        }
    }

    private void onResponseError(String msg) {
        onResultError(msg);
    }

    private void onRequestResult(Response response) {
        if (response.isSuccessful()) {
            try {
                RequestBase.this.onParseResponse(false, response.body().string());
            } catch (IOException e) {
                onResultError(response.message());
            }
        } else {
            onResultError(response.message());
        }
    }

    private static class MyCallback implements Callback {
        WeakReference<RequestBase> structBase;

        MyCallback(RequestBase structBase) {
            this.structBase = new WeakReference<>(structBase);
        }

        @Override
        public void onFailure(Call call, IOException e) {
            if (structBase.get() != null) {
                if (structBase.get().mSync)
                    structBase.get().onRequestFail();
                else if (structBase.get().mHandler != null)
                    structBase.get().mHandler.sendFailureMessage();
            }
        }

        @Override
        public void onResponse(Call call, Response response) throws IOException {
            if (structBase.get() != null) {
                if (structBase.get().mSync) {
                    if (response.isSuccessful())
                        structBase.get().onParseResponse(false, response.body().string());
                    else
                        structBase.get().onResponseError(response.message());
                } else if (structBase.get().mHandler != null)
                    structBase.get().mHandler.sendSuccessMessage(response);
            }
        }
    }

    @Override
    public boolean isLazy() {
        return getSessionId() != null && !getSessionId().equals(mLastSession)
                && !isFetching() || super.isLazy();
    }

    @Override
    public void reset() {
        cancel();

        super.reset();
    }

    protected void cancel() {
        if (isFetching() && mCall != null)
            mCall.cancel();
    }

    protected boolean returnEmpty(int returnCode) {
        switch (returnCode) {
            case -1000:
                return true;
            default:
                return false;
        }
    }

    protected boolean returnSuccess(int returnCode) {
        return returnCode == 0;
    }

    public boolean fromStorage() {
        final String data = getStorage();
        if (data != null && mHandler != null) {
            Log.v(TAG, "read from storage");
            mHandler.sendSuccessMessageFromStorage(data);
            return true;
        }

        return false;
    }

    protected void setStorage(String data) {
      DataStore.putRequestCache("RequestCache_" + getRequestUrl(), data);
    }

    protected boolean isStorageEnabled() {
        return false;
    }

    // if sending success result again after request returns when it has already read from storage.
    protected boolean isQuietWhenFromStorage() {
        return false;
    }

    private String getStorage() {
        String storage = DataStore.getRequesCache("RequestCache_" + getRequestUrl());
        return storage == null ? getDefaultStorage() : storage;
    }

    protected String getDefaultStorage() {
        return null;
    }

    protected void onParseResponse(boolean fromStorage, String string) {
        Log.v(TAG, "mode sync: " + mSync);
        Log.v(TAG, getClass().getSimpleName() + ":" + string);
        int index = string.indexOf('{');
        int index2 = string.indexOf('[');
        if (index2 >= 0 && index2 < index)
            index = index2;
        if (index != -1)
            string = string.substring(index);
        string = string.replace("\\\\/", "/");
        try {
            T bean = parseJson(string);
//            onGetBean(bean);
            int returnCode = 0;
            String returnMsg = null;
            if (bean instanceof BaseBean) {
                returnCode = Integer.parseInt(((BaseBean) bean).returnCode);
                returnMsg = ((BaseBean) bean).returnMsg;
            }
            if (returnSuccess(returnCode)) {
                if (!fromStorage || !isQuietWhenFromStorage()) {
//                Log.v(TAG, "step1");
                    onResponseSuccess(fromStorage, bean);
//                Log.v(TAG, "step2");
                    try {
                        onResultSuccess(returnMsg);
//                    Log.v(TAG, "step3");
                    } catch (ConcurrentModificationException e) {
                        Log.e(TAG, "ConcurrentModificationException");
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
                if (isStorageEnabled())
                    setStorage(string);
            } else if (returnEmpty(returnCode)) {
                onResultEmpty(returnMsg);
            } else {
                setErrorCode(returnCode);
                onResultError(returnMsg);

                //未登录
                if (returnCode == -1014) {
                    // TODO: 2017/4/19
                    //删除登陆信息
//                    EventBus.getDefault().post(new LoginUtil.LoginOut());
                }
            }
        } catch (ConcurrentModificationException e) {
            Log.e(TAG, "ConcurrentModificationException: " + e.getMessage());
            onParseError();
        } catch (Exception e) {
            Log.e(TAG, getClass().getSimpleName() + " parse error: " + e.getClass().getSimpleName());
            onParseError();
        }
    }

//    protected void onGetBean(Object baseBean) {
//
//    }

    protected void onResponseSuccess(boolean fromStorage, T bean) {
        mReturnData = bean;
    }

    protected T parseJson(String json) throws Exception {
        Type superClass = getClass().getGenericSuperclass();
        Type type = ((ParameterizedType) superClass).getActualTypeArguments()[0];
        Log.v(TAG, "parse json class:" + type.toString());
        return g.fromJson(json, type);
    }

    protected abstract String getRequestUrl();

    public void setOutOfDate() {
        mIsOutOfDate = true;
    }

    @Override
    public boolean isOutOfDate() {
        return mIsOutOfDate;
    }

    @Override
    protected void readFromParcel(Parcel parcel) {
        super.readFromParcel(parcel);
        mLastSession = parcel.readString();
        mSync = parcel.readInt() > 0;
        mIsOutOfDate = parcel.readInt() > 0;
        mHasRequestFromStorage = parcel.readInt() > 0;
//        Type superClass = getClass().getGenericSuperclass();
//        Type type = ((ParameterizedType) superClass).getActualTypeArguments()[0];
//            parcel.readParcelable();
    }

    @Override
    protected void saveToParcel(Parcel parcel) {
        super.saveToParcel(parcel);
        parcel.writeString(mLastSession);
        parcel.writeInt(mSync ? 1 : 0);
        parcel.writeInt(mIsOutOfDate ? 1 : 0);
        parcel.writeInt(mHasRequestFromStorage ? 1 : 0);
//        if(mReturnData != null && mReturnData instanceof Parcelable)
//        parcel.writeParcelable((Parcelable)mReturnData, 0);
    }

    private static <T extends JavaBean, E extends RequestBase<T>>
    Observable<E> asObservable(E request) {
        if (mRequestObservableFactory == null)
            mRequestObservableFactory = new RequestObservableFactory();

        return mRequestObservableFactory.from(request);
    }

    private static class ResponseHandler extends Handler {
        WeakReference<RequestBase> structBase;

        ResponseHandler(RequestBase structBase) {
            this.structBase = new WeakReference<>(structBase);
        }

        void sendFailureMessage() {
            try {
                sendEmptyMessage(MSG_REQUEST_FAILURE);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        void sendSuccessMessageFromStorage(String data) {
            try {
                sendMessage(obtainMessage(MSG_SUCCESS, 1, 0, data));
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        void sendSuccessMessage(Response response) {
            if (response.isSuccessful()) {
                try {
                    sendMessage(obtainMessage(MSG_SUCCESS, response.body().string()));
                } catch (IOException e) {
                    sendMessage(obtainMessage(MSG_RESULT_FAILURE, response.message()));
                }
            } else {
                try {
                    sendMessage(obtainMessage(MSG_RESULT_FAILURE, response.message()));
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }

        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case MSG_SUCCESS: {
                    if (structBase.get() != null)
                        structBase.get().onParseResponse(msg.arg1 > 0, (String) msg.obj);
                    break;
                }
                case MSG_REQUEST_FAILURE: {
                    if (structBase.get() != null)
                        structBase.get().onRequestFail();
                    break;
                }
                case MSG_RESULT_FAILURE: {
                    if (structBase.get() != null)
                        structBase.get().onResponseError((String) msg.obj);
                    break;
                }
            }
        }
    }

    private static class IntTypeAdapter extends TypeAdapter<Number> {

        @Override
        public void write(JsonWriter out, Number value)
                throws IOException {
            out.value(value);
        }

        @Override
        public Number read(JsonReader in) throws IOException {
            if (in.peek() == JsonToken.NULL) {
                in.nextNull();
                return 0;
            }
            try {
                String result = in.nextString();
                if ("".equals(result)) {
                    return 0;
                }
                return Integer.parseInt(result);
            } catch (NumberFormatException e) {
                throw new JsonSyntaxException(e);
            }
        }
    }

    private static class LongTypeAdapter extends TypeAdapter<Number> {

        @Override
        public void write(JsonWriter out, Number value)
                throws IOException {
            out.value(value);
        }

        @Override
        public Number read(JsonReader in) throws IOException {
            if (in.peek() == JsonToken.NULL) {
                in.nextNull();
                return 0;
            }
            try {
                String result = in.nextString();
                if ("".equals(result)) {
                    return 0;
                }
                return Long.parseLong(result);
            } catch (NumberFormatException e) {
                throw new JsonSyntaxException(e);
            }
        }
    }

    private static class DateTypeAdapter extends TypeAdapter<Date> {

        private final DateFormat localFormat
                = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.CHINA);

        @Override
        public Date read(JsonReader in) throws IOException {
            if (in.peek() == JsonToken.NULL) {
                in.nextNull();
                return null;
            }
            String result = in.nextString();
            if ("".equals(result))
                return null;
            return deserializeToDate(result);
        }

        private synchronized Date deserializeToDate(String json) {
            try {
                return localFormat.parse(json);
            } catch (ParseException ignored) {
            }
            try {
                return ISO8601Utils.parse(json, new ParsePosition(0));
            } catch (ParseException e) {
                throw new JsonSyntaxException(json, e);
            }
        }

        @Override
        public synchronized void write(JsonWriter out, Date value) throws IOException {
            if (value == null) {
                out.nullValue();
                return;
            }
            String dateFormatAsString = localFormat.format(value);
            out.value(dateFormatAsString);
        }

    }
}