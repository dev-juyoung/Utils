package com.jaychang.media.codec;

import java.io.File;
import java.io.IOException;

public interface Encoder {

  File encode(File input) throws IOException;

  void encodeStream(byte[] tempBuffer, int bytesRead);

  File stopEncodeStream();

}
