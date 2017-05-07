package architecture_o.network.base;

import java.util.List;

/**
 * Created by Herbert on 2015/9/18.
 */
public interface IPageRequest<T> {

    int getCurrentTotalCount();

    int getCountWithLoading();

    boolean isItemLoading(int position);

    List<?> getList(T baseBean);

    Object getItem(int position);

    int currentPage();

    boolean hasNextPage();

    boolean isEnd();

    void requestFirst();

    void clear();
}
