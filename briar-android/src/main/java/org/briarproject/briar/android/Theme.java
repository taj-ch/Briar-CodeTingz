package org.briarproject.briar.android;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.drawable.Drawable;
import android.util.Log;
import android.util.TypedValue;

public class Theme {
	public static int getAttributeColor(
			Context context,
			int attributeId) {
		TypedValue typedValue = new TypedValue();
		context.getTheme().resolveAttribute(attributeId, typedValue, true);
		int colorRes = typedValue.resourceId;
		int color = -1;
		try {
			color = context.getResources().getColor(colorRes);
		} catch (Resources.NotFoundException e) {
			Log.w("COLOR_ERROR", "Not found color resource by id: " + colorRes);
		}
		return color;
	}

	public static Drawable getAttributeDrawable(
			Context context,
			int attributeId) {
		TypedValue typedValue = new TypedValue();
		context.getTheme().resolveAttribute(attributeId, typedValue, true);
		int drawableRes = typedValue.resourceId;
		Drawable drawable = null;
		try {
			drawable = context.getResources().getDrawable(drawableRes);
		} catch (Resources.NotFoundException e) {
			Log.w("DRAWABLE_ERROR", "Not found drawable resource by id: " + drawableRes);
		}
		return drawable;
	}

	public static int getAttributeDrawableInt(
			Context context,
			int attributeId) {
		TypedValue typedValue = new TypedValue();
		context.getTheme().resolveAttribute(attributeId, typedValue, true);
		int drawableRes = typedValue.resourceId;
		return drawableRes;
	}
}
