package com.jaychang.utils;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class HashingUtils {

  private HashingUtils() {
  }

  private static String hash(String data, String algorithm) {
    try {
      MessageDigest digest = MessageDigest.getInstance(algorithm);
      digest.update(data.getBytes());

      byte messageDigest[] = digest.digest();

      StringBuilder hexString = new StringBuilder();
      for (byte aMessageDigest : messageDigest) {
        String h = Integer.toHexString(0xFF & aMessageDigest);
        while (h.length() < 2) {
          h = "0" + h;
        }
        hexString.append(h);
      }
      return hexString.toString();
    } catch (NoSuchAlgorithmException e) {
      e.printStackTrace();
    }
    return "";
  }

  public static String md5(String data) {
    return hash(data, "MD5");
  }

  public static String sha1(String data) {
    return hash(data, "SHA-1");
  }

  public static String sha256(String data) {
    return hash(data, "SHA-256");
  }

  public static String sha384(String data) {
    return hash(data, "SHA-384");
  }

  public static String sha512(String data) {
    return hash(data, "SHA-512");
  }

}
