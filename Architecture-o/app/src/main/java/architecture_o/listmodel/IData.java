package architecture_o.listmodel;

/**
 * Created by Herbert on 16/9/28.
 */

public interface IData {
    interface OnDataListener {
        void onDataReady(String reaseon);
    }

    enum Status {
        StateUnprepare, // 尚未准备数据
        StateNeedFetch, // 数据需要更新
        StateEmpty,     // 数据内容为空
        StateError,     // 数据返回错误
        StateNew,       // 数据已更新
        StateFetching,  // 数据正在获取
        StateUnkown,    // 未知状态
    }

    // 获取数据状态
    Status getState();

    boolean isStateUnprepare();

    boolean isStateNeedFetch();

    boolean isStateEmpty();

    boolean isStateError();

    boolean isStateNew();

    boolean isStateFetching();

    // 异步获取数据
    boolean request();

    void init();

    void uninit();

    boolean needShowLoading();

    boolean requestNext();

    void setDataListener(OnDataListener onDataListener);

}
