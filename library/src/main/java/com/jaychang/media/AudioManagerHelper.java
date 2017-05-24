package com.jaychang.media;

import android.content.Context;
import android.media.AudioManager;

public class AudioManagerHelper {

  private static AudioManagerHelper INSTANCE;
  private AudioManager audioManager;

  private AudioManagerHelper(Context context) {
    audioManager = (AudioManager) context.getApplicationContext().getSystemService(Context.AUDIO_SERVICE);
  }

  public static AudioManagerHelper getInstance(Context context) {
    if (INSTANCE == null) {
      synchronized (AudioManagerHelper.class) {
        if (INSTANCE == null) {
          INSTANCE = new AudioManagerHelper(context);
        }
      }
    }
    return INSTANCE;
  }

  public boolean requestAudioFocus() {
    int result = audioManager.requestAudioFocus(null, AudioManager.STREAM_MUSIC, AudioManager.AUDIOFOCUS_GAIN_TRANSIENT);
    return result == AudioManager.AUDIOFOCUS_REQUEST_GRANTED;
  }

  public void abandonAudioFocus() {
    audioManager.abandonAudioFocus(null);
  }

  public boolean isMusicPlaying() {
    return audioManager.isMusicActive();
  }

  public AudioManager getAudioManager() {
    return audioManager;
  }

}
