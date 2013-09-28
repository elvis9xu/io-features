package com.xjd.io.buf;

/**
 * 边读边写的ByteBuffer, 可多次读取
 * @author  elvis.xu
 * @version 2012-8-10 下午2:59:08
 */
public interface MultiReadByteBuffer extends ByteBuffer {

	/**
	 * 当前有多少字节可读
	 * @param readerIndex reader索引
	 * @return
	 */
	public int availableBytes(int readerIndex);
	
	/**
	 * 读取一个字节
	 * @param readerIndex reader索引
	 * @return 若当前无可读字节,返回-1
	 */
	public int get(int readerIndex);
	
	/**
	 * 读取字节到一个byte数组
	 * @param readerIndex reader索引
	 * @param bs
	 * @return 实际读取的字节数
	 */
	public int get(int readerIndex, byte[] bs);
	
	/**
	 * 读取字节到一个byte数组
	 * @param readerIndex reader索引
	 * @param bs
	 * @param off
	 * @param len
	 * @return 实际读取的字节数
	 */
	public int get(int readerIndex, byte[] bs, int off, int len);
	
	/**
	 * 取消一个reader
	 * 取消后该reader为取消状态, 不可再用
	 * @param readerIndex reader索引
	 */
	public void cancelReader(int readerIndex);
	
	/**
	 * reader是否已被取消
	 * @param readerIndex reader索引
	 * @return
	 */
	public boolean isReaderCanceled(int readerIndex);
	
	/**
	 * 获取reader的总数
	 * @return
	 */
	public int getReaderCount();
}
