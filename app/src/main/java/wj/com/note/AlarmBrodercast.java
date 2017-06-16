package wj.com.note;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

/**
 * Created by wj on 2017/5/31.
 */

public class AlarmBrodercast extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        Intent i = new Intent(context,alarm.class);
        context.startService(i);
    }
}
