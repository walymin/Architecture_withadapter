package architecture_o.network.base;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.List;

import architecture_o.network.bean.JavaBean;
import architecture_o.network.bean.VideoBean;


/**
 * Created by Herbert on 2016/4/14.
 */
public class RequestFixedData extends ListRequestBase<JavaBean> implements Parcelable {
    @Override
    public List<?> getList(JavaBean baseBean) {
        return null;
    }

    @Override
    protected String getRequestUrl() {
        return null;
    }

    public RequestFixedData fromData(List<VideoBean> list) {
        mList.addAll(list);
        mIsEnd = true;
        setStatus(list != null && list.size() > 0 ? IRequest.Status.Status_Success : IRequest.Status.Status_Empty);
        return this;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        saveToParcel(dest);
        dest.writeList(mList);
    }

    public RequestFixedData() {
    }

    protected RequestFixedData(Parcel in) {
        readFromParcel(in);
        mList = in.readArrayList(VideoBean.class.getClassLoader());
    }

    public static final Creator<RequestFixedData> CREATOR = new Creator<RequestFixedData>() {
        @Override
        public RequestFixedData createFromParcel(Parcel source) {
            return new RequestFixedData(source);
        }

        @Override
        public RequestFixedData[] newArray(int size) {
            return new RequestFixedData[size];
        }
    };
}
