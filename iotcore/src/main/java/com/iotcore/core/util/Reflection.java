/**
 * 
 */
package com.iotcore.core.util;

import java.lang.annotation.Annotation;
import java.lang.reflect.Array;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
@author <a href="mailto:garciadjx@gmail.com">J.M. Garcia</a>
 *
 */
public class Reflection {

	private static final Logger LOG = LoggerFactory.getLogger(Reflection.class);
	
	/**
	 * Creates an instance of the given class
	 * @param <T> 
	 * @param clazz
	 * @return
	 * @throws InstantiationException 
	 */
	public static <T> T newInstance(Class<T> clazz) throws InstantiationException  {
		Constructor<T> ctor;
		try {
			ctor = clazz.getConstructor();
			return ctor.newInstance((Object[])null);
		} catch (Exception e) {
			InstantiationException ie = new InstantiationException(e.getMessage());
			ie.initCause(e);
			throw ie;
		}
	}
	
	/**
	 * Creates an instance of the given class
	 * @param <T> 
	 * @param clazz
	 * @param initArgs
	 * @return
	 * @throws InstantiationException 
	 */
	public static <T> T newInstance(Class<T> clazz, Object ...initArgs) throws InstantiationException {
		List<Class<?>> argsClasses = new ArrayList<Class<?>>();
		for (Object arg:initArgs) {
			argsClasses.add(arg.getClass());
		}
		
		Constructor<T> ctor = null;
		try {
			ctor = clazz.getConstructor(argsClasses.toArray(new Class<?>[0]));
			return ctor.newInstance(initArgs);
		} catch (NoSuchMethodException | SecurityException | IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
			InstantiationException ie = new InstantiationException(e.getMessage());
			ie.initCause(e);
			throw ie;
		}
		
	}
	
	/**
	 * @param instance
	 * @param methodName
	 * @return
	 * @throws NoSuchMethodException
	 * @throws SecurityException
	 * @throws IllegalAccessException
	 * @throws IllegalArgumentException
	 * @throws InvocationTargetException
	 */
	public static Object callMethod(Object instance, String methodName) throws NoSuchMethodException, SecurityException, IllegalAccessException, IllegalArgumentException, InvocationTargetException  {
		Class<?> clazz = instance.getClass();
		Method m = clazz.getMethod(methodName, (Class<?>[])null);
		return m.invoke(instance,(Object[])null);
	}
	
	/**
	 * @param instance
	 * @param methodName
	 * @param args
	 * @return
	 * @throws NoSuchMethodException
	 * @throws SecurityException
	 * @throws IllegalAccessException
	 * @throws IllegalArgumentException
	 * @throws InvocationTargetException
	 */
	public static Object callMethod(Object instance, String methodName, Object ... args) throws NoSuchMethodException, SecurityException, IllegalAccessException, IllegalArgumentException, InvocationTargetException  {
		
		Class<?> clazz = instance.getClass();
		Class<?>[] argClass = new Class<?>[args.length];
		for (int i=0;i<args.length;i++) {
			argClass[i] = args[i].getClass();
		}
		Method m = clazz.getMethod(methodName, argClass);
		return m.invoke(instance, args);
	}
	
	/**
	 * @param instance
	 * @param methodName
	 * @return
	 */
	public static Object tryCallMethod(Object instance, String methodName) {
		Object callRes = null;
		Class<?> clazz = instance.getClass();
		Method m;
		try {
			m = clazz.getMethod(methodName, (Class<?>[])null);
			callRes = m.invoke(instance,(Object[])null);
		} catch (NoSuchMethodException | SecurityException e) {
			
		} catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
			
		}
		
