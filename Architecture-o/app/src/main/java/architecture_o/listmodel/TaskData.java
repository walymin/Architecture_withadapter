package architecture_o.listmodel;

import android.os.AsyncTask;

import java.util.List;

/**
 * Created by Herbert on 16/9/28.
 */

public abstract class TaskData<T> extends BaseRequestData {

    List<T> mData;
    DataTask mTask;

    public void reset(){
        mData = null;
        mTask = null;
    }

    @Override
    public boolean isStateUnprepare() {
        return false;
    }

    @Override
    public boolean isStateNeedFetch() {
        return mTask == null;
    }

    @Override
    public boolean isStateEmpty() {
        return mData != null && mData.size() == 0;
//        return mTask != null && mTask.getStatus() == AsyncTask.Status.FINISHED && mData.size() == 0;
    }

    @Override
    public boolean isStateError() {
        return false;
    }

    @Override
    public boolean isStateNew() {
        return mData != null;
//        return mTask != null && mTask.getStatus() == AsyncTask.Status.FINISHED;
    }

    @Override
    public boolean isStateFetching() {
        return mTask != null && mTask.getStatus() == AsyncTask.Status.RUNNING;
    }

    @Override
    public boolean request() {
        if (mTask == null || mTask.getStatus() == AsyncTask.Status.FINISHED) {
            mTask = new DataTask();
            mTask.execute();
            return true;
        }

        return false;
    }

    @Override
    public void init() {
    }

    @Override
    public void uninit() {
        if (mTask != null && mTask.getStatus() == AsyncTask.Status.RUNNING)
            mTask.cancel(true);
    }



    public List<T> getListData() {
        return mData;
    }


    protected abstract List<T> fetchDataInBackdround();

    protected void onDataLoaded(List<T> data) {
        if(onDataListener!=null)
            onDataListener.onDataReady(null);
    }

    class DataTask extends AsyncTask<Void, Integer, List<T>> {

        @Override
        protected List<T> doInBackground(Void... params) {
            return fetchDataInBackdround();
        }

        @Override
        protected void onPostExecute(List<T> result) {
            if (!isCancelled()) {
                mData = result;
                onDataLoaded(mData);
            }
        }
    }
}
