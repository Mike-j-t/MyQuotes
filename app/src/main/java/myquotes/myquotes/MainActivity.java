package myquotes.myquotes;

import android.database.Cursor;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;

public class MainActivity extends AppCompatActivity {

    DatabaseHelper mDBHlpr;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mDBHlpr = new DatabaseHelper(this);
        Cursor csr = mDBHlpr.getQuotesWithTitlesAndAuthors();

        while (csr.moveToNext()) {
            Log.i("CSRINFO",
                    "Quote = " + csr.getString(csr.getColumnIndex(DatabaseHelper.COL_QUOTE_QUOTE)) +
                            "\n\tTitle = " + csr.getString(csr.getColumnIndex(DatabaseHelper.COL_TITLE_TITLE)) +
                            "\n\tAuthour = " + csr.getString(csr.getColumnIndex(DatabaseHelper.COL_AUTHOR_AUTHOR)) +
                            "\n\n\tQuote ID = " + String.valueOf(csr.getLong(csr.getColumnIndex(DatabaseHelper.COL_QUOTE_ID))) +
                            "\n\tTitle ID = " + String.valueOf(csr.getLong(csr.getColumnIndex(DatabaseHelper.DERIVEDCOL_TITLEID))) +
                            "\n\tAuthor ID = " + String.valueOf(csr.getLong(csr.getColumnIndex(DatabaseHelper.DERIVEDCOL_AUTHORID)))
            );
        }
    }
}
