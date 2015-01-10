package babyfon.view.fragment.overview;

import babyfon.init.R;
import babyfon.settings.SharedPrefs;
import android.content.Context;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

public class OverviewBabyFragment extends Fragment {

	// Define UI elements
	private TextView title;

	private SharedPrefs mSharedPrefs;

	private Context mContext;

	// Constructor
	public OverviewBabyFragment(Context mContext) {
		mSharedPrefs = new SharedPrefs(mContext);

		this.mContext = mContext;
	}

	/**
	 * Initialize the UI elements
	 * 
	 * @param view
	 */
	private void initUiElements(View view) {
		// Set Typeface
		Typeface mTypeface = Typeface.createFromAsset(mContext.getAssets(), "fonts/BOOKOSBI.TTF");

		// Initialize TextViews
		title = (TextView) view.findViewById(R.id.title_overview_babymode);
		title.setTypeface(mTypeface);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

		View view = inflater.inflate(R.layout.main_overview_babymode, container, false);

		initUiElements(view);

		return view;
	}
}
