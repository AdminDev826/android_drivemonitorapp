package util;

/**
 * Created by farha on 10/1/2016.
 */
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import assing_task.RSSPullService;
import assing_task.SyncSmartDefenceData;

public class BootCompleteReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {

        Intent service = new Intent(context, RSSPullService.class);
        context.startService(service);
        context.startService(new Intent(context, SyncSmartDefenceData.class));
    }

}