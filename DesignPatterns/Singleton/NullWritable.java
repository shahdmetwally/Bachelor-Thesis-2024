package net.nutch.io;

import java.io.*;

public class NullWritable implements Writable {

  private static final NullWritable THIS = new NullWritable();

  private NullWritable() {}
  public static NullWritable get() { return THIS; }

  public void readFields(DataInput in) throws IOException {}
  public void write(DataOutput out) throws IOException {}
}

