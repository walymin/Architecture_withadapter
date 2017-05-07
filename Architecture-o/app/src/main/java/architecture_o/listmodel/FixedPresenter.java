package architecture_o.listmodel;

import android.content.Context;

/**
 * Created by Herbert on 16/9/28.
 */

public class FixedPresenter extends BasePresenter {
    public FixedPresenter(IRequestView view, IData data) {
        super(view, data);
    }

    @Override
    public void create(Context context, Object object, int listResId) {
        super.create(context, object, listResId);
        view.enableRefresh(false);
    }
}
