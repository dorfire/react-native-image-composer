package com.fire.imagecomposer;

import android.graphics.Point;
import android.graphics.RectF;

import com.facebook.react.bridge.ReadableMap;
import com.facebook.react.bridge.ReadableArray;

/**
 * Created by dorfire on 28/01/2017.
 */
class LayerInfo {
	final int resourceId;
	final float angle;
	final RectF position;
	final Point origin;
	final int alpha;

	LayerInfo(int resourceId, ReadableMap infoMap) {
		this.resourceId = resourceId;
		this.angle = infoMap.hasKey("rotate") ? (float) infoMap.getDouble("rotate") : 0;
		this.alpha = infoMap.hasKey("alpha") ? infoMap.getInt("alpha") : 0xff;

		ReadableArray originArray = infoMap.getArray("origin");
		this.origin = new Point(originArray.getInt(0), originArray.getInt(1));

		ReadableMap pos = infoMap.getMap("position");
		this.position = new RectF((float) pos.getDouble("left"), (float) pos.getDouble("top"),
								  (float) pos.getDouble("right"), (float) pos.getDouble("bottom"));
	}
}
