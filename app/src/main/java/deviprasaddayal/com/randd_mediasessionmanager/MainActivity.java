package deviprasaddayal.com.randd_mediasessionmanager;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity {

    public static final String TAG = MainActivity.class.getSimpleName();

    public static final int READ_STORAGE_PERMISSION = 123;
    public static final String[] permissionReadStorage =  new String[]{Manifest.permission.READ_EXTERNAL_STORAGE};
    private boolean isPermissionGranted = false;

    private Handler handler;

    private Intent mediaListenerService;
    private MediaMetaReceiver mediaMetaReceiver;
    private VolumeChangeObserver volumeChangeObserver;

    private TextView tvTrack, tvVolume, tvLog;

    private String currentVolume = "";
    private String currentFilePath = "No file found.";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        requestPermission();

        tvTrack = findViewById(R.id.tv_track);
        tvVolume = findViewById(R.id.tv_volume);
        tvLog = findViewById(R.id.tv_log);

        mediaListenerService = new Intent(this, MedialListenerService.class);
        startService(mediaListenerService);

        IntentFilter intentFilter = new IntentFilter();
        addIntentFilters(intentFilter);

        mediaMetaReceiver = new MediaMetaReceiver(new OnMediaMetaReceiveListener() {
            @Override
            public void onMetaReceived(String value) {
                currentFilePath = value;
                updateField();
            }
        });
        registerReceiver(mediaMetaReceiver, intentFilter);

        handler = new Handler();
        volumeChangeObserver = new VolumeChangeObserver(handler, this, new OnMediaMetaReceiveListener() {
            @Override
            public void onMetaReceived(String value) {
                currentVolume = value;
                updateField();
            }
        });
        getContentResolver().registerContentObserver(android.provider.Settings.System.CONTENT_URI,
                true, volumeChangeObserver);
    }

    private void updateField(){
        String printText = "Current File Path: " + currentFilePath + ", Current Volume: " + currentVolume;

        if (!isPermissionGranted)
            printText = getString(R.string.grant_permission_prompt);

        tvLog.setText(printText);
        Log.i(TAG, printText);
    }

    private void clearFields(){
        tvVolume.setText("");
        tvTrack.setText("");
    }

    private void addIntentFilters(IntentFilter intentFilter){
        intentFilter.addAction("com.android.music.playstatechanged");
        intentFilter.addAction("com.android.music.musicservicecommand");
        intentFilter.addAction("com.android.music.metachanged");
        intentFilter.addAction("com.android.music.updateprogress");

        intentFilter.addAction("com.htc.music.metachanged");
        intentFilter.addAction("fm.last.android.metachanged");
        intentFilter.addAction("com.sec.android.app.music.metachanged");
        intentFilter.addAction("com.nullsoft.winamp.metachanged");
        intentFilter.addAction("com.amazon.mp3.metachanged");
        intentFilter.addAction("com.miui.player.metachanged");
        intentFilter.addAction("com.real.IMP.metachanged");
        intentFilter.addAction("com.sonyericsson.music.metachanged");
        intentFilter.addAction("com.rdio.android.metachanged");
        intentFilter.addAction("com.samsung.sec.android.MusicPlayer.metachanged");
        intentFilter.addAction("com.andrew.apollo.metachanged");
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        getContentResolver().unregisterContentObserver(volumeChangeObserver);
        unregisterReceiver(mediaMetaReceiver);
        stopService(mediaListenerService);
    }

    private void requestPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {

            isPermissionGranted = ContextCompat.checkSelfPermission(this,
                    permissionReadStorage[0]) == PackageManager.PERMISSION_GRANTED;

            if (!isPermissionGranted){
                if (ActivityCompat.shouldShowRequestPermissionRationale(this, permissionReadStorage[0])) {
                    new AlertDialog.Builder(this)
                            .setTitle("Gallery Permission Needed")
                            .setMessage("Easytrack needs to access your gallery.")
                            .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                    //Prompt the user once explanation has been shown
                                    ActivityCompat.requestPermissions(MainActivity.this, permissionReadStorage, READ_STORAGE_PERMISSION);
                                }
                            }).create().show();
                } else {
                    ActivityCompat.requestPermissions(this, permissionReadStorage, READ_STORAGE_PERMISSION);
                }
            }
        } else {
            isPermissionGranted = true;
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == READ_STORAGE_PERMISSION){
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED)
                isPermissionGranted = true;
        }
    }
}
