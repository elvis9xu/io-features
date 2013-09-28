package com.xjd.io.mock.in;

import java.io.IOException;
import java.io.InputStream;

/**
 * <pre>
 * 模拟的给定大小的常量InputStream
 * 读取时所有读取到的字节均为一常量字节
 * </pre>
 * @author  elvis.xu
 * @version 2013-1-9 下午12:08:45
 */
public class ConstantByteInputStream extends InputStream {
	long size;
	byte[] buf;

	public ConstantByteInputStream() {
		this(-1);
	}
	
	public ConstantByteInputStream(long size) {
		this(size, (byte)0);
	}
	
	public ConstantByteInputStream(long size, byte b) {
		this(size, b, 1024 * 2);
	}
	
	public ConstantByteInputStream(long size, byte b, int bufSize) {
		this.size = size;
		this.buf = new byte[bufSize];
		for (int i=0; i<bufSize; i++) {
			this.buf[i] = b;
		}
	}
	
	@Override
	public int read() throws IOException {
		if (size == 0) {
			return -1;
		} else {
			size --;
			return buf[0];
		}
	}

	@Override
	public int read(byte[] b) throws IOException {
		return read(b, 0, b.length);
	}

	@Override
	public int read(byte[] b, int off, int len) throws IOException {
		if (size == 0) {
			return -1;
		} else {
			int canRead = len < size ? len : (int)size;
			if (size < 0) {
				canRead = len;
			}
			int remain = canRead;
			while (remain > 0) {
				int curRead = remain < buf.length ? remain : buf.length;
				System.arraycopy(buf, 0, b, off + (canRead - remain), curRead);
				remain -= curRead;
			}
			size -= canRead;
			return canRead;
		}
	}

	@Override
	public long skip(long n) throws IOException {
		if (size < 0) {
			return n;
		} else {
			long skiped = n < size ? n : size;
			size -= skiped;
			return skiped;
		}
	}

	@Override
	public int available() throws IOException {
		return Integer.MAX_VALUE < size ? Integer.MAX_VALUE : (int) size;
	}

	@Override
	public void close() throws IOException {
		size = 0;
	}

}
