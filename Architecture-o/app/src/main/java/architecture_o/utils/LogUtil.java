package architecture_o.utils;

import android.util.AndroidException;
import android.util.Log;

import com.orhanobut.logger.Logger;

import java.util.Collection;

import architecture_o.BuildConfig;

/**
 * Created by ZJW on 2017/4/20.
 */

public class LogUtil {
    private static boolean onLog = false;

    public static void init() {
        onLog = BuildConfig.DEBUG;
        Logger.init("MY-TAG");
    }

    public static void d(String tag, String msg) {
        if (onLog)
            Logger.log(Log.DEBUG, tag, msg, new AndroidException());
    }

    public static void v(String tag, String msg) {
        if (onLog)
            Logger.log(Log.VERBOSE, tag, msg, new AndroidException());
    }

    public static void e(String tag, String msg) {
        if (onLog)
            Logger.log(Log.ERROR, tag, msg, new AndroidException());
    }

    public static void w(String tag, String msg) {
        if (onLog)
            Logger.log(Log.WARN, tag, msg, new AndroidException());
    }

    public static void i(String tag, String msg) {
        if (onLog)
            Logger.log(Log.INFO, tag, msg, new AndroidException());
    }


    public static void d(String msg) {
        if (onLog)
            Logger.d(msg);
    }

    public static void v(String msg) {
        if (onLog)
            Logger.v(msg);
    }

    public static void e(String msg) {
        if (onLog)
            Logger.e(msg);
    }

    public static void w(String msg) {
        if (onLog)
            Logger.w(msg);
    }

    public static void i(String msg) {
        if (onLog)
            Logger.i(msg);
    }

    public static void json(String json) {
        if (onLog)
            Logger.json(json);
    }

    //打集合
    public static void lc(Collection c) {
        if (onLog)
            Logger.d(c);
    }

}
