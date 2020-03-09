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
public interface TaskCantiereImgDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insert(TaskCantiereImg img);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertAll(List<TaskCantiereImg> immagini);

    @Query("DELETE FROM task_cantiere_img where id_task_cantiere = :idTaskCantiere")
    int deleteByTaskCantienre(long idTaskCantiere);

    @Delete
    void deleteImaagine(TaskCantiereImg noteEntity);

    @Query("SELECT * FROM task_cantiere_img WHERE id_task_cantiere = :idTaskCantiere")
    TaskCantiereImg getImgByIdTaskCantiere(int idTaskCantiere);


    @Query("SELECT COUNT(*) FROM task_cantiere_img where id_task_cantiere = :idTaskCantiere ")
    int getCount(long idTaskCantiere);


}
