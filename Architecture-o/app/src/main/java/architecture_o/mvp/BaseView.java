package architecture_o.mvp;

/**
 * Created by ZJW on 2017/2/10.
 */

public interface BaseView<T> {
    void setPresenter(T presenter);
}
