package architecture_o.mvp.contract;


import architecture_o.mvp.BasePresenter;
import architecture_o.mvp.BaseView;

/**
 * Created by ZJW on 2017/3/6.
 */

public class TestListContract {

    public interface Presenter extends BasePresenter {


    }
    public interface TView extends BaseView<Presenter> {

    }
}
