package com.xjd.io.util.in;

import java.io.ByteArrayInputStream;
import java.nio.ByteBuffer;
import java.nio.charset.Charset;

public class StringInputStream extends ByteArrayInputStream {
	protected Charset charset = Charset.forName("utf8");

	public StringInputStream(byte[] buf, int offset, int length) {
		super(buf, offset, length);
	}

	public StringInputStream(byte[] buf) {
		super(buf);
	}
	
	public StringInputStream(String source) {
		this(source, null);
	}
	
	public StringInputStream(String source, Charset charset) {
		this(new byte[0]);
		if (charset != null) {
			this.charset = charset;
		}
		ByteBuffer byteBuf = this.charset.encode(source);
		this.buf = byteBuf.array();
		this.pos = byteBuf.position();
		this.count = byteBuf.limit();
		this.mark = this.pos;
	}

}
