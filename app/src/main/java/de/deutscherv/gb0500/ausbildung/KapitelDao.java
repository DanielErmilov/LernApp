package de.deutscherv.gb0500.ausbildung;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;

@Dao
public interface KapitelDao {
    @Insert
    long insert(Kapitel kaptiel);
    @Query("SELECT * FROM Kapitel")
    List<Kapitel> getAll();
    @Query("SELECT * FROM Kapitel WHERE id = :id")
    Kapitel findById(int id);
    @Query("SELECT * FROM Kapitel WHERE sprache_id = :sprache_id")
    List<Kapitel> findBySpracheId(int sprache_id);
    @Query("SELECT * FROM Kapitel WHERE titel = :kapitelTitel")
    Kapitel findByTitel(String kapitelTitel);
    @Update
    void update(Kapitel kaptiel);
    @Delete
    void delete(Kapitel kaptiel);
    @Query("DELETE FROM Kapitel")
    void deleteAll();
}
