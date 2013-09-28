package com.xjd.io.feature.in;

import java.io.FilterInputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import net.sf.cglib.proxy.Enhancer;
import net.sf.cglib.proxy.MethodInterceptor;

import com.xjd.io.feature.ProxyRuntimeException;
import com.xjd.io.feature.in.impl.BytesMatchFeatureImpl;
import com.xjd.io.feature.in.impl.CloseFeatureImpl;
import com.xjd.io.feature.in.impl.CountFeatureImpl;
import com.xjd.io.feature.in.impl.DecorateFeatureImpl;
import com.xjd.io.feature.in.impl.EOFFeatureImpl;
import com.xjd.io.feature.in.impl.FullReadFeatureImpl;

public class InputStreamProxy {

	protected static Map<Class<? extends InputStreamFeature>, Class<? extends InputStreamFeature>> inputStreamFeatures = new HashMap<Class<? extends InputStreamFeature>, Class<? extends InputStreamFeature>>();

	static {
		registerFeatureImpls();
	}

	protected static void registerFeatureImpls() {
		registerFeatureImpl(CountFeature.class, CountFeatureImpl.class);
		registerFeatureImpl(CloseFeature.class, CloseFeatureImpl.class);
		registerFeatureImpl(EOFFeature.class, EOFFeatureImpl.class);
		registerFeatureImpl(DecorateFeature.class, DecorateFeatureImpl.class);
		registerFeatureImpl(BytesMatchFeature.class, BytesMatchFeatureImpl.class);
		registerFeatureImpl(FullReadFeature.class, FullReadFeatureImpl.class);
	}

	public static <T extends InputStreamFeature, V extends T> void registerFeatureImpl(Class<T> featureClass, Class<V> featureImplClass) {
		inputStreamFeatures.put(featureClass, featureImplClass);
	}

	protected static void checkFeatures(Class<? extends InputStreamFeature>... featureClasses) {
		for (Class<? extends InputStreamFeature> featureClass : featureClasses) {
			checkFeature(featureClass);
		}
	}

	protected static void checkFeature(Class<? extends InputStreamFeature> featureClass) {
		Class<? extends InputStreamFeature> impl = inputStreamFeatures.get(featureClass);
		if (impl == null) {
			throw new ProxyRuntimeException("The feature '" + featureClass + "' is not registered.");
		}
	}
	
	public static InputStream newStage(InputStream in) {
		if (in instanceof InputStreamFeatureDelegate) {
			return new InputStreamWrapper(in);
		} else {
			return in;
		}
	}

	public static InputStream bindWithNewStage(InputStream in, Class<? extends InputStreamFeature> featureClass, Object implOrImplClass) {
		return bind(newStage(in), featureClass, implOrImplClass);
	}
	
	public static InputStream bind(InputStream in, Class<? extends InputStreamFeature> featureClass, Object implOrImplClass) {
		if (implOrImplClass instanceof Class && ((Class)implOrImplClass).isInterface()) {
			return bind(in, new Class[]{featureClass, (Class<? extends InputStreamFeature>) implOrImplClass});
		}
		
		Map<Class<? extends InputStreamFeature>, Object> map = new HashMap<Class<? extends InputStreamFeature>, Object>();
		map.put(featureClass, implOrImplClass);
		return bind(in, map);
	}

	public static InputStream bindWithNewStage(InputStream in, Class<? extends InputStreamFeature>... featureClasses) {
		return bind(newStage(in), featureClasses);
	}
	
	public static InputStream bind(InputStream in, Class<? extends InputStreamFeature>... featureClasses) {
		Map<Class<? extends InputStreamFeature>, Object> featuresMap = new LinkedHashMap<Class<? extends InputStreamFeature>, Object>();

		for (Class<? extends InputStreamFeature> clazz : featureClasses) {
			featuresMap.put(clazz, null);
		}

		return bind(in, featuresMap);
	}

	public static InputStream bindWithNewStage(InputStream in, Map<Class<? extends InputStreamFeature>, Object> featuresMap) {
		return bind(newStage(in), featuresMap);
	}
	
	public static InputStream bind(InputStream in, Map<Class<? extends InputStreamFeature>, Object> featuresMap) {
		for (Map.Entry<Class<? extends InputStreamFeature>, Object> entry : featuresMap.entrySet()) {
			if (entry.getValue() == null) {
				Class<? extends InputStreamFeature> impl = inputStreamFeatures.get(entry.getKey());
				if (impl == null) {
					throw new ProxyRuntimeException("The feature '" + entry.getKey() + "' is not registered.");
				}
				featuresMap.put(entry.getKey(), impl);
			}
		}

		InputStreamFeatureDelegate delegate;
		if (in instanceof InputStreamFeatureDelegate) {
			delegate = (InputStreamFeatureDelegate) in;
		} else {
			delegate = new InputStreamFeatureInterceptor(in);
		}

		for (Map.Entry<Class<? extends InputStreamFeature>, Object> entry : featuresMap.entrySet()) {
			delegate.addFeature(entry.getKey(), entry.getValue());
		}

		Enhancer en = new Enhancer();
		en.setSuperclass(NullInputStream.class);
		Class<? extends InputStreamFeature>[] cs = delegate.getFeatures().keySet().toArray(new Class[0]);
		List<Class<? extends InputStreamFeature>> list = new ArrayList<Class<? extends InputStreamFeature>>(cs.length + 1);
		list.addAll(Arrays.asList(cs));
		list.add(InputStreamFeatureDelegate.class);
		en.setInterfaces(list.toArray(new Class[0]));
		en.setCallback((MethodInterceptor) delegate);

		return (InputStream) en.create();
	}

	public static InputStream unbind(InputStream in, Class<? extends InputStreamFeature>... features) {
		if (in instanceof InputStreamFeatureDelegate) {
			InputStreamFeatureDelegate delegate = ((InputStreamFeatureDelegate) in).getDelegateConcrete();
			for (Class<? extends InputStreamFeature> feature : features) {
				delegate.removeFeature(feature);
			}

			Enhancer en = new Enhancer();
			en.setSuperclass(NullInputStream.class);
			Class<? extends InputStreamFeature>[] cs = delegate.getFeatures().keySet().toArray(new Class[0]);
			List<Class<? extends InputStreamFeature>> list = new ArrayList<Class<? extends InputStreamFeature>>(cs.length + 1);
			list.addAll(Arrays.asList(cs));
			list.add(InputStreamFeatureDelegate.class);
			en.setInterfaces(list.toArray(new Class[0]));
			en.setCallback((MethodInterceptor) delegate);

			return (InputStream) en.create();
		}
		return in;
	}

	protected static class NullInputStream extends FilterInputStream {
		public NullInputStream() {
			super(null);
		}
	}
	
	protected static class InputStreamWrapper extends FilterInputStream {
		public InputStreamWrapper(InputStream in) {
			super(in);
		}
		public InputStream getSource() {
			return in;
		}
	}
}
