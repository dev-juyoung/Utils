package com.jaychang.media.codec;

import android.media.MediaCodec;
import android.media.MediaCodecInfo;
import android.media.MediaFormat;
import android.media.MediaMuxer;
import android.util.Log;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.UUID;

/**
 * Encoding profile recommendations: https://developer.android.com/guide/topics/media/media-formats.html#recommendations
 */
public class AACEncoder implements Encoder {

  private static final String TAG = AACEncoder.class.getSimpleName();
  private static final String MIME_TYPE = "audio/mp4a-latm";
  private static final int CODEC_TIMEOUT = 10000;
  private static final int BITRATE = 128000;
  private static final int SAMPLE_RATE = 44100;
  private static final int CHANNEL_COUNT = 2;
  private static final int BYTE_PER_SAMPLE = 2; // PCM_16bit

  private MediaFormat mediaFormat;
  private MediaCodec mediaCodec;
  private MediaMuxer mediaMuxer;
  private ByteBuffer[] codecInputBuffers;
  private ByteBuffer[] codecOutputBuffers;
  private MediaCodec.BufferInfo bufferInfo;
  private int audioTrackId;
  private int totalBytesRead;
  private double presentationTimeUs;
  private File output;

  private AACEncoder(File input) {
    try {
      mediaFormat = MediaFormat.createAudioFormat(MIME_TYPE, SAMPLE_RATE, CHANNEL_COUNT);
      mediaFormat.setInteger(MediaFormat.KEY_AAC_PROFILE, MediaCodecInfo.CodecProfileLevel.AACObjectLC);
      mediaFormat.setInteger(MediaFormat.KEY_BIT_RATE, BITRATE);

      mediaCodec = MediaCodec.createEncoderByType(MIME_TYPE);
      mediaCodec.configure(mediaFormat, null, null, MediaCodec.CONFIGURE_FLAG_ENCODE);
      mediaCodec.start();

      codecInputBuffers = mediaCodec.getInputBuffers();
      codecOutputBuffers = mediaCodec.getOutputBuffers();

      bufferInfo = new MediaCodec.BufferInfo();

      output = new File(input.getAbsoluteFile().getParent(), "_" + UUID.randomUUID() + ".m4a");
      mediaMuxer = new MediaMuxer(output.getAbsolutePath(), MediaMuxer.OutputFormat.MUXER_OUTPUT_MPEG_4);

      totalBytesRead = 0;
      presentationTimeUs = 0;
    } catch (IOException e) {
      e.printStackTrace();
    }
  }

  public static AACEncoder create(File input) {
    return new AACEncoder(input);
  }

