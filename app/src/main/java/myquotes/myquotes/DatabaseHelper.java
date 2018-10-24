package myquotes.myquotes;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.provider.BaseColumns;
import android.util.Log;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public class DatabaseHelper extends SQLiteOpenHelper {

    /*

    CREATE TABLE IF NOT EXISTS author (_id INTEGER PRIMARY KEY, author TEXT UNIQUE);
    CREATE TABLE IF NOT EXISTS title (_id INTEGER PRIMARY KEY, title TEXT UNIQUE);
    CREATE TABLE IF NOT EXISTS quote (_id INTEGER PRIMARY KEY, quote TEXT UNIQUE, author_reference INTEGER REFERENCES author(_id));
    CREATE TABLE IF NOT EXISTS title_quote_map (
        title_reference INTEGER REFERENCES title(_id),
        quote_reference INTEGER REFERENCES quote(_id),
        UNIQUE (title_reference, quote_reference));

     */

    public static final String DB_NAME = "be.db";
    public static final int DB_VERSION = 1;
    public static final String TB_QUOTE = "quote";
    public static final String TB_AUTHOR = "author";
    public static final String TB_TITLE = "title";
    public static final String TB_TITEQUOTEMAP = "title_quote_map";

    public static final String COL_QUOTE_ID = BaseColumns._ID;
    public static final String COL_QUOTE_QUOTE = "quote";
    public static final String COL_QUOTE_AUTHOR_REFERENCE = "author_reference";
    public static final String COL_TITLE_ID = BaseColumns._ID;
    public static final String COL_TITLE_TITLE = "title";
    public static final String COL_AUTHOR_ID = BaseColumns._ID;
    public static final String COL_AUTHOR_AUTHOR = "author";
    public static final String COL_TITLEAUTHORMAP_TITLEREFERENCE = "title_reference";
    public static final String COL_TITLEAUTHORMAP_QUOTEREFERENCE = "quote_reference";

    public static final String DERIVEDCOL_TITLEID = "title_id";
    public static final String DERIVEDCOL_AUTHORID = "author_id";
    public static final String DERIVEDCOL_QUOTEID = "quote_id";

    private static final String LOGTAG = "DBHELPER";

    SQLiteDatabase mDB;
    Context mContext;

    public DatabaseHelper(Context context) {
        super(context,DB_NAME,null,DB_VERSION);
        String dbpath = context.getDatabasePath(DB_NAME).getPath();
        if (!ifDatabaseExists(dbpath)) {
            copyDatabaseFromAssets(context,dbpath,DB_NAME,buildAssetPath(new String[]{"databases"},DB_NAME));
        }
        mDB = this.getWritableDatabase();
    }

    @Override
    public void onConfigure(SQLiteDatabase db) {
        db.setForeignKeyConstraintsEnabled(true); // Turn Foreign Key support ON
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {

    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {
    }

    public Cursor getQuotesWithTitlesAndAuthors() {

        /*

        SELECT quote.quote, title.title, author.author
        FROM title_quote_map
           JOIN title ON title._id = title_quote_map.title_reference
           JOIN quote ON quote._id = title_quote_map.quote_reference
           JOIN author ON author._id = quote.author_reference
           ;

         */

        String table = TB_TITEQUOTEMAP +
                " JOIN " + TB_TITLE + " ON  " +
                TB_TITLE + "." + COL_TITLE_ID + " = " + TB_TITEQUOTEMAP + "." + COL_TITLEAUTHORMAP_TITLEREFERENCE +
                " JOIN " + TB_QUOTE + " ON " +
                TB_QUOTE + "." + COL_QUOTE_ID + " = " + TB_TITEQUOTEMAP + "." + COL_TITLEAUTHORMAP_QUOTEREFERENCE +
                " JOIN " + TB_AUTHOR + " ON " +
                TB_AUTHOR + "." + COL_AUTHOR_ID + " = " + TB_QUOTE + "." + COL_QUOTE_AUTHOR_REFERENCE;
        String[] columns = new String[]
                {
                        TB_QUOTE + "." + COL_QUOTE_ID,
                        COL_QUOTE_QUOTE,
                        COL_TITLE_TITLE,
                        COL_AUTHOR_AUTHOR,
                        TB_TITLE + "." + COL_TITLE_ID + " AS " + DERIVEDCOL_TITLEID,
                        TB_AUTHOR + "." + COL_AUTHOR_ID + " AS " + DERIVEDCOL_AUTHORID
                }
        ;
        return mDB.query(table,columns,null,null,null,null,null);
    }



    /**
     * Check to see if the Database exists,
     * if it doesn't exists then check to see if
     * the database directory exists,
     * if the directory(ies) does(do) not exist then make the directory(ies);
     *
     *
     * @param dbpath        The path to the database
     * @return              true if the database exists, else false
     */
    private boolean ifDatabaseExists(String dbpath) {
        Log.d(LOGTAG,new Object(){}.getClass().getEnclosingMethod().getName() + " initiated.");
        File db = new File(dbpath);
        if(db.exists()) return true;
        File dir = new File(db.getParent());
        if (!dir.exists()) {
            dir.mkdirs();
        }
        return false;
    }

    /**
     * Copy the Database from the assets folder
     * @param context
     * @param dbpath
     * @return
     */
    private boolean copyDatabaseFromAssets(Context context,String dbpath, String dbname, String asset) {
        String thisclass = new Object(){}.getClass().getEnclosingMethod().getName();
        Log.d(LOGTAG,thisclass + " initiated");
        InputStream assetsdb;
        OutputStream database;
        File db = new File(dbpath);
        int filesize;
        // Get the asset file
        try {
            Log.d(LOGTAG,thisclass + " attempting to find asset " + asset);
            assetsdb = context.getAssets().open(asset);
            filesize = assetsdb.available();
            Log.d(LOGTAG,thisclass + " asset " + asset +
                    " located successfully with a size of " +
                    Integer.toString(filesize)
            );
        } catch (IOException e) {
            Log.d(LOGTAG,thisclass + " Did not locate asset " + asset);
            e.printStackTrace();
            return false;
        }

        // Read the first 16 bytes from the asset file
        byte[] dbcheck = new byte[16];
        try {
            assetsdb.read(dbcheck,0,16);
        } catch (IOException e) {
            Log.d(LOGTAG, thisclass + " failed trying to read 16 bytes to check for a valid database. ");
            e.printStackTrace();
            return false;
        }

        // Check that the asset file is an SQLite database
        String chkdb = new String(dbcheck);
        if(!chkdb.equals("SQLite format 3\u0000")) {
            Log.d(LOGTAG,thisclass + " asset " +
                    asset +
                    " is not a valid SQLite Database File (found " +
                    chkdb +
                    " at bytes 1-16 instead of SQLite format 3)");
            try {
                assetsdb.close();
            } catch (IOException e) {
                // Not worth doing anything
            }
            return false;
        }
        // Close the asset file
        try {
            assetsdb.close();
        } catch (IOException e) {
            Log.d(LOGTAG,thisclass +
                    " failed to close assets file after checking for a valid database."
            );
            return false;
        }
        // Re-open the asset file
        try {
            assetsdb = context.getAssets().open(asset);
            filesize = assetsdb.available();
        } catch (IOException e) {
            Log.d(LOGTAG, thisclass +
                    " failed trying to re-open asset " +
                    asset +
                    " after checking for a valid database."
            );
            e.printStackTrace();
            return false;
        }

        // Read the entire asset file into a buffer
        Log.d(LOGTAG, thisclass +
                " copying asset database " +
                dbname +
                " into buffer of size " +
                filesize
        );
        byte[] buffer = new byte[filesize];
        // Close the asset file
        try {
            assetsdb.read(buffer);
            Log.d(LOGTAG,thisclass +
                    " closing asset database " + dbname
            );
            assetsdb.close();
        } catch (IOException e) {
            Log.d(LOGTAG, thisclass +
                    " failed while copying asset database " +
                    dbname +
                    " (or closing asset database)."
            );
            e.printStackTrace();
            return false;
        }
        // Open the new database file
        try {
            Log.d(LOGTAG,thisclass + " attempting to open new database file " + dbpath);
            database = new FileOutputStream(dbpath);
        } catch (IOException e) {
            Log.d(LOGTAG, thisclass + " failed to open new database file.");
            e.printStackTrace();
            return false;
        }
        // Write the new database file
        try {
            Log.d(LOGTAG, thisclass + " writing new database file " + dbpath);
            database.write(buffer);
        } catch (IOException e) {
            Log.d(LOGTAG, thisclass + " failed while writing new database file " + dbpath);
            e.printStackTrace();
            return false;
        }
        // Flush the new database file
        try {
            Log.d(LOGTAG, thisclass + " flushing new database file " + dbpath);
            database.flush();
        } catch (IOException e) {
            Log.d(LOGTAG, thisclass + " failed while flushing new database file " + dbpath);
            e.printStackTrace();
            return false;
        }
        // Close the new database file
        try {
            Log.d(LOGTAG, thisclass + " closing new database file " + dbpath);
            database.close();
        } catch (IOException e) {
            Log.d(LOGTAG, thisclass + " failed while closing new database file " + dbpath);
            e.printStackTrace();
            return  false;
        }
        Log.d(LOGTAG,new Object(){}.getClass().getEnclosingMethod().getName() + " completed.");
        return true;
    }

    /**
     * Build the sub-path to the asset, according to the directories specified
     *
     * @param directories   directories underneath the assets folder where
     *                      the asset files is located, null or empty
     *                      array if file is located directly in the
     *                      assets folder;
     *                      directories must be specified in the order
     *                      in which they appear in the path.
     * @param filename      The filename of the asset
     * @return              The fill sub-path to the asset
     */
    private String buildAssetPath(String[] directories, String filename) {
        StringBuilder sb = new StringBuilder();
        final String SEPERATOR = "/";
        if (directories != null && directories.length > 0) {
            for (String s: directories) {
                sb.append(s);
                if (!s.substring(s.length()-1,s.length()).equals(SEPERATOR)) {
                    sb.append(SEPERATOR);
                }
            }
            sb.append(filename);
            return sb.toString();
        } else {
            return filename;
        }
    }
}
