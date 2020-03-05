package it.imp.lucenteCantieri.model;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;

import it.imp.lucenteCantieri.retrofit.ClienteGerarchia;

@Entity(tableName = "clienti_gerachia")
public class ClienteGerarchiaEntity {
    @PrimaryKey
    @ColumnInfo(name = "id_cliente_geranchia")
    public Long idClienteGerachia;

    @ColumnInfo(name = "id_livello_1")
    public Long idLivello1;

    @ColumnInfo(name = "id_livello_2")
    public Long idLivello2;

    @ColumnInfo(name = "id_livello_3")
    public Long idLivello3;

    @ColumnInfo(name = "id_livello_4")
    public Long idLivello4;

    @ColumnInfo(name = "id_livello_5")
    public Long idLivello5;

    @ColumnInfo(name = "id_livello_6")
    public Long idLivello6;

    @ColumnInfo(name="estenzione")
    public Double estenzione;

    @ColumnInfo(name="ubicazione")
    public String ubicazione;

    @ColumnInfo(name="desc_livello")
    public String descLivello;

    @ColumnInfo(name="ordinamento")
    public Integer ordinamento;

    public ClienteGerarchiaEntity(Long idClienteGerachia,
                                  Long idLivello1, Long idLivello2, Long idLivello3,
                                  Long idLivello4, Long idLivello5, Long idLivello6,
                                  Double estenzione, String ubicazione,
                                  String descLivello, Integer ordinamento) {
        this.idClienteGerachia = idClienteGerachia;
        this.idLivello1 = idLivello1;
        this.idLivello2 = idLivello2;
        this.idLivello3 = idLivello3;
        this.idLivello4 = idLivello4;
        this.idLivello5 = idLivello5;
        this.idLivello6 = idLivello6;
        this.estenzione = estenzione;
        this.ubicazione = ubicazione;
        this.descLivello = descLivello;
        this.ordinamento = ordinamento;
    }

    @Ignore
    public ClienteGerarchiaEntity(ClienteGerarchia valore) {
        this.idClienteGerachia = valore.idClienteGerachia;
        this.idLivello1 = valore.idLivello1;
        this.idLivello2 = valore.idLivello2;
        this.idLivello3 = valore.idLivello3;
        this.idLivello4 = valore.idLivello4;
        this.idLivello5 = valore.idLivello5;
        this.idLivello6 = valore.idLivello6;
        this.estenzione = valore.estenzione;
        this.ubicazione = valore.ubicazione;
        this.descLivello = valore.descLivello;
    }

    public Long getIdClienteGerachia() {
        return idClienteGerachia;
    }

    public void setIdClienteGerachia(Long idClienteGerachia) {
        this.idClienteGerachia = idClienteGerachia;
    }

    public Long getIdLivello1() {
        return idLivello1;
    }

    public void setIdLivello1(Long idLivello1) {
        this.idLivello1 = idLivello1;
    }

    public Long getIdLivello2() {
        return idLivello2;
    }

    public void setIdLivello2(Long idLivello2) {
        this.idLivello2 = idLivello2;
    }

    public Long getIdLivello3() {
        return idLivello3;
    }

    public void setIdLivello3(Long idLivello3) {
        this.idLivello3 = idLivello3;
    }

    public Long getIdLivello4() {
        return idLivello4;
    }

    public void setIdLivello4(Long idLivello4) {
        this.idLivello4 = idLivello4;
    }

    public Long getIdLivello5() {
        return idLivello5;
    }

    public void setIdLivello5(Long idLivello5) {
        this.idLivello5 = idLivello5;
    }

    public Long getIdLivello6() {
        return idLivello6;
    }

    public void setIdLivello6(Long idLivello6) {
        this.idLivello6 = idLivello6;
    }

    public Double getEstenzione() {
        return estenzione;
    }

    public void setEstenzione(Double estenzione) {
        this.estenzione = estenzione;
    }

    public String getUbicazione() {
        return ubicazione;
    }

    public void setUbicazione(String ubicazione) {
        this.ubicazione = ubicazione;
    }

    public String getDescLivello() {
        return descLivello;
    }

    public void setDescLivello(String descLivello) {
        this.descLivello = descLivello;
    }

    public Integer getOrdinamento() {
        return ordinamento;
    }

    public void setOrdinamento(Integer ordinamento) {
        this.ordinamento = ordinamento;
    }
}
