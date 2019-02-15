package com.bgeiotdev.eval;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class DatabaseManager extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "Game.db";
    private static final int DATABASE_VERSION = 2;

    public DatabaseManager( Context context ) {
        super( context, DATABASE_NAME, null, DATABASE_VERSION );
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String strSql = "create table T_Scores ("
                + "    idScore integer primary key autoincrement,"
                + "    name text not null,"
                + "    score integer not null,"
                + "    when_ integer not null"
                + ")";
        db.execSQL( strSql );
        Log.i( "DATABASE", "onCreate invoked" );
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        //String strSql = "alter table T_Scores add column ...";
        String strSql = "drop table T_Scores";
        db.execSQL( strSql );
        this.onCreate( db );
        Log.i( "DATABASE", "onUpgrade invoked" );
    }

    public void insertScore( String name, int score ) {
        name = name.replace( "'", "''" );
        String strSql = "insert into T_Scores (name, score, when_) values ('"
                + name + "', " + score + ", " + new Date().getTime() + ")";
        this.getWritableDatabase().execSQL( strSql );
        Log.i( "DATABASE", "insertScore invoked" );
    }

    public List<ScoreData> readTop10() {
        List<ScoreData> scores = new ArrayList<>();

        // 1ère technique : SQL
        //String strSql = "select * from T_Scores order by score desc limit 10";
        //Cursor cursor = this.getReadableDatabase().rawQuery( strSql, null );

        // 2nd technique "plus objet"
        Cursor cursor = this.getReadableDatabase().query( "T_Scores",
                new String[] { "idScore", "name", "score", "when_" },
                null, null, null, null, "score desc", "10" );
        cursor.moveToFirst();
        while( ! cursor.isAfterLast() ) {
            ScoreData score = new ScoreData( cursor.getInt( 0 ), cursor.getString( 1 ),
                    cursor.getInt( 2 ), new Date( cursor.getLong( 3 ) ) );
            scores.add( score );
            cursor.moveToNext();
        }
        cursor.close();

        return scores;
    }

}