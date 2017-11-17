package top.ykh.yiyan.thread;

import android.content.Context;

import top.ykh.yiyan.bean.SettingsBean;
import top.ykh.yiyan.database.SQLiteHelper;

/**
 * Created by 海智 on 2017/7/9.
 */

public class YiYanRequest extends BaseRequest {

    public YiYanRequest(Context context) {
        super(context);
        SettingsBean bean = new SQLiteHelper(context).getBean(context);
        String type= "abcdefg ".charAt(bean.getRequestType()) + "";
        url = "https://sslapi.hitokoto.cn?c="+type+"&encode=json";
    }

}
