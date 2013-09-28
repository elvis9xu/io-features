package com.xjd.io.util.out;

import java.io.IOException;
import java.io.OutputStream;
import java.io.RandomAccessFile;

public class RandomAccessFileOutputStream extends OutputStream {
	protected RandomAccessFile source;

	public RandomAccessFileOutputStream(RandomAccessFile source) {
		this.source = source;
	}

	@Override
	public void write(int b) throws IOException {
		source.write(b);
	}

	@Override
	public void write(byte[] b) throws IOException {
		source.write(b);
	}

	@Override
	public void write(byte[] b, int off, int len) throws IOException {
		source.write(b, off, len);
	}

	@Override
	public void flush() throws IOException {
		
	}

	@Override
	public void close() throws IOException {
		source.close();
	}

}
