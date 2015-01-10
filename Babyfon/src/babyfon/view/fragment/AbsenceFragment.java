package babyfon.view.fragment;

import babyfon.init.R;
import babyfon.settings.SharedPrefs;
import android.app.Fragment;
import android.content.Context;
import android.graphics.Typeface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

public class AbsenceFragment extends Fragment {

	// Define UI elements
	private TextView title;
	private Button btnDeleteList;

	private SharedPrefs mSharedPrefs;

	private Context mContext;

	// Constructor
	public AbsenceFragment(Context mContext) {
		mSharedPrefs = new SharedPrefs(mContext);

		this.mContext = mContext;
	}

	public void updateUI() {
		// Update buttons
		if (mSharedPrefs.getGender() == 0) {
			btnDeleteList.setBackgroundResource(R.drawable.btn_selector_male);
		} else {
			btnDeleteList.setBackgroundResource(R.drawable.btn_selector_female);
		}
	}

	/**
	 * Initialize the UI elements
	 * 
	 * @param view
	 */
	private void initUiElements(View view) {
		// Set Typeface
		Typeface mTypeface_bi = Typeface.createFromAsset(mContext.getAssets(), "fonts/BOOKOSBI.TTF");
		Typeface mTypeface_i = Typeface.createFromAsset(mContext.getAssets(), "fonts/BOOKOSI.TTF");

		// Initialize Button
		btnDeleteList = (Button) view.findViewById(R.id.btn_delete_list);
		btnDeleteList.setTypeface(mTypeface_i);

		// Initialize TextViews
		title = (TextView) view.findViewById(R.id.title_absence);
		title.setTypeface(mTypeface_bi);

		updateUI();
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

		View view = inflater.inflate(R.layout.main_absence, container, false);

		initUiElements(view);

		return view;
	}

	@Override
	public void onResume() {
		super.onResume();
		if (btnDeleteList != null) {
			updateUI();
		}
	}
}