		return callRes;
	}
	
	
	/**
	 * @param <T> 
	 * @param instance
	 * @param methodName
	 * @param resultClass 
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public static <T> T tryCallMethod(Object instance, String methodName, Class<T> resultClass) {
		T ret = null;
		Object callRes = null;
		Class<?> clazz = instance.getClass();
		Method m;
		try {
			m = clazz.getMethod(methodName, (Class<?>[])null);
			callRes = m.invoke(instance,(Object[])null);
		} catch (NoSuchMethodException | SecurityException e) {
			
		} catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
			
		}
		
		if (callRes == null) {
			return null;
		}
		
		if (resultClass.isAssignableFrom(callRes.getClass())) {
			ret = (T)callRes;
		}
		return ret;
	}
	
	
	/**
	 * @param <T>
	 * @param type
	 * @param size
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public static <T> T[] createArray(Class<T> type, int size){
	    return (T[])Array.newInstance(type, size);
	}

	
	/**
	 * Get the actual annotation of the given class.
	 * Matching is done by looking at the annotation class names to allow this method to operate with proxied objects.
	 * @param clazz
	 * @param annotClass
	 * @return
	 */
	public static Annotation getAnnotation(Class<?> clazz, Class<Annotation> annotClass) {
		
		Annotation ret = null;
		
		for (Annotation a:clazz.getAnnotations()) {
			
			List<String> classAnnotations = new ArrayList<String>();
			
			for (Class<?> iface : a.getClass().getInterfaces()) {
				String name = iface.getName();
				classAnnotations.add(name);
			}
			
			if (classAnnotations.contains(annotClass.getName())) {
				ret = a;
			}
		}
		
		return ret;
	}
		
	/**
	 * Get the actual annotation of the given method.
	 * Matching is done by looking at the annotation class names to allow this method to operate with proxied objects.
	 * @param method 
	 * @param annotClass
	 * @return
	 */
	public static Annotation getAnnotation(Method method, Class<Annotation> annotClass) {
			
		Annotation ret = null;
		
		for (Annotation a:method.getAnnotations()) {
			List<String> methodAnnotations = new ArrayList<String>();
			for (Class<?> iface : a.getClass().getInterfaces()) {
				String name = iface.getName();
				methodAnnotations.add(name);
			}
			
			if (methodAnnotations.contains(annotClass.getName())) {
				LOG.trace("Annotation found");
				ret = a;
				break;
			}
		}
		
		return ret;
	}
	
	/**
	 * Get the actual annotation of the given reflection object (Class, Method, Field, or Parameter instance).
	 * Matching is done by looking at the annotation class names to allow this method to operate with proxied objects.
	 * @param obj Annotated instance of java.lang.Class, Method, Field, or Parameter type 
	 * @param annotClass
	 * @return
	 */
	public static Annotation getAnnotation(Object obj, Class<Annotation> annotClass) {
			
		Objects.requireNonNull(obj);
		Objects.requireNonNull(annotClass);
		
		Annotation ret = null;
		Annotation [] objAnnotations = null;
		
		if (obj instanceof Class<?>) {
			objAnnotations = ((Class<?>)obj).getAnnotations();
		}
		else if (obj instanceof Method) {
			objAnnotations = ((Method)obj).getAnnotations();
		}
		else if (obj instanceof Field) {
			objAnnotations = ((Field)obj).getAnnotations();
		}
		else if (obj instanceof Parameter) {
			objAnnotations = ((Parameter)obj).getAnnotations();
		}
		else {
			throw new IllegalArgumentException("obj is not an instance of a supported reflection type (Class, Method, Field, or Parameter)");
		}
		
		for (Annotation a:objAnnotations) {
			List<String> methodAnnotations = new ArrayList<String>();
			for (Class<?> iface : a.getClass().getInterfaces()) {
				String name = iface.getName();
				methodAnnotations.add(name);
			}
			
			if (methodAnnotations.contains(annotClass.getName())) {
				LOG.trace("Annotation found");
				ret = a;
				break;
			}
		}
		
		return ret;
	}
	
	
	
	/**
	 * @param obj
	 * @return
	 */
	public Map<String, Object> getObjectFields(Object obj) {
		
		Map<String, Object> fieldMap = new HashMap<String, Object>();
		Class<?> clazz = obj.getClass();
		Field[] fields = clazz.getDeclaredFields();
		
		for (Field f:fields) {
			try {
				Object val = f.get(obj);
				fieldMap.put(f.getName(), val);
			} catch (IllegalArgumentException | IllegalAccessException e) {
				e.printStackTrace();
			}
		}
		
		return fieldMap;
	}

}
