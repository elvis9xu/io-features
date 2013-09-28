package com.xjd.io.buf.impl;

import java.util.concurrent.atomic.AtomicInteger;

import com.xjd.io.buf.ByteBuffer;

/**
 * 环形的ByteBuffer, 线程安全
 * @author  elvis.xu
 * @version 2012-10-9 下午4:09:23
 */
public class RingByteBuffer implements ByteBuffer {
	protected byte[] buf;
	protected AtomicInteger r, w;

	public RingByteBuffer(int capacity) {
		if (capacity <= 0)
			throw new IllegalArgumentException("capacity must be greater than 0.");
		if (capacity == Integer.MAX_VALUE)
			throw new IllegalArgumentException("capacity must be less than Integer.MAX_VALUE.");
		buf = new byte[capacity + 1];
		r = new AtomicInteger(0);
		w = new AtomicInteger(0);
	}

	@Override
	public int getCapacity() {
		return buf.length - 1;
	}

	@Override
	public int availableBytes() {
		int tr = r.get();
		int tw = w.get();
		return availableBytes(tr, tw);
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

	@Override
	public int get() {
		synchronized (r) {
			int tr = r.get();
			int tw = w.get();
			if (tr == tw) { // empty
				return -1;
			}
			byte b = buf[tr++];
			if (tr == buf.length) {
				tr = 0;
			}
			r.set(tr);
			return b & 255;
		}
	}

	@Override
	public int get(byte[] bs) {
		return get(bs, 0, bs.length);
	}

	@Override
	public int get(byte[] bs, int off, int len) {
		synchronized (r) {
			int tr = r.get();
			int tw = w.get();
			int maxGet = availableBytes(tr, tw);
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
	public int availableSpace() {
		int tr = r.get();
		int tw = w.get();
		return availableSpace(tr, tw);
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
	public boolean put(byte b) {
		synchronized (w) {
			int tr = r.get();
			int tw = w.get();
			if (tr == 0) {
				tr = buf.length;
			}
			if ((tw + 1) == tr) { // full
				return false;
			}
			buf[tw++] = b;
			if (tw == buf.length) {
				tw = 0;
			}
			w.set(tw);
			return true;
		}
	}

	@Override
	public boolean put(byte[] bs) {
		return put(bs, 0, bs.length);
	}

	@Override
	public boolean put(byte[] bs, int off, int len) {
		synchronized (w) {
			int tr = r.get();
			int tw = w.get();
			int maxPut = availableSpace(tr, tw);
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
}
