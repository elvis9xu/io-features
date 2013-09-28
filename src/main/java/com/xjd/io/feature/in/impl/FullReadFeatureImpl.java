package com.xjd.io.feature.in.impl;

import java.io.IOException;
import java.io.InputStream;

import com.xjd.io.feature.in.FullReadFeature;

public class FullReadFeatureImpl extends AbstractInputStreamFeature implements FullReadFeature {

	public FullReadFeatureImpl(InputStream in, int size) {
		super(in, size);
	}

	public FullReadFeatureImpl(InputStream in) {
		super(in);
	}

	@Override
	public int read(byte[] b, int off, int len) throws IOException {
		int remain = len;
		while (remain > 0) {
			int i = super.read(b, off + (len - remain), remain);
			if (i == -1) {
				break;
			}
			remain -= i;
		}
		return len - remain;
	}
	
}
