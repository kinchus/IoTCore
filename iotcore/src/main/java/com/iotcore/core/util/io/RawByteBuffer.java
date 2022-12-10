package com.iotcore.core.util.io;

import java.nio.ByteBuffer;

/**
 * 
@author <a href="mailto:garciadjx@gmail.com">J.M. Garcia</a>o
 *
 */
public class RawByteBuffer {
	private static final int r = 16;
	private byte data[];
	int currindex = 0;
	
	/**
	 * @param data
	 * @return
	 */
	public static RawByteBuffer wrap(byte [] data) {
		return new RawByteBuffer(data);
	}
	
	/**
	 * @param data
	 * @return
	 */
	public static RawByteBuffer wrap(ByteBuffer data) {
		return new RawByteBuffer(data.array());
	}
	
	/**
	 * Constructor 
	 * @param data
	 */
	public RawByteBuffer(byte [] data) {
		this.data = data;
	}
	
	/**
	 * Constructor 
	 * @param message
	 */
	public RawByteBuffer(String message) {
		String v = null;
		int n = 0;
		int div = 2;
		// this.data = new byte[(int) Math.ceil(message.length()/2.0)];
		this.data = new byte[message.length()/div];
		for (int i=0; i<message.length()-1;i+=div) {
			v = message.substring(i, i+div);
			int intVal = Integer.parseInt(v, r);
			data[n++] = (byte)intVal;
		}
		// this.data = message.getBytes();
	}
	
	/**
	 * Get the raw byte at the current position
	 * @return
	 */
	public byte getByte(){
		if (data.length - currindex < 1) throw new IllegalArgumentException("byte missing");
		return this.data[this.currindex++];
	}
	
	/**
	 * Get the raw byte at any position. Does not modify the current index
	 * @param i 
	 * @return
	 */
	public byte getByte(int i){
		if ((i < 0) || (i >= this.length())) throw new IllegalArgumentException("Wrong index");
		return this.data[i];
	}
	
	/**
	 * Get a copy of the remaining bytes in the buffer starting from the current position.
	 * Must reset!
	 * @return
	 */
	public byte[] getBytes(){
		if (currindex >= (this.data.length - 1)) {
			return null;
		}
		int len = (this.data.length) - this.currindex;
		byte result[] = new byte[len];
		System.arraycopy( this.data, this.currindex, result, 0, len );
		currindex += (len);
		return result;
	}
	
	
	/**
	 * Get a copy of the next sz bytes in the buffer starting from the current positio
	 * @param sz 
	 * @return 
	 */
	public byte[] getBytes(long sz){
		if (sz > (data.length - currindex)) {
			return null;
		}
		byte result[] = new byte[(int) sz];
		System.arraycopy( this.data, this.currindex, result, 0, result.length );
		currindex += sz;
		return result;
	}
	
	
	/**
	 * Get the next 16 bits of the buffer as an unsinged 16 bits integer (Little endian)
	 * @return
	 */
	public int uint16_LE(){ 
		//little endian
		int result = (getByte())& 0xFF;
		result |= (getByte()<<8)& 0xFFFF;
		return result;
	}
	
	/**
	 * Get the next 16 bits of the buffer as an unsinged 16 bits integer
	 * @return
	 */
	public int uint16(){ 
		int result = (getByte()<<8)& 0xFFFF;
		result |= (getByte())& 0xFF;
		
		return result;
	}
	
	
	/**
	 * @return
	 */
	public long uint32_LE(){ 
		//little endianness
		long result = (getByte())& 0xFF;
		result |= (getByte()<<8)& 0xFFFF;
		result |= (getByte()<<16)& 0xFFFFFF;
		result |= (getByte()<<24)& 0xFFFFFFFF;
		
		return result;
	}
	
	/**
	 * @return
	 */
	public long uint32(){
		long result = (getByte()<<24)& 0xFFFFFFFF;
		result |= (getByte()<<16)& 0xFFFFFF;
		result |= (getByte()<<8)& 0xFFFF;
		result |= (getByte())& 0xFF;
		
		return result;
	}
	
	/**
	 * @return
	 */
	public int length(){
		return data.length;
	}
	
	/**
	 * @return
	 */
	public int remaining(){
		return data.length - currindex;
	}
	
	/**
	 * 
	 */
	public void reset() {
		currindex = 0;
	}
	
	/**
	 * @param other
	 * @return
	 */
	public boolean equals(RawByteBuffer other) {
		
		if (data.length != other.remaining()) {
			return false;
		}
		
		for (int i=0;i<data.length;i++) {
			if (data[i] != other.data[i]) {
				return false;
			}
		}
		
		return true;
	}
 }