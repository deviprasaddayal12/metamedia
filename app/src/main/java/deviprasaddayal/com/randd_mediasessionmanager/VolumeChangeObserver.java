package deviprasaddayal.com.randd_mediasessionmanager;

import android.content.Context;
import android.database.ContentObserver;
import android.media.AudioManager;
import android.net.Uri;
import android.os.Handler;
import android.util.Log;

import java.util.Locale;

public class VolumeChangeObserver extends ContentObserver {

    public static final String TAG = VolumeChangeObserver.class.getSimpleName();

    private Context context;
    private AudioManager audioManager;
    private int previousVolume;

    private OnMediaMetaReceiveListener onMediaMetaReceiveListener;

    public VolumeChangeObserver(Handler handler, Context context, OnMediaMetaReceiveListener onMediaMetaReceiveListener) {
        super(handler);
        this.context = context;
        audioManager = (AudioManager) context.getSystemService(Context.AUDIO_SERVICE);
        if (this.audioManager != null)
            previousVolume = audioManager.getStreamVolume(AudioManager.STREAM_MUSIC);
        else
            previousVolume = 0;

        this.onMediaMetaReceiveListener = onMediaMetaReceiveListener;

        onMediaMetaReceiveListener.onMetaReceived(String.valueOf(previousVolume));
    }

    @Override
    public void onChange(boolean selfChange, Uri uri) {
        super.onChange(selfChange, uri);

        int currentVolume = -1;
        if (this.audioManager != null)
            currentVolume = audioManager.getStreamVolume(AudioManager.STREAM_MUSIC);
        Log.i(TAG, "onChange: currentVolume = " + currentVolume);

        onMediaMetaReceiveListener.onMetaReceived(String.valueOf(currentVolume));
    }
}
