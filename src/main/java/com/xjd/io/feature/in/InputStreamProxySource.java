package com.xjd.io.feature.in;

import java.io.InputStream;

public interface InputStreamProxySource {
	
	InputStream getSource();

	void setSource(InputStream source);
	
}
