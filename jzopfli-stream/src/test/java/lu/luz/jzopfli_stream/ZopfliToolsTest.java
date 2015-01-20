package lu.luz.jzopfli_stream;

import static org.junit.Assert.assertArrayEquals;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import java.util.zip.ZipOutputStream;

import lu.luz.jzopfli_stream.ZopfliTools;

import org.junit.Test;

public class ZopfliToolsTest
{
	private static byte[] data1=TestUtils.newByteArray(980);
	private static byte[] data2=TestUtils.newByteArray(40000);
	private static byte[] data3=TestUtils.newByteArray(60000);


	@Test
	public void test() throws Exception {
		ByteArrayOutputStream bos=new ByteArrayOutputStream();
		ZipOutputStream zos=new ZipOutputStream(bos);
		zos.putNextEntry(new ZipEntry("data1"));
		zos.write(data1);
		zos.putNextEntry(new ZipEntry("data2"));
		zos.write(data2);
		zos.putNextEntry(new ZipEntry("data3"));
		zos.write(data3);
		zos.close();
		byte[] zip= bos.toByteArray();

		byte[] zopfli;
		try(ZipInputStream zis=new ZipInputStream(new ByteArrayInputStream(zip));
				ByteArrayOutputStream bos2=new ByteArrayOutputStream();	){
			ZopfliTools.recompress(zis, bos2);
			zopfli= bos2.toByteArray();
		}
		System.out.println("diff "+(zopfli.length-zip.length));

		ZipInputStream zis=new ZipInputStream(new ByteArrayInputStream(zopfli));
		assertArrayEquals(data1,TestUtils.decompressEntry(zis));
		assertArrayEquals(data2,TestUtils.decompressEntry(zis));
		assertArrayEquals(data3,TestUtils.decompressEntry(zis));


	}
}
