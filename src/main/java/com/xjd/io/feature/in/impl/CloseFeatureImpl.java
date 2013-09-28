package com.xjd.io.feature.in.impl;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collection;

import com.xjd.io.feature.in.CloseFeature;
import com.xjd.io.feature.in.CloseFeature.CloseListener;

public class CloseFeatureImpl extends AbstractInputStreamFeature implements CloseFeature {
	protected int modeWhenClosed = MODE_RETURN_MINUS_1;
	protected boolean closeSourceWhenClosed = true;
	protected boolean closed = false;
	protected Collection<CloseListener> lsnrs = null;

	public CloseFeatureImpl(InputStream in, int size) {
		super(in, size);
	}

	public CloseFeatureImpl(InputStream source) {
		super(source);
	}

	protected void checkMode(int mode) {
		switch (mode) {
		case MODE_RETURN_MINUS_1:
		case MODE_THROW_EXCEPTION:
			break;

		default:
			throw new IllegalArgumentException("mode[" + mode + "] is invalid.");
		}
	}

	@Override
	public void setModeWhenClosed(int mode) {
		if (getModeWhenClosed() != mode) {
			checkMode(mode);
			modeWhenClosed = mode;
		}
	}

	@Override
	public int getModeWhenClosed() {
		return modeWhenClosed;
	}

	@Override
	public void setCloseSourceWhenClosed(boolean close) {
		if (close != getCloseSourceWhenClosed()) {
			closeSourceWhenClosed = close;
		}
	}

	@Override
	public boolean getCloseSourceWhenClosed() {
		return closeSourceWhenClosed;
	}

	@Override
	public boolean isClosed() {
		return closed;
	}
	
	protected void fireClose() {
		if (lsnrs != null && isClosed()) {
			for (CloseListener listener : lsnrs) {
				listener.onClose(this);
			}
			lsnrs = null;
		}
	}

	@Override
	public void addCloseListener(CloseListener listener) {
		if (lsnrs == null) {
			lsnrs = new ArrayList<CloseFeature.CloseListener>();
		}
		lsnrs.add(listener);
		fireClose();
	}

	@Override
	public void removeCloseListener(CloseListener listener) {
		if (lsnrs != null) {
			lsnrs.remove(listener);
		}
	}

	protected boolean checkClosed() throws IOException {
		if (isClosed()) {
			if (getModeWhenClosed() == MODE_THROW_EXCEPTION) {
				throw new IOException("stream has been closed.");
			}
			return false;
		}
		return true;
	}

	@Override
	public void unread(int b) throws IOException {
		if (checkClosed())
			super.unread(b);
	}

	@Override
	public void unread(byte[] b, int off, int len) throws IOException {
		if (checkClosed())
			super.unread(b, off, len);
	}

	@Override
	public int read() throws IOException {
		if (checkClosed())
			return super.read();
		else
			return -1;
	}

	@Override
	public int read(byte[] b, int off, int len) throws IOException {
		if (checkClosed())
			return super.read(b, off, len);
		else
			return -1;
	}

	@Override
	public int available() throws IOException {
		if (checkClosed())
			return super.available();
		else
			return 0;
	}

	@Override
	public long skip(long n) throws IOException {
		if (checkClosed())
			return super.skip(n);
		else
			return 0;
	}

	@Override
	public boolean markSupported() {
		return super.markSupported();
	}

	@Override
	public synchronized void mark(int readlimit) {
		super.mark(readlimit);
	}

	@Override
	public synchronized void reset() throws IOException {
		if (checkClosed())
			super.reset();
	}

	@Override
	public synchronized void close() throws IOException {
		closed = true;
		if (getCloseSourceWhenClosed())
			super.close();
		fireClose();
	}

}
