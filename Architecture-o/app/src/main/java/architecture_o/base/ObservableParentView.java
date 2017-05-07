package architecture_o.base;

import android.support.v7.widget.RecyclerView;

/**
 * Created by Herbert on 2015/11/16.
 */
public interface ObservableParentView {
    int getHeaderHeight();

    RecyclerView.OnScrollListener getOnScrollListener();
}
