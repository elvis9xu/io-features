package com.xjd.io.feature.in;

import java.io.IOException;
import java.util.EventListener;

public interface EOFFeature extends InputStreamFeature {

	boolean isEOF() throws IOException;
	
	void addEOFListener(EOFListener lsnr);
	
	void removeEOFListener(EOFListener lsnr);
	
	public static interface EOFListener extends EventListener {
		void onEOF(EOFFeature in);
	}
}
