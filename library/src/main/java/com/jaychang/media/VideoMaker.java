package com.jaychang.media;

import android.content.Context;
import android.media.MediaCodec;
import android.media.MediaExtractor;
import android.media.MediaFormat;
import android.media.MediaMuxer;
import android.util.Log;

import java.io.File;
import java.io.IOException;
import java.nio.ByteBuffer;

import rx.Observable;

public class VideoMaker {

  private static final String TAG = VideoMaker.class.getSimpleName();

  private static VideoMaker INSTANCE;
  private Context appContext;

  private VideoMaker(Context context) {
    appContext = context.getApplicationContext();
  }

  public static VideoMaker getInstance(Context context) {
    if (INSTANCE == null) {
      synchronized (VideoMaker.class) {
        if (INSTANCE == null) {
          INSTANCE = new VideoMaker(context);
        }
      }
    }
    return INSTANCE;
  }

  public Observable<File> merge(File inputAudio, File inputVideo, File output) {
    return Observable.fromCallable(() -> {
      mergeInternal(inputAudio, inputVideo, output);
      return output;
    });
  }

  private void mergeInternal(File inputAudioFile, File inputVideoFile, File outputFile) throws IOException {
    Log.d(TAG, "Start merge");

    long time = System.currentTimeMillis();

    // extractAudio audio track
    MediaExtractor audioExtractor = new MediaExtractor();
    audioExtractor.setDataSource(inputAudioFile.getAbsolutePath());

    MediaFormat targetAudioFormat = null;
    for (int i = 0; i < audioExtractor.getTrackCount(); ++i) {
      MediaFormat format = audioExtractor.getTrackFormat(i);
      String mime = format.getString(MediaFormat.KEY_MIME);
      if (mime.startsWith("audio/")) {
        targetAudioFormat = format;
        audioExtractor.selectTrack(i);
        break;
      }
    }

    // extractAudio video track
    MediaExtractor videoExtractor = new MediaExtractor();
    videoExtractor.setDataSource(inputVideoFile.getAbsolutePath());

    MediaFormat targetVideoFormat = null;
    for (int i = 0; i < videoExtractor.getTrackCount(); ++i) {
      MediaFormat format = videoExtractor.getTrackFormat(i);
      String mime = format.getString(MediaFormat.KEY_MIME);
      if (mime.startsWith("video/")) {
        targetVideoFormat = format;
        videoExtractor.selectTrack(i);
        break;
      }
    }

    if (targetAudioFormat == null || targetVideoFormat == null) {
      String errorMsg = "Expect one audio track and one video track";
      Log.e(TAG, errorMsg);
      throw new RuntimeException(errorMsg);
    }

    // add audio & video tracks to muxer
    MediaMuxer muxer = new MediaMuxer(outputFile.getAbsolutePath(), MediaMuxer.OutputFormat.MUXER_OUTPUT_MPEG_4);
    int targetAudioOutputTrack = muxer.addTrack(targetAudioFormat);
    int targetVideoOutputTrack = muxer.addTrack(targetVideoFormat);
    muxer.start();

    ByteBuffer inputBuffer = ByteBuffer.allocate(2048 * 1024);
    MediaCodec.BufferInfo bufferInfo = new MediaCodec.BufferInfo();

    // write audio data
    boolean audioMuxDone = false;
    int frameCount = 0;
    long presentationTimeUs = -1;
    while (!audioMuxDone) {
      inputBuffer.clear();
      int bytesRead = audioExtractor.readSampleData(inputBuffer, 0);
      if (bytesRead < 0) {
        audioMuxDone = true;
      } else {
        if (presentationTimeUs == -1) {
          presentationTimeUs = audioExtractor.getSampleTime();
        }
        bufferInfo.presentationTimeUs = audioExtractor.getSampleTime() - presentationTimeUs;
        bufferInfo.flags = audioExtractor.getSampleFlags();
        bufferInfo.size = bytesRead;
        muxer.writeSampleData(targetAudioOutputTrack, inputBuffer, bufferInfo);
        Log.d(TAG, "Appended audio frame: " + targetAudioOutputTrack + ":" + frameCount + ":" + bufferInfo.presentationTimeUs);
        audioExtractor.advance();
        frameCount++;
      }
    }

    // write video data
    boolean videoMuxDone = false;
    frameCount = 0;
    presentationTimeUs = -1;
    while (!videoMuxDone) {
      inputBuffer.clear();
      int bytesRead = videoExtractor.readSampleData(inputBuffer, 0);
      if (bytesRead < 0) {
        videoMuxDone = true;
      } else {
        if (presentationTimeUs == -1) {
          presentationTimeUs = videoExtractor.getSampleTime();
        }
        bufferInfo.presentationTimeUs = videoExtractor.getSampleTime() - presentationTimeUs;
        bufferInfo.flags = videoExtractor.getSampleFlags();
        bufferInfo.size = bytesRead;
        muxer.writeSampleData(targetVideoOutputTrack, inputBuffer, bufferInfo);
        Log.d(TAG, "Appended video frame: " + targetVideoOutputTrack + ":" + frameCount + ":" + bufferInfo.presentationTimeUs);
        videoExtractor.advance();
        frameCount++;
      }
    }

    // release resources
    audioExtractor.release();
    videoExtractor.release();
    muxer.stop();
    muxer.release();

    Log.d(TAG, "End merge, used time:" + (System.currentTimeMillis() - time) + " path: " + outputFile.getAbsolutePath());
  }

  public Observable<File> concat(File file1, File file2, File output) {
    return FFmpegHelper.getInstance(appContext).concat(file1, file2, output);
  }

}
