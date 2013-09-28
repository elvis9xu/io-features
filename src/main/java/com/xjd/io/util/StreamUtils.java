package com.xjd.io.util;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public abstract class StreamUtils {

	/**
	 * 复制流全部内容,复制后关闭流
	 * 
	 * @param in
	 * @param out
	 * @throws IOException
	 */
	public static void copy(InputStream in, OutputStream out) throws IOException {
		copy(in, out, 0, 1024 * 2, true);
	}
	
	/**
	 * 复制流全部内容
	 * 
	 * @param in
	 * @param out
	 * @throws IOException
	 */
	public static void copy(InputStream in, OutputStream out, boolean closeAfterFinished) throws IOException {
		copy(in, out, 0, 1024 * 2, closeAfterFinished);
	}

	/**
	 * 复制流内容
	 * 
	 * @param in
	 * @param out
	 * @param copySize
	 * @param closeAfterFinished
	 * @throws IOException
	 */
	public static void copy(InputStream in, OutputStream out, long copySize, boolean closeAfterFinished)
			throws IOException {
		copy(in, out, copySize, 1024 * 2, closeAfterFinished);
	}

	/**
	 * 复制流内容
	 * 
	 * @param in
	 * @param out
	 * @param copySize
	 *            要复制的内容大小, <=0表示全部复制, 当流的内容>copySize时只复制copySize大小的内容,
	 *            当流内容<copySize时复制流的全部内容。
	 * @param bufferSize
	 *            使用buffer的大小, 必须>0
	 * @param closeAfterFinished
	 *            完成后是否关闭流
	 * @throws IOException
	 */
	public static void copy(InputStream in, OutputStream out, long copySize, int bufferSize, boolean closeAfterFinished)
			throws IOException {
		try {
			if (bufferSize <= 0) {
				throw new IllegalArgumentException("bufferSize must be greater than 0!");
			}
			boolean copyAll = false;
			if (copySize <= 0) {
				copyAll = true;
			}

			byte[] buf = new byte[bufferSize];
			int c = 0;
			if (copyAll) {
				while ((c = in.read(buf, 0, bufferSize)) != -1) {
					out.write(buf, 0, c);
				}
			} else {
				long remainSize = copySize;
				int nextReadSize;
				while (true) {
					if (remainSize <= 0) {
						break;
					}
					nextReadSize = (int) (remainSize < bufferSize ? remainSize : bufferSize);
					c = in.read(buf, 0, nextReadSize);
					if (c == -1) {
						break;
					}
					out.write(buf, 0, c);
					remainSize -= c;
				}
			}
			out.flush();
		} finally {
			if (closeAfterFinished) {
				in.close();
				out.close();
			}
		}
	}

	/**
	 * 复制流全部内容,复制后关闭流
	 * 
	 * @param in
	 * @param out
	 * @throws IOException
	 */
	public static void copy(InputStream in, DataOutput out) throws IOException {
		copy(in, out, 0, 1024 * 2, true);
	}

	/**
	 * 复制流内容
	 * 
	 * @param in
	 * @param out
	 * @param copySize
	 * @param closeAfterFinished
	 * @throws IOException
	 */
	public static void copy(InputStream in, DataOutput out, long copySize, boolean closeAfterFinished)
			throws IOException {
		copy(in, out, copySize, 1024 * 2, closeAfterFinished);
	}

	/**
	 * 复制流内容
	 * 
	 * @param in
	 * @param out
	 * @param copySize
	 *            要复制的内容大小, <=0表示全部复制, 当流的内容>copySize时只复制copySize大小的内容,
	 *            当流内容<copySize时复制流的全部内容。
	 * @param bufferSize
	 *            使用buffer的大小, 必须>0
	 * @param closeAfterFinished
	 *            完成后是否关闭流
	 * @throws IOException
	 */
	public static void copy(InputStream in, DataOutput out, long copySize, int bufferSize, boolean closeAfterFinished)
			throws IOException {
		try {
			if (bufferSize <= 0) {
				throw new IllegalArgumentException("bufferSize must be greater than 0!");
			}
			boolean copyAll = false;
			if (copySize <= 0) {
				copyAll = true;
			}

			byte[] buf = new byte[bufferSize];
			int c = 0;
			if (copyAll) {
				while ((c = in.read(buf, 0, bufferSize)) != -1) {
					out.write(buf, 0, c);
				}
			} else {
				long remainSize = copySize;
				int nextReadSize;
				while (true) {
					if (remainSize <= 0) {
						break;
					}
					nextReadSize = (int) (remainSize < bufferSize ? remainSize : bufferSize);
					c = in.read(buf, 0, nextReadSize);
					if (c == -1) {
						break;
					}
					out.write(buf, 0, c);
					remainSize -= c;
				}
			}
		} finally {
			if (closeAfterFinished) {
				in.close();
			}
		}
	}

	/**
	 * 复制流内容
	 * 
	 * @param in
	 * @param out
	 * @param copySize
	 * @param closeAfterFinished
	 * @throws IOException
	 */
	public static void copy(DataInput in, OutputStream out, long copySize, boolean closeAfterFinished)
			throws IOException {
		copy(in, out, copySize, 1024 * 2, closeAfterFinished);
	}

	/**
	 * 复制流内容
	 * 
	 * @param in
	 * @param out
	 * @param copySize
	 *            要复制的内容大小, <=0表示全部复制, 当流的内容>copySize时只复制copySize大小的内容,
	 *            当流内容<copySize时复制流的全部内容。
	 * @param bufferSize
	 *            使用buffer的大小, 必须>0
	 * @param closeAfterFinished
	 *            完成后是否关闭流
	 * @throws IOException
	 */
	public static void copy(DataInput in, OutputStream out, long copySize, int bufferSize, boolean closeAfterFinished)
			throws IOException {
		try {
			if (bufferSize <= 0) {
				throw new IllegalArgumentException("bufferSize must be greater than 0!");
			}

			byte[] buf = new byte[bufferSize];
			long remainSize = copySize;
			int nextReadSize;
			while (true) {
				if (remainSize <= 0) {
					break;
				}
				nextReadSize = (int) (remainSize < bufferSize ? remainSize : bufferSize);
				in.readFully(buf, 0, nextReadSize);
				out.write(buf, 0, nextReadSize);
				remainSize -= nextReadSize;
				out.flush();
			}
		} finally {
			if (closeAfterFinished) {
				out.close();
			}
		}
	}

}
