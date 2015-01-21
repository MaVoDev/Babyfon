package babyfon.adapter;

import java.util.ArrayList;

import android.app.Activity;
import android.content.Context;
import android.graphics.Typeface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;
import babyfon.init.R;
import babyfon.model.DeviceListItemModel;

public class DeviceListAdapter extends BaseAdapter {

	private Context mContext;
	private ArrayList<DeviceListItemModel> deviceItems;

	public DeviceListAdapter(Context context, ArrayList<DeviceListItemModel> deviceItems) {
		this.mContext = context;
		this.deviceItems = deviceItems;
	}
	
	@Override
	public int getCount() {
		return deviceItems.size();
	}

	@Override
	public Object getItem(int position) {
		return deviceItems.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		if (convertView == null) {
			LayoutInflater mInflater = (LayoutInflater) mContext.getSystemService(Activity.LAYOUT_INFLATER_SERVICE);
			convertView = mInflater.inflate(R.layout.listview_devices, null);
		}

		TextView deviceName = (TextView) convertView.findViewById(R.id.device_name);
		deviceName.setText(deviceItems.get(position).getDeviceName());

		// Set Typeface
		Typeface mTypeface_i = Typeface.createFromAsset(mContext.getAssets(), "fonts/BOOKOSI.TTF");
		deviceName.setTypeface(mTypeface_i);

		return convertView;
	}
}
