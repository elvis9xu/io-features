package com.xjd.io.feature.in;

import java.util.EventListener;

public interface CloseFeature extends InputStreamFeature {
	static final int MODE_RETURN_MINUS_1 = 0;
	static final int MODE_THROW_EXCEPTION = 1;

	void setModeWhenClosed(int mode);

	int getModeWhenClosed();

	void setCloseSourceWhenClosed(boolean close);

	boolean getCloseSourceWhenClosed();

	boolean isClosed();

	void addCloseListener(CloseListener listener);

	void removeCloseListener(CloseListener listener);

	public static interface CloseListener extends EventListener {
		void onClose(CloseFeature in);
	}
}
