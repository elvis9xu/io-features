package com.xjd.io.feature.in;

import java.util.LinkedHashMap;

interface InputStreamFeatureDelegate extends InputStreamFeature, InputStreamProxySource {
	
	InputStreamFeatureDelegate getDelegateConcrete();

	boolean addFeature(Class<? extends InputStreamFeature> featureClass, Object implOrImplClass);

	boolean removeFeature(Class<? extends InputStreamFeature> featureClass);

	LinkedHashMap<Class<? extends InputStreamFeature>, InputStreamFeature> getFeatures();
}
