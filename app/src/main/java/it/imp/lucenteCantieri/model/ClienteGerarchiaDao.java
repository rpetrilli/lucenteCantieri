package it.imp.lucenteCantieri.model;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import java.util.List;

import it.imp.lucenteCantieri.retrofit.ClienteLivello;

@Dao
public interface ClienteGerarchiaDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insert(ClienteGerarchiaEntity nodoGerachia);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertAll(List<ClienteGerarchiaEntity> nodiGerachia);

    @Query("DELETE FROM clienti_gerachia")
    int deleteAll();

    @Delete
    void deleteNote(ClienteGerarchiaEntity noteEntity);

    @Query("SELECT * FROM clienti_gerachia WHERE id_cliente_geranchia = :id")
    ClienteGerarchiaEntity getNoteById(int id);

    @Query("SELECT * FROM clienti_gerachia ORDER BY ordinamento DESC")
    LiveData<List<ClienteGerarchiaEntity>> getAll();

    @Query("SELECT COUNT(*) FROM clienti_gerachia")
    int getCount();

}
