package de.deutscherv.gb0500.ausbildung;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.PrimaryKey;

@Entity(foreignKeys = {@ForeignKey(entity = Sprache.class, parentColumns = "id", childColumns = "sprache_id")})
public class Kapitel {
    @PrimaryKey(autoGenerate = true)
    private int id;
    @ColumnInfo(name = "titel")
    private String titel;
    @ColumnInfo(name = "beschreibung")
    private String beschreibung;
    @ColumnInfo(name = "kapitelNummer")
    private int kapitelNummer;
    @ColumnInfo(name = "sprache_id")
    private int sprache_id;


    public Kapitel(String titel, String beschreibung, int kapitelNummer, int sprache_id) {
        this.titel = titel;
        this.beschreibung = beschreibung;
        this.kapitelNummer = kapitelNummer;
        this.sprache_id = sprache_id;

    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getTitel() {
        return titel;
    }

    public void setTitel(String titel) {
        this.titel = titel;
    }

    public String getBeschreibung() {
        return beschreibung;
    }

    public void setBeschreibung(String beschreibung) {
        this.beschreibung = beschreibung;

    }

    public int getKapitelNummer() {
        return kapitelNummer;
    }

    public void setKapitelNummer(int kapitelNummer) {
        this.kapitelNummer = kapitelNummer;
    }

    public int getSprache_id() {
        return sprache_id;
    }

    public void setSprache_id(int sprache_id) {
        this.sprache_id = sprache_id;
    }
}
