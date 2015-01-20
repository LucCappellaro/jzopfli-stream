package lu.luz.compression_utils;

/*
 * #%L
 * JZopfli Stream
 * %%
 * Copyright (C) 2015 Luc Cappellaro
 * %%
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * #L%
 */


final class InBuffer{
	private final int windowSize, masterBlockSize, size;
	private final byte[] array;
	private int readIndex, writeIndex, bytesRead;
	private byte[] overflowB;
	private int overflowOff, overflowLen;

	public InBuffer(int windowSize, int masterBlockSize){
		this.windowSize=windowSize;
		this.masterBlockSize=masterBlockSize;
		//buffer must include a window, to allow a dictionary to increase compression
		//buffer must be bigger that masterBlockSize to allow last Block detection
		this.size=windowSize+masterBlockSize+1;
		this.array=new byte[size];
	}

	public void add(byte[] buf, int off, int len) {
		int spaceLeft=size-writeIndex;
		if(len<=spaceLeft){
			System.arraycopy(buf, off, getArray(), writeIndex, len);
			writeIndex+=len;
			overflowB=null;
		}else{
			//fill buffer
			System.arraycopy(buf, off, getArray(), writeIndex, spaceLeft);
			writeIndex=size;
			//append overflow buffer
			overflowB=buf;
			overflowOff=off+spaceLeft;
			overflowLen=len-spaceLeft;
		}
	}

	public boolean hasSpaceLeft() {
		return writeIndex<size;
	}

	public int getBytesRead() {
		return bytesRead;
	}

	public boolean isOneBlock(){
		return writeIndex-readIndex<=masterBlockSize;
	}

	public int getStart(){
		return readIndex;
	}

	public int getEnd(){
		return getStart()+getReadLength();
	}

	int getReadLength(){
		return Math.min(masterBlockSize, writeIndex-readIndex);
	}

	public byte[] getArray() {
		return array;
	}

	public void read(boolean finalBock) {
		int readLength=getReadLength();
		bytesRead+=readLength;

		if(!finalBock){
			if(bytesRead>windowSize){ //shift window
				int shift=readIndex+readLength-windowSize;
				System.arraycopy(getArray(), shift, getArray(), 0, writeIndex-shift);
				writeIndex-=shift;
				readIndex=windowSize;
			}else{
				readIndex+=readLength;
			}

			if(overflowB!=null && overflowLen>0) //add overflow buffer
				add(overflowB, overflowOff, overflowLen);
		}
	}

	public void reset() {
		bytesRead = 0;
		writeIndex = readIndex = 0;
		overflowB = null;
	}
}
