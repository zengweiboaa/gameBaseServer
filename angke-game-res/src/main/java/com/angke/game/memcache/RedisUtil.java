package com.angke.game.memcache;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.angke.common.config.GlobalConfig;
import com.angke.common.constant.GlobalConfigNames;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;
import redis.clients.jedis.exceptions.JedisConnectionException;

public class RedisUtil {

	/**
	 * 可用连接实例的最大数目，默认值为8;
	 * 如果赋值为-1，则表示不限制；如果pool已经分配了maxTotal个jedis实例，则此时pool的状态为exhausted(耗尽)。
	 */
	private static int MAX_TOTAL = 100;

	/** 控制一个pool最多有多少个状态为idle(空闲的)的jedis实例，默认值也是8。 */
	private static int MAX_IDLE = 20;

	/** 等待可用连接的最大时间,单位毫秒,默认值为-1,表示永不超时。如果超过等待时间,则直接抛出JedisConnectionException; */
	private static int MAX_WAIT = 20000;

	/** 超时时间 毫秒 */
	private static int TIMEOUT = 20000;

	/** Redis连接池 */
	private static JedisPool jedisPool = null;

	/**
	 * 初始化Redis连接池
	 */
	static {
		try {
			JedisPoolConfig config = new JedisPoolConfig();
			/** 连接耗尽时是否阻塞, false报异常,ture阻塞直到超时, 默认true */
			config.setBlockWhenExhausted(true);
			/** 设置的逐出策略类名, 默认DefaultEvictionPolicy(当连接超过最大空闲时间,或连接数超过最大空闲连接数) */
			config.setEvictionPolicyClassName("org.apache.commons.pool2.impl.DefaultEvictionPolicy");
			/** 是否启用pool的jmx管理功能, 默认true */
			config.setJmxEnabled(true);
			/** 最大空闲连接数, 默认8个 控制一个pool最多有多少个状态为idle(空闲的)的jedis实例。 */
			config.setMaxIdle(MAX_IDLE);
			/** 最大连接数, 默认8个 */
			config.setMaxTotal(MAX_TOTAL);
			/** 表示当borrow(引入)一个jedis实例时，最大的等待时间，如果超过等待时间，则直接抛出JedisConnectionException; */
			config.setMaxWaitMillis(MAX_WAIT);
			/** 在borrow一个jedis实例时，是否提前进行validate操作；如果为true，则得到的jedis实例均是可用的； */
			config.setTestOnBorrow(true);
			/**  */
			config.setTestOnReturn(true);
			jedisPool = new JedisPool(config, GlobalConfig.get(GlobalConfigNames.REDIS_HOST),
					Integer.parseInt(GlobalConfig.get(GlobalConfigNames.REDIS_PORT)), TIMEOUT,
					GlobalConfig.get(GlobalConfigNames.REDIS_AUTH));
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * 获取Jedis实例
	 * 
	 * @return
	 */
	public synchronized static Jedis getInstance() {
		Jedis resource = null;
		try {
			if (jedisPool != null) {
				resource = jedisPool.getResource();
				return resource;
			} else {
				return null;
			}
		} catch (JedisConnectionException e) {
			close(resource);
			e.printStackTrace();
			return null;
		}
	}

	/**
	 * 清空所有数据
	 * 
	 * @return
	 */
	public static boolean flushAll() {
		Jedis jedis = null;
		try {
			jedis = getInstance();
			jedis.flushAll();
		} catch (Exception e) {
			close(jedis);
			e.printStackTrace();
		} finally {
			close(jedis);
		}
		return true;
	}

	/**
	 * @功能：通过Redis的key获取值，并释放连接资源
	 * @参数：key，键值 @返回： 成功返回value，失败返回null
	 */
	public static String get(String key) {
		Jedis jedis = null;
		String value = null;
		try {
			jedis = getInstance();
			value = jedis.get(key);
		} catch (Exception e) {
			close(jedis);
			e.printStackTrace();
		} finally {
			close(jedis);
		}
		return value;
	}

	public static Set<String> keys(String key) {
		Jedis jedis = null;
		Set<String> value = null;
		// String value = null;
		try {
			jedis = getInstance();
			value = jedis.keys(key);
		} catch (Exception e) {
			close(jedis);
			e.printStackTrace();
		} finally {
			close(jedis);
		}
		return value;
	}

	/**
	 * 取出序列化对象
	 * 
	 * @param key
	 * @return
	 */
	public static byte[] get(byte[] byteArrKey) {

		Jedis jedis = null;
		byte[] byteArrV = null;
		try {
			jedis = getInstance();
			byteArrV = jedis.get(byteArrKey);
		} catch (Exception e) {
			close(jedis);
			e.printStackTrace();
		} finally {
			close(jedis);
		}
		return byteArrV;

	}

	/**
	 * 以序列化的方式存值
	 * 
	 * @param byteArrKey
	 * @param byteArrValue
	 * @return 成功返回“OK”，失败返回“FAIL”
	 */
	public static String set(byte[] byteArrKey, byte[] byteArrValue) {

		Jedis jedis = null;
		String ret = "FAIL";
		try {
			jedis = getInstance();
			System.out.println("K的字节 " + byteArrKey.length);
			System.out.println("V的字节 " + byteArrValue.length);
			System.out.println(byteArrKey);
			System.out.println(byteArrValue);
			ret = jedis.set(byteArrKey, byteArrValue);
		} catch (Exception e) {
			close(jedis);
			e.printStackTrace();
		} finally {
			close(jedis);
		}
		return ret;
	}

	/**
	 * @功能：向redis存入key和value（如果key已经存在 则覆盖），并释放连接资源
	 * @参数：key，键
	 * @参数：value，与key对应的值 @返回：成功返回“OK”，失败返回“FAIL”
	 */
	public static String set(String key, String value) {
		Jedis jedis = null;
		String ret = "FAIL";
		try {
			jedis = getInstance();
			ret = jedis.set(key, value);
		} catch (Exception e) {
			close(jedis);
			e.printStackTrace();
		} finally {
			close(jedis);
		}
		return ret;
	}

	/**
	 * @功能：向redis存入具有过期时间的key和value（如果key已经存在 则覆盖），并释放连接资源
	 * @参数：key，键
	 * @参数：seconds，过期时间（秒） @参数：value，与key对应的值 @返回：成功返回“OK”，失败返回“FAIL”
	 */
	public static String setex(String key, Integer seconds, String value) {
		Jedis jedis = null;
		String ret = "FAIL";
		try {
			jedis = getInstance();
			ret = jedis.setex(key, seconds, value);
		} catch (Exception e) {
			close(jedis);
			e.printStackTrace();
		} finally {
			close(jedis);
		}
		return ret;
	}

	/**
	 * @功能：通过Redis的key删除，并释放连接资源
	 * @参数：key，键值 @返回： 成功返回value，失败返回null
	 */
	public static Long del(String key) {
		Jedis jedis = null;
		Long ret = null;
		try {
			jedis = getInstance();
			ret = jedis.del(key);
		} catch (Exception e) {
			close(jedis);
			e.printStackTrace();
		} finally {
			close(jedis);
		}
		return ret;
	}

	/**
	 * @功能：通过Redis的key删除，并释放连接资源
	 * @参数：key，键值 @返回： 成功返回value，失败返回null
	 */
	public static Long del(byte[] byteArrKey) {
		Jedis jedis = null;
		Long ret = null;
		try {
			jedis = getInstance();
			ret = jedis.del(byteArrKey);
		} catch (Exception e) {
			close(jedis);
			e.printStackTrace();
		} finally {
			close(jedis);
		}
		return ret;
	}

	/**
	 * @功能：通过Redis设置key的过期时间，并释放连接资源
	 * @参数：key，键值
	 * @参数：seconds，过期秒数 @返回： 成功返回value，失败返回null
	 */
	public static Long expire(String key, int seconds) {
		Jedis jedis = null;
		Long ret = null;
		try {
			jedis = getInstance();
			ret = jedis.expire(key, seconds);
		} catch (Exception e) {
			close(jedis);
			e.printStackTrace();
		} finally {
			close(jedis);
		}
		return ret;
	}

	/**
	 * 对key对应的数字做减1操作。 如果key不存在，那么在操作之前，这个key对应的值会被置为0。
	 * 如果key有一个错误类型的value或者是一个不能表示成数字的字符串，就返回错误。 这个操作最大支持在64位有符号的整型数字。
	 * 查看命令INCR了解关于增减操作的额外信息。
	 * 
	 * @param key
	 * @return
	 */
	public static Long decr(String key) {
		Jedis jedis = null;
		Long ret = null;
		try {
			jedis = getInstance();
			ret = jedis.decr(key);
		} catch (Exception e) {
			close(jedis);
			e.printStackTrace();
		} finally {
			close(jedis);
		}
		return ret;
	}

	/**
	 * 释放jedis资源
	 * 
	 * @param jedis
	 */
	public static void close(final Jedis jedis) {
		if (jedis != null) {
			jedis.close();
		}
	}

	public static void main(String[] args) {
		Jedis jedis = RedisUtil.getInstance();
		// jedis.set("test", "liangshengxian");
		int count = 0;
		for (int i = 0; i < 10000; i++) {
			int s = (int) ((Math.random() * 9 + 1) * 100000);
			System.out.println(s);
			String string = String.valueOf(s);
			if (jedis.get(string) == null) {
				jedis.set(string, string);
			} else {
				count++;
			}
			// jedis.set("test", "liangshengxian");

		}
		System.out.println(count);
		// System.out.println(jedis.get("SMSCODE:18000534097"));
		// System.out.println(jedis.get("PLAYERSEO:edd44ee"));
		// RedisUtil.testString(jedis);
		RedisUtil.testMap(jedis);
		// RedisUtils.testList(jedis);
		// RedisUtils.testSet(jedis);
		jedis.flushAll();
		RedisUtil.close(jedis);
	}

	/**
	 * 字符串测试
	 * 
	 * @param jedis
	 */
	public static void testString(Jedis jedis) {
		jedis.set("name", "xxxx");// 向key-->name中放入了value-->xinxin
		System.out.println(jedis.get("name"));// 执行结果：xinxin

		jedis.append("name", " is my lover"); // 拼接
		System.out.println(jedis.get("name"));

		jedis.del("name"); // 删除某个键
		System.out.println(jedis.get("name"));
		// 设置多个键值对
		jedis.mset("name", "某某某", "age", "24", "qq", "476777XXX");
		jedis.incr("age"); // 进行加1操作
		System.out.println(jedis.get("name") + "-" + jedis.get("age") + "-" + jedis.get("qq"));
	}

	/**
	 * map 用法
	 * 
	 * @param jedis
	 */
	public static void testMap(Jedis jedis) {
		// -----添加数据----------
		Map<String, String> map = new HashMap<String, String>();
		map.put("name", "xinxin");
		map.put("age", "22");
		map.put("qq", "123456");
		jedis.hmset("user", map);
		// 取出user中的name，执行结果:[minxr]-->注意结果是一个泛型的List
		// 第一个参数是存入redis中map对象的key，后面跟的是放入map中的对象的key，后面的key可以跟多个，是可变参数
		List<String> rsmap = jedis.hmget("user", "name", "age", "qq");
		System.out.println(rsmap);

		// 删除map中的某个键值
		jedis.hdel("user", "age");
		System.out.println(jedis.hmget("user", "age")); // 因为删除了，所以返回的是null
		System.out.println(jedis.hlen("user")); // 返回key为user的键中存放的值的个数2
		System.out.println(jedis.exists("user"));// 是否存在key为user的记录 返回true
		System.out.println(jedis.hkeys("user"));// 返回map对象中的所有key
		System.out.println(jedis.hvals("user"));// 返回map对象中的所有value

		Iterator<String> iter = jedis.hkeys("user").iterator();
		while (iter.hasNext()) {
			String key = iter.next();
			System.out.println(key + ":" + jedis.hmget("user", key));
		}
	}

	/**
	 * jedis操作List
	 */
	public static void testList(Jedis jedis) {
		// 开始前，先移除所有的内容
		jedis.del("java framework");
		System.out.println(jedis.lrange("java framework", 0, -1));
		// 先向key java framework中存放三条数据
		jedis.lpush("java framework", "spring");
		jedis.lpush("java framework", "struts");
		jedis.lpush("java framework", "hibernate");
		// 再取出所有数据jedis.lrange是按范围取出，
		// 第一个是key，第二个是起始位置，第三个是结束位置，jedis.llen获取长度 -1表示取得所有
		System.out.println(jedis.lrange("java framework", 0, -1));

		jedis.del("java framework");
		jedis.rpush("java framework", "spring");
		jedis.rpush("java framework", "struts");
		jedis.rpush("java framework", "hibernate");
		System.out.println(jedis.lrange("java framework", 0, -1));
	}

	/**
	 * jedis操作Set
	 */
	public static void testSet(Jedis jedis) {
		// 添加
		jedis.sadd("user", "liuling");
		jedis.sadd("user", "xinxin");
		jedis.sadd("user", "ling");
		jedis.sadd("user", "zhangxinxin");
		jedis.sadd("user", "who");
		// 移除noname
		jedis.srem("user", "who");
		System.out.println(jedis.smembers("user"));// 获取所有加入的value
		System.out.println(jedis.sismember("user", "who"));// 判断 who
															// 是否是user集合的元素
		System.out.println(jedis.srandmember("user"));
		System.out.println(jedis.scard("user"));// 返回集合的元素个数
	}
	////////////////////////////// set//////////////////////////////

	public static boolean isSetValue(String setName, String property) {

		Jedis jedis = null;
		boolean result = false;
		try {
			jedis = getInstance();
			result = jedis.sismember(setName, property);
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			close(jedis);
		}
		return result;
	}

	public static void setSet(String key, String... value) {
		Jedis jedis = null;
		try {
			jedis = getInstance();
			jedis.sadd(key, value);
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			close(jedis);
		}
	}

	public static void delValueBySet(String setName, String... values) {

		Set<String> sets = new HashSet<String>();
		sets.addAll(Arrays.asList(values));
		for (int i = 0; i < values.length; i++) {
			if (values[i] == null) {
				sets.remove(values[i]);
			}
		}
		if (sets == null || sets.size() == 0) {
			return;
		}
		Jedis jedis = null;
		try {
			jedis = getInstance();
			jedis.srem(setName, sets.toArray(new String[0]));
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			close(jedis);
		}
	}

	public static Set<String> getSetValues(String key) {
		Jedis jedis = null;
		Set<String> result = null;
		try {
			jedis = getInstance();
			result = jedis.smembers(key);
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			close(jedis);
		}
		return result;
	}

	////////////////////////////// set//////////////////////////////

	////////////////////////////// map//////////////////////////////

	public static Map<String, String> getMap(String mapName, Map<String, String> map) {
		Jedis jedis = null;
		try {
			jedis = getInstance();
			Iterator<String> iter = jedis.hkeys(mapName).iterator();
			while (iter.hasNext()) {
				String oldkey = iter.next();
				map.put(oldkey, jedis.hmget(mapName, oldkey).get(0));
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

		return map;
	}

	public static Set<String> addMap(String mapName, String key, String value) {

		Jedis jedis = null;
		Set<String> keys = null;
		Map<String, String> map = new HashMap<String, String>();
		try {
			jedis = getInstance();
			map = getMap(mapName, map);

		} catch (Exception e) {
			// close(jedis);
			e.printStackTrace();
		} finally {
			map.put(key, value);
			jedis.hmset(mapName, map);
			close(jedis);
		}
		keys = jedis.hkeys(mapName);// 返回map对象中的所有key
		return keys;
	}

	public static Set<String> batchAddMap(String mapName, String[] keys, String[] values) {

		if (keys.length != values.length) {
			throw new IllegalArgumentException("传入的键值不匹配");
		}
		Jedis jedis = null;
		Set<String> result = null;
		Map<String, String> map = new HashMap<String, String>();
		try {
			jedis = getInstance();
			map = getMap(mapName, map);

		} catch (Exception e) {
			// close(jedis);
			e.printStackTrace();
		} finally {
			for (int i = 0; i < keys.length; i++) {
				map.put(keys[i], values[i]);
			}
			jedis.hmset(mapName, map);
			close(jedis);
		}
		result = jedis.hkeys(mapName);// 返回map对象中的所有key
		return result;
	}

	public static boolean delMapOfKey(String mapName, String keyName) {
		Jedis jedis = null;
		boolean result = false;

		try {
			jedis = getInstance();
			jedis.hdel(mapName, keyName);
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			List<String> hmget = jedis.hmget(mapName, keyName);
			if (hmget == null) {// 因为删除了，所以返回的是null
				result = true;
			}
			close(jedis);
		}
		return result;
	}

	public static void delMap(String mapName) {
		Jedis jedis = null;
		try {
			jedis = getInstance();
			jedis.hdel(mapName);
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			close(jedis);
		}
	}
}
