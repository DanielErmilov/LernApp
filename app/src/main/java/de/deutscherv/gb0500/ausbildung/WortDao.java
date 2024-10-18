package de.deutscherv.gb0500.ausbildung;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;

import java.util.List;

@Dao
public interface WortDao {
    @Insert
    void insert(Wort wort);
    @Query("SELECT * FROM Wort")
    List<Wort> getAll();
    @Query("SELECT * FROM Wort WHERE id = :id")
    Wort findById(int id);
    @Query("SELECT * FROM Wort WHERE kapitel_id = :kapitel_id")
    List<Wort> findByKapitelId(int kapitel_id);
    @Query("DELETE FROM Wort")
    void deleteAll();
}
