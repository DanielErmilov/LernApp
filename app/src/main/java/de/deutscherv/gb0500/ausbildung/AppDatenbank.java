package de.deutscherv.gb0500.ausbildung;

import androidx.room.Database;
import androidx.room.RoomDatabase;

@Database(entities = {Benutzer.class, Sprache.class, Kapitel.class, Wort.class, BenutzerWort.class, BenutzerKapitel.class}, version = 1)
public abstract class AppDatenbank extends RoomDatabase {

    public abstract BenutzerDao benutzerDao();

    public abstract SpracheDao spracheDao();

    public abstract KapitelDao kapitelDao();

    public abstract WortDao wortDao();

    public abstract BenutzerWortDao benutzerWortDao();

    public abstract BenutzerKapitelDao benutzerKapitelDao();
}

