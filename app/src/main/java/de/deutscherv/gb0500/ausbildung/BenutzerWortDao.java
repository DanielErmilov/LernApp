package de.deutscherv.gb0500.ausbildung;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;

@Dao
public interface BenutzerWortDao {
    @Insert
    void insert(BenutzerWort benutzerWort);
    @Query("SELECT * FROM BenutzerWort")
    List<BenutzerWort> getAll();
    @Query("SELECT * FROM BenutzerWort WHERE benutzer_id = :benutzer_id")
    List<BenutzerWort> findByBenutzerId(int benutzer_id);
    @Query("SELECT * FROM BenutzerWort WHERE benutzer_id = :benutzer_id AND kapitel_id =:kaptiel_id")
    List<BenutzerWort> findWoerterByBenutzerAndKapitelId(int benutzer_id, int kaptiel_id);
    @Query("SELECT * FROM BenutzerWort WHERE benutzer_id = :benutzer_id AND sprache_id =:sprache_id")
    BenutzerWort findByBenutzerIDAndSpracheID(int benutzer_id, int sprache_id);
    @Query("DELETE FROM BenutzerWort WHERE benutzer_id = :benutzer_id")
    void deleteByBenutzerId(int benutzer_id);
    @Update
    void update(BenutzerWort benutzerWort);
    @Delete
    void delete(BenutzerWort benutzerWort);
    @Query("DELETE FROM BenutzerWort")
    void deleteAll();
}
