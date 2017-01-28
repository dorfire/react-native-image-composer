package com.fire.imagecomposer;

import com.facebook.react.bridge.*;
import com.facebook.react.common.SystemClock;

import android.content.res.Resources;
import android.graphics.RectF;
import android.net.Uri;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;

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

	private final Resources mAppRes;

	Module(ReactApplicationContext reactContext) {
		super(reactContext);
		mAppRes = reactContext.getResources();
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

	private File createTmpImage() throws IOException {
		String name = "image-" + UUID.randomUUID().toString() + ".jpg";
		File dir = this.getReactApplicationContext().getExternalCacheDir();
		File newFile = new File(dir, name);
		if (!dir.mkdirs() || !newFile.createNewFile())
			return null;
		return newFile;
	}

	private int getResourceIdByName(String name) {
		int res = this.mAppRes.getIdentifier(name, "drawable", this.getReactApplicationContext().getPackageName());
		if (res == 0)
			throw new Resources.NotFoundException(name);
		return res;
	}

	@ReactMethod
	public void compose(String bgImageURI, ReadableArray layers, Promise promise) {
		long startTime = SystemClock.currentTimeMillis();
		try {
			Bitmap backgroundBitmap = BitmapFactory.decodeFile(Uri.parse(bgImageURI).getPath(), BG_IMAGE_DECODE_OPTS);
			if (backgroundBitmap == null) {
				promise.reject("E_DECODE_BG", String.format("Could not load background image from %s", bgImageURI));
				return;
			}

			Canvas canvas = new Canvas(backgroundBitmap);

			int layerCount = layers.size();
			for (int i = 0; i < layerCount; ++i) {
				ReadableMap layerInfo = layers.getMap(i);

				int layerRID = this.getResourceIdByName(layerInfo.getString("resourceName"));
				Bitmap layerBitmap = BitmapFactory.decodeResource(this.mAppRes, layerRID);
				if (layerBitmap == null) {
					promise.reject("E_DECODE_LAYER", String.format("Could not load layer bitmap from resource #%d", layerRID));
					return;
				}

				canvas.save();
				canvas.rotate(layerInfo.getInt("angle"));

				ReadableMap pos = layerInfo.getMap("position");
				RectF layerPosition = new RectF((float) pos.getDouble("left"), (float) pos.getDouble("top"),
												(float) pos.getDouble("right"), (float) pos.getDouble("bottom"));

				canvas.drawBitmap(layerBitmap, null, layerPosition, null);
				canvas.restore();
			}

			File composedImageFile = createTmpImage();
			FileOutputStream composedImageStream = new FileOutputStream(composedImageFile);

			if (backgroundBitmap.compress(Bitmap.CompressFormat.JPEG, COMPOSED_IMAGE_COMPRESS_QUALITY, composedImageStream)) {
				WritableMap resultMap = Arguments.createMap();
				resultMap.putString("uri", Uri.fromFile(composedImageFile).toString());
				resultMap.putInt("time", (int) (SystemClock.currentTimeMillis() - startTime));
				promise.resolve(resultMap);
			} else {
				promise.reject("E_COMPRESS_COMPOSED", "Could not compress composed image into output stream");
			}
		} catch (Exception exc) {
			promise.reject(exc);
		}
	}
}