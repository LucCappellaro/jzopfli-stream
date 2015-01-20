package lu.luz.jzopfli_stream;

import static org.junit.Assert.assertArrayEquals;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.util.Arrays;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import lu.luz.jzopfli_stream.ZopfliOutputStream;

import org.junit.Test;

public class ZipEntryTest
{
	private static byte[] data1=new byte[100];
	private static byte[] data2=new byte[200];
	private static byte[] data3=new byte[300];
	static{
		Arrays.fill(data1, (byte)0x11);
		Arrays.fill(data2, (byte)0x22);
		Arrays.fill(data3, (byte)0x33);
	}

	@Test
	public void test() throws Exception {
		ByteArrayOutputStream bos=new ByteArrayOutputStream();
		ZopfliOutputStream zos=new ZopfliOutputStream(bos);
		zos.putNextEntry(new ZipEntry("data1"));
		zos.write(data1);
		zos.putNextEntry(new ZipEntry("data2"));
		zos.write(data2);
		zos.putNextEntry(new ZipEntry("data3"));
		zos.write(data3);
		zos.close();
		byte[] result= bos.toByteArray();
		//Files.copy(new ByteArrayInputStream(result), Paths.get("test.zip"), StandardCopyOption.REPLACE_EXISTING);

		ZipInputStream zis=new ZipInputStream(new ByteArrayInputStream(result));

		assertArrayEquals(data1,TestUtils.decompressEntry(zis));
		assertArrayEquals(data2,TestUtils.decompressEntry(zis));
		assertArrayEquals(data3,TestUtils.decompressEntry(zis));
	}
}
