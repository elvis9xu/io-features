package com.xjd.io.feature.in.impl;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import com.xjd.io.feature.in.BytesMatchFeature;

public class BytesMatchFeatureImpl extends CountFeatureImpl implements BytesMatchFeature {
	protected byte[] matchBytes = null;
	protected boolean filterMatch = false;
	protected List<BytesMatchListener> lsnrs = null;

	public BytesMatchFeatureImpl(InputStream in, int size) {
		super(in, size);
	}

	public BytesMatchFeatureImpl(InputStream in) {
		super(in);
	}

	protected void rebuildBuffer() {
		byte[] tmp = null;
		if (this.buf != null && this.pos < this.buf.length) {
			tmp = new byte[this.buf.length - this.pos + this.matchBytes.length];
			System.arraycopy(this.buf, this.pos, tmp, tmp.length - (this.buf.length - this.pos), this.buf.length - this.pos);
			this.pos = tmp.length - (this.buf.length - this.pos);
			this.buf = tmp;
		} else {
			tmp = new byte[this.matchBytes.length];
			this.pos = tmp.length;
			this.buf = tmp;
		}
	}

	@Override
	public void setBytesToMatch(byte[] bytes) {
		this.matchBytes = bytes;
		rebuildBuffer();
	}

	@Override
	public byte[] getBytesToMatch() {
		return this.matchBytes;
	}

	@Override
	public void setFilterMatch(boolean filter) {
		this.filterMatch = filter;
	}

	@Override
	public boolean getFilterMatch() {
		return this.filterMatch;
	}

	@Override
	public void addBytesMatchListener(BytesMatchListener lsnr) {
		if (lsnrs == null) {
			lsnrs = new ArrayList<BytesMatchListener>();
		}
		lsnrs.add(lsnr);
	}

	@Override
	public void removeBytesMatchListener(BytesMatchListener lsnr) {
		if (lsnrs != null) {
			lsnrs.remove(lsnr);
		}
	}

	protected void fireBytesMatched() {
		if (lsnrs != null) {
			for (BytesMatchListener lsnr : lsnrs) {
				lsnr.onBytesMatched(this, getUsedBytes() - matchBytes.length);
			}
		}
	}

	@Override
	public int read() throws IOException {
		// 无搜索条件无需match
		if (matchBytes == null || matchBytes.length == 0) {
			return super.read();
		}

		int r = super.read();

		// 流已结束无需match
		if (r == -1) {
			return r;
		}

		boolean match = true;
		int i = 0;
		for (; i < matchBytes.length; i++) {
			if (r == matchBytes[i]) {
				if (i == matchBytes.length - 1) {
					// matched
					break;
				}
				r = super.read();
				if (r == -1) {
					match = false;
					break;
				}
			} else {
				super.unread(r);
				i--;
				match = false;
				break;
			}
		}

		if (match) {
			fireBytesMatched();
		}

		if (!filterMatch || !match) {
			for (int z = i; z >= 0; z--) {
				super.unread(matchBytes[z]);
			}
		}

		if (match) {
			if (filterMatch) {
				return read();
			} else {
				return super.read();
			}
		} else {
			return super.read();
		}
	}

	@Override
	public int read(byte[] b, int off, int len) throws IOException {
		for (int i = 0; i < len; i++) {
			int r = this.read();
			if (r == -1) {
				if (i == 0) {
					return r;
				} else {
					return i;
				}
			}
			b[off + i] = (byte) r;
		}
		return len;
	}

}
