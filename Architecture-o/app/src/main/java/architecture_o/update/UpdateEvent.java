package architecture_o.update;


import architecture_o.network.base.RequestStatusBase;

/**
 * Created by Herbert on 2015/5/16.
 */
public class UpdateEvent {

    public enum DownloadState {
        Downloading, Finish, Fail
    }

    public static class UpdateRequestEvent {
        public RequestStatusBase.StructResult result;
        public boolean isForce;

        public UpdateRequestEvent(RequestStatusBase.StructResult result, boolean isForce) {
            this.result = result;
            this.isForce = isForce;
        }
    }

    public static class UpdateInstallLastEvent {
        public String filePath;

        public UpdateInstallLastEvent(String filePath) {
            this.filePath = filePath;
        }
    }

    public static class UpdateConfirmEvent {

        public final boolean confirm;

        public UpdateConfirmEvent(boolean confirm){
            this.confirm = confirm;
        }
    }

    public static class UpdateShowNotifyEvent {
        public boolean showNotify;

        public UpdateShowNotifyEvent(boolean showNotify) {
            this.showNotify = showNotify;
        }
    }

    public static class UpdateCancelEvent {

    }

    public static class UpdateProgressEvent {
        public int progress;
        public DownloadState state;
        public String path;

        public UpdateProgressEvent(String path, int progress) {
            this.path = path;
            this.progress = progress;
            this.state = DownloadState.Downloading;
        }

        public UpdateProgressEvent(String path, DownloadState state) {
            this.path = path;
            this.state = state;
        }
    }
}
