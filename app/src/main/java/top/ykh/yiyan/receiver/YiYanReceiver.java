package top.ykh.yiyan.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import top.ykh.yiyan.service.YiYanService;

public class YiYanReceiver extends BroadcastReceiver {

    private static final String TAG = "YiYanReceiver";

    @Override
    public void onReceive(Context context, Intent intent) {
        context.startService(new Intent(context, YiYanService.class));
    }
}
