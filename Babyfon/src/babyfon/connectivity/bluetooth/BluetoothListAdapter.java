package babyfon.connectivity.bluetooth;

import babyfon.init.R;
import android.bluetooth.BluetoothDevice;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

public class BluetoothListAdapter extends ArrayAdapter<BluetoothDevice> {

	private Context context;
	private int resource;

	public BluetoothListAdapter(Context context, int resource) {
		super(context, resource);

		this.context = context;
		this.resource = resource;

	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		View rowView = convertView;

		// reuse views
		if (rowView == null) {
			LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

			rowView = inflater.inflate(resource, null);
			// configure view holder
			ViewHolder viewHolder = new ViewHolder();
			viewHolder.name = (TextView) rowView.findViewById(R.id.name);
			viewHolder.address = (TextView) rowView.findViewById(R.id.address);
			rowView.setTag(viewHolder);
		}

		// fill data
		ViewHolder holder = (ViewHolder) rowView.getTag();
		holder.name.setText(getItem(position).getName());
		holder.address.setText(getItem(position).getAddress());

		return rowView;
	}

	static class ViewHolder {
		public TextView name;
		public TextView address;
	}
}
