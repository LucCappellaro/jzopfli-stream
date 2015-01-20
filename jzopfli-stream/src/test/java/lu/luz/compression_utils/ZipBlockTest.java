package lu.luz.compression_utils;

import static org.junit.Assert.assertArrayEquals;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameter;
import org.junit.runners.Parameterized.Parameters;

@RunWith(Parameterized.class)
public class ZipBlockTest
{
	private static byte[] input;
	static{
		try {
			input=Files.readAllBytes(Paths.get("src/test/resources/100.txt"));
		} catch (IOException e) {
			throw new RuntimeException(e.getMessage(), e);
		}
	}

	@Parameter(0)
	public int blocksize;
	@Parameter(1)
	public int windowSize;

	@Parameters(name="{0}-{1}")
	public static Collection<Object[]> params() {
		List<Object[]> params=new ArrayList<>();

		List<Integer> bs=new ArrayList<>();
		for (int b = 10; b < 120; b++)
			bs.add(b);

		List<Integer> ws=new ArrayList<>();
		ws.add(0);
		for (int w = 2; w < 120; w*=2)
			ws.add(w);

		for (int b: bs)
			for (int w: ws)
				params.add(new Object[]{b, w});

		return params;
	}

	@Test
	public void test() throws Exception {
		assertArrayEquals(blocksize+"-"+windowSize, input, TestUtils.decompressZip(TestUtils.compressZopfliZip(input, blocksize, windowSize)));
	}

}
