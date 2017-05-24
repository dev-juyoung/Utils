package com.jaychang.media;

import android.media.AudioFormat;
import android.media.AudioManager;
import android.media.AudioTrack;
import android.support.annotation.WorkerThread;
import android.util.Log;

public class StreamAudioPlayer {

  private static final String TAG = StreamAudioPlayer.class.getSimpleName();

  private static final int BUFFER_SIZE = StreamAudioRecorder.BUFFER_SIZE;
  private static final int SAMPLE_RATE = StreamAudioRecorder.SAMPLE_RATE;
  private static final int STREAM_TYPE = AudioManager.STREAM_MUSIC;
  private static final int CHANNEL_CONFIG_STEREO = AudioFormat.CHANNEL_IN_STEREO;
  private static final int CHANNEL_CONFIG_MONO = AudioFormat.CHANNEL_IN_MONO;
  private static final int AUDIO_FORMAT = AudioFormat.ENCODING_PCM_16BIT;
  private static final int PLAY_MODE = AudioTrack.MODE_STREAM;

  private AudioTrack audioTrack;

  private StreamAudioPlayer() {
  }

  private static final class StreamAudioPlayerHolder {
    private static final StreamAudioPlayer INSTANCE = new StreamAudioPlayer();
  }

  public static StreamAudioPlayer getInstance() {
    return StreamAudioPlayerHolder.INSTANCE;
  }

  public synchronized void init() {
    if (audioTrack != null) {
      audioTrack.release();
      audioTrack = null;
    }

    int minBufferSize = AudioTrack.getMinBufferSize(SAMPLE_RATE, CHANNEL_CONFIG_STEREO, AUDIO_FORMAT);
    boolean isStereoSupport = minBufferSize != AudioTrack.ERROR_BAD_VALUE;
    if (!isStereoSupport) {
      minBufferSize = AudioTrack.getMinBufferSize(SAMPLE_RATE, CHANNEL_CONFIG_MONO, AUDIO_FORMAT);
    }
    audioTrack = new AudioTrack(STREAM_TYPE, SAMPLE_RATE,
      isStereoSupport ? CHANNEL_CONFIG_STEREO : CHANNEL_CONFIG_MONO,
      AUDIO_FORMAT, Math.max(minBufferSize, BUFFER_SIZE), PLAY_MODE);
    audioTrack.play();
  }

  @WorkerThread
  public synchronized boolean play(byte[] data, int size) {
    if (audioTrack != null) {
      try {
        int ret = audioTrack.write(data, 0, size);
        switch (ret) {
          case AudioTrack.ERROR_INVALID_OPERATION:
            Log.w(TAG, "play fail: ERROR_INVALID_OPERATION");
            return false;
          case AudioTrack.ERROR_BAD_VALUE:
            Log.w(TAG, "play fail: ERROR_BAD_VALUE");
            return false;
          case AudioManager.ERROR_DEAD_OBJECT:
            Log.w(TAG, "play fail: ERROR_DEAD_OBJECT");
            return false;
          default:
            return true;
        }
      } catch (IllegalStateException e) {
        Log.w(TAG, "play fail: " + e.getMessage());
        return false;
      }
    }
    Log.w(TAG, "play fail: null audioTrack");
    return false;
  }

  public synchronized void release() {
    if (audioTrack != null) {
      audioTrack.release();
      audioTrack = null;
    }
  }

}