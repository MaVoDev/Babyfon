package babyfon.performance;

import android.content.Context;

public class Screen {

	private Context mContext;

	public Screen(Context mContext) {
		this.mContext = mContext;
	}

	public void setScreenBrightness(int brightness) {
		android.provider.Settings.System.putInt(mContext.getContentResolver(),
				android.provider.Settings.System.SCREEN_BRIGHTNESS, brightness);
	}
	
	public void setScreenState(boolean state) {
		
	}
}
