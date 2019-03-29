package deviprasaddayal.com.randd_mediasessionmanager;

import android.content.Intent;
import android.content.IntentFilter;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;

public class MainActivity extends AppCompatActivity {

    public static final String TAG = MainActivity.class.getSimpleName();

    private Intent mediaListenerService;
    private MediaMetaReceiver mediaMetaReceiver;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mediaListenerService = new Intent(this, MedialListenerService.class);
        startService(mediaListenerService);

        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction("com.android.music.playstatechanged");
        intentFilter.addAction("com.android.music.musicservicecommand");
        intentFilter.addAction("com.android.music.metachanged");
        intentFilter.addAction("com.android.music.updateprogress");

        mediaMetaReceiver = new MediaMetaReceiver();
        registerReceiver(mediaMetaReceiver, intentFilter);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        unregisterReceiver(mediaMetaReceiver);
        stopService(mediaListenerService);
    }
}
