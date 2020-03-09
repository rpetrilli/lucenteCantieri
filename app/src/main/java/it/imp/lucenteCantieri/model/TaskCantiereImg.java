package it.imp.lucenteCantieri.model;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "task_cantiere_img")
public class TaskCantiereImg {
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "id_task_cantiere_img")
    public int idTaskCantiereImg;

    @ColumnInfo(name = "id_task_cantiere")
    public Long idTaskCantiere;

    @ColumnInfo(name = "nome_immagine"  )
    public String nomeImmagine;

    public int getIdTaskCantiereImg() {
        return idTaskCantiereImg;
    }

    public void setIdTaskCantiereImg(int idTaskCantiereImg) {
        this.idTaskCantiereImg = idTaskCantiereImg;
    }

    public Long getIdTaskCantiere() {
        return idTaskCantiere;
    }

    public void setIdTaskCantiere(Long idTaskCantiere) {
        this.idTaskCantiere = idTaskCantiere;
    }

    public String getNomeImmagine() {
        return nomeImmagine;
    }

    public void setNomeImmagine(String nomeImmagine) {
        this.nomeImmagine = nomeImmagine;
    }
}
