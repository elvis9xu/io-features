package com.xjd.io.feature.in.impl;

import java.io.IOException;
import java.io.InputStream;
import java.io.PushbackInputStream;

import com.xjd.io.feature.in.InputStreamFeature;
import com.xjd.io.feature.in.InputStreamProxySource;

public abstract class AbstractInputStreamFeature extends PushbackInputStream implements InputStreamFeature, InputStreamProxySource {

	public InputStream getSource() {
		if (this.in != null && this.in instanceof InputStreamProxySource) {
			return ((InputStreamProxySource) this.in).getSource();
		}
		return in;
	}

	public void setSource(InputStream source) {
		if (this.in != null && this.in instanceof InputStreamProxySource) {
			((InputStreamProxySource) this.in).setSource(source);
		} else {
			this.in = source;
		}
	}

	public AbstractInputStreamFeature(InputStream in) {
		super(in);
	}

	public AbstractInputStreamFeature(InputStream in, int size) {
		super(in, size);
	}

	@Override
	public void unread(int b) throws IOException {
		checkUnread(b);
		super.unread(b);
	}

	@Override
	public void unread(byte[] b, int off, int len) throws IOException {
		for (int i = off; i < off + len; i++) {
			checkUnread(b[i]);
		}
		super.unread(b, off, len);
	}

	protected void checkUnread(int b) throws IOException {
		if (b == -1) {
			throw new IOException("Cannot unread -1");
		}
	}

	@Override
	public synchronized void close() throws IOException {
		if (in != null)
			in.close();
	}

}