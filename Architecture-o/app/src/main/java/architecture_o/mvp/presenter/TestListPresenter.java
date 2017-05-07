package architecture_o.mvp.presenter;


import architecture_o.listmodel.IData;
import architecture_o.listmodel.IRequestView;
import architecture_o.listmodel.RequestPresenter;
import architecture_o.mvp.contract.TestListContract;

/**
 * Created by ZJW on 2017/3/6.
 */

public class TestListPresenter extends RequestPresenter implements TestListContract.Presenter {

    private TestListContract.TView mView;

    public TestListPresenter(TestListContract.TView view, IRequestView view2, IData data) {
        super(view2, data);
        mView = view;
        mView.setPresenter(this);
    }

    @Override
    public void subscribe() {

    }

    @Override
    public void unsubscribe() {

    }
}
