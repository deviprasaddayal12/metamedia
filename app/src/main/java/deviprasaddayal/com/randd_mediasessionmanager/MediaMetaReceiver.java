package deviprasaddayal.com.randd_mediasessionmanager;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.media.session.MediaController;
import android.net.Uri;
import android.provider.MediaStore;
import android.util.Log;

import java.util.Locale;

public class MediaMetaReceiver extends BroadcastReceiver {
    public static final String TAG = MediaMetaReceiver.class.getSimpleName();

    private OnMediaMetaReceiveListener onMediaMetaReceiveListener;

    public MediaMetaReceiver(OnMediaMetaReceiveListener onMediaMetaReceiveListener) {
        this.onMediaMetaReceiveListener = onMediaMetaReceiveListener;
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        String path = "Path not found.";
        String track = intent.getStringExtra("track");
        Log.i(TAG, "onReceive: " + intent.getAction() + ", track = " + track);

        Uri audioUri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
        String[] projection = {"*"};
        String selection = MediaStore.Audio.Media.TITLE + " == \"" + track + "\"";

        try {
            Cursor cursor = context.getContentResolver().query(audioUri, projection, selection, null, null);
            if (cursor != null && cursor.getCount() > 0) {
                cursor.moveToFirst();

                if (cursor.getColumnIndex(MediaStore.Audio.Media.TITLE) != -1) {
                    path = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.DATA));
                }

                cursor.close();
            }
        } catch (Exception e){
            e.printStackTrace();
        }
        onMediaMetaReceiveListener.onMetaReceived(String.format(Locale.getDefault(),"Current track = %s", path));
    }

    /*
        currentContainerName
        currentContainerTypeValue
        currentContainerId
        currentContainerExtData
        currentContainerExtId

        duration
        artist
        domain
        songId
        playing
        album
        track
        position

        trackMetajamId
        ListPosition
        playerState
        */
}
