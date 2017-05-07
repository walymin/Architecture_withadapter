package architecture_o.network.impl;


import java.util.List;

import architecture_o.network.RequestUrls;
import architecture_o.network.base.PageRequestNoTotal;
import architecture_o.network.base.RequestParams;
import architecture_o.network.bean.BaseBean;


/**
 * Created by Herbert on 16/12/7.
 */

public class RequestComposerDetail extends PageRequestNoTotal<RequestComposerDetail.Bean> {
    private String id;

    public void setId(String id) {
        this.id = id;
    }

    @Override
    protected RequestParams getParams() {
        RequestParams params = super.getParams();
        params.put("id", id);
        return params;
    }

    @Override
    protected void onResponseSuccess(boolean fromStorage, Bean bean) {
        super.onResponseSuccess(fromStorage, bean);

    }

    @Override
    protected String getRequestUrl() {
        return RequestUrls.testUrl;
    }

    @Override
    public List<?> getList(Bean baseBean) {
        return baseBean.list.book_list;
    }

  /*  @Override
    public List<?> getList(Bean baseBean) {
        return baseBean.list.book_list;
    }*/


    public static class Bean extends BaseBean {
        public DataBean list;

        public static class DataBean {
            public String id;
            public String name;
            public String english_name;
            public String first_spell;
            public String nationality;
            public String birthdate;
            public String deathdate;
            public String image_cover;
            public String describe;
            public String simple_description;
            public int all_work_num;
            public int all_support_num;
            public List<ItemBean> book_list;

            public static class ItemBean {
                public String id;
                public String name;
                public int book_num;
                public String hit_num;
                public String support_num;
                public String book_id;
            }
        }
    }
}
