package wsn.park.navi.task;

import wsn.park.util.DbHelper;
import android.database.Cursor;
import android.os.AsyncTask;

public class DBAsyncTask extends AsyncTask<Object, Object, Cursor> {
    DbHelper db = DbHelper.getInstance();

    @Override
    protected Cursor doInBackground(Object... params) {
    	db.getWritableDatabase();
		return null;
    }

    @Override
    protected void onPostExecute(Cursor result) {
        if (result != null) {
            db.close();
        }
    }
}