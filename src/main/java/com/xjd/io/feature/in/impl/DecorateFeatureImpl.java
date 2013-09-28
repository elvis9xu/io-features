package com.xjd.io.feature.in.impl;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collection;

import com.xjd.io.feature.in.DecorateFeature;
import com.xjd.io.feature.in.EOFFeature;
import com.xjd.io.feature.in.DecorateFeature.DecorateListener;

public class DecorateFeatureImpl extends AbstractInputStreamFeature implements DecorateFeature {
	protected byte[] head, tail;
	protected int hPoint = 0, tPoint = 0;
	protected boolean hBegin = false, hEnd = false, tBegin = false, tEnd = false;;
	protected Collection<DecorateListener> lsnrs = null;

	public DecorateFeatureImpl(InputStream in, int size) {
		this((EOFFeature) new EOFFeatureImpl(in), size);
	}

	public DecorateFeatureImpl(InputStream source) {
		this((EOFFeature) new EOFFeatureImpl(source));
	}
	
	public DecorateFeatureImpl(EOFFeature in, int size) {
		super((InputStream) in, size);
	}
	
	public DecorateFeatureImpl(EOFFeature source) {
		super((InputStream) source);
	}
	
	@Override
	public void setDecoratedHead(byte[] bytes) {
		if (hBegin) {
			throw new IllegalStateException("head decorate started.");
		}
		head = bytes;
	}

	@Override
	public byte[] getDecoratedHead() {
		return head;
	}

	@Override
	public void setDecoratedTail(byte[] bytes) {
		if (tBegin) {
			throw new IllegalStateException("tail decorate started.");
		}
		tail = bytes;
	}

	@Override
	public byte[] getDecoratedTail() {
		return tail;
	}

	@Override
	public void addDecorateListener(DecorateListener lsnr) {
		if (lsnrs == null) {
			lsnrs = new ArrayList<DecorateFeature.DecorateListener>();
		}
		lsnrs.add(lsnr);
	}

	@Override
	public void removeDecorateListener(DecorateListener lsnr) {
		if (lsnrs != null) {
			lsnrs.remove(lsnr);
		}
	}

	protected static String BEFORE_HEAD = "BEFORE_HEAD";
	protected static String AFTER_HEAD = "AFTER_HEAD";
	protected static String BEFORE_TAIL = "BEFORE_TAIL";
	protected static String AFTER_TAIL = "AFTER_TAIL";

	protected void fireBeforeDecorateHead() {
		if (!hBegin) {
			fire(BEFORE_HEAD);
			hBegin = true;
		}
	}

	protected void fireAfterDecorateHead() {
		if (hBegin && !hEnd && (head == null || hPoint == head.length)) {
			fire(AFTER_HEAD);
			hEnd = true;
		}
	}

	protected void fireBeforeDecorateTail() {
		if (!tBegin) {
			fire(BEFORE_TAIL);
			tBegin = true;
		}
	}

	protected void fireAfterDecorateTail() {
		if (tBegin && !tEnd && (tail == null || tPoint == tail.length)) {
			fire(AFTER_TAIL);
			tEnd = true;
		}
	}

	protected void fire(String event) {
		if (lsnrs != null) {
			for (DecorateListener lsnr : lsnrs) {
				if (BEFORE_HEAD.equals(event)) {
					lsnr.onBeforeDecorateHead(this);
				} else if (AFTER_HEAD.equals(event)) {
					lsnr.onAfterDecorateHead(this);
				} else if (BEFORE_TAIL.equals(event)) {
					lsnr.onBeforeDecorateTail(this);
				} else if (AFTER_TAIL.equals(event)) {
					lsnr.onAfterDecorateTail(this);
				}
			}
		}
	}

	@Override
	public int read() throws IOException {
		int i;
		fireBeforeDecorateHead();
		if (head != null && hPoint < head.length) {
			i = head[hPoint++];
			fireAfterDecorateHead();
			return i;
		}
		fireAfterDecorateHead();

		i = super.read();

		if (i == -1) {
			fireBeforeDecorateTail();
			if (tail != null && tPoint < tail.length) {
				i = tail[tPoint++];
				fireAfterDecorateTail();
				return i;
			}
			fireAfterDecorateTail();
		}
		return i;
	}

	@Override
	public int read(byte[] b, int off, int len) throws IOException {
		fireBeforeDecorateHead();
		if (head != null && hPoint < head.length) {
			int dlen = head.length - hPoint;
			int r = dlen < len ? dlen : len;
			System.arraycopy(head, hPoint, b, off, r);
			hPoint += r;
			fireAfterDecorateHead();
			return r;
		}
		fireAfterDecorateHead();

		int i = super.read(b, off, len);

		if (i == -1) {
			fireBeforeDecorateTail();
			if (tail != null && tPoint < tail.length) {
				int dlen = tail.length - tPoint;
				int r = dlen < len ? dlen : len;
				System.arraycopy(tail, tPoint, b, off, r);
				tPoint += r;
				fireAfterDecorateTail();
				return r;
			}
			fireAfterDecorateTail();
		}

		return i;
	}

	@Override
	public long skip(long n) throws IOException {
		fireBeforeDecorateHead();
		if (head != null && hPoint < head.length) {
			int dlen = head.length - hPoint;
			int r = dlen < n ? dlen : (int) n;
			hPoint += r;
			fireAfterDecorateHead();
			return r;
		}
		fireAfterDecorateHead();

		long i = super.skip(n);

		if (i == 0 && ((EOFFeature) in).isEOF()) {
			fireBeforeDecorateTail();
			if (tail != null && tPoint < tail.length) {
				int dlen = tail.length - tPoint;
				int r = dlen < n ? dlen : (int) n;
				tPoint += r;
				fireAfterDecorateTail();
				return r;
			}
			fireAfterDecorateTail();
		}

		return i;
	}

	@Override
	public int available() throws IOException {
		int i = 0;
		if (head != null) {
			i += head.length - hPoint;
		}

		i += super.available();

		if (i == 0 && tail != null && ((EOFFeature) in).isEOF()) {
			i += tail.length - tPoint;
		}

		return i;
	}

}
