package architecture_o.network;


public class RequestUrls {
    private static final String mUrlTest = "http://gangqinputest.yusi.tv/?urlparam=";
    private static final String mUrl = "http://gangqinpu.yusi.tv/?urlparam=";
    private static final String mFinalUrl = /*(BuildConfig.TEST_ENV ? mUrlTest : mUrl);*/mUrl;

    public static final String testUrl = mFinalUrl + "pad/performer/getperformerinfo";

    public static final String updateUrl = "http://gangqinpu.yusi.tv/?urlparam=cginfo/userinfo/getnewestversioninfo";
}
