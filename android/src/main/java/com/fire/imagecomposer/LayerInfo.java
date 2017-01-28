package com.fire.imagecomposer;

import android.graphics.RectF;

import com.facebook.react.bridge.ReadableMap;

/**
 * Created by dorfire on 28/01/2017.
 */
class LayerInfo {
	final int resourceId;
	final float angle;
	final RectF position;
	final int alpha;

	LayerInfo(int resourceId, ReadableMap infoMap) {
		this.resourceId = resourceId;
		this.angle = (float) infoMap.getDouble("angle");
		this.alpha = infoMap.hasKey("alpha") ? infoMap.getInt("alpha") : 0xff;

		ReadableMap pos = infoMap.getMap("position");
		this.position = new RectF((float) pos.getDouble("left"), (float) pos.getDouble("top"),
								  (float) pos.getDouble("right"), (float) pos.getDouble("bottom"));
	}
}
