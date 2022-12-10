package com.iotcore.core.util;

import java.util.BitSet;

final class bit {
	public static int getBit(int x, int n) {
		return x >> n & 1;
	}

	public static int setBit(int x, int n) {
		return x | 1 << n;
	}

	public static int rstBit(int x, int n) {
		return x & ~(1 << n);
	}
}

/**
 * 
 */
public class BitString {
	private byte data[];
	private int size = 0;
	//private int maxSize = 0;
	
	/**
	 * Constructor 
	 * @param size
	 */
	public BitString(int size) {
       if (size < 1) throw new IllegalArgumentException("Size must be positive >0");
       this.data = new byte[size/8 +1];
       this.size = 0;
	}
	
	/**
	 * Constructor 
	 * @param data
	 * @param currentSize
	 */
	public BitString(byte data[], int currentSize) {
		this.data = data;
		if (currentSize < 0) 
		    currentSize = this.data.length * 8;
		
		this.size = currentSize;
	}
	
	/**
	 * @param data
	 * @return
	 */
	public boolean addBit(int data){
		if (this.size >= this.data.length*8) return false;

		int b = (this.size)/8;
		int temp = this.size;
		while(temp >= 8) temp -= 8;
		int pos = 7 - temp;

		if (data >= 1)
			this.data[b] = (byte)bit.setBit(this.data[b],pos);
		else
			this.data[b] = (byte)bit.rstBit(this.data[b],pos);

		this.size++;

		return true;
	}
	
	/**
	 * @param data
	 * @return
	 */
	public boolean addByte(byte data){
		for(int i = 7;i>=0;i--)
			this.addBit(bit.getBit(data,i));
		return true;
	}
	
	/**
	 * @param pos
	 * @return
	 */
	public int getBit(int pos){
		if (pos < 0 || pos > this.size) throw new IllegalArgumentException("index out of range");
		int b = (pos)/8;
		int temp = pos;
		while(temp >= 8) temp -= 8;
		int posbit = 7 - temp;
		
		return bit.getBit(this.data[b],posbit);
	}

	/**
	 * @return
	 */
	public int byteLength(){
		if (this.size % 8 == 0)
			return  (this.size)/8;
		return  ((this.size)/8)+1;
	}
	
	/**
	 * @return
	 */
	public int length(){
		return this.size;
	}
	
	/**
	 * @return
	 */
	public byte[] getBytes(){
		byte result[] = new byte[this.byteLength()];
		System.arraycopy( this.data, 0, result, 0, result.length );
		return result;
	}
	
	/**
	 * @return
	 */
	public boolean[] toBooleanArray(){
		boolean result[] = new boolean[this.size];
		for(int i = 0;i<this.size;i++)
			result[i] = this.getBit(i) > 0 ? true:false;
		return result;
	}
	
	/**
	 * @return
	 */
	public BitSet toBitSet() {
		BitSet result = new BitSet(this.size);
		for(int i = 0;i<this.size;i++)
			result.set(i, this.getBit(i) > 0 ? true:false);
		return result;
	}
}
