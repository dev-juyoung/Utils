package com.jaychang.media;

import android.content.Context;
import android.util.Log;

import com.github.hiteshsondhi88.libffmpeg.ExecuteBinaryResponseHandler;
import com.github.hiteshsondhi88.libffmpeg.FFmpeg;
import com.github.hiteshsondhi88.libffmpeg.LoadBinaryResponseHandler;
import com.github.hiteshsondhi88.libffmpeg.exceptions.FFmpegCommandAlreadyRunningException;
import com.github.hiteshsondhi88.libffmpeg.exceptions.FFmpegNotSupportedException;

import java.io.File;

import rx.Emitter;
import rx.Observable;

class FFmpegHelper {

  private static final String TAG = FFmpegHelper.class.getSimpleName();
  private static FFmpegHelper INSTANCE;
  private Context appContext;

  private FFmpegHelper(Context context) {
    appContext = context.getApplicationContext();
  }

  public static FFmpegHelper getInstance(Context context) {
    if (INSTANCE == null) {
      synchronized (FFmpegHelper.class) {
        if (INSTANCE == null) {
          INSTANCE = new FFmpegHelper(context);
        }
      }
    }
    return INSTANCE;
  }

  public static void init(Context context) {
    FFmpeg ffmpeg = FFmpeg.getInstance(context.getApplicationContext());
    try {
      ffmpeg.loadBinary(new LoadBinaryResponseHandler() {

        @Override
        public void onStart() {
        }

        @Override
        public void onFailure() {
          Log.e(TAG, "FFmpeg load failed");
        }

        @Override
        public void onSuccess() {
          Log.d(TAG, "FFmpeg load success");
        }

        @Override
        public void onFinish() {
        }
      });
    } catch (FFmpegNotSupportedException e) {
      Log.e(TAG, "FFmpeg is not supported by device");
    }
  }

  public Observable<File> concat(File file1, File file2, File output) {
    String tempTs1 = file1.getAbsolutePath() + ".ts";
    String tempTs2 = file2.getAbsolutePath() + ".ts";
    String cmd1 = String.format("-i %1$s -c copy -bsf h264_mp4toannexb %2$s", file1.getAbsolutePath(), tempTs1);
    String cmd2 = String.format("-i %1$s -c copy -bsf h264_mp4toannexb %2$s", file2.getAbsolutePath(), tempTs2);
    String cmd3 = String.format("-i concat:%1$s|%2$s -c copy -bsf aac_adtstoasc %3$s", tempTs1, tempTs2, output.getAbsolutePath());

    Observable<File> cmdTask1 = Observable.create(emitter -> {
      try {
        FFmpeg.getInstance(appContext).execute(cmd1.split(" "), new ExecuteBinaryResponseHandler() {
          @Override
          public void onSuccess(String message) {
            emitter.onNext(null);
            emitter.onCompleted();
          }

          @Override
          public void onFailure(String message) {
            emitter.onError(new RuntimeException(message));
          }
        });
      } catch (FFmpegCommandAlreadyRunningException e) {
        e.printStackTrace();
      }
    }, Emitter.BackpressureMode.BUFFER);

    Observable<File> cmdTask2 = Observable.create(emitter -> {
      try {
        FFmpeg.getInstance(appContext).execute(cmd2.split(" "), new ExecuteBinaryResponseHandler() {
          @Override
          public void onSuccess(String message) {
            emitter.onNext(null);
            emitter.onCompleted();
          }

          @Override
          public void onFailure(String message) {
            emitter.onError(new RuntimeException(message));
          }
        });
      } catch (FFmpegCommandAlreadyRunningException e) {
        e.printStackTrace();
      }
    }, Emitter.BackpressureMode.BUFFER);

    Observable<File> cmdTask3 = Observable.create(emitter -> {
      try {
        FFmpeg.getInstance(appContext).execute(cmd3.split(" "), new ExecuteBinaryResponseHandler() {
          @Override
          public void onSuccess(String message) {
            emitter.onNext(output);
            emitter.onCompleted();
          }

          @Override
          public void onFailure(String message) {
            emitter.onError(new RuntimeException(message));
          }
        });
      } catch (FFmpegCommandAlreadyRunningException e) {
        e.printStackTrace();
      }
    }, Emitter.BackpressureMode.BUFFER);

    return Observable.concat(cmdTask1, cmdTask2, cmdTask3).takeLast(1);
  }

}
