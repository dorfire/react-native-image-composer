package com.fire.imagecomposer;

import android.widget.Toast;

import com.facebook.react.bridge.ReactContextBaseJavaModule;
import com.facebook.react.bridge.ReactApplicationContext;
import com.facebook.react.bridge.ReactMethod;
import com.facebook.react.bridge.ReadableMap;
import com.facebook.react.bridge.Callback;

import java.util.Map;
import java.util.HashMap;

class Module extends ReactContextBaseJavaModule {
	Module(ReactApplicationContext reactContext) {
		super(reactContext);
	}

	@Override
	public String getName() {
		return "ImageComposer";
	}

	@Override
	public Map<String, Object> getConstants() {
		final Map<String, Object> consts = new HashMap<>();
		consts.put("BLA_BLA", Toast.LENGTH_LONG);
		return consts;
	}

	@ReactMethod
	public void compose(String bgImageURI, ReadableMap layers, Callback successCallback, Callback errorCallback) {
		Toast.makeText(getReactApplicationContext(), bgImageURI, Toast.LENGTH_LONG).show();
	}
}