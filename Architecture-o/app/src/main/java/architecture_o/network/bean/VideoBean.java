package architecture_o.network.bean;

import android.os.Parcel;
import android.os.Parcelable;

public class VideoBean implements Parcelable {
    public String video_name;
    public String picture;
    public long id;
    public int type = TYPE_VIDEO;
    public int type_ext;    // 1.专题；2.专辑；3.榜单
    public String type1;
    public String name;
    public String tvname;

    public static final int TYPE_VIDEO = 1;
    public static final int TYPE_ALBUM = 1024;
    public static final String TYPE_SEARCH_VIDEO = "video";
    public static final String TYPE_SEARCH_AUTHOR = "author";

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.video_name);
        dest.writeString(this.picture);
        dest.writeLong(this.id);
        dest.writeInt(this.type);
        dest.writeInt(this.type_ext);
        dest.writeString(this.name);
        dest.writeString(this.type1);
        dest.writeString(this.tvname);
    }

    public VideoBean() {
    }

    public VideoBean(long id, int type, int type_ext, String name, String picture) {
        this.id = id;
        this.type = type;
        this.type_ext = type_ext;
        this.name = this.video_name = this.tvname = name;
        this.picture = picture;
    }

    protected VideoBean(Parcel in) {
        this.video_name = in.readString();
        this.picture = in.readString();
        this.id = in.readLong();
        this.type = in.readInt();
        this.type_ext = in.readInt();
        this.name = in.readString();
        this.type1 = in.readString();
        this.tvname = in.readString();
    }

    public static final Creator<VideoBean> CREATOR = new Creator<VideoBean>() {
        @Override
        public VideoBean createFromParcel(Parcel source) {
            return new VideoBean(source);
        }

        @Override
        public VideoBean[] newArray(int size) {
            return new VideoBean[size];
        }
    };
}