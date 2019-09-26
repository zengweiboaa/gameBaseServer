package com.angke.common.serializable;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.Arrays;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 使用java底层原生序列化。需要序列化的对象及属性需要完全实现Serializable接口
 * 序列化完整，效率偏低，占用空间大
 * @author LiangShengxian
 */
public class JavaSerializable {

	private static final Logger logger = LoggerFactory.getLogger(JavaSerializable.class);
	
	/**
	 * JavaObj转换为序列化二进制对象
	 * @param object 实现了Serializable 接口的对象
	 * @return
	 */
	public static byte[] writeToByteArray(Serializable object) {
		
		long startTime = System.currentTimeMillis();
		
		ByteArrayOutputStream baos = null;
		ObjectOutputStream oos = null;
		byte[] bytes = null;
		try {
			// 序列化
			baos = new ByteArrayOutputStream();
			oos = new ObjectOutputStream(baos);
			oos.writeObject(object);
			bytes = baos.toByteArray();
			return bytes;
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				oos.close();
				baos.close();
//				logger.info("序列化 Obj:" + object.getClass().getSimpleName());
//				logger.info("Finished ByteArr: " + Arrays.toString(bytes));
//				logger.info("Serializable Finished used time :" + (System.currentTimeMillis() - startTime) + " ms");
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		
		
		return bytes;
	}

	/**
	 * 反序列化，二进制数据转换为javaObj
	 * @param bytes
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public static <T> T readFromByteArrayToObj(byte[] byteArray) {
		ByteArrayInputStream bais = null;
		ObjectInputStream ois = null;
		try {
			// 反序列化
			bais = new ByteArrayInputStream(byteArray);
			ois = new ObjectInputStream(bais);
			return (T)ois.readObject();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				ois.close();
				bais.close();
			} catch (IOException e1) {
				e1.printStackTrace();
			}
		}
		return null;
	}
}
