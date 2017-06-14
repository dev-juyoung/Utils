/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2016 Piasy
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

package com.jaychang.media;

import android.media.AudioFormat;
import android.media.AudioRecord;
import android.media.MediaRecorder;
import android.support.annotation.NonNull;
import android.support.annotation.WorkerThread;
import android.util.Log;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicBoolean;

public class StreamAudioRecorder {

  private static final String TAG = StreamAudioRecorder.class.getSimpleName();

  public static final int BUFFER_SIZE = 2048;
  public static final int SAMPLE_RATE = 44100;
  private static final int SOURCE = MediaRecorder.AudioSource.MIC;
  private static final int CHANNEL_CONFIG_STEREO = AudioFormat.CHANNEL_IN_STEREO;
  private static final int CHANNEL_CONFIG_MONO = AudioFormat.CHANNEL_IN_MONO;
  private static final int AUDIO_FORMAT = AudioFormat.ENCODING_PCM_16BIT;

  private AtomicBoolean isRecording;
  private ExecutorService executorService;
  private AudioAmplitudeCallback audioAmplitudeCallback;

  private StreamAudioRecorder() {
    isRecording = new AtomicBoolean(false);
  }

  public static StreamAudioRecorder getInstance() {
    return StreamAudioRecorderHolder.INSTANCE;
  }

  public synchronized boolean start(@NonNull AudioDataCallback audioDataCallback) {
    stop();
    executorService = Executors.newSingleThreadExecutor();
    if (isRecording.compareAndSet(false, true)) {
      executorService.execute(new AudioRecordRunnable(audioDataCallback));
      return true;
    }
    return false;
  }

  public synchronized void stop() {
    isRecording.compareAndSet(true, false);

    if (executorService != null) {
      executorService.shutdown();
      executorService = null;
    }
  }

  public void setAudioAmplitudeCallback(AudioAmplitudeCallback audioAmplitudeCallback) {
    this.audioAmplitudeCallback = audioAmplitudeCallback;
  }

  public interface AudioDataCallback {
    @WorkerThread
    void onAudioDataReady(byte[] data, int size);
    @WorkerThread
    void onError();
  }

  public interface AudioAmplitudeCallback {
    @WorkerThread
    void onAudioAmplitudeReady(int amplitude);
  }

  private static final class StreamAudioRecorderHolder {
    private static final StreamAudioRecorder INSTANCE = new StreamAudioRecorder();
  }

  private class AudioRecordRunnable implements Runnable {

    private static final int BYTE_BUFFER_SIZE = BUFFER_SIZE;
    private static final int SHORT_BUFFER_SIZE = BYTE_BUFFER_SIZE / 2;

    private AudioRecord audioRecord;
    private AudioDataCallback audioDataCallback;
    private byte[] byteBuffer;
    private short[] shortBuffer;

    AudioRecordRunnable(@NonNull AudioDataCallback audioDataCallback) {
      int minBufferSize = AudioRecord.getMinBufferSize(SAMPLE_RATE, CHANNEL_CONFIG_STEREO, AUDIO_FORMAT);
      boolean isStereoSupport = minBufferSize != AudioRecord.ERROR_BAD_VALUE;
      if (!isStereoSupport) {
        minBufferSize = AudioRecord.getMinBufferSize(SAMPLE_RATE, CHANNEL_CONFIG_MONO, AUDIO_FORMAT);
      }
      this.byteBuffer = new byte[BYTE_BUFFER_SIZE];
      this.shortBuffer = new short[SHORT_BUFFER_SIZE];
      this.audioRecord = new AudioRecord(SOURCE, SAMPLE_RATE,
        isStereoSupport ? CHANNEL_CONFIG_STEREO: CHANNEL_CONFIG_MONO,
        AUDIO_FORMAT, Math.max(minBufferSize, BYTE_BUFFER_SIZE));
      this.audioDataCallback = audioDataCallback;
    }

    @Override
    public void run() {
      if (audioRecord.getState() != AudioRecord.STATE_INITIALIZED) {
        return;
      }

      try {
        audioRecord.startRecording();
      } catch (IllegalStateException e) {
        Log.w(TAG, "startRecording fail: " + e.getMessage());
        audioDataCallback.onError();
        return;
      }

      while (isRecording.get()) {
        int readShorts = audioRecord.read(shortBuffer, 0, SHORT_BUFFER_SIZE);
        if (readShorts > 0) {
          audioDataCallback.onAudioDataReady(
            short2byte(shortBuffer, readShorts, byteBuffer), readShorts * 2);
          if (audioAmplitudeCallback != null) {
            audioAmplitudeCallback.onAudioAmplitudeReady(getAmplitude(readShorts, shortBuffer));
          }
        } else {
          onError(readShorts);
          break;
        }
      }

      audioRecord.stop();
      audioRecord.release();
    }

    private int getAmplitude(int readSize, short[] mBuffer) {
      double sum = 0;
      for (int i = 0; i < readSize; i++) {
        sum += mBuffer[i] * mBuffer[i];
      }

      int amplitude = 0;
      if (readSize > 0) {
        amplitude = (int) Math.sqrt(sum / readSize);
      }

      return amplitude;
    }

    private byte[] short2byte(short[] sData, int size, byte[] bData) {
      if (size > sData.length || size * 2 > bData.length) {
        Log.w(TAG, "short2byte: too long short data array");
      }
      for (int i = 0; i < size; i++) {
        bData[i * 2] = (byte) (sData[i] & 0x00FF);
        bData[(i * 2) + 1] = (byte) (sData[i] >> 8);
      }
      return bData;
    }

    private void onError(int errorCode) {
      if (errorCode == AudioRecord.ERROR_INVALID_OPERATION) {
        Log.e(TAG, "audio record fail: ERROR_INVALID_OPERATION");
        audioDataCallback.onError();
      } else if (errorCode == AudioRecord.ERROR_BAD_VALUE) {
        Log.e(TAG, "audio record fail: ERROR_BAD_VALUE");
        audioDataCallback.onError();
      }
    }
  }

}
