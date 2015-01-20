package lu.luz.compression_utils;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Before;
import org.junit.Test;

public class InBufferTest
{
	private InBuffer buffer;
	private static final int WINDOW=2, BLOCK=3;

	@Before
	public void init(){
		buffer=new InBuffer(WINDOW, BLOCK);
	}

	@Test
	public void testInit() throws Exception {
		assertEquals(0,buffer.getStart());
		assertEquals(0,buffer.getReadLength());
		assertEquals(0,buffer.getBytesRead());
		assertTrue(buffer.hasSpaceLeft());
		assertTrue(buffer.isOneBlock());
	}

	@Test
	public void testAddComponent() throws Exception {
		byte[] in = {1};

		buffer.add(in, 0, in.length);
		assertEquals(0,buffer.getStart());
		assertEquals(in.length,buffer.getReadLength());
		assertEquals(0,buffer.getBytesRead());
		assertTrue(buffer.hasSpaceLeft());
		assertTrue(buffer.isOneBlock());
		buffer.read(false);
		assertEquals(1,buffer.getStart());
		assertEquals(0,buffer.getReadLength());
		assertEquals(1,buffer.getBytesRead());
		assertTrue(buffer.hasSpaceLeft());
		assertTrue(buffer.isOneBlock());

		buffer.add(in, 0, in.length);
		assertEquals(1,buffer.getStart());
		assertEquals(in.length,buffer.getReadLength());
		assertEquals(1,buffer.getBytesRead());
		assertTrue(buffer.hasSpaceLeft());
		assertTrue(buffer.isOneBlock());
		buffer.read(false);
		assertEquals(2,buffer.getStart());
		assertEquals(0,buffer.getReadLength());
		assertEquals(2,buffer.getBytesRead());
		assertTrue(buffer.hasSpaceLeft());
		assertTrue(buffer.isOneBlock());

		buffer.add(in, 0, in.length);
		assertEquals(WINDOW,buffer.getStart());
		assertEquals(in.length,buffer.getReadLength());
		assertEquals(2,buffer.getBytesRead());
		assertTrue(buffer.hasSpaceLeft());
		assertTrue(buffer.isOneBlock());
		buffer.read(false);
		assertEquals(WINDOW,buffer.getStart());
		assertEquals(0,buffer.getReadLength());
		assertEquals(3,buffer.getBytesRead());
		assertTrue(buffer.hasSpaceLeft());
		assertTrue(buffer.isOneBlock());

		in = new byte[BLOCK+1];
		buffer.add(in, 0, in.length);
		assertEquals(WINDOW,buffer.getStart());
		assertEquals(BLOCK,buffer.getReadLength());
		assertEquals(3,buffer.getBytesRead());
		assertFalse(buffer.hasSpaceLeft());
		assertFalse(buffer.isOneBlock());
		buffer.read(false);
		assertEquals(WINDOW,buffer.getStart());
		assertEquals(1,buffer.getReadLength());
		assertEquals(3+BLOCK,buffer.getBytesRead());
		assertTrue(buffer.hasSpaceLeft());
		assertTrue(buffer.isOneBlock());
	}

	@Test
	public void testAddComponent2() throws Exception {
		byte[] in = {1,2};
		buffer.add(in, 0, in.length);
		assertEquals(0,buffer.getStart());
		assertEquals(in.length,buffer.getReadLength());
		assertEquals(0,buffer.getBytesRead());
		assertTrue(buffer.hasSpaceLeft());
		assertTrue(buffer.isOneBlock());

		buffer.read(false);
		assertEquals(WINDOW,buffer.getStart());
		assertEquals(0,buffer.getReadLength());
		assertEquals(in.length,buffer.getBytesRead());
		assertTrue(buffer.hasSpaceLeft());
		assertTrue(buffer.isOneBlock());
	}

	@Test
	public void testAddComponent3() throws Exception {
		byte[] in1 = {1};
		buffer.add(in1, 0, in1.length);
		byte[] in2 = {2};
		buffer.add(in2, 0, in2.length);
		assertEquals(0,buffer.getStart());
		assertEquals(2,buffer.getReadLength());
		assertEquals(0,buffer.getBytesRead());
		assertTrue(buffer.hasSpaceLeft());
		assertTrue(buffer.isOneBlock());
	}

	@Test
	public void testAddComponent4() throws Exception {
		byte[] in = {1,2};
		buffer.add(in, 0, 1);
		assertEquals(0,buffer.getStart());
		assertEquals(1,buffer.getReadLength());
		assertEquals(0,buffer.getBytesRead());
		assertTrue(buffer.hasSpaceLeft());
		assertTrue(buffer.isOneBlock());
	}

	@Test
	public void testAddComponent5() throws Exception {
		byte[] in = new byte[BLOCK+1];
		buffer.add(in, 0, in.length);
		assertEquals(0,buffer.getStart());
		assertEquals(BLOCK,buffer.getReadLength());
		assertEquals(0,buffer.getBytesRead());
		assertTrue(buffer.hasSpaceLeft());
		assertFalse(buffer.isOneBlock());
	}

	@Test
	public void testAddComponent6() throws Exception {
		byte[] in = new byte[]{1,1,2,2,2,3,3,3,4,4,4};
		buffer.add(in, 0, in.length);
		assertEquals(0,buffer.getStart());
		assertEquals(BLOCK,buffer.getReadLength());
		assertEquals(0*BLOCK,buffer.getBytesRead());
		assertFalse(buffer.hasSpaceLeft());
		assertFalse(buffer.isOneBlock());

		buffer.read(false);
		assertEquals(WINDOW,buffer.getStart());
		assertEquals(BLOCK,buffer.getReadLength());
		assertEquals(1*BLOCK,buffer.getBytesRead());
		assertFalse(buffer.hasSpaceLeft());
		assertFalse(buffer.isOneBlock());

		buffer.read(false);
		assertEquals(WINDOW,buffer.getStart());
		assertEquals(BLOCK,buffer.getReadLength());
		assertEquals(2*BLOCK,buffer.getBytesRead());
		assertFalse(buffer.hasSpaceLeft());
		assertFalse(buffer.isOneBlock());

		buffer.read(false);
		assertEquals(WINDOW,buffer.getStart());
		assertEquals(2,buffer.getReadLength());
		assertEquals(3*BLOCK,buffer.getBytesRead());
		assertTrue(buffer.hasSpaceLeft());
		assertTrue(buffer.isOneBlock());
	}
}