package architecture_o.update;

import android.app.Notification;
import android.app.Service;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.IBinder;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationManagerCompat;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Toast;


import org.greenrobot.eventbus.EventBus;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

import architecture_o.R;
import architecture_o.cache.DataStore;
import architecture_o.network.base.RequestStatusBase;
import architecture_o.network.impl.RequestUpdate;
import architecture_o.utils.NetworkUtils;
import architecture_o.utils.StorageUtils;


public class UpdateService extends Service implements RequestStatusBase.OnResultListener {
    private static final String TAG = UpdateService.class.getName();
    private static final String NotifyTag = "NotifyTagUpdate";
    private static final int NotifyId = 1;

    private RequestUpdate mRequestUpdate;
    private boolean mPassiveMode = true;
    private String mFilepath;
    private DownloadApkThread mDownloadThread;

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        Log.v(TAG, "onCreate");
        EventBus.getDefault().register(this);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if (intent != null)
            mPassiveMode = intent.getBooleanExtra("passive", true);
        Log.v(TAG, "onStartCommand, passive=" + mPassiveMode);
        checkUpdate();
        return START_STICKY_COMPATIBILITY;
    }

    @Override
    public void onDestroy() {
        Log.v(TAG, "onDestroy");
        EventBus.getDefault().unregister(this);
        super.onDestroy();
    }

    @Override
    public void onResult(RequestStatusBase struct, RequestStatusBase.StructResult result, String reason) {
        UpdateManager.getInstance().dismissProgressDialog();

        if (result != RequestStatusBase.StructResult.RequestFail) {
            String time = new SimpleDateFormat("yyyyMMddHHmmss", Locale.getDefault()).format(Calendar.getInstance().getTime());
            //Hawk.put(DataStore.Keys.UpdateLastCheckTime.name(), time);
            DataStore.putLastUpdateTime(time);
        }

        if (result == RequestStatusBase.StructResult.Success) {
            if (mRequestUpdate.getReturnData().data != null) {
                int currVersion = getVersionCode();
                String filename = getString(R.string.app_name) + "_"
                        + mRequestUpdate.getReturnData().data.version_name + ".apk";
                File file = StorageUtils.createDownloadApkFile(getApplicationContext(), filename);
                if (file != null) mFilepath = file.getAbsolutePath();

                if (mRequestUpdate.getReturnData().data.version_code > currVersion) {
                    // 显示提示对话框
                    String lastPath = checkLastUpdate();
                    if (lastPath != null) {
                        installLastApk(lastPath);
                    } else if (isForceVersion() && NetworkUtils.isWifiConnected()) {
                        downloadApk();
                    } else if (!showNoticeDialog())
                        downloadApk();
                    return;
                }
            }
            if (!mPassiveMode) {
                Toast.makeText(this, "已经是最新版本", Toast.LENGTH_LONG).show();
            }
        }
    }

    private int getVersionCode() {
        int versionCode = 0;
        try {
            // 获取软件版本号，对应AndroidManifest.xml下android:versionCode
            versionCode = getPackageManager().getPackageInfo(
                    getPackageName(), 0).versionCode;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        return versionCode;
    }

    private String checkLastUpdate() {
        int lastVersion = 0;//= Hawk.get(DataStore.Keys.UpdateLastVersion.name(), 0);
        // String lastPath = Hawk.get(DataStore.Keys.UpdateLastPath.name(), null);
        String lastPath = DataStore.getLastUpdatePath();
        String ver = DataStore.getLastUpdateVesion();
        if (!TextUtils.isEmpty(ver)) {
            lastVersion = Integer.parseInt(ver);
        }
        int newVersion = mRequestUpdate.getReturnData().data.version_code;
        if (newVersion > lastVersion) {
            return null;
        }

        if (lastPath == null)
            return null;

        File file = new File(lastPath);
        if (!file.exists())
            return null;

        if (file.isFile()) {

            return lastPath;
        }
        return null;
    }

    private void checkUpdate() {
        mRequestUpdate = new RequestUpdate();
        mRequestUpdate.addOnResultListener(this);
        mRequestUpdate.request();
    }

    private boolean showNoticeDialog() {
        Bundle bundle = new Bundle();
        bundle.putBoolean("force", isForceVersion());
        bundle.putString("version_name", mRequestUpdate.getReturnData().data.version_name);
        bundle.putString("version_desc", mRequestUpdate.getReturnData().data.version_discription);
        return UpdateManager.getInstance().showDialogNotice(bundle);
    }

    private void downloadApk() {
        // 启动新线程下载软件
        mDownloadThread = new DownloadApkThread();
        mDownloadThread.start();
    }

    private void saveApkInfo(String path) {
        DataStore.putLastUpdatePath(path);
        DataStore.putLastUpdateVesion(mRequestUpdate.getReturnData().data.version_code + "");
        //Hawk.put(DataStore.Keys.UpdateLastVersion.name(), mRequestUpdate.getReturnData().data.version_code);
        // Hawk.put(DataStore.Keys.UpdateLastPath.name(), path);
    }

    private void installLastApk(final String lastPath) {
        Bundle bundle = new Bundle();
        bundle.putBoolean("force", isForceVersion());
        bundle.putString("version_name", mRequestUpdate.getReturnData().data.version_name);
        bundle.putString("version_desc", mRequestUpdate.getReturnData().data.version_discription);
        bundle.putString("file_path", lastPath);
        if (!UpdateManager.getInstance().showDialogInstall(bundle)) {
            installApk(lastPath);
        }
    }

    private void installApk(String filePath) {
        File apkfile = new File(filePath);
        if (!apkfile.exists()) {
            return;
        }
        // 通过Intent安装APK文件
        Intent i = new Intent(Intent.ACTION_VIEW);
        i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        i.setDataAndType(Uri.parse("file://" + apkfile.toString()),
                "application/vnd.android.package-archive");
        startActivity(i);

        if (isForceVersion())
            UpdateManager.getInstance().kill();
    }

    private void showNotify(int progress) {
        Notification noti = new NotificationCompat.Builder(this)
                .setContentTitle("正在下载更新包" + String.valueOf(progress) + "%")
                .setAutoCancel(false)
                .setSmallIcon(android.R.drawable.stat_sys_download)
                .setLargeIcon(BitmapFactory.decodeResource(getResources(), R.mipmap.ic_launcher))
                .setCategory(Notification.CATEGORY_PROGRESS)
                .setProgress(100, progress, false)
                .setOngoing(true)
                .build();
        NotificationManagerCompat nm = NotificationManagerCompat.from(getApplicationContext());
        nm.notify(NotifyTag, NotifyId, noti);
    }

    private void showFailure() {
        NotificationManagerCompat nm = NotificationManagerCompat.from(getApplicationContext());
        nm.cancel(NotifyTag, NotifyId);
    }

    private void clearNotify() {
        try {
            NotificationManagerCompat nm = NotificationManagerCompat.from(getApplicationContext());
            nm.cancel(NotifyTag, NotifyId);
        } catch (Exception e) {
            // 处理没有提示的时候的nullPointerException
            e.printStackTrace();
        }
    }

    private boolean isForceVersion() {
        try {
            return mRequestUpdate.getReturnData().data.version_code_force >= getVersionCode();
        } catch (Exception e) {
            return false;
        }
    }

    public void onEventMainThread(UpdateEvent.UpdateInstallLastEvent event) {
        installApk(event.filePath);
    }

    public void onEventMainThread(UpdateEvent.UpdateConfirmEvent event) {
        if (event.confirm) {
            if (isForceVersion())
                installApk(mFilepath);
            else {
                downloadApk();
                Toast.makeText(this, "开始下载", Toast.LENGTH_SHORT).show();
            }
        } else {
            if (isForceVersion())
                UpdateManager.getInstance().kill();
        }
    }

    public void onEventMainThread(UpdateEvent.UpdateProgressEvent event) {
        if (event.state == UpdateEvent.DownloadState.Downloading) {
//            if (showNotify)
            showNotify(event.progress);
        } else if (event.state == UpdateEvent.DownloadState.Finish) {
            clearNotify();
            saveApkInfo(event.path);
            if (isForceVersion())
                showNoticeDialog();
            else
                installApk(event.path);

        } else if (event.state == UpdateEvent.DownloadState.Fail) {
            showFailure();
        }
    }

    public void onEventMainThread(UpdateEvent.UpdateCancelEvent event) {
        if (mDownloadThread != null) mDownloadThread.cancelUpdate = true;
    }

    private class DownloadApkThread extends Thread {
        private float progress;
        private boolean cancelUpdate;

        @Override
        public void run() {
            HttpURLConnection conn = null;
            File apkFile = null;
            try {
                URL url = new URL(mRequestUpdate.getReturnData().data.version_url);
                // 创建连接
                conn = (HttpURLConnection) url.openConnection();
                conn.setInstanceFollowRedirects(true);
                conn.setConnectTimeout(20000);
                conn.setReadTimeout(20000);
                conn.connect();

                // 获取文件大小
                int length = conn.getContentLength();
                // 创建输入流
                InputStream is = conn.getInputStream();

                apkFile = new File(mFilepath);

                FileOutputStream fos = new FileOutputStream(apkFile);
                int count = 0;
                // 缓存
                byte buf[] = new byte[1024];
                EventBus.getDefault().post(new UpdateEvent.UpdateProgressEvent(apkFile.getAbsolutePath(), (int) progress));
                do {
                    int numread = is.read(buf);
                    count += numread;
                    // 计算进度条位置
                    if (((float) count / length) * 100 - progress > 1) {
                        progress = ((float) count / length) * 100;
                        // 更新进度
                        Log.v(TAG, "progress:" + progress);
                        EventBus.getDefault().post(new UpdateEvent.UpdateProgressEvent(apkFile.getAbsolutePath(), (int) progress));
                    }
                    if (numread <= 0) {
                        break;
                    } else if (numread > 0)
                        fos.write(buf, 0, numread);
                } while (!cancelUpdate);// 点击取消就停止下载.

//                if (showNotify)
                clearNotify();
                fos.close();
                is.close();

                if (!cancelUpdate) {
                    // 下载完成
                    EventBus.getDefault().post(new UpdateEvent.UpdateProgressEvent(apkFile.getAbsolutePath(), 100));
                    EventBus.getDefault().post(new UpdateEvent.UpdateProgressEvent(apkFile.getAbsolutePath(), UpdateEvent.DownloadState.Finish));
                }

            } catch (IOException e) {
                if (apkFile != null)
                    EventBus.getDefault().post(new UpdateEvent.UpdateProgressEvent(apkFile.getAbsolutePath(), UpdateEvent.DownloadState.Fail));
                e.printStackTrace();
            } finally {
                if (conn != null)
                    conn.disconnect();
            }
        }
    }
}
