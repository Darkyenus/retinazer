package com.darkyen.retinazer.util;

import com.badlogic.gdx.utils.IntArray;
import com.badlogic.gdx.utils.Pool.Poolable;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Arrays;

/** Bit mask of an arbitrary size. */
@SuppressWarnings("unused")
public final class Mask implements Poolable {

	private static final long[] EMPTY = new long[0];

	private long[] words = EMPTY;

	/**
	 * Sets this mask to the value of the other mask.
	 *
	 * @param other The value to set.
	 * @return {@code this} mask instance
	 */
	@NotNull
	public Mask set(@NotNull Mask other) {
		return set(other.words);
	}

	@NotNull
	public Mask set(@NotNull long[] otherWords) {
		final long[] words = this.words;
		if (words.length < otherWords.length) {
			this.words = Arrays.copyOf(otherWords, otherWords.length);
			return this;
		}
		System.arraycopy(otherWords, 0, words, 0, otherWords.length);
		Arrays.fill(words, otherWords.length, words.length, 0L);
		return this;
	}

	/** Clears all bits in this mask. */
	public void clear() {
		long[] words = this.words;
		Arrays.fill(words, 0, words.length, 0L);
	}

	/** Sets all bits in this mask that are set in the other mask. */
	public void or(@NotNull Mask other) {
		long[] words = this.words;
		final long[] otherWords = other.words;

		final int commonWords;
		if (words.length < otherWords.length) {
			commonWords = words.length;
			this.words = words = Arrays.copyOf(otherWords, otherWords.length);
		} else {
			commonWords = otherWords.length;
		}

		for (int i = 0; i < commonWords; i++) {
			words[i] |= otherWords[i];
		}
	}

	/**
	 * Clears all bits in this mask that are also in the other masks, and sets
	 * all bits in this mask that are in the other mask but not in this mask.
	 *
	 * @param other The other operand.
	 */
	public void xor(@NotNull Mask other) {
		long[] words = this.words;
		final long[] otherWords = other.words;

		final int commonWords;
		if (words.length < otherWords.length) {
			commonWords = words.length;
			this.words = words = Arrays.copyOf(otherWords, otherWords.length);
		} else {
			commonWords = otherWords.length;
		}

		for (int i = 0; i < commonWords; i++) {
			words[i] ^= otherWords[i];
		}
	}

	/**
	 * Clears all bits in this mask that are not in the other mask.
	 *
	 * @param other The other operand.
	 */
	public void and(@NotNull Mask other) {
		final long[] words = this.words;
		final long[] otherWords = other.words;
		int commonWords = Math.min(words.length, otherWords.length);

		for (int i = 0; i < commonWords; i++) {
			words[i] &= otherWords[i];
		}
		if (otherWords.length < words.length) {
			Arrays.fill(words, otherWords.length, words.length, 0L);
		}
	}

	/**
	 * Clears all the bits in this mask contained in the other mask.
	 *
	 * @param other The other operand.
	 */
	public void andNot(@NotNull Mask other) {
		final long[] words = this.words;
		final long[] otherWords = other.words;
		final int commonWords = Math.min(words.length, otherWords.length);

		for (int i = 0; i < commonWords; i++) {
			words[i] &= ~otherWords[i];
		}
	}

	/** Sets the bit at the given index in this mask. */
	public void set(int index) {
		long[] words = this.words;
		final int wordIndex = index >> 6;
		if (wordIndex >= words.length) {
			int newCapacity = Bag.capacityFor(wordIndex + 1);
			long[] newWords = new long[newCapacity];
			System.arraycopy(words, 0, newWords, 0, words.length);
			this.words = words = newWords;
		}
		// Note: index is truncated before shifting
		words[wordIndex] |= 1L << index;
	}

