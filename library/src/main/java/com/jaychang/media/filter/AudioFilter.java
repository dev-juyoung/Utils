package com.jaychang.media.filter;

import java.io.File;
import java.io.IOException;

public interface AudioFilter {

  File process(File input) throws IOException;

  byte[] processStream(byte[] data);

}
