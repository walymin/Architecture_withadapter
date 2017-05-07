package architecture_o.listmodel;

import android.content.Context;

/**
 * Created by Herbert on 16/9/28.
 */

public interface IPresenter {
    // 下拉并刷新
    void refresh();

    void create(Context context, Object object, int listResId);

    void init();

    void destroy();

    // 检查数据状态, 并刷新ui
    void update(String error);


//    void doStateUnprepare();
//
//    void doStateNeedFetch();
//
//    void doStateEmpty(String error);
//
//    void doStateError(String error);
//
//    void doStateNew();
//
//    void doStateFetching();

}
