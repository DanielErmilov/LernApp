package de.deutscherv.gb0500.ausbildung;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.Insert;
import androidx.room.PrimaryKey;

@Entity(foreignKeys = {@ForeignKey(entity = Benutzer.class, parentColumns = "id", childColumns = "benutzer_id"), @ForeignKey(entity = Kapitel.class, parentColumns = "id", childColumns = "kapitel_id")})
public class BenutzerKapitel {
    @PrimaryKey(autoGenerate = true)
    private int id;
    @ColumnInfo(name = "benutzer_id")
    private int benutzer_id;
    @ColumnInfo(name = "kapitel_id")
    private int kapitel_id;

    public BenutzerKapitel(int benutzer_id, int kapitel_id) {
        this.benutzer_id = benutzer_id;
        this.kapitel_id = kapitel_id;
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

    public void setBenutzer_id(int benutzer_id) {
        this.benutzer_id = benutzer_id;
    }

    public int getKapitel_id() {
        return kapitel_id;
    }

    public void setKapitel_id(int kapitel_id) {
        this.kapitel_id = kapitel_id;
    }
}
