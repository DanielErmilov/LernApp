package de.deutscherv.gb0500.ausbildung;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;

@Dao
public interface BenutzerDao {
    @Insert
    void insert(Benutzer benutzer);
    @Query("SELECT * FROM Benutzer")
    List<Benutzer> getAll();
    @Query("SELECT * FROM Benutzer WHERE id = :id")
    Benutzer findById(int id);
    @Query("SELECT * FROM Benutzer WHERE name = :name")
    Benutzer findByName(String name);
    @Query("SELECT * FROM Benutzer WHERE email = :email")
    Benutzer findByEmail(String email);
    @Update
    void update(Benutzer benutzer);
    @Delete
    void delete(Benutzer benutzer);
    @Query("DELETE FROM Benutzer")
    void deleteAll();


}
