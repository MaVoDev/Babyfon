package babyfon.adapter;

import java.util.ArrayList;

import android.app.Activity;
import android.content.Context;
import android.graphics.Typeface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import babyfon.init.R;
import babyfon.model.AbsenceListItemModel;

public class AbsenceListAdapter extends BaseAdapter {

	private Context mContext;
	private ArrayList<AbsenceListItemModel> absenceItems;

	public AbsenceListAdapter(Context context, ArrayList<AbsenceListItemModel> absenceItems) {
		this.mContext = context;
		this.absenceItems = absenceItems;
	}

	@Override
	public int getCount() {
		return absenceItems.size();
	}

	@Override
	public Object getItem(int position) {
		return absenceItems.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		if (convertView == null) {
			LayoutInflater mInflater = (LayoutInflater) mContext.getSystemService(Activity.LAYOUT_INFLATER_SERVICE);
			convertView = mInflater.inflate(R.layout.listview_absence, null);
		}

		ImageView type = (ImageView) convertView.findViewById(R.id.absence_type);
		if (absenceItems.get(position).getType() == 0) {
			type.setImageResource(android.R.drawable.sym_call_missed);
		} else {
			type.setImageResource(android.R.drawable.sym_action_email);
		}

		TextView numberName = (TextView) convertView.findViewById(R.id.absence_number_name);
		numberName.setText(absenceItems.get(position).getNumber());

		TextView message = (TextView) convertView.findViewById(R.id.absence_message);
		message.setText(absenceItems.get(position).getMessage());

		// Set Typeface
		Typeface mTypeface_i = Typeface.createFromAsset(mContext.getAssets(), "fonts/BOOKOSI.TTF");
		Typeface mTypeface_bi = Typeface.createFromAsset(mContext.getAssets(), "fonts/BOOKOSBI.TTF");
		numberName.setTypeface(mTypeface_bi);
		message.setTypeface(mTypeface_i);

		return convertView;
	}
}
