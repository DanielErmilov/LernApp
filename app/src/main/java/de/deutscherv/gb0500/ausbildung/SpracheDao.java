package de.deutscherv.gb0500.ausbildung;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;

@Dao
public interface SpracheDao {
    @Insert
    long insert(Sprache sprache);
    @Query("SELECT * FROM Sprache")
    List<Sprache> getAll();
    @Query("SELECT * FROM Sprache WHERE id = :id")
    Sprache findById(int id);
    @Query("SELECT * FROM Sprache WHERE name = :name")
    Sprache findByName(String name);
    @Query("SELECT COUNT(*) FROM Sprache")
    int countSprachen();
    @Update
    void update(Sprache sprache);
    @Delete
    void delete(Sprache sprache);
    @Query("DELETE FROM Sprache")
    void deleteAll();
}