	/**
	 * Sets the bit at the given index in this mask.
	 *
	 * @return true iff changed
	 */
	public boolean setChanged(int index) {
		long[] words = this.words;
		final int wordIndex = index >> 6;
		if (wordIndex >= words.length) {
			int newCapacity = Bag.capacityFor(wordIndex + 1);
			long[] newWords = new long[newCapacity];
			System.arraycopy(words, 0, newWords, 0, words.length);
			this.words = words = newWords;
		}
		// Note: index is truncated before shifting
		final long bit = 1L << index;
		final boolean changed = (words[wordIndex] & bit) == 0L;
		words[wordIndex] |= bit;
		return changed;
	}

	/**
	 * Sets the bit at the given index in this mask to the given value.
	 *
	 * @param index The index of the bit.
	 * @param value The value of the bit.
	 */
	public void set(int index, boolean value) {
		if (value)
			set(index);
		else
			clear(index);
	}

	/** Clears the bit at the given index in this mask. */
	public void clear(int index) {
		int wordIndex = index >> 6;
		if (wordIndex >= words.length) {
			return;
		}
		words[wordIndex] &= ~(1L << index);
	}

	/**
	 * Clears the bit at the given index in this mask.
	 *
	 * @return true iff the bit changed
	 */
	public boolean clearChanged(int index) {
		final long[] words = this.words;
		int wordIndex = index >> 6;
		if (wordIndex >= words.length) {
			return false;
		}

		final long bit = 1L << index;
		final boolean changed = (words[wordIndex] & bit) != 0L;
		words[wordIndex] &= ~bit;
		return changed;
	}

	/**
	 * Gets the value of the bit at the given index in this mask.
	 *
	 * @param index The index of the bit.
	 * @return The value of the bit.
	 */
	public boolean get(int index) {
		int wordIndex = index >> 6;
		if (wordIndex >= words.length) {
			return false;
		}
		return (words[wordIndex] & (1L << index)) != 0L;
	}

	/**
	 * Returns the index of the set bit that is higher than or equal to the
	 * given index. Returns -1 in case no such bit exists.
	 *
	 * @param index The index to start looking from.
	 * @return The index of the next set bit.
	 */
	public int nextSetBit(int index) {
		long[] words = this.words;
		int wordIndex = index >> 6;
		if (wordIndex >= words.length) {
			return -1;
		}
		long word = words[wordIndex] & (-1L << index);
		while (true) {
			if (word != 0)
				return (wordIndex << 6) + Long.numberOfTrailingZeros(word);
			if (++wordIndex == words.length)
				return -1;
			word = words[wordIndex];
		}
	}

	/**
	 * Returns the index of the clear bit that is higher than or equal to the
	 * given index.
	 *
	 * @param index The index to start looking from.
	 * @return The index of the next clear bit.
	 */
	public int nextClearBit(int index) {
		int wordIndex = index >> 6;
		if (wordIndex >= words.length) {
			return words.length >> 6;
		}
		long word = ~words[wordIndex] & (-1L << index);
		while (true) {
			if (word != 0)
				return (wordIndex << 6) + Long.numberOfTrailingZeros(word);
			if (++wordIndex == words.length)
				return words.length << 6;
			word = ~words[wordIndex];
		}
	}

	/**
	 * Returns whether all bits of the other mask are also contained in this mask.
	 *
	 * @param other The other mask.
	 * @return Whether this mask is a superset of the other mask.
	 */
	public boolean isSupersetOf(@NotNull Mask other) {
		final long[] words = this.words;
		final long[] otherWords = other.words;
		final int commonWords = Math.min(words.length, otherWords.length);
		for (int i = commonWords, n = otherWords.length; i < n; i++)
			if (otherWords[i] != 0)
				return false;
		for (int i = 0; i < commonWords; i++)
			if ((words[i] & otherWords[i]) != otherWords[i])
				return false;
		return true;
	}

	/**
	 * Returns whether all bits of this mask are also contained in the other mask.
	 *
	 * @param other The other mask
	 * @return Whether this mask is a subset of the other mask.
	 */
	public boolean isSubsetOf(@NotNull Mask other) {
		return other.isSupersetOf(this);
	}

