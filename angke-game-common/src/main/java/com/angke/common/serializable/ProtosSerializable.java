package com.angke.common.serializable;

import java.util.Arrays;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.protostuff.LinkedBuffer;
import io.protostuff.ProtobufIOUtil;
import io.protostuff.runtime.RuntimeSchema;

public class ProtosSerializable {

	private static final Logger logger = LoggerFactory.getLogger(JavaSerializable.class);
	
	public static <T> byte[] writeToByteArray(T t) {

		long startTime = System.currentTimeMillis();
		
		@SuppressWarnings("unchecked")
		Class<T> clazz = (Class<T>) t.getClass();
		byte[] clazzArr = JavaSerializable.writeToByteArray(clazz);

		byte[] byteArray = ProtobufIOUtil.toByteArray(t, RuntimeSchema.createFrom(clazz),
				LinkedBuffer.allocate(LinkedBuffer.DEFAULT_BUFFER_SIZE));

		byte[] result = new byte[1 + clazzArr.length + byteArray.length];
		result[0] = (byte) (((short) clazzArr.length) & 0xff);
		System.arraycopy(clazzArr, 0, result, 1, clazzArr.length);
		System.arraycopy(byteArray, 0, result, clazzArr.length + 1, byteArray.length);
		
		logger.info("序列化 Obj:" + t.getClass().getSimpleName());
		logger.info("Finished ByteArr: " + Arrays.toString(result));
		logger.info("Serializable Finished used time :" + (System.currentTimeMillis() - startTime) + " ms");
		
		return result;

	}

	public static <T> T readFromByteArrayToObj(byte[] data) {

		byte low = data[0];
		short s0 = (short) (low & 0xff);
		short length = (short) (s0);

		byte[] clazzArr = new byte[length];
		System.arraycopy(data, 1, clazzArr, 0, clazzArr.length);

		@SuppressWarnings("unchecked")
		Class<T> clazz = (Class<T>) JavaSerializable.readFromByteArrayToObj(clazzArr);

		byte[] objArr = new byte[data.length - 1 - length];
		System.arraycopy(data, 1 + length, objArr, 0, objArr.length);

		RuntimeSchema<T> runtimeSchema = RuntimeSchema.createFrom(clazz);
		T t = runtimeSchema.newMessage();
		ProtobufIOUtil.mergeFrom(objArr, t, runtimeSchema);
		return t;
	}
}
