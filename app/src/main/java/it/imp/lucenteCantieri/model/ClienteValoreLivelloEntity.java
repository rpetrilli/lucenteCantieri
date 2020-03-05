package it.imp.lucenteCantieri.model;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;

import it.imp.lucenteCantieri.retrofit.ClienteLivello;

@Entity(tableName = "clienti_valori_livello")
public class ClienteValoreLivelloEntity {
    @PrimaryKey
    @ColumnInfo(name = "id_cliente_livello")
    public Long idClienteLivello;
    @ColumnInfo(name = "desc_voce_livello")
    public String descVoceLivello;
    @ColumnInfo(name = "cod_voce_livello")
    public String codVoceLivello;
    @ColumnInfo(name = "livello")
    public Integer livello;
    @ColumnInfo(name = "ordinamento")
    public Integer ordinamento;

    public ClienteValoreLivelloEntity(Long idClienteLivello, String descVoceLivello, String codVoceLivello, Integer livello, Integer ordinamento) {
        this.idClienteLivello = idClienteLivello;
        this.descVoceLivello = descVoceLivello;
        this.codVoceLivello = codVoceLivello;
        this.livello = livello;
        this.ordinamento = ordinamento;
    }

    @Ignore
    public ClienteValoreLivelloEntity() {
    }

    @Ignore
    public ClienteValoreLivelloEntity(ClienteLivello valore) {
        this.idClienteLivello = valore.idClienteLivello;
        this.descVoceLivello = valore.descVoceLivello;
        this.codVoceLivello = valore.codVoceLivello;
        this.livello = valore.livello;
        this.ordinamento = valore.ordinamento;
    }

    public Long getIdClienteLivello() {
        return idClienteLivello;
    }

    public void setIdClienteLivello(Long idClienteLivello) {
        this.idClienteLivello = idClienteLivello;
    }

    public String getDescVoceLivello() {
        return descVoceLivello;
    }

    public void setDescVoceLivello(String descVoceLivello) {
        this.descVoceLivello = descVoceLivello;
    }

    public String getCodVoceLivello() {
        return codVoceLivello;
    }

    public void setCodVoceLivello(String codVoceLivello) {
        this.codVoceLivello = codVoceLivello;
    }

    public Integer getLivello() {
        return livello;
    }

    public void setLivello(Integer livello) {
        this.livello = livello;
    }

    public Integer getOrdinamento() {
        return ordinamento;
    }

    public void setOrdinamento(Integer ordinamento) {
        this.ordinamento = ordinamento;
    }
}
