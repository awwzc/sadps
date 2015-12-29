package net.sk.deploy.util;

import java.beans.PropertyDescriptor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * bean,map转换工具类
 * 
 * @author 
 * 
 */
public class BeanMapUtil {

	private static Log logger = LogFactory.getLog(BeanMapUtil.class);

	/**
	 * map转换为DTO
	 * 
	 * @param <T>
	 * @param list
	 * @param beanClass
	 * @return
	 */
	public static <T> List<T> map2Bean(List<Map<Object, Object>> list, Class<T> beanClass) {

		List<T> insts = new ArrayList<T>(list.size());

		try {
			for (Map<Object, Object> map : list) {
				T inst = beanClass.newInstance();

				setMap2Bean(map, inst, beanClass);

				insts.add(inst);
			}

		} catch (Exception e) {
			logger.error(e);
		}

		return insts;
	}

	/**
	 * map转换为DTO
	 * 
	 * @param <T>
	 * @param map
	 * @param beanClass
	 * @return
	 */
	public static <T> T map2Bean(Map<Object, Object> map, Class<T> beanClass) {

		T inst = null;
		try {
			inst = beanClass.newInstance();

			setMap2Bean(map, inst, beanClass);

		} catch (Exception e) {
			logger.error(e);
		}

		return inst;
	}

	/**
	 * DTO列表转换为map，不包含空值的key
	 * 
	 * @param list
	 * @return
	 */
	public static List<Map<Object, Object>> bean2Maps(List<? extends Object> list) {
		return bean2Maps(list, false);
	}

	/**
	 * DTO列表转换为map
	 * 
	 * @param list
	 * @param havingNullValKey
	 *            是否存在空值的KEY，“真”存在，反之不存在。
	 * @return
	 */
	public static List<Map<Object, Object>> bean2Maps(List<? extends Object> list, boolean havingNullValKey) {
		List<Map<Object, Object>> insts = new ArrayList<Map<Object, Object>>(list.size());
		if (list.size() == 0) {
			return insts;
		}

		try {

			Map<Object, Object> map = null;

			for (Object inst : list) {

				map = new HashMap<Object, Object>();

				Class<?> cls = inst.getClass();

				setBean2Map(map, inst, cls, havingNullValKey);

				insts.add(map);

			}
		} catch (Exception e) {
			logger.error(e);
		}
		return insts;
	}

	/**
	 * DTO对象转换为map，不包含空值的key
	 * 
	 * @param inst
	 * @return
	 */
	public static Map<Object, Object> bean2Map(Object inst) {
		return bean2Map(inst, false);
	}

	/**
	 * DTO对象转换为map
	 * 
	 * @param inst
	 * @param havingNullValKey
	 *            是否存在空值的KEY，“真”存在，反之不存在。
	 * @return
	 */
	public static Map<Object, Object> bean2Map(Object inst, boolean havingNullValKey) {
		Map<Object, Object> map = new HashMap<Object, Object>();
		if (inst == null) {
			return map;
		}

		try {

			Class<?> cls = inst.getClass();

			setBean2Map(map, inst, cls, havingNullValKey);

		} catch (Exception e) {
			logger.error(e);
		}
		return map;
	}

	private static void setBean2Map(Map<Object, Object> map, Object obj, Class<?> cls, boolean havingNullValKey) {
		try {
			Field[] fields = cls.getDeclaredFields();
			for (Field field : fields) {
				int mod = field.getModifiers();
				if (Modifier.isFinal(mod) || Modifier.isStatic(mod) || Modifier.isNative(mod)
						|| Modifier.isSynchronized(mod) || Modifier.isTransient(mod)) {
					continue;
				}

				try {
					PropertyDescriptor pd = new PropertyDescriptor(field.getName(), cls);
					Method method = pd.getReadMethod();
					Object val = method.invoke(obj);
					if (val != null) {
						map.put(field.getName(), val);
					} else if (havingNullValKey) {
						map.put(field.getName(), null);
					}

				} catch (Exception e) {
					logger.error(e);
				}
			}

		} catch (Exception e) {
			logger.error(e);
		}

	}

	private static <T> void setMap2Bean(Map<Object, Object> map, T obj, Class<T> cls) {
		Field[] fields = cls.getDeclaredFields();
		for (Field field : fields) {
			int mod = field.getModifiers();
			if (Modifier.isFinal(mod) || Modifier.isStatic(mod) || Modifier.isNative(mod)
					|| Modifier.isSynchronized(mod) || Modifier.isTransient(mod)) {
				continue;
			}

			Object val = map.get(field.getName());
			if (val == null) {
				continue;
			}

			try {
				PropertyDescriptor pd = new PropertyDescriptor(field.getName(), obj.getClass());
				Method method = pd.getWriteMethod();

				String simpleName = field.getType().getSimpleName();
				if ("String".equals(simpleName)) {
					method.invoke(obj, val.toString());
				} else if (!val.toString().isEmpty()) {
					if ("Long".equals(simpleName) || "long".equals(simpleName)) {
						if (val instanceof Number) {
							method.invoke(obj, Long.valueOf(((Number) val).longValue()));
						} else {
							method.invoke(obj, Long.valueOf(val.toString()));
						}
					} else if ("Integer".equals(simpleName) || "int".equals(simpleName)) {
						if (val instanceof Number) {
							method.invoke(obj, Integer.valueOf(((Number) val).intValue()));
						} else {
							method.invoke(obj, Integer.valueOf(val.toString()));
						}
					} else if ("BigDecimal".equals(simpleName)) {
						if (val instanceof BigDecimal) {
							method.invoke(obj, val);
						} else {
							method.invoke(obj, new BigDecimal(val.toString()));
						}
					} else if ("Timestamp".equals(simpleName)) {
						if (val instanceof java.util.Date) {
							method.invoke(obj, new Timestamp(((java.util.Date) val).getTime()));
						}
					} else if ("java.util.Date".equals(field.getType().getName())) {
						if (val instanceof java.util.Date) {
							method.invoke(obj, val);
						}
					} else if ("java.sql.Date".equals(field.getType().getName())) {
						if (val instanceof java.util.Date) {
							method.invoke(obj, new java.sql.Date(((java.util.Date) val).getTime()));
						}
					} else {
						method.invoke(obj, val);
					}
				}
			} catch (Exception e) {
				logger.error(e);
			}

		}
	}

}