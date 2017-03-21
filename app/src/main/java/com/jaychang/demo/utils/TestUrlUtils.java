package com.jaychang.demo.utils;

import static com.jaychang.utils.UrlUtils.isValid;

public class TestUrlUtils {

  public static void test() {
    String url = "http://stackoverflow.com/questions/5617749/how-to-validate-a-url-website-name-in-edittext-in-android/7754297";
    String url1 = "https://goo.gl/fjwdPB";
    String url2 = "goo.gl/fjwdPB";
    String url3 = "foobar";
    System.out.println("isValid:" + isValid(url));
    System.out.println("isValid:" + isValid(url1));
    System.out.println("isValid:" + isValid(url2));
    System.out.println("isValid:" + isValid(url3));
  }

}
