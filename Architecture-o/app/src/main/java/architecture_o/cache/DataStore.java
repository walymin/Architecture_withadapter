package architecture_o.cache;

import architecture_o.app.AppApplication;

import static architecture_o.cache.DataStore.Keys.LAST_UPDATE_PATH;
import static architecture_o.cache.DataStore.Keys.LAST_UPDATE_TIME;
import static architecture_o.cache.DataStore.Keys.LAST_UPDATE_VERSION;


/**
 * Created by ZJW on 2017/4/19.
 */

public class DataStore {
    public static ACache mACache;

    public static void init() {
        mACache = ACache.get(AppApplication.getApplication());
    }

    public static class Keys {
        public static final String SESSION_ID = "Sessionid";
        public static final String SESSION_ID_INIT = "SessionidInit";
        public static final String VERSION = "Version";
        public static final String LAST_UPDATE_VERSION = "up_ver";
        public static final String LAST_UPDATE_PATH = "up_path";
        public static final String LAST_UPDATE_TIME = "last_up_time";
    }

    public static String getSessionId() {
        return mACache.getAsString(Keys.SESSION_ID);
    }

    public static String getInitSessionId() {
        return mACache.getAsString(Keys.SESSION_ID_INIT);
    }

    public static String getVersion() {
        return mACache.getAsString(Keys.VERSION);
    }

    public static String getRequesCache(String key) {
        return mACache.getAsString(key);
    }

    public static void putRequestCache(String key, String data) {
        mACache.put(key, data);
    }

    public static void putLastUpdateVesion(String data) {
        mACache.put(LAST_UPDATE_VERSION, data);
    }

    public static void putLastUpdatePath(String path) {
        mACache.put(LAST_UPDATE_PATH, path);
    }

    public static String getLastUpdateVesion() {
        return mACache.getAsString(LAST_UPDATE_VERSION);
    }

    public static String getLastUpdatePath() {
        return mACache.getAsString(LAST_UPDATE_PATH);
    }

    public static void putLastUpdateTime(String time) {
        mACache.put(LAST_UPDATE_TIME, time);
    }
}
