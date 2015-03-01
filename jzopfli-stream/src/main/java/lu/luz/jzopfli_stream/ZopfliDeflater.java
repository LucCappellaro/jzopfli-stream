package lu.luz.jzopfli_stream;

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


import java.util.zip.Deflater;

import lu.luz.jzopfli.Deflate;
import lu.luz.jzopfli.ZopfliH.ZopfliOptions;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

final class ZopfliDeflater extends Deflater {
	private static final Logger LOG = LoggerFactory.getLogger(ZopfliDeflater.class);

	private final ZopfliOptions options;
	private final int masterBlockSize;
	private int strategy;
	private boolean finish, finalBlock;
	private final InBuffer inBlock;
	private byte[][] outBlock;
	private final int[] outBlockSize={0};
	private int outBlockOffset;
	private final byte[] bp={0};
	private byte lastByte;
	private long bytesWritten;

	public ZopfliDeflater() {
		this(new ZopfliDeflaterOptions());
	}

	public ZopfliDeflater(ZopfliDeflaterOptions options) {
		this.options=new ZopfliOptions();
		this.options.verbose = options.isVerbose();
		this.options.verbose_more = options.isVerboseMore();
		this.options.numiterations = options.getNumIterations();
		this.options.blocksplitting = options.isBlockSplitting();
		this.options.blocksplittinglast = options.isBlockSplittingLast();
		this.options.blocksplittingmax = options.getBlockSplittingMax();
		this.strategy=options.getStrategy().getValue();
		this.masterBlockSize = options.getMasterBlockSize();
		inBlock=new InBuffer(options.getWindowSize(), masterBlockSize);
	}

	@Override
	public void setInput(byte[] b, int off, int len) {
		LOG.debug("setInput b[{}], off={}, len={}", b.length, off, len);
		inBlock.add(b, off, len);
	}

	@Override
	public boolean needsInput() {//buffer underrun
		LOG.debug("needsInput");
		return outBlockWritten() && inBlock.hasSpaceLeft();
	}

	@Override
	public int deflate(byte[] b, int off, int len, int flush) {
		LOG.debug("deflate b[{}], off={}, len={}", b.length, off, len);

		if(outBlockWritten()){
			if(inBlock.getBytesRead() == 0){ //init
				outBlock = new byte[1][masterBlockSize];
				outBlockSize[0]=0;
			}else {
				outBlock[0][0]=lastByte; //insert last byte as first of next block
				outBlockSize[0]=1;
			}
			int inStart=inBlock.getStart();
			int inEnd=inBlock.getEnd();
			finalBlock=finish && inBlock.isOneBlock();
			LOG.debug("deflate block {}-{}, f={}", inStart, inEnd, finalBlock);
			Deflate.ZopfliDeflatePart(options, strategy, finalBlock, inBlock.getArray(), inStart, inEnd, bp, outBlock, outBlockSize);
			inBlock.read(finalBlock);
			lastByte=outBlock[0][outBlockSize[0]-1];
			outBlockOffset=0;

		}
		int writeLen = outBlockSize[0]-outBlockOffset;
		if(!finalBlock) //do not write last byte, because it is not done yet
			writeLen--;
		writeLen = Math.min(writeLen, len);
		LOG.debug("copy {}, finalBlock={},  bp={}, lastByte={}", writeLen, finalBlock, bp[0], lastByte);
		System.arraycopy(outBlock[0], outBlockOffset, b, off, writeLen);
		outBlockOffset+=writeLen;
		bytesWritten+=writeLen;
		return writeLen;
	}

	@Override
	public void finish() {
		LOG.debug("finish");
		this.finish = true;
	}

	@Override
	public boolean finished() {//buffer empty
		LOG.debug("finished");
		return finalBlock && outBlockWritten();
	}

	private boolean outBlockWritten(){
		return outBlockOffset>=(outBlockSize[0]-1);
	}

	@Override
	public long getBytesRead() {
		LOG.debug("getBytesRead {}", inBlock.getBytesRead());
		return inBlock.getBytesRead();
	}

	@Override
	public long getBytesWritten() {
		LOG.debug("getBytesWritten {}", bytesWritten);
		return bytesWritten;
	}

	@Override
	public void reset() {
		LOG.debug("reset");
		finish = false;
		finalBlock = false;
		bp[0] = 0;
		inBlock.reset();
		bytesWritten = 0;
	}

	@Override
	public void end() {
		LOG.debug("end");
	}

	@Override
	public void setLevel(int level) {
		LOG.debug("setLevel");
		options.numiterations=level;
	}

	@Override
	public void setStrategy(int strategy) {
		this.strategy=strategy;
	}

	@Override
	public int getAdler() {
		throw new UnsupportedOperationException();
	}

	@Override
	public void setDictionary(byte[] b, int off, int len) {
		throw new UnsupportedOperationException();
	}
}
