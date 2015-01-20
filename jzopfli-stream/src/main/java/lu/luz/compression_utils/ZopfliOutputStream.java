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


import java.io.OutputStream;
import java.util.zip.ZipOutputStream;

import lu.luz.jzopfli.UtilH;

public final class ZopfliOutputStream extends ZipOutputStream{

	public ZopfliOutputStream(OutputStream out) {
		this(out, UtilH.ZOPFLI_MASTER_BLOCK_SIZE, UtilH.ZOPFLI_WINDOW_SIZE);
	}

	public ZopfliOutputStream(OutputStream out, int blocksize) {
		this(out, blocksize, UtilH.ZOPFLI_WINDOW_SIZE);
	}

	ZopfliOutputStream(OutputStream out, int blocksize, int windowSize) {
		super(out);
		this.def=new ZopfliDeflater(blocksize, windowSize);
	}
}
