package architecture_o.ui.activitys;

import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.v4.view.ViewCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.LinearLayout;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import architecture_o.R;
import architecture_o.adapter.BaseMultiItemQuickAdapter;
import architecture_o.adapter.BaseViewHolder;
import architecture_o.adapter.entity.MultiItemEntity;
import architecture_o.base.BaseActivity;
import architecture_o.utils.DisplayUtils;
import butterknife.BindView;

public class UiActivity extends BaseActivity {
    @BindView(R.id.recycle_view)
    RecyclerView recycleView;
    @BindView(R.id.collaps_layout)
    CollapsingToolbarLayout collapsLayout;
    @BindView(R.id.appbar)
    AppBarLayout appbar;
    @BindView(R.id.toolbar)
    Toolbar toolbar;
    @BindView(R.id.toolbar_root)
    LinearLayout toolbarRoot;

    private ArrayList<Enty> list = new ArrayList<>();

    @Override
    protected int getLayoutId() {
        return R.layout.activity_ui;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        DisplayUtils.initScreen(this);
        boolean isHigh = Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP;
        if (isHigh) {
            ViewGroup mContentView = (ViewGroup) findViewById(Window.ID_ANDROID_CONTENT);
            View mChildView = mContentView.getChildAt(0);
            if (mChildView != null) {
                ViewCompat.setFitsSystemWindows(mChildView, false);
            }
        }
        ((ViewGroup.MarginLayoutParams) toolbarRoot.getLayoutParams()).setMargins(0, isHigh ? DisplayUtils.statusBarHeight : 0, 0, 0);

        appbar.addOnOffsetChangedListener((appBarLayout, verticalOffset) -> {
            int maxScroll = appBarLayout.getTotalScrollRange();
            float percentage = (float) Math.abs(verticalOffset) / (float) maxScroll;
            int background = (int) (255 * percentage);
            toolbarRoot.getBackground().mutate().setAlpha(background);
        });
        initList();
    }

    private void initList() {
        Random random = new Random();
        for (int i = 0; i < 100; i++) {
            list.add(new Enty("mag :"+i,random.nextInt(2)+112));
        }
        recycleView.setLayoutManager(new LinearLayoutManager(this));
        recycleView.setAdapter(new Adapter(list));
    }

    @Override
    protected boolean originalToolBarEnable() {
        return false;
    }



    static class Enty implements MultiItemEntity {
        public static final int YPPE_A =11;
        public static final int YPPE_B =12;

        public Enty(String msg, int type) {
            this.msg = msg;
            this.type = type;
        }

        public String msg;
        public int type;

        @Override
        public int getItemType() {
            return type;
        }
    }


    class Adapter extends BaseMultiItemQuickAdapter<Enty, BaseViewHolder> {

        public Adapter(List<Enty> data) {
            super(data);
            addItemType(Enty.YPPE_A, R.layout.ui_list_item);
            addItemType(Enty.YPPE_B, R.layout.ui_list_item_b);
        }

        @Override
        protected void convert(BaseViewHolder helper, Enty item) {
            switch (item.getItemType()) {
                case Enty.YPPE_A:
                    helper.setText(R.id.conten,item.msg);
                    break;
                case Enty.YPPE_B:
                    helper.setText(R.id.conten,item.msg);
                    break;
            }
        }
    }

}
