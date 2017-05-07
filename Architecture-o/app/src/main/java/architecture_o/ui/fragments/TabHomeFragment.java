package architecture_o.ui.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import architecture_o.R;
import architecture_o.base.BaseFragment;
import architecture_o.ui.activitys.LisetTestActivity;
import architecture_o.update.UpdateManager;
import butterknife.OnClick;


public class TabHomeFragment extends BaseFragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;


    public TabHomeFragment() {
        // Required empty public constructor
    }


    public static TabHomeFragment newInstance(String param1, String param2) {
        TabHomeFragment fragment = new TabHomeFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_tab_home;
    }


    @OnClick({R.id.list_activity, R.id.list_uitest})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.list_activity:
                startActivity(new Intent(getActivity(), LisetTestActivity.class));
                break;
            case R.id.list_uitest:
                // UpdateManager.checkUpdate(getActivity(), false);
              //  startActivity(new Intent(getActivity(), UiActivity.class));
                UpdateManager.checkUpdate(getActivity(),false);
                break;
        }
    }
}
