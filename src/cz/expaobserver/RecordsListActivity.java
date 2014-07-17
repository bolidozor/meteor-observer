package cz.expaobserver;

import java.util.*;
import android.os.*;
import android.app.*;
import android.view.*;
import android.widget.*;
import android.content.*;

public class RecordsListActivity extends ListActivity {
	private class SelectableArrayAdapter extends ArrayAdapter<Record>
					implements CompoundButton.OnCheckedChangeListener {
		private final List<Record> list;
		private final TreeSet<Long> selectedIds;
		private final Activity context;

		public SelectableArrayAdapter(Activity context, List<Record> list) {
			super(context, R.layout.record_entry, list);
			this.context = context;
			this.list = list;
			this.selectedIds = new TreeSet<Long>();
		}

		public long getItemId(int position) {
			return getItem(position).id;
		}

		public void setSelected(Record record, boolean isSelected) {
			if (isSelected)
				selectedIds.add(record.id);
			else
				selectedIds.remove(record.id);
		}

		public boolean isSelected(Record record) {
			return selectedIds.contains(record.id);
		}

		public List<Record> getSelectedRecords() {
			List<Record> records = new ArrayList<Record>();

			for (Record record : list) {
				if (selectedIds.contains(record.id))
					records.add(record);
			}

			return records;
		}

		public View getView(int position, View convertView, ViewGroup parent) {
			View v = convertView;

			if (v == null) {
				LayoutInflater inflater = context.getLayoutInflater();
				v = inflater.inflate(R.layout.record_entry, null);
				((CheckBox) v.findViewById(R.id.check)).setOnCheckedChangeListener(this);
			}

			Record record = list.get(position);

			((TextView) v.findViewById(R.id.desc)).setText(record.toString());
			CheckBox checkbox = (CheckBox) v.findViewById(R.id.check);
			checkbox.setTag(record);
			checkbox.setChecked(isSelected(record));

			return v;
		}

		public void onCheckedChanged(CompoundButton view, boolean isChecked) {
			setSelected((Record) view.getTag(), isChecked);
		}
	}

	RecordStore recordStore;
	private List<Record> recordsList;
	private SelectableArrayAdapter adapter;

	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		recordStore = ((ObserverApplication) getApplication()).getRecordStore();
		recordsList = recordStore.getAllRecords();

		adapter = new SelectableArrayAdapter(this, recordsList);
		setListAdapter(adapter);

		registerForContextMenu(getListView());
	}

	public void onListItemClick(ListView l, View v, int position, long id) {
	}

	public boolean onCreateOptionsMenu(Menu menu)
	{
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.list_options, menu);
		return true;
	}

	public boolean onOptionsItemSelected(MenuItem item)
	{
		switch (item.getItemId()) {
			case R.id.delete_selected:
				recordStore.deleteRecordsById(adapter.selectedIds);
				// TODO
				recordsList.clear();
				recordsList.addAll(recordStore.getAllRecords());
				adapter.selectedIds.clear();
				adapter.notifyDataSetChanged();
				return true;
			case R.id.upload_selected:
				List<Record> records = adapter.getSelectedRecords();
				UploadRecordsTask.Listener listener = new UploadRecordsTask.Listener() {
					public void onFinish() {
						// TODO
						recordsList.clear();
						recordsList.addAll(recordStore.getAllRecords());
						adapter.selectedIds.clear();
						adapter.notifyDataSetChanged();
					};
				};

				new UploadRecordsTask(this, listener, records).execute();

				return true;
			case R.id.add_note_to_selected:
				AlertDialog.Builder alert = new AlertDialog.Builder(this);
				alert.setTitle(getResources().getString(R.string.note));

				final EditText input = new EditText(this);
				alert.setView(input);
				alert.setPositiveButton("OK", new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int whichButton) {
						List<Record> records = adapter.getSelectedRecords();
						String note = input.getText().toString();

						for (Record record : records) {
							record.note = note;
							recordStore.updateRecord(record);
						}

						adapter.notifyDataSetChanged();
					}
				});

				alert.show();

				return true;
			case R.id.select_all:
				for (Record record : recordsList) {
					adapter.setSelected(record, true);
				}
				adapter.notifyDataSetChanged();
				return true;
			default:
				return super.onOptionsItemSelected(item);
		}
	}
}
