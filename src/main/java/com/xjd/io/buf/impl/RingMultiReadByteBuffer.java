package com.xjd.io.buf.impl;

import java.util.concurrent.atomic.AtomicInteger;

import com.xjd.io.buf.MultiReadByteBuffer;

/**
 * 环形的MultiReadByteBuffer, 线程安全
 * @author  elvis.xu
 * @version 2012-10-9 下午4:32:48
 */
public class RingMultiReadByteBuffer implements MultiReadByteBuffer {
	protected byte[] buf;
	protected AtomicInteger w;
	protected AtomicInteger[] rs;

	public RingMultiReadByteBuffer(int capacity, int readerNum) {
		if (capacity <= 0)
			throw new IllegalArgumentException("capacity must be greater than 0!");
		if (capacity == Integer.MAX_VALUE)
			throw new IllegalArgumentException("capacity must be less than Integer.MAX_VALUE.");
		if (readerNum <= 0)
			throw new IllegalArgumentException("reader number must be greater than 0!");
		buf = new byte[capacity + 1];
		w = new AtomicInteger(0);
		rs = new AtomicInteger[readerNum];
		for (int i = 0; i < readerNum; i++) {
			rs[i] = new AtomicInteger(0);
		}
	}

	@Override
	public int getCapacity() {
		return buf.length - 1;
	}

	@Override
	public void cancelReader(int readerIndex) {
		AtomicInteger r = getReader(readerIndex);
		synchronized (r) {
			r.set(-1); //取消设为-1
		}
	}
	
	@Override
	public boolean isReaderCanceled(int readerIndex) {
		return getReader(readerIndex).get() == -1;
	}

	@Override
	public int getReaderCount() {
		return rs.length;
	}

	protected AtomicInteger getReader(int readerIndex) {
		return rs[readerIndex];
	}

	@Override
	public int availableBytes(int readerIndex) {
		int tr = getReader(readerIndex).get();
		if (tr == -1) { //已取消
			return 0;
		}
		return availableBytes(tr, w.get());
	}

	@Override
	public int availableSpace() {
		int as = getCapacity();
		int tw = w.get();
		int tr = 0;
		for (AtomicInteger r : rs) {
			tr = r.get();
			if (tr == -1) {
				continue; //已取消
			}
			int i = availableSpace(tr, tw);
			if (i < as) {
				as = i;
			}
		}
		return as;
	}

	protected int availableBytes(int tr, int tw) {
		if (tr == tw) {
			return 0;
		} else if (tw > tr) {
			return tw - tr;
		} else {
			return buf.length - tr + tw;
		}
	}

	protected int availableSpace(int tr, int tw) {
		if (tr == tw) {
			return getCapacity();
		} else if (tw < tr) {
			return tr - 1 - tw;
		} else {
			return buf.length + tr - 1 - tw;
		}
	}

	@Override
	public int get(int readerIndex) {
		byte[] bs = new byte[1];
		int i = get(readerIndex, bs);
		if (i == 0) {
			return -1;
		} else {
			return bs[0] & 255;
		}
	}

	@Override
	public int get(int readerIndex, byte[] bs) {
		return get(readerIndex, bs, 0, bs.length);
	}

	@Override
	public int get(int readerIndex, byte[] bs, int off, int len) {
		AtomicInteger r = getReader(readerIndex);
		synchronized (r) {
			int tr = r.get();
			int maxGet = availableBytes(readerIndex);
			int canGet = len < maxGet ? len : maxGet;
			if (canGet <= 0) {
				return 0;
			}
			if (tr + canGet <= buf.length) {
				System.arraycopy(buf, tr, bs, off, canGet);
				tr += canGet;
			} else {
				int nextGet = buf.length - tr;
				System.arraycopy(buf, tr, bs, off, nextGet);
				int lastGet = nextGet;
				nextGet = canGet - lastGet;
				System.arraycopy(buf, 0, bs, off + lastGet, nextGet);
				tr = nextGet;
			}
			if (tr == buf.length) {
				tr = 0;
			}
			r.set(tr);
			return canGet;
		}
	}

	@Override
	public boolean put(byte b) {
		byte[] bs = new byte[] { b };
		return put(bs);
	}

	@Override
	public boolean put(byte[] bs) {
		return put(bs, 0, bs.length);
	}

	@Override
	public boolean put(byte[] bs, int off, int len) {
		synchronized (w) {
			int tw = w.get();
			int maxPut = availableSpace();
			if (maxPut < len) {
				return false;
			}
			if (tw + len <= buf.length) {
				System.arraycopy(bs, off, buf, tw, len);
				tw += len;
			} else {
				int nextPut = buf.length - tw;
				System.arraycopy(bs, off, buf, tw, nextPut);
				int lastPut = nextPut;
				nextPut = len - lastPut;
				System.arraycopy(bs, off + lastPut, buf, 0, nextPut);
				tw = nextPut;
			}
			if (tw == buf.length) {
				tw = 0;
			}
			w.set(tw);
			return true;
		}
	}

	@Override
	public int availableBytes() {
		return availableBytes(0);
	}

	@Override
	public int get() {
		return get(0);
	}

	@Override
	public int get(byte[] bs) {
		return get(0, bs);
	}

	@Override
	public int get(byte[] bs, int off, int len) {
		return get(0, bs, off, len);
	}

}
