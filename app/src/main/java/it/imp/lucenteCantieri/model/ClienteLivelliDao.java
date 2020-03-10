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
public interface ClienteLivelliDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insert(ClienteValoreLivelloEntity valoreLivello);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertAll(List<ClienteGerarchiaEntity> valoriLivelli);

    @Query("DELETE FROM clienti_valori_livello")
    int deleteAll();

    @Delete
    void deleteNote(ClienteValoreLivelloEntity noteEntity);

    @Query("SELECT * FROM clienti_valori_livello WHERE id_cliente_livello = :id")
    ClienteValoreLivelloEntity getById(int id);

    @Query("SELECT * FROM clienti_valori_livello ORDER BY ordinamento ASC")
    List<ClienteValoreLivelloEntity> getAll();

    @Query("SELECT COUNT(*) FROM clienti_valori_livello")
    int getCount();

}
