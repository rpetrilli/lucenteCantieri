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
public interface TaskCantiereDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insert(TaskCantiereEntity valoreLivello);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertAll(List<TaskCantiereEntity> valoriLivelli);

    @Query("DELETE FROM task_cantiere")
    int deleteAll();

    @Query("DELETE FROM task_cantiere where id_task_cantiere = :id")
    int deleteById(long id);


    @Delete
    void deleteNote(TaskCantiereEntity noteEntity);

    @Query("SELECT * FROM task_cantiere WHERE id_task_cantiere = :id")
    TaskCantiereEntity getNoteById(long id);

    @Query("SELECT * FROM task_cantiere ORDER BY data_prestazione DESC")
    LiveData<List<TaskCantiereEntity>> getAll();

    @Query("SELECT COUNT(*) FROM task_cantiere")
    int getCount();

    @Query("SELECT * FROM task_cantiere WHERE data_prestazione = :dt and not eseguita")
    List<TaskCantiereEntity> getByDate(Date dt);

}
