package com.xjd.io.mock.in;

import java.io.FilterInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

/**
 * 接InputStream中被读取的数据，写入到OutputStream中
 * @author  elvis.xu
 * @version 2013-1-24 下午4:09:23
 */
public class RecordInputStream extends FilterInputStream {
	protected OutputStream out;

	public RecordInputStream(InputStream in, OutputStream out) {
		super(in);
		this.out = out;
	}

	@Override
	public int read() throws IOException {
		int r = super.read();
		if (r != -1) {
			out.write(r);
		}
		return r;
	}

	@Override
	public int read(byte[] b, int off, int len) throws IOException {
		int c = super.read(b, off, len);
		if (c != -1) {
			out.write(b, off, c);
		}
		return c;
	}
}
