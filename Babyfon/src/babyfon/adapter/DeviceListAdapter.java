package babyfon.adapter;

import android.content.Context;
import android.graphics.Typeface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;
import babyfon.init.R;
import babyfon.model.DeviceListItemModel;

public class DeviceListAdapter extends ArrayAdapter<DeviceListItemModel> {

	private Context mContext;
	private int resource;

	public DeviceListAdapter(Context context, int resource) {
		super(context, resource);

		this.mContext = context;
		this.resource = resource;

	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		View rowView = convertView;

		// reuse views
		if (rowView == null) {
			LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

			rowView = inflater.inflate(resource, null);
			// configure view holder
			ViewHolder viewHolder = new ViewHolder();
			viewHolder.name = (TextView) rowView.findViewById(R.id.device_name);

			// Set Typeface
			Typeface mTypeface_i = Typeface.createFromAsset(mContext.getAssets(), "fonts/BOOKOSI.TTF");
			viewHolder.name.setTypeface(mTypeface_i);
			rowView.setTag(viewHolder);
		}

		// fill data
		ViewHolder holder = (ViewHolder) rowView.getTag();
		holder.name.setText(getItem(position).getDeviceName());

		return rowView;
	}

	static class ViewHolder {
		public TextView name;
	}
}
