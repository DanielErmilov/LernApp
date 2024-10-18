package de.deutscherv.gb0500.ausbildung;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.PrimaryKey;

@Entity(foreignKeys = {@ForeignKey(entity = Benutzer.class, parentColumns = "id", childColumns = "benutzer_id"), @ForeignKey(entity = Wort.class, parentColumns = "id", childColumns = "wort_id")})
public class BenutzerWort {
    @PrimaryKey(autoGenerate = true)
    private int id;
    @ColumnInfo(name = "benutzer_id")
    private int benutzer_id;
    @ColumnInfo(name = "wort_id")
    private int wort_id;
    @ColumnInfo(name = "kapitel_id")
    private int kapitel_id;
    @ColumnInfo(name = "sprache_id")
    private int sprache_id;
    @ColumnInfo(name = "wiederholt")
    private boolean wiederholt;

    public BenutzerWort(int benutzer_id, int wort_id, int kapitel_id, int sprache_id, boolean wiederholt) {
        this.benutzer_id = benutzer_id;
        this.wort_id = wort_id;
        this.kapitel_id = kapitel_id;
        this.sprache_id = sprache_id;
        this.wiederholt = wiederholt;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getBenutzer_id() {
        return benutzer_id;
    }

    public int getSprache_id() {
        return sprache_id;
    }

    public void setSprache_id(int sprache_id) {
        this.sprache_id = sprache_id;
    }

    public int getKapitel_id() {
        return kapitel_id;
    }

    public void setKapitel_id(int kapitel_id) {
        this.kapitel_id = kapitel_id;
    }

    public void setBenutzer_id(int benutzer_id) {
        this.benutzer_id = benutzer_id;
    }

    public int getWort_id() {
        return wort_id;
    }

    public void setWort_id(int wort_id) {
        this.wort_id = wort_id;
    }

    public boolean isWiederholt() {
        return wiederholt;
    }

    public void setWiederholt(boolean wiederholt) {
        this.wiederholt = wiederholt;
    }
}