	/**
	 * Returns whether any bits of this mask are also contained in the other mask.
	 *
	 * @param other The other mask.
	 * @return Whether this mask intersects the other mask.
	 */
	public boolean intersects(@NotNull Mask other) {
		final long[] words = this.words;
		final long[] otherWords = other.words;
		final int commonWords = Math.min(words.length, otherWords.length);
		for (int i = 0; i < commonWords; i++)
			if ((words[i] & otherWords[i]) != 0L)
				return true;
		return false;
	}

	/** @return The number of set bits in this mask. */
	public int cardinality() {
		int cardinality = 0;
		for (long word : this.words) cardinality += Long.bitCount(word);
		return cardinality;
	}

	/**
	 * Returns the index of the highest set bit in this mask plus one.
	 *
	 * @return The length of this mask.
	 */
	public int length() {
		final long[] words = this.words;
		for (int i = words.length - 1; i >= 0; i--) {
			if (words[i] != 0L) {
				return (i << 6) + (64 - Long.numberOfLeadingZeros(words[i]));
			}
		}
		return 0;
	}

	/**
	 * Returns the indices of the set bits in this mask.
	 *
	 * @return The indices of the set bits in this mask.
	 */
	@NotNull
	public int[] getIndices() {
		int[] indices = new int[cardinality()];
		for (int i = 0, b = nextSetBit(0), n = indices.length; i < n; i++, b = nextSetBit(b + 1)) {
			indices[i] = b;
		}
		return indices;
	}

	/** Add the indices of the set bits into the given {@link IntArray}. */
	public void getIndices(@NotNull IntArray out) {
		final int offset = out.size;
		int count = cardinality();
		out.ensureCapacity(count);
		int[] items = out.items;
		for (int i = 0, b = nextSetBit(0); i < count; i++, b = nextSetBit(b + 1)) {
			items[offset + i] = b;
		}
		out.size += count;
	}

	public long getWord(int index) {
		final long[] words = this.words;
		return index < words.length ? words[index] : 0L;
	}

	public void setWord(int index, long word) {
		long[] words = this.words;
		if (index >= words.length) {
			this.words = words = Arrays.copyOf(words, Bag.capacityFor(index + 1));
		}
		words[index] = word;
	}

	/** Gets the amount of necessary words in this mask. */
	public int getWordCount() {
		final long[] words = this.words;
		for (int i = words.length - 1; i > 0; i--) {
			// Exclude trailing zero words
			if (words[i] != 0L) {
				return i;
			}
		}

		return 0;
	}

	/** Gets the backing buffer of this mask. Does not exclude trailing zero words. */
	@NotNull
	public long[] getWords() {
		return words;
	}

	/** Return true iff all all bits of the mask are zero. */
	public boolean isEmpty() {
		for (long word : this.words) {
			if (word != 0L) {
				return false;
			}
		}
		return true;
	}

	@NotNull
	@Override
	public String toString() {
		char[] value = new char[length()];
		for (int i = 0, n = value.length; i < n; i++) {
			value[n - 1 - i] = get(i) ? '1' : '0';
		}
		return new String(value);
	}

	@Override
	public boolean equals(@Nullable Object obj) {
		if (this == obj)
			return true;
		if (!(obj instanceof Mask))
			return false;
		Mask mask = (Mask) obj;
		long[] shorterWords = this.words;
		long[] longerWords = mask.words;
		if (shorterWords.length > longerWords.length) {
			shorterWords = mask.words;
			longerWords = this.words;
		}

		// Check that exceeding words are zero
		for (int i = shorterWords.length, n = longerWords.length; i < n; i++) {
			if (longerWords[i] != 0L) {
				return false;
			}
		}

		for (int i = shorterWords.length - 1; i >= 0; i--) {
			if (shorterWords[i] != longerWords[i]) {
				return false;
			}
		}

		return true;
	}

	@Override
	public int hashCode() {
		long[] words = this.words;
		int h = 0;
		// As trailing zero words do not count in the hash, this should result
		// in the same hash no matter the size of the buffer.
		for (int i = words.length - 1; i >= 0; i--) {
			long word = words[i];
			h = h * 31 + (int) (word ^ (word >>> 32));
		}
		return h;
	}

	@Override
	public void reset() {
		clear();
	}
}
