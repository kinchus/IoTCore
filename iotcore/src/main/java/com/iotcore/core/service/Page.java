package com.iotcore.core.service;

/**
 * 
 * @author <a href="mailto:garciadjx@gmail.com">J.M. Garcia</a>
 */
public class Page {
	
	private int offset;
	private int count;

	/**
	 * Constructor 
	 */
	public Page() {
	}

	/**
	 * Constructor 
	 * @param offset
	 * @param count
	 */
	public Page(int offset, int count) {
		super();
		this.offset = offset;
		this.count = count;
	}
	
	/**
	 * @return
	 */
	public Page next() {
		this.offset += count;
		return this;
	}

	/**
	 * @return the offset
	 */
	public int getOffset() {
		return offset;
	}

	/**
	 * @param offset the offset to set
	 */
	public void setOffset(int offset) {
		this.offset = offset;
	}

	/**
	 * @return the count
	 */
	public int getCount() {
		return count;
	}

	/**
	 * @param count the count to set
	 */
	public void setCount(int count) {
		this.count = count;
	}

}
