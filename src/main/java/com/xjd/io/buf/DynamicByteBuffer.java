package com.xjd.io.buf;

/**
 * 边读边写的ByteBuffer, 可自动改变buffer的大小
 * @author  elvis.xu
 * @version 2012-10-9 下午4:57:14
 */
public interface DynamicByteBuffer extends ByteBuffer {

	/**
	 * 获取当前buffer的大小
	 * 注意: 不一定是实际缓存的数据量
	 * @return
	 */
	public int getCurrentBufferSize();

}
