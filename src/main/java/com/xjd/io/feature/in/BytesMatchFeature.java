package com.xjd.io.feature.in;

import java.util.EventListener;

public interface BytesMatchFeature extends InputStreamFeature {

	void setBytesToMatch(byte[] bytes);
	
	byte[] getBytesToMatch();
	
	void setFilterMatch(boolean filter);
	
	boolean getFilterMatch();
	
	void addBytesMatchListener(BytesMatchListener lsnr);
	
	void removeBytesMatchListener(BytesMatchListener lsnr);
	
	public static interface BytesMatchListener extends EventListener {
		public void onBytesMatched(BytesMatchFeature in, long index);
	}
}
