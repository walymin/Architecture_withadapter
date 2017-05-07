package architecture_o.update;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v7.app.AlertDialog;

import org.greenrobot.eventbus.EventBus;

import java.lang.ref.WeakReference;

import architecture_o.base.BaseActivity;


public class UpdateManager {

    private WeakReference<ProgressDialog> mProgressDialogRef;
    private static boolean mPassiveMode;

    private static UpdateManager instance;

    static UpdateManager getInstance() {
        if (instance == null) {
            instance = new UpdateManager();
        }
        return instance;
    }

    /**
     * 检查更新
     *
     * @param context
     * @param passive 开关  保证打开应用后只调用一次
     */
    public static void checkUpdate(Context context, boolean passive) {
        // 保证被动模式每次打开应用只尝试一次
        // TODO
        if (passive && BaseActivity.topActivityRef != null && BaseActivity.topActivityRef.get() != null)
            return;

        Intent intent = new Intent(context, UpdateService.class);
        intent.putExtra("passive", passive);
        context.startService(intent);
        mPassiveMode = passive;

        if (context instanceof FragmentActivity && !passive)
            getInstance().showProgressDialog(context);
    }

    private void showProgressDialog(Context context) {
        if (context instanceof Activity) {
            if (mProgressDialogRef == null || mProgressDialogRef.get() == null) {
                mProgressDialogRef = new WeakReference<>(new ProgressDialog(context));
                mProgressDialogRef.get().setMessage("正在检查更新...");
            }
            mProgressDialogRef.get().show();
        }
    }

    public static boolean needUpdate(Context context, int version) {
        try {
            if (version > context.getPackageManager().getPackageInfo(
                    context.getPackageName(), 0).versionCode) return true;
        } catch (Exception e) {
            e.printStackTrace();
        }

        return false;
    }

    void dismissProgressDialog() {
        if (mProgressDialogRef != null && mProgressDialogRef.get() != null) {
            mProgressDialogRef.get().dismiss();
            mProgressDialogRef = null;
        }
    }

    boolean showDialogNotice(Bundle bundle) {
        if (BaseActivity.topActivityRef != null && BaseActivity.topActivityRef.get() != null) {
            AlertDialog.Builder builder = new AlertDialog.Builder(BaseActivity.topActivityRef.get()).setTitle("发现新版本：v" + bundle.getString("version_name") + "\n更新内容：");
            String content = bundle.getString("version_desc");
            if(content != null && content.length() > 0)
                    builder.setMessage(content);
            builder.setPositiveButton("立即更新", (dialog, which) ->
                    EventBus.getDefault().post(new UpdateEvent.UpdateConfirmEvent(true))).setNegativeButton("下次再说", (dialog, which) ->
                    EventBus.getDefault().post(new UpdateEvent.UpdateConfirmEvent(false)));
            builder.show();
            return true;
        }
        return false;
    }

    boolean showDialogInstall(final Bundle bundle) {
        if (BaseActivity.topActivityRef != null && BaseActivity.topActivityRef.get() != null) {
            AlertDialog.Builder builder = new AlertDialog.Builder(BaseActivity.topActivityRef.get()).setTitle("新版本已准备好：v" + bundle.getString("version_name") + "\n更新内容：");
            String content = bundle.getString("version_desc");
            if(content != null && content.length() > 0)
                builder.setMessage(content);
            builder.setPositiveButton("立即安装", (dialog, which) ->
                    EventBus.getDefault().post(new UpdateEvent.UpdateInstallLastEvent(bundle.getString("file_path")))).setNegativeButton("下次再说", (dialog, which) ->
            {
                if (bundle.getBoolean("force"))
                    UpdateManager.getInstance().kill();
            });
            builder.show();

            return true;
        }
        return false;
    }

    boolean kill() {
        if (BaseActivity.topActivityRef != null && BaseActivity.topActivityRef.get() != null && mPassiveMode) {
            BaseActivity.topActivityRef.get().finish();
            if (Build.VERSION.SDK_INT >= 16)
                BaseActivity.topActivityRef.get().finishAffinity();
            return true;
        }

        return false;
    }
}
