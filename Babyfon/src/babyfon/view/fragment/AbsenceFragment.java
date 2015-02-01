package babyfon.view.fragment;

import java.util.ArrayList;
import java.util.Set;
import java.util.Timer;
import java.util.TimerTask;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import babyfon.adapter.AbsenceListAdapter;
import babyfon.init.R;
import babyfon.model.AbsenceListItemModel;
import babyfon.settings.SharedPrefs;
import babyfon.view.activity.MainActivity;

public class AbsenceFragment extends Fragment {

	// Define UI elements
	private TextView title;
	private Button btnDeleteList;
	private static ListView listViewAbsence;

	private static ArrayList<AbsenceListItemModel> messages;

	// Timer
	private Timer updateTimer;

	private static SharedPrefs mSharedPrefs;

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
			listViewAbsence.setBackgroundResource(R.drawable.listview_male);
		} else {
			btnDeleteList.setBackgroundResource(R.drawable.btn_selector_female);
			listViewAbsence.setBackgroundResource(R.drawable.listview_female);
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
		builder.setTitle(numberName).setMessage(message).setCancelable(true)
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
		mSharedPrefs.deleteCallSMS();
		mSharedPrefs.setCallSMSCounter(0);
		updateList();
	}

	public static void setNewMessage() {
		Set<String> set = mSharedPrefs.getCallSMS();
		if (set != null) {
			ArrayList<String> callSmsList = new ArrayList<String>(set);
			messages = new ArrayList<AbsenceListItemModel>();

			for (int i = 0; i < callSmsList.size(); i++) {
				String listItem = callSmsList.get(i);
				String[] listArray = listItem.split(";");

				messages.add(new AbsenceListItemModel(Integer.parseInt(listArray[0]), listArray[1], listArray[2],
						listArray[3], listArray[4]));
			}
		}
		updateList();
	}

	public void startUiUpdateThread() {
		if (updateTimer == null) {
			updateTimer = new Timer();
		}
		updateTimer.scheduleAtFixedRate(new TimerTask() {
			public void run() {
				if (mSharedPrefs.getCallSMSCounter() != mSharedPrefs.getCallSMSCounterTemp()) {
					mSharedPrefs.setCallSMSCounterTemp(mSharedPrefs.getCallSMSCounter());
					((MainActivity) mContext).runOnUiThread(new Runnable() {
						@Override
						public void run() {
							setNewMessage();
						}
					});
				}
			}
		}, 0, 1000);
	}

	@Override
	public void onPause() {
		super.onPause();

		if (updateTimer != null) {
			updateTimer.cancel();
			updateTimer = null;
		}
	}

	@Override
	public void onDestroy() {
		super.onDestroy();

		if (updateTimer != null) {
			updateTimer.cancel();
			updateTimer = null;
		}
	}

	@Override
	public void onResume() {
		super.onResume();

		if (updateTimer == null) {
			updateTimer = new Timer();
		}
		startUiUpdateThread();

		if (messages == null) {
			messages = new ArrayList<AbsenceListItemModel>();
		}
		setNewMessage();

		if (btnDeleteList != null && listViewAbsence != null) {
			updateUI();
		}
	}
}
