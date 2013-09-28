package com.xjd.io.feature.in;

import java.io.IOException;

public interface InputStreamFeature {
	public int read() throws IOException;

	public int read(byte b[]) throws IOException;

	public int read(byte b[], int off, int len) throws IOException;

	public long skip(long n) throws IOException;

	public int available() throws IOException;

	public void close() throws IOException;

	public void mark(int readlimit);

	public boolean markSupported();
}
