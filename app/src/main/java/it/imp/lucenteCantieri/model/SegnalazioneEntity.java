package it.imp.lucenteCantieri.model;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Entity(tableName = "segnalazione")
public class SegnalazioneEntity {
	@PrimaryKey(autoGenerate = true)
	@ColumnInfo(name = "id_segnalazione")
	public Long idSegnalazione;

	@ColumnInfo(name = "id_cliente")
	public Long idCliente;

	@ColumnInfo(name = "descrizione")
	public String descrizione;

	@ColumnInfo(name = "id_cliente_squadra")
	public Long idClienteSquadra;

	@ColumnInfo(name="data_creazione")
	public Date dataCreazione = new Date();

	@ColumnInfo(name="id_cliente_gerachia")
	public Long idClienteGerachia;

	@Ignore
	public SegnalazioneEntity(){
	}

	public SegnalazioneEntity(Long idSegnalazione, Long idCliente, String descrizione, Long idClienteSquadra, Date dataCreazione, Long idClienteGerachia) {
		this.idSegnalazione = idSegnalazione;
		this.idCliente = idCliente;
		this.descrizione = descrizione;
		this.idClienteSquadra = idClienteSquadra;
		this.dataCreazione = dataCreazione;
		this.idClienteGerachia = idClienteGerachia;
	}

	public Long getIdSegnalazione() {
		return idSegnalazione;
	}

	public void setIdSegnalazione(Long idSegnalazione) {
		this.idSegnalazione = idSegnalazione;
	}

	public Long getIdCliente() {
		return idCliente;
	}

	public void setIdCliente(Long idCliente) {
		this.idCliente = idCliente;
	}

	public String getDescrizione() {
		return descrizione;
	}

	public void setDescrizione(String descrizione) {
		this.descrizione = descrizione;
	}

	public Long getIdClienteSquadra() {
		return idClienteSquadra;
	}

	public void setIdClienteSquadra(Long idClienteSquadra) {
		this.idClienteSquadra = idClienteSquadra;
	}

	public Date getDataCreazione() {
		return dataCreazione;
	}

	public void setDataCreazione(Date dataCreazione) {
		this.dataCreazione = dataCreazione;
	}

	public Long getIdClienteGerachia() {
		return idClienteGerachia;
	}

	public void setIdClienteGerachia(Long idClienteGerachia) {
		this.idClienteGerachia = idClienteGerachia;
	}
}
