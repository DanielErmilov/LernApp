package de.deutscherv.gb0500.ausbildung;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

import java.io.Serializable;

@Entity
public class Benutzer implements Serializable {
    @PrimaryKey(autoGenerate = true)
    private int id;
    @ColumnInfo(name = "name")
    private String name;
    @ColumnInfo(name = "email")
    private String email;
    @ColumnInfo(name = "passwort")
    private String passwort;
    @ColumnInfo(name = "autologin")
    private boolean autologin;

    public Benutzer(String name, String email, String passwort, boolean autologin) {
        this.name = name;
        this.email = email;
        this.passwort = passwort;
        this.autologin = autologin;
    }

    public String getEmail() {
        return email;
    }

    public String getName() {
        return name;
    }

    public String getPasswort() {
        return passwort;
    }

    public boolean isAutologin() {
        return autologin;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setPasswort(String passwort) {
        this.passwort = passwort;
    }

    public void setAutologin(boolean autologin) {
        this.autologin = autologin;
    }

}
