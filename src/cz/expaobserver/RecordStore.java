package cz.expaobserver;

import java.util.List;
import java.util.Set;
import java.util.ArrayList;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class RecordStore extends SQLiteOpenHelper
{
	private static final int DATABASE_VERSION = 3;

	private static final String DATABASE_NAME = "meteorObserver";

	private static final String TABLE_RECORDS = "records";

	private static final String KEY_ID = "id";
	private static final String KEY_TIME = "time";
	private static final String KEY_TRAIL_BEG_X = "trail_beg_x";
	private static final String KEY_TRAIL_BEG_Y = "trail_beg_y";
	private static final String KEY_TRAIL_BEG_Z = "trail_beg_z";
	private static final String KEY_TRAIL_END_X = "trail_end_x";
	private static final String KEY_TRAIL_END_Y = "trail_end_y";
	private static final String KEY_TRAIL_END_Z = "trail_end_z";
	private static final String KEY_LOC_LAT = "loc_lat";
	private static final String KEY_LOC_LONG = "loc_long";

	public RecordStore(Context context)
	{
		super(context, DATABASE_NAME, null, DATABASE_VERSION);
	}

	public void onCreate(SQLiteDatabase db)
	{
		String CREATE_RECORDS_TABLE = "CREATE TABLE " + TABLE_RECORDS + "("
				+ KEY_ID + " INTEGER PRIMARY KEY,"
				+ KEY_TIME + " INTEGER,"
				+ KEY_TRAIL_BEG_X + " REAL,"
				+ KEY_TRAIL_BEG_Y + " REAL,"
				+ KEY_TRAIL_BEG_Z + " REAL,"
				+ KEY_TRAIL_END_X + " REAL,"
				+ KEY_TRAIL_END_Y + " REAL,"
				+ KEY_TRAIL_END_Z + " REAL,"
				+ KEY_LOC_LAT + " REAL,"
				+ KEY_LOC_LONG + " REAL" + ")";
		db.execSQL(CREATE_RECORDS_TABLE);
	}

	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion)
	{
		db.execSQL("DROP TABLE IF EXISTS " + TABLE_RECORDS);
		onCreate(db);
	}

	public void onOpen(SQLiteDatabase db)
	{
	}

	public static Record getRecordForRow(Cursor cursor)
	{
		Record record = new Record();

		record.id = cursor.getLong(0);
		record.time = cursor.getLong(1);
		record.trailBeg = new Vector3(cursor.getFloat(2),
									cursor.getFloat(3),
									cursor.getFloat(4));
		record.trailEnd = new Vector3(cursor.getFloat(5),
									cursor.getFloat(6),
									cursor.getFloat(7));
		record.locLat = cursor.getDouble(8);
		record.locLong = cursor.getDouble(9);

		return record;
	}

	public static ContentValues getValuesForRecord(Record record)
	{
		ContentValues values = new ContentValues();

		values.put(KEY_TIME, record.time);
		values.put(KEY_TRAIL_BEG_X, record.trailBeg.x);
		values.put(KEY_TRAIL_BEG_Y, record.trailBeg.y);
		values.put(KEY_TRAIL_BEG_Z, record.trailBeg.z);
		values.put(KEY_TRAIL_END_X, record.trailEnd.x);
		values.put(KEY_TRAIL_END_Y, record.trailEnd.y);
		values.put(KEY_TRAIL_END_Z, record.trailEnd.z);
		values.put(KEY_LOC_LAT, record.locLat);
		values.put(KEY_LOC_LONG, record.locLong);

		return values;
	}

	public List<Record> getAllRecords()
	{
		SQLiteDatabase db = this.getReadableDatabase();
		Cursor c = db.rawQuery("SELECT * FROM " + TABLE_RECORDS, null);

		List<Record> recordsList = new ArrayList<Record>();

		if (c.moveToFirst()) {
			do {
				recordsList.add(getRecordForRow(c));
			} while(c.moveToNext());
		}

		return recordsList;
	}

	public void addRecord(Record record)
	{
		SQLiteDatabase db = this.getWritableDatabase();
		db.insertOrThrow(TABLE_RECORDS, null, getValuesForRecord(record));
		db.close();
	}

	public void updateRecord(Record record)
	{
		SQLiteDatabase db = this.getWritableDatabase();
		db.update(TABLE_RECORDS, getValuesForRecord(record), KEY_ID + " = ?",
					new String[] { String.valueOf(record.id) });
		db.close();
	}

	public void deleteRecord(Record record)
	{
		SQLiteDatabase db = this.getWritableDatabase();
		db.delete(TABLE_RECORDS, KEY_ID + " = ?",
					new String[] { String.valueOf(record.id) });
		db.close();
	}

	static private String joinIds(Set<Long> list)
	{
		StringBuilder sb = new StringBuilder();
		boolean first = true;

		for (Long id : list)
		{
			if (first)
				first = false;
			else
				sb.append(", ");
			sb.append(id);
		}
		return sb.toString();
	}

	public void deleteRecordsById(Set<Long> ids) {
		SQLiteDatabase db = this.getWritableDatabase();
		db.delete(TABLE_RECORDS, KEY_ID + " IN "
					+ "(" + joinIds(ids) + ")", null);
		db.close();
	}

	public Cursor cursorForRecordsById(Set<Long> ids) {
		SQLiteDatabase db = this.getReadableDatabase();
		return db.rawQuery("SELECT * FROM " + TABLE_RECORDS
							+ " WHERE " + KEY_ID + " IN "
							+ "(" + joinIds(ids) + ")", null);
	}
}
