package it.imp.lucenteCantieri.model;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import java.util.Date;
import java.util.List;

@Dao
public interface SegnalazioniDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    long insert(SegnalazioneEntity item);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertAll(List<SegnalazioneEntity> list);

    @Query("DELETE FROM segnalazione")
    int deleteAll();

    @Query("DELETE FROM segnalazione where id_segnalazione = :id")
    int deleteById(long id);


    @Delete
    void delete(SegnalazioneEntity item);

    @Query("SELECT * FROM segnalazione WHERE id_segnalazione = :id")
    SegnalazioneEntity getById(long id);

    @Query("SELECT * FROM segnalazione")
    List<TaskCantiereEntity> getAll();

}
