package com.google.security.zynamics.bindiff.project.matches;

import com.google.security.zynamics.zylib.disassembly.CAddress;
import com.google.security.zynamics.zylib.disassembly.IAddress;

// TODO(cblichmann): Replace this class with a class that holds a regular address pair and a single
//                   address. The way this class is currently used is like a struct.
public class AddressTriple {
  private final long firstAddr;
  private final long secondAddr;
  private final long thirdAddr;

  public AddressTriple(final long priAddr, final long secAddr, final long terAddr) {
    firstAddr = priAddr;
    secondAddr = secAddr;
    thirdAddr = terAddr;
  }

  @Override
  public boolean equals(final Object obj) {
    return (firstAddr == ((AddressTriple) obj).getAddress(EIndex.FIRST)
        && secondAddr == ((AddressTriple) obj).getAddress(EIndex.SECOND)
        && thirdAddr == ((AddressTriple) obj).getAddress(EIndex.THIRD));
  }

  public long getAddress(final EIndex index) {
    switch (index) {
      case FIRST:
        return firstAddr;
      case SECOND:
        return secondAddr;
      case THIRD:
        return thirdAddr;
    }

    throw new IllegalArgumentException("Illegal argument.");
  }

  public IAddress getIAddress(final EIndex index) {
    return new CAddress(getAddress(index));
  }

  @Override
  public int hashCode() {
    return (int)
        ((firstAddr ^ firstAddr >>> 32)
            * (firstAddr ^ secondAddr >>> 32)
            * (thirdAddr ^ thirdAddr >>> 32));
  }

  public enum EIndex {
    FIRST,
    SECOND,
    THIRD
  }
}