package lu.luz.jzopfli_stream;

import static org.junit.Assert.assertArrayEquals;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.Collection;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;

@RunWith(Parameterized.class)
public class FileTest
{
	private byte[] input;

	public FileTest(String input) throws IOException {
		this.input = Files.readAllBytes(Paths.get(input));
	}

	@Parameters
	public static Collection<Object[]> data() {
		return Arrays.asList(new Object[][] {
				{"src/test/resources/100.txt"},
				{"src/test/resources/1musk10.txt"},
				{"src/test/resources/101.EXE"},
				{"src/test/resources/lena3.tif"},
		});
	}

	@Test
	public void testZip() throws Exception {
		byte[] ouput = TestUtils.compressZopfliZip(input, null, null);
		byte[] decompressed = TestUtils.decompressZip(ouput);
		assertArrayEquals(input, decompressed);
	}
}
