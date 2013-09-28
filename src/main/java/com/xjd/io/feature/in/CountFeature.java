package com.xjd.io.feature.in;

import java.util.EventListener;

public interface CountFeature extends InputStreamFeature {

	/**
	 * 通过<code>InputStream.read()</code>读取的字节数
	 * 
	 * @return
	 */
	long getReadBytes();

	/**
	 * 通过<code>InputStream.skip()</code>跳过的字节数
	 * 
	 * @return
	 */
	long getSkippedBytes();

	/**
	 * 通过<code>InputStream.read()</code>和<code>InputStream.skip()</code>使用的字节数和
	 * 
	 * @return
	 */
	long getUsedBytes();

	/**
	 * 修正读取的字节数
	 * 
	 * @param l
	 */
	void reviseReadBytes(long l);

	/**
	 * 修正跳过的字节数
	 * 
	 * @param l
	 */
	void reviseSkippedBytes(long l);
	
	void addProgressListener(ProgressListener listener);
	
	void removeProgressListener(ProgressListener listener);
	
	void addNotifySizeListener(ReachSizeListener listener);
	
	void removeNotifySizeListener(ReachSizeListener listener);

	/**
	 * 通知字节读取进度
	 * @author  elvis.xu
	 * @version 2013-1-25 下午7:06:13
	 */
	public static interface ProgressListener extends EventListener {

		void onProgress(CountFeature obj, long totallUsedBytes, long curUsedBytes);

	}

	/**
	 * 当读取字节总数达到指定大小时通知
	 * @author  elvis.xu
	 * @version 2013-1-25 下午7:06:55
	 */
	public static interface ReachSizeListener extends EventListener {

		/**
		 * @return > 0
		 */
		long getNotifySize();

		void onReachSize(CountFeature obj);
	}
}
