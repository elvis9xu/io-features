package com.xjd.io.feature.in;

import java.io.IOException;
import java.io.InputStream;

import org.junit.Test;

import com.xjd.io.mock.in.ConstantByteInputStream;

public class InputStreamProxyTest {

	@Test
	public void test() throws IOException {
		ConstantByteInputStream in = new ConstantByteInputStream();
		
		InputStream in2 = InputStreamProxy.bind(in, CountFeature.class);
		
		System.out.println(in2.read());
		
		InputStream in3 = InputStreamProxy.bindWithNewStage(in2, CountFeature.class);
		
		System.out.println(in3.read());;
		
		System.out.println(((CountFeature)in2).getReadBytes());
		System.out.println(((CountFeature)in3).getReadBytes());
	}

}
