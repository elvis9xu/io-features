package com.xjd.io.feature.in.impl;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collection;

import com.xjd.io.feature.in.EOFFeature;
import com.xjd.io.feature.in.EOFFeature.EOFListener;

public class EOFFeatureImpl extends AbstractInputStreamFeature implements EOFFeature {
	protected boolean eof = false;
	protected Collection<EOFListener> lsnrs = null;

	public EOFFeatureImpl(InputStream in, int size) {
		super(in, size + 1);
	}

	public EOFFeatureImpl(InputStream source) {
		super(source);
	}

	@Override
	public boolean isEOF() throws IOException {
		if (eof) {
			return true;
		}
		int i = read();
		if (i == -1) {
			eof();
			return true;
		} else {
			unread(i);
			return false;
		}
	}
	
	protected void eof() {
		eof = true;
		fireEOF();
	}

	protected void fireEOF() {
		if (lsnrs != null && eof) {
			for (EOFListener lsnr : lsnrs) {
				lsnr.onEOF(this);
			}
			lsnrs = null;
		}
	}

	@Override
	public void addEOFListener(EOFListener lsnr) {
		if (lsnrs == null) {
			lsnrs = new ArrayList<EOFFeature.EOFListener>();
		}
		lsnrs.add(lsnr);
		fireEOF();
	}

	@Override
	public void removeEOFListener(EOFListener lsnr) {
		if (lsnrs != null) {
			lsnrs.remove(lsnr);
		}
	}

	@Override
	public int read() throws IOException {
		int i = super.read();
		if (i == -1) {
			eof();
		}
		return i;
	}

	@Override
	public int read(byte[] b, int off, int len) throws IOException {
		int i = super.read(b, off, len);
		if (i == -1) {
			eof();
		}
		return i;
	}
	
}
