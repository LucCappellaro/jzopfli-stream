package lu.luz.compression_utils;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

public class TestUtils {

	private TestUtils(){}

	public static byte[] decompressZip(byte[] data) throws IOException {
		try(ZipInputStream zis=new ZipInputStream(new ByteArrayInputStream(data))){
			return decompressEntry(zis);
		}
	}

	public static byte[] decompressEntry(ZipInputStream zis) throws IOException {
		ByteArrayOutputStream bos=new ByteArrayOutputStream();
		zis.getNextEntry();
		byte[] buffer=new byte[1024];
		int read;
		while((read=zis.read(buffer))!=-1)
			bos.write(buffer, 0, read);
		return bos.toByteArray();
	}

	public static byte[] compressZopfliZip(byte[] in, Integer blocksize, Integer windowSize) throws IOException {
		ByteArrayOutputStream bos=new ByteArrayOutputStream();
		ZopfliOutputStream zos;
		if(blocksize==null && windowSize==null)
			zos=new ZopfliOutputStream(bos);
		else
			zos=new ZopfliOutputStream(bos, blocksize, windowSize);
		zos.putNextEntry(new ZipEntry("file"));
		zos.write(in);
		zos.close();
		byte[] result= bos.toByteArray();
		//Files.copy(new ByteArrayInputStream(ouput), Paths.get("max.zip"), StandardCopyOption.REPLACE_EXISTING);
		return result;
	}

	public static byte[] newByteArray(int length) {
		byte[] array = new byte[length];

		try(FileInputStream fis=new FileInputStream("src/test/resources/1musk10.txt")){
			fis.read(array);
		}catch(IOException e){
		}
		//new Random().nextBytes(array);
		//for (int i = 0; i < array.length; i++)
		//	array[i]=(byte)i;
		return array;
	}
}
