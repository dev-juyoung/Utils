package com.jaychang.media;

import android.annotation.SuppressLint;
import android.content.Context;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.AsyncTask;
import android.support.annotation.RawRes;

import java.io.File;

public class AudioPlayerHelper {

  public interface Callback {
    void onCompletion();
    void onError();
  }

  @SuppressLint("StaticFieldLeak")
  private static AudioPlayerHelper INSTANCE;
  private MediaPlayer player;
  private Context appContext;

  private AudioPlayerHelper(Context context) {
    appContext = context.getApplicationContext();
  }

  public static AudioPlayerHelper getInstance(Context context) {
    if (INSTANCE == null) {
      synchronized (AudioPlayerHelper.class) {
        if (INSTANCE == null) {
          INSTANCE = new AudioPlayerHelper(context);
        }
      }
    }
    return INSTANCE;
  }

  private boolean requestAudioFocus() {
    AudioManager audioManager = (AudioManager) appContext.getSystemService(Context.AUDIO_SERVICE);
    int result = audioManager.requestAudioFocus(null, AudioManager.STREAM_MUSIC, AudioManager.AUDIOFOCUS_GAIN_TRANSIENT);
    return result == AudioManager.AUDIOFOCUS_REQUEST_GRANTED;
  }

  private void abandonAudioFocus() {
    AudioManager audioManager = (AudioManager) appContext.getSystemService(Context.AUDIO_SERVICE);
    audioManager.abandonAudioFocus(null);
  }

  public void play(@RawRes final int fileRes) {
    play(fileRes, new Callback() {
      @Override
      public void onCompletion() {

      }

      @Override
      public void onError() {

      }
    });
  }

  public void play(@RawRes final int fileRes, final Callback callback) {
    if (!requestAudioFocus()) {
      return;
    }

    stop();

    AsyncTask.execute(() -> {
      player = MediaPlayer.create(appContext, fileRes);

      if (callback != null) {
        player.setOnCompletionListener(mp -> {
          callback.onCompletion();
          abandonAudioFocus();
        });
        player.setOnErrorListener((mp, what, extra) -> {
          callback.onError();
          abandonAudioFocus();
          return true;
        });
      }

      player.start();
    });
  }

  public void play(File file) {
    play(file, new Callback() {
      @Override
      public void onCompletion() {

      }

      @Override
      public void onError() {

      }
    });
  }

  public void play(File file, final Callback callback) {
    if (!requestAudioFocus()) {
      return;
    }

    stop();

    AsyncTask.execute(() -> {
      player = MediaPlayer.create(appContext, Uri.fromFile(file));

      if (callback != null) {
        player.setOnCompletionListener(mp -> {
          callback.onCompletion();
          abandonAudioFocus();
        });
        player.setOnErrorListener((mp, what, extra) -> {
          callback.onError();
          abandonAudioFocus();
          return true;
        });
      }

      player.start();
    });
  }

  public void stop() {
    if (player == null || !player.isPlaying()) {
      return;
    }

    player.stop();
    player.reset();
    player.release();
    player.setOnCompletionListener(null);
    player.setOnErrorListener(null);
    player = null;
  }

  public boolean isPlaying() {
    return player != null && player.isPlaying();
  }

}