  @Override
  public File encode(File input) throws IOException {
    Log.d(TAG, "Start encode");

    FileInputStream inputStream = new FileInputStream(input.getAbsolutePath());
    byte[] tempBuffer = new byte[2 * SAMPLE_RATE];
    boolean hasMoreData = true;
    boolean stop = false;

    while (!stop) {
      int inputBufferIndex = 0;
      int currentBatchRead = 0;
      while (inputBufferIndex != MediaCodec.INFO_TRY_AGAIN_LATER && hasMoreData && currentBatchRead <= 50 * SAMPLE_RATE) {
        inputBufferIndex = mediaCodec.dequeueInputBuffer(CODEC_TIMEOUT);

        if (inputBufferIndex >= 0) {
          ByteBuffer buffer = codecInputBuffers[inputBufferIndex];
          buffer.clear();

          int bytesRead = inputStream.read(tempBuffer, 0, buffer.limit());
          if (bytesRead == -1) {
            mediaCodec.queueInputBuffer(inputBufferIndex, 0, 0, (long) presentationTimeUs, MediaCodec.BUFFER_FLAG_END_OF_STREAM);
            hasMoreData = false;
            stop = true;
          } else {
            totalBytesRead += bytesRead;
            currentBatchRead += bytesRead;
            buffer.put(tempBuffer, 0, bytesRead);
            mediaCodec.queueInputBuffer(inputBufferIndex, 0, bytesRead, (long) presentationTimeUs, 0);
            presentationTimeUs = 1000000L * (totalBytesRead / BYTE_PER_SAMPLE / SAMPLE_RATE);
          }
        }
      }

      int outputBufferIndex = 0;
      while (outputBufferIndex != MediaCodec.INFO_TRY_AGAIN_LATER) {
        outputBufferIndex = mediaCodec.dequeueOutputBuffer(bufferInfo, CODEC_TIMEOUT);
        if (outputBufferIndex >= 0) {
          ByteBuffer encodedData = codecOutputBuffers[outputBufferIndex];
          encodedData.position(bufferInfo.offset);
          encodedData.limit(bufferInfo.offset + bufferInfo.size);

          if ((bufferInfo.flags & MediaCodec.BUFFER_FLAG_CODEC_CONFIG) != 0 && bufferInfo.size != 0) {
            mediaCodec.releaseOutputBuffer(outputBufferIndex, false);
          } else {
            mediaMuxer.writeSampleData(audioTrackId, codecOutputBuffers[outputBufferIndex], bufferInfo);
            mediaCodec.releaseOutputBuffer(outputBufferIndex, false);
          }
        } else if (outputBufferIndex == MediaCodec.INFO_OUTPUT_FORMAT_CHANGED) {
          mediaFormat = mediaCodec.getOutputFormat();
          audioTrackId = mediaMuxer.addTrack(mediaFormat);
          mediaMuxer.start();
        }
      }
    }

    inputStream.close();
    mediaCodec.stop();
    mediaCodec.release();
    mediaMuxer.stop();
    mediaMuxer.release();

    Log.d(TAG, "Finish encode");

    return output;
  }

  @Override
  public void encodeStream(byte[] tempBuffer, int bytesRead) {
    try {
      int inputBufferIndex = mediaCodec.dequeueInputBuffer(CODEC_TIMEOUT);
      if (inputBufferIndex >= 0) {
        ByteBuffer buffer = codecInputBuffers[inputBufferIndex];
        totalBytesRead += bytesRead;
        buffer.put(tempBuffer, 0, bytesRead);
        mediaCodec.queueInputBuffer(inputBufferIndex, 0, bytesRead, (long) presentationTimeUs, 0);
        presentationTimeUs = 1000000L * (totalBytesRead / BYTE_PER_SAMPLE / SAMPLE_RATE);
      }

      int outputBufferIndex = mediaCodec.dequeueOutputBuffer(bufferInfo, CODEC_TIMEOUT);
      if (outputBufferIndex >= 0) {
        ByteBuffer encodedData = codecOutputBuffers[outputBufferIndex];
        encodedData.position(bufferInfo.offset);
        encodedData.limit(bufferInfo.offset + bufferInfo.size);

        if ((bufferInfo.flags & MediaCodec.BUFFER_FLAG_CODEC_CONFIG) != 0 && bufferInfo.size != 0) {
          mediaCodec.releaseOutputBuffer(outputBufferIndex, false);
        } else {
          mediaMuxer.writeSampleData(audioTrackId, encodedData, bufferInfo);
          mediaCodec.releaseOutputBuffer(outputBufferIndex, false);
        }
      } else if (outputBufferIndex == MediaCodec.INFO_OUTPUT_FORMAT_CHANGED) {
        mediaFormat = mediaCodec.getOutputFormat();
        audioTrackId = mediaMuxer.addTrack(mediaFormat);
        mediaMuxer.start();
      }
    } catch (IllegalStateException ex) {
      Log.e(TAG, "AAC encode error");
    }
  }

  @Override
  public File stopEncodeStream() {
    mediaCodec.stop();
    mediaCodec.reset();
    mediaCodec.release();
    mediaMuxer.stop();
    mediaMuxer.release();
    Log.d(TAG, "Finish encode stream");
    return output;
  }

}
