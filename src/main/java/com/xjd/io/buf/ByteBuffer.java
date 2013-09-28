package com.xjd.io.buf;

/**
 * 边写边读的byte buffer
 * @author  elvis.xu
 * @version 2012-8-10 下午2:46:25
 */
public interface ByteBuffer {
	
	/**
	 * 获取容量
	 * @return
	 */
	public int getCapacity();
	
	/**
	 * 当前有多少字节可读
	 * @return
	 */
	public int availableBytes();
	
	/**
	 * 读取一个字节
	 * @return 若当前无可读字节,返回-1
	 */
	public int get();
	
	/**
	 * 读取字节到一个byte数组
	 * @param bs
	 * @return 实际读取的字节数
	 */
	public int get(byte[] bs);
	
	/**
	 * 读取字节到一个byte数组
	 * @param bs
	 * @param off
	 * @param len
	 * @return 实际读取的字节数
	 */
	public int get(byte[] bs, int off, int len);
	
	
	/**
	 * 当前有多少空闲空间(用于缓存数据)
	 * @return
	 */
	public int availableSpace();
	
	/**
	 * 缓存一个byte
	 * @param b
	 * @return 缓存成功, 返回true; 若当前无空闲空间, 返回false
	 */
	public boolean put(byte b);
	
	/**
	 * 缓存整个byte数组
	 * @param bs
	 * @return 缓存成功, 返回true; 若当前无足够空闲空间, 返回false
	 */
	public boolean put(byte[] bs);
	
	/**
	 * 缓存byte数组中的指定部分
	 * @param bs
	 * @param off
	 * @param len
	 * @return 缓存成功, 返回true; 若当前无足够空闲空间, 返回false
	 */
	public boolean put(byte[] bs, int off, int len);
}
