package cz.expaobserver.ui;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.ListFragment;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;
import java.util.TreeSet;

import cz.expaobserver.R;
import cz.expaobserver.background.UploadRecordsTask;
import cz.expaobserver.model.Record;
import cz.expaobserver.model.RecordStore;
import cz.expaobserver.util.Util;

/**
 * Created by pechanecjr on 28. 11. 2014.
 */
public class RecordListFragment extends ListFragment {

  private RecordStore mRecordStore;
  private List<Record> mRecordsList;

  private SelectableArrayAdapter mAdapter;

  public static RecordListFragment newInstance() {
    return new RecordListFragment();
  }

  public RecordListFragment() {
  }

  @Override
  public void onAttach(Activity activity) {
    super.onAttach(activity);

    if (mRecordStore == null) {
      mRecordStore = ((ObserverApplication) getActivity().getApplication()).getRecordStore();
    }

    mRecordsList = mRecordStore.getAllRecords();
  }

  @Override
  public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);

    setHasOptionsMenu(true);
  }

  @Override
  public void onViewCreated(View view, Bundle savedInstanceState) {
    super.onViewCreated(view, savedInstanceState);

    mAdapter = new SelectableArrayAdapter(getActivity(), mRecordsList);
    setListAdapter(mAdapter);

    setEmptyText(getString(R.string.mo_empty_list));

  }

  @Override
  public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
    super.onCreateOptionsMenu(menu, inflater);

    inflater.inflate(R.menu.record_list_actions, menu);

    Util.Material.tintMenu(menu, getActivity());
  }

  @Override
  public boolean onOptionsItemSelected(MenuItem item) {
    switch (item.getItemId()) {
      case R.id.delete_selected:
        mRecordStore.deleteRecordsById(mAdapter.selectedIds);
        // TODO
        mRecordsList.clear();
        mRecordsList.addAll(mRecordStore.getAllRecords());
        mAdapter.selectedIds.clear();
        mAdapter.notifyDataSetChanged();
        return true;
      case R.id.upload_selected:
        List<Record> records = mAdapter.getSelectedRecords();
        UploadRecordsTask.Listener listener = new UploadRecordsTask.Listener() {
          public void onFinish() {
            // TODO
            mRecordsList.clear();
            mRecordsList.addAll(mRecordStore.getAllRecords());
            mAdapter.selectedIds.clear();
            mAdapter.notifyDataSetChanged();
          }

          ;
        };

        new UploadRecordsTask(getActivity(), listener, records).execute();

        return true;
      case R.id.add_note_to_selected:
        AlertDialog.Builder alert = new AlertDialog.Builder(getActivity());
        alert.setTitle(getResources().getString(R.string.mo_note));

        final EditText input = new EditText(getActivity());
        alert.setView(input);
        alert.setPositiveButton("OK", new DialogInterface.OnClickListener() {
          public void onClick(DialogInterface dialog, int whichButton) {
            List<Record> records = mAdapter.getSelectedRecords();
            String note = input.getText().toString();

            for (Record record : records) {
              record.note = note;
              mRecordStore.updateRecord(record);
            }

            mAdapter.notifyDataSetChanged();
          }
        });

        alert.show();

        return true;
      case R.id.select_all:
        for (Record record : mRecordsList) {
          mAdapter.setSelected(record, true);
        }
        mAdapter.notifyDataSetChanged();
        return true;
      default:
        return super.onOptionsItemSelected(item);
    }
  }

  private static class SelectableArrayAdapter extends ArrayAdapter<Record> implements
      CompoundButton.OnCheckedChangeListener {
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
        v = inflater.inflate(R.layout.record_entry, parent, false);
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
}
