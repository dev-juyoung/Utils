package com.jaychang.media.filter;

import android.content.Context;
import android.support.annotation.FloatRange;
import android.util.Log;

import com.caminotoys.transformer.av.StreamAudioRecorder;
import com.github.piasy.audioprocessor.AudioProcessor;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.UUID;

public class PitchShiftFilter implements AudioFilter {

  private static final int BUFFER = 2048;
  private static final float DEFAULT_RATIO = 0.5f;

  private float ratio;
  private AudioProcessor audioProcessor;
  private File output;

  private PitchShiftFilter(Context context, float ratio) {
    this.ratio = ratio;
    this.audioProcessor = new AudioProcessor(BUFFER);
    this.output = new File(context.getExternalCacheDir(), "pitchShift_audio_" + UUID.randomUUID() + ".pcm");
  }

  public static PitchShiftFilter newInstance(Context context) {
    return new PitchShiftFilter(context, DEFAULT_RATIO);
  }

  public static PitchShiftFilter newInstance(Context context, @FloatRange(from = 0f, to = 2f) float ratio) {
    return new PitchShiftFilter(context, ratio);
  }

  @Override
  public File process(File input) throws IOException {
    long time = System.currentTimeMillis();
    FileInputStream inputStream = new FileInputStream(input);
    FileOutputStream outputStream = new FileOutputStream(output);
    byte[] audioBuffer = new byte[2048];
    while (inputStream.read(audioBuffer) > 0) {
      outputStream.write(audioProcessor.process(ratio, audioBuffer, StreamAudioRecorder.SAMPLE_RATE));
    }
    outputStream.close();
    inputStream.close();

    Log.i("test_time", getClass().getSimpleName() + " time:" + (System.currentTimeMillis() - time));
    return output;
  }

  @Override
  public byte[] processStream(byte[] data) {
    return audioProcessor.process(ratio, data, StreamAudioRecorder.SAMPLE_RATE);
  }

}
