package architecture_o.utils;

/**
 * Created by Herbert on 2015/3/27.
 */

import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Environment;
import android.util.Log;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

import static android.os.Environment.MEDIA_MOUNTED;

/**
 * Provides application storage paths
 *
 * @author Sergey Tarasevich (nostra13[at]gmail[dot]com)
 * @since 1.0.0
 */
public final class StorageUtils {
    private static final String TAG = StorageUtils.class.getName();

    private static final String EXTERNAL_STORAGE_PERMISSION = "android.permission.WRITE_EXTERNAL_STORAGE";

    /**
     * Returns application cache directory. Cache directory will be created on SD card
     * <i>("/Android/data/[app_package_name]/cache")</i> if card is mounted and app has appropriate permission. Else -
     * Android defines cache directory on device's file system.
     *
     * @param context Application context
     * @return Cache {@link File directory}.<br />
     * <b>NOTE:</b> Can be null in some unpredictable cases (if SD card is unmounted and
     * {@link Context#getCacheDir() Context.getCacheDir()} returns null).
     */
    public static File getFileDirectory(Context context, String folder) {
        return getFileDirectory(context, folder, true);
    }

    public static File getCacheDirectory(Context context, String folder) {
        return getCacheDirectory(context, folder, true);
    }

    /**
     * Returns application cache directory. Cache directory will be created on SD card
     * <i>("/Android/data/[app_package_name]/cache")</i> (if card is mounted and app has appropriate permission) or
     * on device's file system depending incoming parameters.
     *
     * @param context        Application context
     * @param preferExternal Whether prefer external location for cache
     * @return Cache {@link File directory}.<br />
     * <b>NOTE:</b> Can be null in some unpredictable cases (if SD card is unmounted and
     * {@link Context#getCacheDir() Context.getCacheDir()} returns null).
     */
    public static File getFileDirectory(Context context, String folder, boolean preferExternal) {
        File appCacheDir = null;
        String externalStorageState;
        try {
            externalStorageState = Environment.getExternalStorageState();
        } catch (NullPointerException e) { // (sh)it happens (Issue #660)
            externalStorageState = "";
        }
        if (preferExternal && MEDIA_MOUNTED.equals(externalStorageState) && hasExternalStoragePermission(context)) {
            appCacheDir = getExternalFileDir(context, folder);
        }
//        if (appCacheDir == null) {
//            appCacheDir = context.getCacheDir();
//        }
        if (appCacheDir == null) {
            String cacheDirPath = context.getFilesDir().getPath() + "/" + folder;
            appCacheDir = new File(cacheDirPath);
        }

        appCacheDir.mkdirs();

        return appCacheDir;
    }

    /**
     * Returns specified application cache directory. Cache directory will be created on SD card by defined path if card
     * is mounted and app has appropriate permission. Else - Android defines cache directory on device's file system.
     *
     * @param context Application context
     * @param folder  Cache directory path (e.g.: "AppCacheDir", "AppDir/cache/images")
     * @return Cache {@link File directory}
     */
    public static File getCacheDirectory(Context context, String folder, boolean preferExternal) {
        File appCacheDir = null;
        String externalStorageState;
        try {
            externalStorageState = Environment.getExternalStorageState();
        } catch (NullPointerException e) { // (sh)it happens (Issue #660)
            externalStorageState = "";
        }
        if (preferExternal && MEDIA_MOUNTED.equals(externalStorageState) && hasExternalStoragePermission(context)) {
            appCacheDir = getExternalCacheDir(context, folder);
        }

        if (appCacheDir == null) {
            String cacheDirPath = context.getCacheDir().getPath() + "/" + folder;
            appCacheDir = new File(cacheDirPath);
        }

        appCacheDir.mkdirs();

        return appCacheDir;
    }

    public static File getSDPath(Context context, String folder) {
        String externalStorageState;
        try {
            externalStorageState = Environment.getExternalStorageState();
            if (MEDIA_MOUNTED.equals(externalStorageState) && hasExternalStoragePermission(context)) {
                File root = Environment.getExternalStorageDirectory();
                File dir = new File(root, folder);
                dir.mkdirs();
                return dir;
            }
        } catch (NullPointerException e) {
            e.printStackTrace();
        }
        return null;
    }

    private static File getExternalFileDir(Context context, String folder) {
        File dir = context.getExternalFilesDir(null);
        if (dir != null) {
            String path = dir.getAbsolutePath() + "/" + folder;
            dir = new File(path);
            if (!dir.exists())
                if (!dir.mkdirs()) {
                    Log.w(TAG, "Unable to create external file directory");
                    return null;
                }
            return dir;
        }
        return null;
    }

    private static File getExternalCacheDir(Context context, String folder) {
        File dir = context.getExternalCacheDir();
        if (dir != null) {
            String path = dir.getAbsolutePath() + "/" + folder;
            dir = new File(path);
            if (!dir.exists())
                if (!dir.mkdirs()) {
                    Log.w(TAG, "Unable to create external cache directory");
                    return null;
                }
            return dir;
        }
        return null;
    }

    private static boolean hasExternalStoragePermission(Context context) {
        int perm = context.checkCallingOrSelfPermission(EXTERNAL_STORAGE_PERMISSION);
        return perm == PackageManager.PERMISSION_GRANTED;
    }

    public static File createDownloadApkFile(Context context, String name) {
        File dir = getSDPath(context, "Download");
        if (dir != null) {
            return new File(dir, name);
        }

        try {
            FileOutputStream fos = context.openFileOutput(name, Context.MODE_WORLD_READABLE | Context.MODE_WORLD_WRITEABLE);
            fos.close();
            return new File(context.getFilesDir(), name);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }
}
