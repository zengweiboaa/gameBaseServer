package com.angke.game.model;

public class BitStates extends Number implements BaseBitState {

	protected long value = 0;

	public BitStates() {
	}

	public BitStates(long value) {
		this();
		this.value = value;
	}

	@Override
	public long getStatesValue() {
		return value;
	}

	@Override
	public BaseBitState setStates(long newValue, boolean reSetStatesValue) {
		if (reSetStatesValue) {
			this.value = newValue;
		}
		return this;
	}

	@Override
	public int getBitSize() {
		int size = 0;
		long valueBuf = this.value + 0;
		while (valueBuf > 0) {
			valueBuf = valueBuf & (valueBuf - 1);
			size++;
		}
		return size;
	}

	@Override
	public boolean hasState(long value) {
		return (this.value & value) == value;
	}

	@Override
	public boolean hasState(long... values) {
		boolean result = true;
		for (int i = 0; result && i < values.length; i++) {
			result = hasState(values[i]);
		}
		return result;
	}

	public boolean dontHaveStates(long... dontHaveBitStates) {
		boolean result = true;
		for (int i = 0; result && i < dontHaveBitStates.length; i++) {
			result = !hasState(dontHaveBitStates[i]);
		}
		return result;
	}

	@Override
	public BaseBitState addState(long value) {
		long lodValue = this.value;
		if (hasState(value)) {
			return this;
		}
		this.value = (this.value | value);
		System.out.println("************* 进行单状态[" + value + "]添加操作,状态添加前为: " + lodValue + "   操作后参数为: tableStates:"
				+ this.value + "      房间是否满员:" + this.hasState(512L) + "  *************");
		if (lodValue == 0 && this.value == 64) {
		}
		return this;
	}

	@Override
	public BaseBitState addState(long... values) {
		long lodValue = this.value;
		boolean hasFull = this.hasState(512L);
		for (int i = 0; i < values.length; i++) {
			addState(values[i]);
		}
		System.out.println("************* 进行批量状态[" + values.toString() + "]添加操作,状态添加前为: " + lodValue
				+ "   操作后参数为: tableStates:" + this.value + "      房间是否满员:" + this.hasState(512L) + "  *************");
		if (hasFull && !this.hasState(512L)) {
			try {
				throw new Exception();
			} catch (Exception e) {
				e.printStackTrace();
			}
			System.out.println("满员状态被清除");
		}
		return this;
	}

	@Override
	public BaseBitState removeState(long value) {
		long lodValue = this.value;
		if (!hasState(value)) {
			return this;
		}
		this.value = (this.value ^ value);
		System.out.println("************* 进行单状态[" + value + "]删除操作,状态添加前为: " + lodValue + "   操作后参数为: tableStates:"
				+ this.value + "      房间是否满员:" + this.hasState(512L) + "  *************");
		return this;
	}

	@Override
	public BaseBitState removeState(long... values) {
		long lodValue = this.value;
		for (int i = 0; i < values.length; i++) {
			removeState(values[i]);
		}
		System.out.println("************* 进行批量状态[" + values.toString() + "]删除操作,状态添加前为: " + lodValue
				+ "   操作后参数为: tableStates:" + this.value + "      房间是否满员:" + this.hasState(512L) + "  *************");
		return this;
	}

	/**
	 * Returns the value of this {@code Long} as a {@code byte}.
	 */
	public byte byteValue() {
		return ((Long) value).byteValue();
	}

	/**
	 * Returns the value of this {@code Long} as a {@code short}.
	 */
	public short shortValue() {
		return ((Long) value).shortValue();
	}

	/**
	 * Returns the value of this {@code Long} as an {@code int}.
	 */
	public int intValue() {
		return ((Long) value).intValue();
	}

	/**
	 * Returns the value of this {@code Long} as a {@code long} value.
	 */
	public long longValue() {
		return ((Long) value).longValue();
	}

	/**
	 * Returns the value of this {@code Long} as a {@code float}.
	 */
	public float floatValue() {
		return ((Long) value).floatValue();
	}

	/**
	 * Returns the value of this {@code Long} as a {@code double}.
	 */
	public double doubleValue() {
		return ((Long) value).doubleValue();
	}

	public boolean equals(Object obj) {
		if (obj instanceof BitStates) {
			return value == ((BitStates) obj).longValue();
		}
		return false;
	}

	public static BitStates valueOf(long l) {
		final int offset = 128;
		if (l >= -128 && l <= 127) { // will cache
			return BitStatesCache.cache[(int) l + offset];
		}
		return new BitStates(l);
	}

	private static class BitStatesCache {
		private BitStatesCache() {
		}

		static final BitStates cache[] = new BitStates[-(-128) + 127 + 1];

		static {
			for (int i = 0; i < cache.length; i++)
				cache[i] = new BitStates(i - 128);
		}
	}

	/**	 */
	private static final long serialVersionUID = 6602900155358335396L;

}
