package com.xjd.io.util.in;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 把多个inputStream顺序组合成一个inputStream使用
 * 
 * @author elvis.xu
 * @version 2013-2-27 下午2:27:43
 */
public class CombinedInputStream extends InputStream {
	private static Logger log = LoggerFactory.getLogger(CombinedInputStream.class);

	protected volatile boolean inUse = false;
	protected volatile boolean closed = false;
	protected boolean autoClose = true;
	protected List<InputStream> sources = new ArrayList<InputStream>();
	protected int index = 0;

	public CombinedInputStream() {};
	
	public CombinedInputStream(boolean autoClose) {
		setAutoClose(autoClose);
	};
	
	public CombinedInputStream(InputStream... sources) {
		addSourceStream(sources);
	};
	
	public CombinedInputStream(boolean autoClose, InputStream... sources) {
		setAutoClose(autoClose);
		addSourceStream(sources);
	};
	
	protected void setInUse() {
		if (!inUse) {
			inUse = true;
		}
	}

	protected void checkInUse() {
		if (inUse) {
			throw new IllegalStateException("cannot change parameters when stream in use.");
		}
	}
	
	protected void checkClosed() throws IOException {
		if (closed) {
			throw new IOException("stream has been closed.");
		}
	}

	/**
	 * 设置当其中一条inputStream读取到末尾时是否自动关闭之
	 * 
	 * @param autoClose
	 */
	void setAutoClose(boolean autoClose) {
		checkInUse();
		if (this.autoClose != autoClose) {
			this.autoClose = autoClose;
		}
	}

	boolean isAutoClose() {
		return autoClose;
	}

	/**
	 * 添加inputStream作为该stream的源
	 * 
	 * @param sources
	 */
	void addSourceStream(InputStream... sources) {
		checkInUse();
		this.sources.addAll(Arrays.asList(sources));
	}

	protected InputStream curSource() {
		if (sources.size() > index) {
			return sources.get(index);
		}
		return null;
	}

	protected InputStream nextSource() {
		if (isAutoClose()) { // 自动关闭的情况：关闭已读取完的stream,后移除之
			InputStream cur = curSource();
			if (cur != null) {
				try {
					cur.close();
				} catch (IOException e) {
					log.error("close input stream fail!", e);
				}
				sources.remove(cur);
			}
		} else { // 不自动关闭的情况: 不关闭stream,增加索引值
			index++;
		}
		return curSource();
	}

	@Override
	public int read() throws IOException {
		checkClosed();
		setInUse();

		InputStream in = curSource();
		while (in != null) {
			int i = in.read();
			if (i != -1) {
				return i;
			} else {
				in = nextSource();
			}
		}

		return -1;
	}

	@Override
	public int read(byte[] b) throws IOException {
		return read(b, 0, b.length);
	}

	@Override
	public int read(byte[] b, int off, int len) throws IOException {
		checkClosed();
		setInUse();
		
		InputStream in = curSource();
		while (in != null) {
			int i = in.read(b, off, len);
			if (i != -1) {
				return i;
			} else {
				in = nextSource();
			}
		}
		
		return -1;
	}

	@Override
	public long skip(long n) throws IOException {
		checkClosed();
		InputStream in = curSource();
		if (in != null) {
			return in.skip(n);
		}
		return 0;
	}

	@Override
	public int available() throws IOException {
		checkClosed();
		InputStream in = curSource();
		if (in != null) {
			return in.available();
		}
		return 0;
	}

	@Override
	public void close() throws IOException {
		if (!closed) {
			closed = true;
			for (InputStream in : sources) {
				try {
					in.close();
				} catch (Exception e) {
					log.error("close input stream fail!", e);
				}
			}
			sources.clear();
		}
	}

	@Override
	public synchronized void mark(int readlimit) {
		super.mark(readlimit);
	}

	@Override
	public synchronized void reset() throws IOException {
		super.reset();
	}

	@Override
	public boolean markSupported() {
		return super.markSupported();
	}

}
