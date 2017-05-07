package architecture_o.app;

import android.app.Application;

import architecture_o.cache.DataStore;
import architecture_o.utils.LogUtil;


/**
 * Created by ZJW on 2017/4/19.
 */

public class AppApplication extends Application {
    private static AppApplication mApplication;

    @Override
    public void onCreate() {
        super.onCreate();
        mApplication = this;
        DataStore.init();
        LogUtil.init();
    }

    public static AppApplication getApplication() {
        return mApplication;
    }
}
