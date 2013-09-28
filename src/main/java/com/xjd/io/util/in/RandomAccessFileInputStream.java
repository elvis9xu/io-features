package com.xjd.io.util.in;

import java.io.IOException;
import java.io.InputStream;
import java.io.RandomAccessFile;

public class RandomAccessFileInputStream extends InputStream {
	protected RandomAccessFile source;
	public RandomAccessFileInputStream(RandomAccessFile source) {
		this.source = source;
	}
	
	@Override
	public int read() throws IOException {
		return source.read();
	}

	@Override
	public int read(byte[] b) throws IOException {
		return source.read(b);
	}

	@Override
	public int read(byte[] b, int off, int len) throws IOException {
		return source.read(b, off, len);
	}

	@Override
	public long skip(long n) throws IOException {
		int toSkip = Integer.MAX_VALUE < n ? Integer.MAX_VALUE : (int) n;
		return source.skipBytes(toSkip);
	}

	@Override
	public int available() throws IOException {
		return 0;
	}

	@Override
	public void close() throws IOException {
		source.close();
	}

	@Override
	public synchronized void mark(int readlimit) {
		super.mark(readlimit);
	}

	@Override
	public synchronized void reset() throws IOException {
		super.reset();
	}

	@Override
	public boolean markSupported() {
		return super.markSupported();
	}

}
