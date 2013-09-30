package com.xjd.io.mock.in;

import java.io.IOException;
import java.io.InputStream;

/**
 * <pre>
 * 模拟的给定大小的常量byte数组InputStream
 * 读取时所有读取到的字节均来自byte数组(循环读取)
 * </pre>
 * @author  elvis.xu
 * @version 2013-1-9 下午12:08:45
 */
public class ConstantBytesInputStream extends InputStream {
	long size;
	byte[] bytes;
	int point = 0;

	public ConstantBytesInputStream(byte[] bytes) {
		this(bytes, -1);
	}

	public ConstantBytesInputStream(byte[] bytes, long size) {
		this.bytes = bytes;
		this.size = size;
	}
	
	public ConstantBytesInputStream(long size, byte[] bytes) {
		this.bytes = bytes;
		this.size = size;
	}

	@Override
	public int read() throws IOException {
		if (size == 0) {
			return -1;
		} else {
			size--;
			byte b = bytes[point++];
			if (point == bytes.length) {
				point = 0;
			}
			return b;
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
			int canRead = len < size ? len : (int) size;
			if (size < 0) {
				canRead = len;
			}
			
			if (canRead <= (bytes.length - point)) {
				System.arraycopy(bytes, point, b, off, canRead);
				point += canRead;
				if (point == bytes.length) {
					point = 0;
				}
				size -= canRead;
				return canRead;
			} else {
				System.arraycopy(bytes, point, b, off, bytes.length - point);
				off += (bytes.length - point);
				int remainRead = canRead - (bytes.length - point);
				
				int cycle = remainRead / bytes.length;
				int remain = remainRead % bytes.length;
				
				while (cycle > 0) {
					System.arraycopy(bytes, 0, b, off, bytes.length);
					off += bytes.length;
					cycle --;
				}
				System.arraycopy(bytes, 0, b, off, remain);
				point = remain;
				size -= canRead;
				return canRead;
			}
		}
	}

	@Override
	public long skip(long n) throws IOException {
		long skiped = n;
		if (size >= 0) {
			skiped = n < size ? n : size;
		}
		int r = (int) ( skiped % bytes.length );
		point += r;
		if (point >= bytes.length) {
			point -= bytes.length;
		}
		return skiped;
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
