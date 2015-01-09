package babyfon.view.fragment;

import babyfon.init.R;
import android.app.Fragment;
import android.content.Context;
import android.graphics.Typeface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

public class AbsenceFragment extends Fragment {

	// Define UI elements
	private TextView textTitleAbsence;

	private Context mContext;

	// Constructor
	public AbsenceFragment(Context mContext) {
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
		textTitleAbsence = (TextView) view.findViewById(R.id.text_titleAbsence);
		textTitleAbsence.setTypeface(mTypeface);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

		View view = inflater.inflate(R.layout.layout_absence, container, false);
		
		initUiElements(view);

		return view;
	}
}
