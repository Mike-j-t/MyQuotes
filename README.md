# MyQuotes
MyQuotes example of copying external database


**Step 1 – Create the database externally**

Create database externally in DB Browser for SQLite. For this example the new database (be.db) was created and then the following SQL was executed :-

    DROP TABLE IF EXISTS title_quote_map;
    DROP TABLE IF EXISTS quote;
    DROP TABLE IF EXISTS author;
    DROP TABLE IF EXISTS title;

    CREATE TABLE IF NOT EXISTS author (_id INTEGER PRIMARY KEY, author TEXT UNIQUE);
    CREATE TABLE IF NOT EXISTS title (_id INTEGER PRIMARY KEY, title TEXT UNIQUE);
    CREATE TABLE IF NOT EXISTS quote (_id INTEGER PRIMARY KEY, quote TEXT UNIQUE, author_reference INTEGER REFERENCES author(_id));
    CREATE TABLE IF NOT EXISTS title_quote_map (
        title_reference INTEGER REFERENCES title(_id), 
        quote_reference INTEGER REFERENCES quote(_id), 
        UNIQUE (title_reference, quote_reference));

    INSERT INTO title (title) VALUES 
        ('Life'),
        ('Happiness'),
        ('Positivity'),
        ('Famous Quotes'),
        ('Friendship'),
        ('Love'),
        ('Family'),
        ('Motivation')
    ;

    INSERT INTO author (author) VALUES
        ('Leonardo da-Vinci'),
        ('Mahatma Ghandi'),
        ('Winston Churchill'),
        ('anon')
    ;

    INSERT INTO quote (quote,author_reference) VALUES
        ('Life is my message.',2),
        ('Not how long, but how well you have lived.',4),
        ('Never in the field of human combat have so many owed so much to so few',3),
        ('I love those who can smile in trouble',1)
    ;

    INSERT INTO title_quote_map VALUES
        (1,1),(1,2),(1,4), -- Life quotes
        (2,2),(2,4), -- Happiness quotes
        (3,1),(3,2),(3,4), -- Positivity quotes
        (4,1),(4,3),(4,4), -- Famous quotes
        (6,4), -- Love quotes
        (8,1),(8,2),(8,4)
    ;

The file was saved (click on Write Changes).

**Step 2 – Create the project**
Create a Project in Android Studio.
Dialog 1
Application name used was  MyQuotes with a package name of myquotes. 
The projects location was D:\Android_Applications\MyQuotes 
Dialog 2
Form factors and minimum SDK was Phone and Tablet and the API was 16 (Jelly Bean).
Dialog 3
Empty Activity was chosen.
Dialog 4
Activity name used was MainActivity
General Layout ticked
Layout name used was activity_main.xml
Backwards compatibility was ticked
The default App was run to check that at this stage it is OK.

**Step 3 – Copy the database file into the assets/databases folder**
In file explorer go to the location of the database.
Check that its size is greater than 16k (if not then open it in DB Browser amend if need be and save (example was 36K)).
Right click on the file and copy.
Go to the Application’s directory open direct app then src then main and create a folder/directory name assets and then within the assets folder create a folder named databases. Finally paste the file into the database folder.

**Step 4 – Create the Database Helper DatabaseHelper.java**
Copy the file from https://github.com/Mike-j-t/MyQuotes

**Step 5 – Modify MainActivity.java**

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
