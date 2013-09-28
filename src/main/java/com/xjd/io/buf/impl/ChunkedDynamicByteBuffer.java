package com.xjd.io.buf.impl;

import java.util.ArrayList;

import com.xjd.io.buf.DynamicByteBuffer;

/**
 * 数据块形式的DynamicByteBuffer
 * @author  elvis.xu
 * @version 2012-10-9 下午5:02:10
 */
public class ChunkedDynamicByteBuffer implements DynamicByteBuffer {
	public static int DEFAULT_CHUNK_SIZE = 1024 * 2; //2k
	public static float DEFAULT_FREE_KEEP_FACTOR = 0.5f; //1/2
	
	protected int capacity;
	protected int chunkSize;
	protected float freeKeepFactor;
	
	protected volatile ChunkedNode first;
	protected volatile ChunkedNode last;
	protected volatile int nodeCount;
	
	protected ChunkedNodePointer r;
	protected ChunkedNodePointer w;

	public ChunkedDynamicByteBuffer(int capacity) {
		this(capacity, DEFAULT_CHUNK_SIZE, DEFAULT_FREE_KEEP_FACTOR);
	}
	
	public ChunkedDynamicByteBuffer(int capacity, int chunkSize, float freeKeepFactor) {
		this.capacity = capacity;
		this.chunkSize = chunkSize;
		this.freeKeepFactor = freeKeepFactor;
		ChunkedNode node = new ChunkedNode();
		node.setBuf(new byte[chunkSize]);
		first = last = node;
		nodeCount = 1;
		r = new ChunkedNodePointer();
		r.pointTo(node, 0);
		w = new ChunkedNodePointer();
		w.pointTo(node, 0);
	}
	
	@Override
	public int getCapacity() {
		return capacity;
	}
	
	@Override
	public int getCurrentBufferSize() {
		return nodeCount * chunkSize;
	}
	
	public int getChunkSize() {
		return chunkSize;
	}

	@Override
	public int availableBytes() {
		synchronized (r) {
			ChunkedNodePointer tw = w.clone();
			return size(r, tw);
		}
	}
	
	protected int size(ChunkedNodePointer from, ChunkedNodePointer to) {
		if (from.node == to.node) {
			return to.index - from.index;
		} else {
			ChunkedNodePointer newFrom = new ChunkedNodePointer();
			newFrom.pointTo(from.node.latter, 0);
			return chunkSize - from.index + size(newFrom, to);
		}
	}

	@Override
	public int get() {
		byte[] bs = new byte[1];
		int i = get(bs);
		if (i == 0) {
			return -1;
		} else {
			return bs[0] & 255;
		}
	}

	@Override
	public int get(byte[] bs) {
		return get(bs, 0, bs.length);
	}

	@Override
	public int get(byte[] bs, int off, int len) {
		synchronized (r) {
			ChunkedNodePointer tw = w.clone();
			int available = size(r, tw);
			int canRead = len < available ? len : available;
			if (canRead <= 0) {
				return 0;
			}
			int remain = canRead;
			int nextRead;
			while (remain > 0) {
				nextRead = (chunkSize - r.index) < remain ?  (chunkSize - r.index) : remain;
				System.arraycopy(r.node.buf, r.index, bs, off + (canRead - remain), nextRead);
				remain -= nextRead;
				r.index += nextRead;
				if (r.index >= chunkSize && r.node != tw.node) {
					r.node = r.node.latter;
					r.index = 0;
				}
			}
			return canRead;
		}
	}
	
	@Override
	public int availableSpace() {
		synchronized (w) {
			ChunkedNodePointer tr = r.clone();
			//compute available bytes
			int ab = size(tr, w);
			ChunkedNodePointer lp = new ChunkedNodePointer();
			lp.node = last;
			lp.index = chunkSize;
			int curAs = size(w, lp);
			
			//check space (reuse freee nodes)
			ArrayList<ChunkedNode> freeNodes = new ArrayList<ChunkedNode>();
			ChunkedNode rNode = tr.node;
			while ( first != rNode ){
				first = first.latter;
				-- nodeCount;
				freeNodes.add(first.removeFormer());
			}
			
			int keep = (int) Math.ceil((ab * freeKeepFactor - curAs) / chunkSize);
			keep = keep > 0 ? keep : 0; 
			keep = keep < freeNodes.size() ? keep : freeNodes.size();
			for (int i=0; i<keep; i++) {
				last.setLatter(freeNodes.get(i));
				last = last.latter;
				++ nodeCount;
			}
			
			//compute real available space
			return capacity - ab - tr.index;
		}
	}
	
	@Override
	public boolean put(byte b) {
		byte[] bs = new byte[]{b};
		return put(bs);
	}

	@Override
	public boolean put(byte[] bs) {
		return put(bs, 0, bs.length);
	}

	@Override
	public boolean put(byte[] bs, int off, int len) {
		synchronized (w) {
			int as = availableSpace();
			if (len > as) { //超出最大容量
				return false;
			}
			
			put(bs, off, len, w);
			return true;
		}
	}
	
	protected void put(byte[] bs, int off, int len, ChunkedNodePointer wp) {
		int as = chunkSize - wp.index;
		int put = len < as ? len : as;
		System.arraycopy(bs, off, wp.node.buf, wp.index, put);
		wp.index += put;
		int remain = len - put;
		if (remain > 0) { //还有
			if (wp.node == last) { //需要新增
				ChunkedNode node = new ChunkedNode();
				node.setBuf(new byte[chunkSize]);
				last.setLatter(node);
				last = last.latter;
				++ nodeCount;
			}
			wp.node = wp.node.latter;
			wp.index = 0;
		
			put(bs, off + put, remain, wp);
		}
	}

	static class ChunkedNode {
		byte[] buf;
		
		ChunkedNode former;
		ChunkedNode latter;
		
		void setBuf(byte[] buf) {
			this.buf = buf;
		}
		
		void setLatter(ChunkedNode latter) {
			latter.former = this;
			this.latter = latter;
		}
		
		ChunkedNode removeFormer() {
			ChunkedNode f = former;
			if (f != null) {
				this.former = null;
				f.latter = null;
			}
			return f;
		}
	}
	
	static class ChunkedNodePointer {
		ChunkedNode node;
		int index;
		
		void pointTo(ChunkedNode node, int index) {
			this.node = node;
			this.index = index;
		}
		
		void pointTo(int index) {
			this.index = index;
		}
		
		protected synchronized ChunkedNodePointer clone() {
			ChunkedNodePointer p = new ChunkedNodePointer();
			p.node = node;
			p.index = index;
			return p;
		}
	}
	
}
