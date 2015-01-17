package babyfon.view.fragment;

import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.ArrayList;

import babyfon.Message;
import babyfon.adapter.AbsenceListAdapter;
import babyfon.adapter.DeviceListAdapter;
import babyfon.connectivity.wifi.WifiHandler;
import babyfon.init.R;
import babyfon.model.AbsenceListItemModel;
import babyfon.model.DeviceListItemModel;
import babyfon.settings.SharedPrefs;
import babyfon.view.activity.MainActivity;
import babyfon.view.fragment.setup.parentmode.SetupConnectionParentsModeFragment;
import android.app.AlertDialog;
import android.app.Fragment;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Typeface;
import android.os.Bundle;
import android.text.InputFilter;
import android.text.method.DigitsKeyListener;
import android.util.Log;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.AdapterView.OnItemClickListener;

public class AbsenceFragment extends Fragment {

	// Define UI elements
	private TextView title;
	private Button btnDeleteList;
	private static ListView listViewAbsence;

	private static ArrayList<AbsenceListItemModel> messages;

	private SharedPrefs mSharedPrefs;

	private static Context mContext;

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

	public static void updateList() {
		AbsenceListAdapter adapter = new AbsenceListAdapter(mContext.getApplicationContext(), messages);

		// Assign adapter to ListView
		listViewAbsence.setAdapter(adapter);

		// ListView Item Click Listener
		listViewAbsence.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

				String numberName = messages.get(position).getNumber();
				String message = messages.get(position).getMessage();

				openMessage(numberName, message);
			}
		});
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

		// Initialize ListView
		listViewAbsence = (ListView) view.findViewById(R.id.listView_absence);

		// Initialize TextViews
		title = (TextView) view.findViewById(R.id.title_absence);
		title.setTypeface(mTypeface_bi);

		updateUI();
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

		View view = inflater.inflate(R.layout.main_absence, container, false);

		initUiElements(view);

		btnDeleteList.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				if (messages.size() > 0) {
					new AlertDialog.Builder(getActivity())
							.setTitle(mContext.getString(R.string.dialog_title_delete_list))
							.setMessage(mContext.getString(R.string.dialog_message_delete_list))
							.setNegativeButton(mContext.getString(R.string.dialog_button_no), null)
							.setPositiveButton(mContext.getString(R.string.dialog_button_yes),
									new DialogInterface.OnClickListener() {
										@Override
										public void onClick(DialogInterface dialog, int id) {
											deleteList();
										}
									}).create().show();
				}
			}
		});

		return view;
	}

	public static void openMessage(final String numberName, String message) {
		AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
		builder.setTitle(numberName).setMessage(message).setCancelable(false)
				.setPositiveButton(mContext.getString(R.string.close), new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int id) {
						// do nothing, just close dialog
					}
				});
		AlertDialog alert = builder.create();
		alert.show();
	}

	public void deleteList() {
		messages.clear();
		updateList();
	}

	public static void setNewMessage(int type, String numberName, String message) {
		if (messages == null) {
			messages = new ArrayList<AbsenceListItemModel>();
		}
		messages.add(new AbsenceListItemModel(type, numberName, message));

		updateList();
	}

	@Override
	public void onResume() {
		super.onResume();

		if (messages == null) {
			messages = new ArrayList<AbsenceListItemModel>();
		}

		if (btnDeleteList != null) {
			updateUI();
		}
	}
}
