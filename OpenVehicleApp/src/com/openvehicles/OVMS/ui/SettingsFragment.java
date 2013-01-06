package com.openvehicles.OVMS.ui;

import java.util.ArrayList;

import android.content.Context;
import android.content.res.Configuration;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuInflater;
import com.actionbarsherlock.view.MenuItem;
import com.openvehicles.OVMS.R;
import com.openvehicles.OVMS.entities.CarData;
import com.openvehicles.OVMS.ui.settings.CarEditorFragment;
import com.openvehicles.OVMS.ui.utils.Ui;

public class SettingsFragment extends BaseFragment implements OnItemClickListener {
	private ListView mListView;
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		mListView = new ListView(container.getContext());
		return mListView;
	}
	
	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		mListView.setChoiceMode(ListView.CHOICE_MODE_SINGLE);
		mListView.setOnItemClickListener(this);
		mListView.setAdapter(new SettingsAdapter(getActivity(), getSavedCarData()));
		setHasOptionsMenu(true);
	}
	
	@Override
	public void onStart() {
		super.onStart();
		registerForUpdate();
	}
	
	@Override
	public void onStop() {
		super.onStop();
		unregisterForUpdate();
	}
	
	@Override
	public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
		inflater.inflate(R.menu.add, menu);
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		if (item.getItemId() == R.id.mi_add) {
			BaseFragmentActivity.show(getActivity(), CarEditorFragment.class,  
					Configuration.ORIENTATION_UNDEFINED);
			return true;
		}
		return super.onOptionsItemSelected(item);
	}
	
	@Override
	public void update(CarData pCarData) {
		int count = mListView.getCount();
		for (int i=0; i<count; i++) {
			if (pCarData == mListView.getItemAtPosition(i)) {
				mListView.setItemChecked(i, true);
				break;
			}
		}
		mListView.invalidateViews();
	}
	
	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
		Log.e("DEBUG", "onItemClick: " + position + ", v: " + view);

		BaseFragmentActivity.show(getActivity(), CarEditorFragment.class,  
				Configuration.ORIENTATION_UNDEFINED);
	}
	
	private static class SettingsAdapter extends BaseAdapter implements OnClickListener {
		private final LayoutInflater mInflater;
		private final ArrayList<CarData> mItems;
		private ListView mListView;

		public SettingsAdapter(Context pContext, ArrayList<CarData> pItems) {
			mItems = pItems;
			mInflater = LayoutInflater.from(pContext);
		}

		@Override
		public int getCount() {
			return mItems.size();
		}

		@Override
		public Object getItem(int position) {
			return mItems.get(position);
		}

		@Override
		public long getItemId(int position) {
			return 0;
		}
		
		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			if (convertView == null) {
				convertView = mInflater.inflate(R.layout.item_car, null);
			}

			ImageButton btnEdit = (ImageButton) convertView.findViewById(R.id.btn_edit);
			ImageButton btnInfo = (ImageButton) convertView.findViewById(R.id.btn_info);
			btnEdit.setOnClickListener(this);
			btnInfo.setOnClickListener(this);
			btnEdit.setTag(position);
			btnInfo.setTag(position);
			
			CarData it = mItems.get(position);
			ImageView iv = (ImageView) convertView.findViewById(R.id.img_car);
			iv.setImageResource(Ui.getDrawableIdentifier(parent.getContext(), it.sel_vehicle_image));
			
			((TextView)convertView.findViewById(R.id.txt_title)).setText(it.sel_vehicleid);
			
			if (mListView == null && parent instanceof ListView) {
				mListView = (ListView) parent;
			}
			if (mListView == null) return convertView;
			
			iv = (ImageView) convertView.findViewById(R.id.img_signal_rssi);
			if (mListView.isItemChecked(position)) {
				convertView.setBackgroundColor(0x8033B5E5);
				btnInfo.setVisibility(View.VISIBLE);
				iv.setVisibility(View.VISIBLE);
				iv.setImageResource(Ui.getDrawableIdentifier(parent.getContext(), "signal_strength_" + it.car_gsm_bars));
			} else {
				convertView.setBackgroundColor(0);
				iv.setVisibility(View.INVISIBLE);
				btnInfo.setVisibility(View.INVISIBLE);
			}
			return convertView;
		}

		@Override
		public void onClick(View v) {
			if (mListView == null || mListView.getOnItemClickListener() == null) return;
			
			mListView.getOnItemClickListener().onItemClick(mListView, v, 
					(Integer) v.getTag(), v.getId());
		}
	}
}