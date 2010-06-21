package org.apache.lucene.util;

import java.io.IOException;
import java.util.BitSet;
import org.apache.lucene.search.DocIdSet;
import org.apache.lucene.search.DocIdSetIterator;

/**存储和迭代在内存中压缩过的排序的数字
 * Store and iterate sorted integers in compressed form in RAM. <br>
 * The code for compressing the differences between ascending integers was
 * borrowed from {@link org.apache.lucene.store.IndexInput} and
 * {@link org.apache.lucene.store.IndexOutput}.
 */
public class SortedVIntList extends DocIdSet {
	/**
	 * When a BitSet has fewer than 1 in BITS2VINTLIST_SIZE bits set, a
	 * SortedVIntList representing the index numbers of the set bits will be
	 * smaller than that BitSet.
	 */
	/**
	 * BitSet.size()小于1时，SortedVIntList重置集合中索引数字小于BitSet.size()
	 */
	final static int BITS2VINTLIST_SIZE = 8;

	private int size;
	private byte[] bytes;
	private int lastBytePos;

	/**
	 * Create a SortedVIntList from all elements of an array of integers.
	 * @param sortedInts  A sorted array of non negative integers.
	 */
	public SortedVIntList(int[] sortedInts) {
		this(sortedInts, sortedInts.length);
	}

	/**
	 * Create a SortedVIntList from an array of integers.
	 * @param sortedInts  An array of sorted non negative integers.
	 * @param inputSize   The number of integers to be used from the array.
	 */
	public SortedVIntList(int[] sortedInts, int inputSize) {
		SortedVIntListBuilder builder = new SortedVIntListBuilder();
		for (int i = 0; i < inputSize; i++) {
			builder.addInt(sortedInts[i]);
		}
		builder.done();
	}

	/**
	 * Create a SortedVIntList from a BitSet.
	 * @param bits   A bit set representing a set of integers.
	 */
	public SortedVIntList(BitSet bits) {
		SortedVIntListBuilder builder = new SortedVIntListBuilder();
		int nextInt = bits.nextSetBit(0);
		while (nextInt != -1) {
			builder.addInt(nextInt);
			nextInt = bits.nextSetBit(nextInt + 1);
		}
		builder.done();
	}

	/**
	 * Create a SortedVIntList from an OpenBitSet.
	 * 
	 * @param bits
	 *            A bit set representing a set of integers.
	 */
	public SortedVIntList(OpenBitSet bits) {
		SortedVIntListBuilder builder = new SortedVIntListBuilder();
		int nextInt = bits.nextSetBit(0);
		while (nextInt != -1) {
			builder.addInt(nextInt);
			nextInt = bits.nextSetBit(nextInt + 1);
		}
		builder.done();
	}

	/**
	 * Create a SortedVIntList.
	 * 
	 * @param docIdSetIterator
	 *            An iterator providing document numbers as a set of integers.
	 *            This DocIdSetIterator is iterated completely when this
	 *            constructor is called and it must provide the integers in non
	 *            decreasing order.
	 */
	public SortedVIntList(DocIdSetIterator docIdSetIterator) throws IOException {
		SortedVIntListBuilder builder = new SortedVIntListBuilder();
		while (docIdSetIterator.next()) {
			builder.addInt(docIdSetIterator.doc());
		}
		builder.done();
	}

	private class SortedVIntListBuilder {
		private int lastInt = 0;

		SortedVIntListBuilder() {
			initBytes();
			lastInt = 0;
		}

		void addInt(int nextInt) {
			int diff = nextInt - lastInt;
			if (diff < 0) {
				throw new IllegalArgumentException(
						"Input not sorted or first element negative.");
			}

			if ((lastBytePos + MAX_BYTES_PER_INT) > bytes.length) {
				// biggest possible int does not fit
				resizeBytes((bytes.length * 2) + MAX_BYTES_PER_INT);
			}

			// See org.apache.lucene.store.IndexOutput.writeVInt()
			while ((diff & ~VB1) != 0) { // The high bit of the next byte
											// needs to be set.
				bytes[lastBytePos++] = (byte) ((diff & VB1) | ~VB1);
				diff >>>= BIT_SHIFT;
			}
			bytes[lastBytePos++] = (byte) diff; // Last byte, high bit not set.
			size++;
			lastInt = nextInt;
		}

		void done() {
			resizeBytes(lastBytePos);
		}
	}

	/**
	 * 此方法描述的是：初始化
	 * @version 创建时间：Nov 14, 2009 1:15:12 PM 
	 * void
	 */
	private void initBytes() {
		size = 0;
		bytes = new byte[128]; // initial byte size
		lastBytePos = 0;
	}

	private void resizeBytes(int newSize) {
		if (newSize != bytes.length) {
			byte[] newBytes = new byte[newSize];
			System.arraycopy(bytes, 0, newBytes, 0, lastBytePos);
			bytes = newBytes;
		}
	}

	private static final int VB1 = 0x7F;
	private static final int BIT_SHIFT = 7;
	private final int MAX_BYTES_PER_INT = (31 / BIT_SHIFT) + 1;

	/**
	 * @return The total number of sorted integers.
	 */
	public int size() {
		return size;
	}

	/**
	 * @return The size of the byte array storing the compressed sorted
	 *         integers.
	 */
	public int getByteSize() {
		return bytes.length;
	}

	/**
	 * @return An iterator over the sorted integers.
	 */
	public DocIdSetIterator iterator() {
		return new DocIdSetIterator() {
			int bytePos = 0;
			int lastInt = 0;

			private void advance() {
				// See org.apache.lucene.store.IndexInput.readVInt()
				byte b = bytes[bytePos++];
				lastInt += b & VB1;
				for (int s = BIT_SHIFT; (b & ~VB1) != 0; s += BIT_SHIFT) {
					b = bytes[bytePos++];
					lastInt += (b & VB1) << s;
				}
			}

			public int doc() {
				return lastInt;
			}

			public boolean next() {
				if (bytePos >= lastBytePos) {
					return false;
				} else {
					advance();
					return true;
				}
			}

			public boolean skipTo(int docNr) {
				while (bytePos < lastBytePos) {
					advance();
					if (lastInt >= docNr) { // No skipping to docNr available.
						return true;
					}
				}
				return false;
			}
		};
	}
}
