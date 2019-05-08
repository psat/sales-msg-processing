package com.psat.util;

public class Preconditions {

  private Preconditions() {
    // should not construct instances of this class - static methods access only
  }

  public static <T> void checkNotNull(T type) {
    if (type == null) throw new NullPointerException("Must not be null: ");
  }
}
