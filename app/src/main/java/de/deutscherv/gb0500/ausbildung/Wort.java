package de.deutscherv.gb0500.ausbildung;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.PrimaryKey;

@Entity(foreignKeys = {@ForeignKey(entity = Kapitel.class, parentColumns = "id", childColumns = "kapitel_id")})
public class Wort {
    @PrimaryKey(autoGenerate = true)
    private int id;
    @ColumnInfo(name = "wort")
    private String wort;
    @ColumnInfo(name = "uebersetzung")
    private String uebersetzung;
    @ColumnInfo(name = "kapitel_id")
    private int kapitel_id;

    public Wort(String wort, String uebersetzung, int kapitel_id){
        this.wort = wort;
        this.uebersetzung = uebersetzung;
        this.kapitel_id = kapitel_id;
    }

    public int getKapitel_id() {
        return kapitel_id;
    }

    public void setKapitel_id(int kaptiel_id) {
        this.kapitel_id = kaptiel_id;
    }

    public String getUebersetzung() {
        return uebersetzung;
    }

    public void setUebersetzung(String uebersetzung) {
        this.uebersetzung = uebersetzung;
    }

    public String getWort() {
        return wort;
    }

    public void setWort(String wort) {
        this.wort = wort;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

}
