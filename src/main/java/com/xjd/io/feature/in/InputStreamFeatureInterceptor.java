package com.xjd.io.feature.in;

import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Method;
import java.util.LinkedHashMap;

import net.sf.cglib.proxy.MethodInterceptor;
import net.sf.cglib.proxy.MethodProxy;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.xjd.io.feature.ProxyRuntimeException;

public class InputStreamFeatureInterceptor implements InputStreamFeatureDelegate, MethodInterceptor {
	private static Logger log = LoggerFactory.getLogger(InputStreamFeatureInterceptor.class);

	protected InputStream source;
	protected InputStream lastDelegateObject;
	LinkedHashMap<Class<? extends InputStreamFeature>, InputStreamFeature> featuresMap = new LinkedHashMap<Class<? extends InputStreamFeature>, InputStreamFeature>();

	public InputStreamFeatureInterceptor(InputStream source) {
		this.source = source;
		this.lastDelegateObject = this.source;
	}
	
	@Override
	public void setSource(InputStream source) {
		this.source = source;
	}
	
	@Override
	public InputStream getSource() {
		return source;
	}

	@Override
	public InputStreamFeatureDelegate getDelegateConcrete() {
		return this;
	}
	
	@Override
	public LinkedHashMap<Class<? extends InputStreamFeature>, InputStreamFeature> getFeatures() {
		return featuresMap;
	}

	@Override
	public boolean addFeature(Class<? extends InputStreamFeature> featureClass, Object implOrImplClass) {
		if (featureClass.isAssignableFrom(this.getClass())) { // 该对象不能被覆盖
			return false;
		}
		
		if (featuresMap.get(featureClass) != null) { //已有
			return false;
		}
		
		InputStreamFeature impl = null;
		if (implOrImplClass instanceof Class) { //Class
			try {
				impl = (InputStreamFeature) ((Class<InputStreamFeature>) implOrImplClass).getConstructor(InputStream.class).newInstance(this.lastDelegateObject);
			} catch (Exception e) {
				throw new ProxyRuntimeException(e);
			}
		} else { //Instance
			impl = (InputStreamFeature) implOrImplClass;
			((InputStreamProxySource)impl).setSource(lastDelegateObject);
		}
		
		lastDelegateObject = (InputStream) impl;
		featuresMap.put(featureClass, impl);
		return true;
	}

	@Override
	public boolean removeFeature(Class<? extends InputStreamFeature> featureClass) {
		boolean rt = false;
		Class<? extends InputStreamFeature> curC = null, nexC = null;
		InputStreamProxySource curI = null, nexI = null;
		InputStream preI = null;

		Class<? extends InputStreamFeature>[] cs = getFeatures().keySet().toArray(new Class[0]);
		for (int i = 0; i < cs.length; i++) {
			curC = cs[i];
			if (curC.equals(featureClass)) {
				curI = (InputStreamProxySource) featuresMap.get(curC);
				preI = curI.getSource();
				if (curI == lastDelegateObject) {
					lastDelegateObject = preI;
					featuresMap.remove(curC);
				} else {
					nexC = cs[i + 1];
					nexI = (InputStreamProxySource) featuresMap.get(nexC);
					nexI.setSource(preI);
					featuresMap.remove(curC);
				}
				rt = true;
				break;
			}
		}
		return rt;
	}


	@Override
	public Object intercept(Object obj, Method method, Object[] args, MethodProxy proxy) throws Throwable {
		Class mc = method.getDeclaringClass();
		log.trace("intercept method : " + method);
		
		if (mc.equals(Object.class)) { //调用的是Object的方法
			return method.invoke(this, args);
		}
		
		//是Feature
		if (InputStreamFeature.class.isAssignableFrom(mc) && !mc.equals(InputStreamFeature.class)) {
			Object delegate = featuresMap.get(mc);
			
			//找到
			if (delegate != null) {
				return method.invoke(delegate, args);
			}
			
			//如InputStreamProxySource
			if (mc.isAssignableFrom(InputStreamFeatureDelegate.class)) {
				return method.invoke(this, args);
			}
			
			//寻找是否有子类注册
			for (Class<? extends InputStreamFeature> clazz : getFeatures().keySet().toArray(new Class[0])) {
				if (mc.isAssignableFrom(clazz) && featuresMap.get(clazz) != null) {
					return method.invoke(featuresMap.get(clazz), args);
				}
			}
		}
		return method.invoke(this.lastDelegateObject, args);
	}

	@Override
	public int read() throws IOException {
		throw new IOException("Not support for this object!");
	}

	@Override
	public int read(byte[] b) throws IOException {
		throw new IOException("Not support for this object!");
	}

	@Override
	public int read(byte[] b, int off, int len) throws IOException {
		throw new IOException("Not support for this object!");
	}

	@Override
	public long skip(long n) throws IOException {
		throw new IOException("Not support for this object!");
	}

	@Override
	public int available() throws IOException {
		throw new IOException("Not support for this object!");
	}

	@Override
	public void close() throws IOException {
		throw new IOException("Not support for this object!");
	}

	@Override
	public void mark(int readlimit) {
	}

	@Override
	public boolean markSupported() {
		return false;
	}
	
}
