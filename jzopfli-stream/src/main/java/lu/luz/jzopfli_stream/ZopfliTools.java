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


import java.io.IOException;
import java.io.OutputStream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

public final class ZopfliTools {
	private static final int BUFFER_SIZE = 8192;

	private ZopfliTools(){}

	@SuppressWarnings("resource")//do not close streams we did not open
	public static void recompress(ZipInputStream zis, OutputStream os) throws IOException{
		ZopfliOutputStream zos=new ZopfliOutputStream(os);
		byte[] buffer=new byte[BUFFER_SIZE];
		ZipEntry entry;
		int read;
		while( (entry=zis.getNextEntry()) !=null ){
			zos.putNextEntry(entry);
			while((read=zis.read(buffer))!=-1)
				zos.write(buffer, 0, read);
		}
		zos.finish();
	}
}
