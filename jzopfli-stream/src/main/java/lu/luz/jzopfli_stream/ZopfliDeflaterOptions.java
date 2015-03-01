package lu.luz.jzopfli_stream;

import lu.luz.jzopfli.UtilH;

public class ZopfliDeflaterOptions {
	private boolean verbose = false;
	private boolean verboseMore = false;
	private int numIterations = 15;
	private boolean blockSplitting = true;
	private boolean blockSplittingLast = false;
	private int blockSplittingMax = 15;

	private Strategy strategy = Strategy.ZOPFLI_DYNAMIC_TREE;
	private int masterBlockSize = UtilH.ZOPFLI_MASTER_BLOCK_SIZE;
	private int windowSize = UtilH.ZOPFLI_WINDOW_SIZE;

	public boolean isVerbose() {
		return verbose;
	}

	/**
	 * Whether to print output
	 */
	public void setVerbose(boolean verbose) {
		this.verbose = verbose;
	}

	public boolean isVerboseMore() {
		return verboseMore;
	}

	/**
	 * Whether to print more detailed output
	 */
	public void setVerboseMore(boolean verboseMore) {
		this.verboseMore = verboseMore;
	}

	/**
	 * Maximum amount of times to rerun forward and backward pass to optimize
	 * LZ77 compression cost. Good values: 10, 15 for small files, 5 for files
	 * over several MB in size or it will be too slow.
	 */
	public int getNumIterations() {
		return numIterations;
	}

	public void setNumIterations(int numIterations) {
		this.numIterations = numIterations;
	}

	public boolean isBlockSplitting() {
		return blockSplitting;
	}

	/**
	 * If true, splits the data in multiple deflate blocks with optimal choice
	 * for the block boundaries. Block splitting gives better compression.
	 * Default: true (1).
	 */
	public void setBlocSplitting(boolean blocksplitting) {
		this.blockSplitting = blocksplitting;
	}

	public boolean isBlockSplittingLast() {
		return blockSplittingLast;
	}

	/**
	 * If true, chooses the optimal block split points only after doing the
	 * iterative LZ77 compression. If false, chooses the block split points
	 * first, then does iterative LZ77 on each individual block. Depending on
	 * the file, either first or last gives the best compression. Default: false
	 * (0).
	 */
	public void setBlockSplittingLast(boolean blockSplittingLast) {
		this.blockSplittingLast = blockSplittingLast;
	}

	public int getBlockSplittingMax() {
		return blockSplittingMax;
	}

	/**
	 * Maximum amount of blocks to split into (0 for unlimited, but this can
	 * give extreme results that hurt compression on some files). Default value:
	 * 15.
	 */
	public void setBlockSplittingMax(int blocksplittingmax) {
		this.blockSplittingMax = blocksplittingmax;
	}

	public Strategy getStrategy() {
		return strategy;
	}

	public void setStrategy(Strategy strategy) {
		this.strategy = strategy;
	}

	public int getMasterBlockSize() {
		return masterBlockSize;
	}

	/**
	 * A block structure of huge, non-smart, blocks to divide the input into, to
	 * allow operating on huge files without exceeding memory, such as the 1GB
	 * wiki9 corpus. The whole compression algorithm, including the smarter
	 * block splitting, will be executed independently on each huge block.
	 * Dividing into huge blocks hurts compression, but not much relative to the
	 * size. Set this to, for example, 20MB (20000000). Set it to 0 to disable
	 * master blocks.
	 */
	public void setMasterBlockSize(int masterBlockSize) {
		this.masterBlockSize = masterBlockSize;
	}

	public int getWindowSize() {
		return windowSize;
	}

	/**
	 * The window size for deflate. Must be a power of two. This should be
	 * 32768, the maximum possible by the deflate spec. Anything less hurts
	 * compression more than speed.
	 */
	public void setWindowSize(int windowSize) {
		this.windowSize = windowSize;
	}

	public enum Strategy {
		ZOPFLI_UNCOMPRESSED(0),
		ZOPFLI_FIXED_TREE(1),
		ZOPFLI_DYNAMIC_TREE(2);

		private int value;

		private Strategy(int value) {
			this.value = value;
		}

		public int getValue() {
			return value;
		}
	}
}
