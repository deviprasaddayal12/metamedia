package deviprasaddayal.com.randd_mediasessionmanager;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

public class MediaMetaReceiver extends BroadcastReceiver {
    public static final String TAG = MediaMetaReceiver.class.getSimpleName();

    @Override
    public void onReceive(Context context, Intent intent) {
        Log.i(TAG, "onReceive: " + intent.getAction());
    }
}
