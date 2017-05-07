package architecture_o.network.impl;



import architecture_o.network.RequestUrls;
import architecture_o.network.base.RequestBase;
import architecture_o.network.base.RequestParams;
import architecture_o.network.bean.BaseBean;


/**
 * Created by Herbert on 16/12/7.
 */

public class RequestUpdate extends RequestBase<RequestUpdate.Bean> {

    @Override
    protected RequestParams getParams() {
        RequestParams params = super.getParams();
        params.put("app_type", 2);
        params.put("channel","default");
        params.put("platform", "android");
        return params;
    }

    @Override
    protected String getRequestUrl() {
        return RequestUrls.updateUrl;
    }


    public static class Bean extends BaseBean {
        public DataBean data;

        public static class DataBean {
            public String version_name;
            public int version_code;
            public String ver_channel;
            public String version_url;
            public String version_discription;
            public int version_code_force;
            public String is_force_update;


        }
    }
}
