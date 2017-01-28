package com.fire.imagecomposer;

import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Environment;
import android.support.annotation.RequiresPermission;

import com.facebook.react.bridge.ReactContextBaseJavaModule;
import com.facebook.react.bridge.ReactApplicationContext;
import com.facebook.react.bridge.ReactMethod;
import com.facebook.react.bridge.ReadableArray;
import com.facebook.react.bridge.ReadableMap;
import com.facebook.react.bridge.Promise;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Map;
import java.util.HashMap;
import java.util.UUID;

class Module extends ReactContextBaseJavaModule {
	private final static int COMPOSED_IMAGE_COMPRESS_QUALITY = 85;
	private final static BitmapFactory.Options BG_IMAGE_DECODE_OPTS = new BitmapFactory.Options();
	static {
		BG_IMAGE_DECODE_OPTS.inMutable = true;
	}

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
//		consts.put("BLA_BLA", Toast.LENGTH_LONG);
		return consts;
	}

	private File createNewFile(boolean tmp) throws IOException {
		String name = "image-" + UUID.randomUUID().toString() + ".jpg";
		File dir = tmp ? this.getReactApplicationContext().getExternalCacheDir() : Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES);
		File newFile = new File(dir, name);
		dir.mkdirs();
		newFile.createNewFile();
		return newFile;
	}

	@ReactMethod
	public void compose(String bgImageURIString, ReadableArray layers, Promise promise) {
		try {
			Bitmap backgroundBitmap = BitmapFactory.decodeFile(Uri.parse(bgImageURIString).getPath(), BG_IMAGE_DECODE_OPTS);
			if (backgroundBitmap == null) {
				promise.reject("E_DECODE_BG", String.format("Could not load background image from %s", bgImageURIString));
				return;
			}

			Resources appRes = this.getReactApplicationContext().getResources();
			Canvas canvas = new Canvas(backgroundBitmap);

			int layerCount = layers.size();
			for (int i = 0; i < layerCount; ++i) {
				ReadableMap layerInfo = layers.getMap(i);
				int layerRID = appRes.getIdentifier(layerInfo.getString("resourceName"), "drawable", this.getReactApplicationContext().getPackageName());
				Bitmap layerBitmap = BitmapFactory.decodeResource(appRes, layerRID);
				if (layerBitmap == null) {
					promise.reject("E_DECODE_LAYER", String.format("Could not load layer bitmap from resource #%d", layerRID));
					return;
				}

				ReadableMap layerPosition = layerInfo.getMap("position");
				// TODO: use canvas.drawBitmap(bitmap, null, Rect dst, Paint paint) to benefit from the auto-scaling
				canvas.drawBitmap(layerBitmap, (float) layerPosition.getDouble("x"), (float) layerPosition.getDouble("y"), null);
			}

			File composedImageFile = createNewFile(true);
			FileOutputStream composedImageStream = new FileOutputStream(composedImageFile);

			if (backgroundBitmap.compress(Bitmap.CompressFormat.JPEG, COMPOSED_IMAGE_COMPRESS_QUALITY, composedImageStream)) {
				promise.resolve(Uri.fromFile(composedImageFile).toString());
			} else {
				promise.reject("E_COMPRESS_COMPOSED", "Could not compress composed image into output stream");
			}
		} catch (Exception exc) {
			promise.reject(exc);
		}
	}
}