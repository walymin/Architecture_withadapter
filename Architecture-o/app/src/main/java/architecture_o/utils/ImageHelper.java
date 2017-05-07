package architecture_o.utils;

import android.content.Context;
import android.support.annotation.DrawableRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.net.HttpURLConnection;
import java.net.URL;


public class ImageHelper {
    private static final String TAG = ImageHelper.class.getName();
    public static final double VIDEO_SCALE = 1.7778f;
    private static int mScreenWidth;
    private static int mScreenHeight;
    private static float mDensity;

    public static void downloadImageSync(String imgUrl, String filePath) {
        try {
            // 创建流
            URL url = new URL(imgUrl);
            HttpURLConnection connection = (HttpURLConnection) url
                    .openConnection();
            connection.setConnectTimeout(20000);
            connection.setReadTimeout(20000);
            DataInputStream in = new DataInputStream(
                    connection.getInputStream());

			/* 此处也可用BufferedInputStream与BufferedOutputStream 需要保存的路径 */
            File file = new File(filePath);
            file.getParentFile().mkdirs();
            if (file.exists())
                file.delete();
            file.createNewFile();

            DataOutputStream out = new DataOutputStream(new FileOutputStream(
                    filePath));

			/* 将参数savePath，即将截取的图片的存储在本地地址赋值给out输出流所指定的地址 */
            byte[] buffer = new byte[4096];
            int count = 0;
            while ((count = in.read(buffer)) > 0)/* 将输入流以字节的形式读取并写入buffer中 */ {
                out.write(buffer, 0, count);
            }
            out.close();/* 后面三行为关闭输入输出流以及网络资源的固定格式 */
            in.close();
            connection.disconnect();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    public static Builder with(@NonNull Context context) {
        return new Builder(context);
    }

    public static class Builder {
        Context mContext;
        String url;
        ImageView imageView;
        int phRes;

        public Builder(Context context) {
            mContext = context;
        }

        public Builder url(@Nullable String url) {
            this.url = url;
            return this;
        }

        public Builder placeHolder(@DrawableRes int res) {
            phRes = res;
            return this;
        }

        public void into(@Nullable ImageView imageView) {
            this.imageView = imageView;
            load();
        }

        private void load() {
            Glide.with(mContext)
                    .load(url)
                    .dontAnimate()
                    .diskCacheStrategy(DiskCacheStrategy.ALL)
                    .placeholder(phRes)
                    .into(imageView);
        }
    }

    public static class PauseOnScrollListener extends RecyclerView.OnScrollListener {
        private final boolean pauseOnScroll;
        private final boolean pauseOnFling;
        private final Context mContext;

        public PauseOnScrollListener(Context c) {
            this.pauseOnScroll = false;
            this.pauseOnFling = false;
            mContext = c;
        }

        public PauseOnScrollListener(Context c, boolean pauseOnScroll, boolean pauseOnFling) {
            this.pauseOnFling = pauseOnFling;
            this.pauseOnScroll = pauseOnScroll;
            mContext = c;
        }

        @Override
        public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
            super.onScrollStateChanged(recyclerView, newState);
            switch (newState) {
                case RecyclerView.SCROLL_STATE_IDLE:
                    Glide.with(mContext).resumeRequests();
                    Log.v(TAG, "scroll idle, resume image display");
                    break;
                case RecyclerView.SCROLL_STATE_SETTLING:
                    if (pauseOnScroll) {
                        Glide.with(mContext).pauseRequests();
                        Log.v(TAG, "scroll settling, pause image display");
                    }
                    break;
                case RecyclerView.SCROLL_STATE_DRAGGING:
                    if (pauseOnFling) {
                        Glide.with(mContext).pauseRequests();
                        Log.v(TAG, "scroll dragging, pause image display");
                    }
                    break;
            }
        }
    }

}
