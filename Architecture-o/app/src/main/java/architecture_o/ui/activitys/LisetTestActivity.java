package architecture_o.ui.activitys;

import android.os.Bundle;
import android.support.annotation.LayoutRes;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import java.util.List;

import architecture_o.R;
import architecture_o.adapter.BaseQuickAdapter;
import architecture_o.adapter.BaseViewHolder;
import architecture_o.base.BaseListActivity;
import architecture_o.databinding.TestListItemBinding;
import architecture_o.listmodel.BaseRequestView;
import architecture_o.listmodel.IPresenter;
import architecture_o.listmodel.RequestData;
import architecture_o.listmodel.RequestPresenter;
import architecture_o.mvp.contract.TestListContract;
import architecture_o.network.impl.RequestComposerDetail;

public class LisetTestActivity extends BaseListActivity implements TestListContract.TView{
    RequestComposerDetail request   ;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setToolbarTitle("TEST LIST");
    }

    @Override
    protected int getLayoutId() {
        return R.layout.activity_liset_test;
    }


    @Override
    protected IPresenter createPresenter() {
        request  = new RequestComposerDetail();
        return new RequestPresenter( new BaseRequestView() {
            @Override
            public BaseQuickAdapter createAdapter() {
                return new NewAdapter(R.layout.ui_list_item,  request.mList);
            }
        }, new RequestData(request){
            @Override
            public void init() {
                super.init();
                request.setId("21");
            }
        });
    }
    class  NewAdapter extends BaseQuickAdapter<Object,BaseViewHolder>{
        public NewAdapter(@LayoutRes int layoutResId, @Nullable List<Object> data) {
            super(layoutResId, data);
        }
        @Override
        protected void convert(BaseViewHolder helper,Object object) {
            RequestComposerDetail.Bean.DataBean.ItemBean item = (RequestComposerDetail.Bean.DataBean.ItemBean) object;
            helper.setText(R.id.conten,item.name);
        }
    }











    @Override
    public void setPresenter(TestListContract.Presenter presenter) {

    }


    class ItemHolder extends RecyclerView.ViewHolder {
        TestListItemBinding binding;

        public ItemHolder(View itemView) {
            super(itemView);
            binding = TestListItemBinding.bind(itemView);
        }
    }

 /*   class MyAdapter extends RecyclerView.Adapter {
        public MyAdapter(){
            setHasStableIds(true);
        }
        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            return new ItemHolder(LayoutInflater.from(LisetTestActivity.this).inflate(R.layout.test_list_item, parent, false));
        }

        @Override
        public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
            ((ItemHolder) holder).binding.setData((RequestComposerDetail.Bean.DataBean.ItemBean) request.getItem(position ));
        }

        @Override
        public int getItemCount() {
            return request.getCurrentTotalCount();
        }
    }*/
}
