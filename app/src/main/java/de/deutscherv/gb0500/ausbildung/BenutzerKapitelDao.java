package de.deutscherv.gb0500.ausbildung;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;

import java.util.List;

@Dao
public interface BenutzerKapitelDao {
    @Insert
    void insert(BenutzerKapitel benutzerKapitel);
    @Query("SELECT * FROM BenutzerKapitel")
    List<BenutzerKapitel> getALl();
    @Query("SELECT * FROM BenutzerKapitel WHERE benutzer_id = :benutzer_id")
    List<BenutzerKapitel> findByBenutzerId(int benutzer_id);
    @Query("DELETE FROM BenutzerKapitel WHERE benutzer_id = :benutzer_id")
    void deleteByBenutzerId(int benutzer_id);
    @Query("DELETE FROM BenutzerKapitel")
    void deleteAll();
}
