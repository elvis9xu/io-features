package com.xjd.io.feature.in;

import java.util.EventListener;

public interface DecorateFeature extends InputStreamFeature {

	void setDecoratedHead(byte[] bytes);
	byte[] getDecoratedHead();
	
	void setDecoratedTail(byte[] bytes);
	byte[] getDecoratedTail();
	
	void addDecorateListener(DecorateListener lsnr);
	
	void removeDecorateListener(DecorateListener lsnr);
	
	public static interface DecorateListener extends EventListener {
		
		void onBeforeDecorateHead(DecorateFeature in);
		
		void onAfterDecorateHead(DecorateFeature in);
		
		void onBeforeDecorateTail(DecorateFeature in);
		
		void onAfterDecorateTail(DecorateFeature in);
	}
}
