package deviprasaddayal.com.randd_mediasessionmanager;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.media.MediaMetadata;
import android.media.session.MediaController;
import android.media.session.MediaSession;
import android.media.session.MediaSessionManager;
import android.media.session.PlaybackState;
import android.os.Bundle;
import android.os.IBinder;
import android.service.notification.NotificationListenerService;
import android.service.notification.StatusBarNotification;
import android.util.Log;

import java.util.List;

public class MedialListenerService extends NotificationListenerService {
    public static final String TAG = MedialListenerService.class.getSimpleName();

    private MediaSessionManager mediaSessionManager;
    private MediaController mediaController;
    private MediaMetadata mediaMetadata;
    private ComponentName componentName;

    @Override
    public void onNotificationPosted(StatusBarNotification statusBarNotification) {

    }

    @Override
    public void onNotificationRemoved(StatusBarNotification statusBarNotification) {

    }

    @Override
    public void onCreate() {
        mediaSessionManager = (MediaSessionManager) getSystemService(Context.MEDIA_SESSION_SERVICE);
        try {
            componentName = new ComponentName(this, MedialListenerService.class);

            mediaSessionManager.addOnActiveSessionsChangedListener(sessionListener, componentName);
            List<MediaController> controllers = mediaSessionManager.getActiveSessions(componentName);
            mediaController = pickController(controllers);

            if (mediaController != null) {
                mediaController.registerCallback(callbackMediaController);
                mediaMetadata = mediaController.getMetadata();
                showMetadata();
            }
        } catch (SecurityException e) {
            e.printStackTrace();
        }
    }

    @Override
    public int onStartCommand(Intent i, int startId, int i2) {
        if (mediaController == null) {
            try {
                List<MediaController> controllers = mediaSessionManager.getActiveSessions(componentName);
                mediaController = pickController(controllers);
                if (mediaController != null) {
                    mediaController.registerCallback(callbackMediaController);
                    mediaMetadata = mediaController.getMetadata();
                    showMetadata();
                }
            } catch (SecurityException e) {

            }
        }
        return START_STICKY;
    }

    MediaController.Callback callbackMediaController = new MediaController.Callback() {
        @Override
        public void onSessionDestroyed() {
            super.onSessionDestroyed();
            mediaController = null;
            mediaMetadata = null;
        }

        @Override
        public void onSessionEvent(String event, Bundle extras) {
            super.onSessionEvent(event, extras);
            showMetadata();
        }

        @Override
        public void onPlaybackStateChanged(PlaybackState state) {
            super.onPlaybackStateChanged(state);
            showMetadata();
        }

        @Override
        public void onMetadataChanged(MediaMetadata metadata) {
            super.onMetadataChanged(metadata);
            mediaMetadata = metadata;
            showMetadata();
        }

        @Override
        public void onQueueChanged(List<MediaSession.QueueItem> queue) {
            super.onQueueChanged(queue);
        }

        @Override
        public void onQueueTitleChanged(CharSequence title) {
            super.onQueueTitleChanged(title);
        }

        @Override
        public void onExtrasChanged(Bundle extras) {
            super.onExtrasChanged(extras);
        }

        @Override
        public void onAudioInfoChanged(MediaController.PlaybackInfo info) {
            super.onAudioInfoChanged(info);
        }
    };

    @Override
    public void onDestroy() {
        mediaController = null;
        mediaSessionManager.removeOnActiveSessionsChangedListener(sessionListener);
    }

    private boolean currentlyPlaying = false;
    private String currentArtist, currentSong, currentAlbum;

    public void showMetadata() {
        if (mediaController != null && mediaController.getPlaybackState() != null) {
            currentlyPlaying = mediaController.getPlaybackState().getState() == PlaybackState.STATE_PLAYING;
        }

        if (mediaMetadata == null)
            return;

        currentSong = mediaMetadata.getString(MediaMetadata.METADATA_KEY_TITLE);
        if (currentSong == null) {
            currentSong = mediaMetadata.getString(MediaMetadata.METADATA_KEY_DISPLAY_TITLE);
        }

        currentAlbum = mediaMetadata.getString(MediaMetadata.METADATA_KEY_ALBUM);
        if (currentArtist == null) {
            currentArtist = mediaMetadata.getString(MediaMetadata.METADATA_KEY_ALBUM_ARTIST);
        }

        currentArtist = mediaMetadata.getString(MediaMetadata.METADATA_KEY_ARTIST);
        if (currentArtist == null) {
            currentArtist = mediaMetadata.getString(MediaMetadata.METADATA_KEY_AUTHOR);
        }
        if (currentArtist == null) {
            currentArtist = mediaMetadata.getString(MediaMetadata.METADATA_KEY_DISPLAY_SUBTITLE);
        }
        if (currentArtist == null) {
            currentArtist = mediaMetadata.getString(MediaMetadata.METADATA_KEY_WRITER);
        }
        if (currentArtist == null) {
            currentArtist = mediaMetadata.getString(MediaMetadata.METADATA_KEY_COMPOSER);
        }

        if (currentArtist == null)
            currentArtist = "Not found";
        if (currentSong == null)
            currentSong = "Not found";
        if (currentAlbum == null)
            currentAlbum = "Not found";

        Log.e(TAG, "showMetadata: currentArtist = " + currentArtist
                + "currentSong = " + currentSong
                + "currentAlbum = " + currentAlbum
                + "currentlyPlaying = " + currentlyPlaying);
    }

    private MediaController pickController(List<MediaController> controllers) {
        for (int i = 0; i < controllers.size(); i++) {
            MediaController mc = controllers.get(i);
            if (mc != null && mc.getPlaybackState() != null &&
                    mc.getPlaybackState().getState() == PlaybackState.STATE_PLAYING) {
                return mc;
            }
        }
        if (controllers.size() > 0) return controllers.get(0);
        return null;
    }

    MediaSessionManager.OnActiveSessionsChangedListener sessionListener =
            new MediaSessionManager.OnActiveSessionsChangedListener() {
                @Override
                public void onActiveSessionsChanged(List<MediaController> controllers) {
                    mediaController = pickController(controllers);
                    if (mediaController == null) return;
                    mediaController.registerCallback(callbackMediaController);
                    mediaMetadata = mediaController.getMetadata();
                    showMetadata();
                }
            };

    public IBinder onBind(Intent intent) {
        return null;
    }
}
