package it.imp.lucenteCantieri.model;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

import java.util.Date;


@Entity(tableName = "task_cantiere")
public class TaskCantiereEntity {
	@PrimaryKey(autoGenerate = true)
	@ColumnInfo(name = "id_task_cantiere")
	public Long idTaskCantiere;
	

	@ColumnInfo(name="id_cliente_geranchia")
	public Long idClienteGeranchia;
	
	@ColumnInfo(name="descrizione")
	public String descrizione;

	@ColumnInfo(name="note")
	public String note; 
	
	@ColumnInfo(name="stato")
	public String stato;	

	@ColumnInfo(name="id_tipo_servizio")
	public String idTipoServizio;
	
	@ColumnInfo(name="id_attivita_soggetto")
	public Long idAttivitaSoggetto;
	
	@ColumnInfo(name="data_prestazione")
	public Date dataPrestazione;	   
	
	@ColumnInfo(name="eseguita")
	public Boolean eseguita;
	
	@ColumnInfo(name="id_contratto_oggetto")
	public Long idContrattoOggetto;
	
	@ColumnInfo(name="id_contratto")
	public Long idContratto;

	@ColumnInfo(name="id_ticket")
	public Long idTicket;


	public TaskCantiereEntity(Long idTaskCantiere, Long idClienteGeranchia, String descrizione, String note, String stato, String idTipoServizio, Long idAttivitaSoggetto, Date dataPrestazione, Boolean eseguita, Long idContrattoOggetto, Long idContratto, Long idTicket) {
		this.idTaskCantiere = idTaskCantiere;
		this.idClienteGeranchia = idClienteGeranchia;
		this.descrizione = descrizione;
		this.note = note;
		this.stato = stato;
		this.idTipoServizio = idTipoServizio;
		this.idAttivitaSoggetto = idAttivitaSoggetto;
		this.dataPrestazione = dataPrestazione;
		this.eseguita = eseguita;
		this.idContrattoOggetto = idContrattoOggetto;
		this.idContratto = idContratto;
		this.idTicket = idTicket;
	}

	public Long getIdTaskCantiere() {
		return idTaskCantiere;
	}

	public void setIdTaskCantiere(Long idTaskCantiere) {
		this.idTaskCantiere = idTaskCantiere;
	}

	public Long getIdClienteGeranchia() {
		return idClienteGeranchia;
	}

	public void setIdClienteGeranchia(Long idClienteGeranchia) {
		this.idClienteGeranchia = idClienteGeranchia;
	}

	public String getDescrizione() {
		return descrizione;
	}

	public void setDescrizione(String descrizione) {
		this.descrizione = descrizione;
	}

	public String getNote() {
		return note;
	}

	public void setNote(String note) {
		this.note = note;
	}

	public String getStato() {
		return stato;
	}

	public void setStato(String stato) {
		this.stato = stato;
	}

	public String getIdTipoServizio() {
		return idTipoServizio;
	}

	public void setIdTipoServizio(String idTipoServizio) {
		this.idTipoServizio = idTipoServizio;
	}

	public Long getIdAttivitaSoggetto() {
		return idAttivitaSoggetto;
	}

	public void setIdAttivitaSoggetto(Long idAttivitaSoggetto) {
		this.idAttivitaSoggetto = idAttivitaSoggetto;
	}

	public Date getDataPrestazione() {
		return dataPrestazione;
	}

	public void setDataPrestazione(Date dataPrestazione) {
		this.dataPrestazione = dataPrestazione;
	}

	public Boolean getEseguita() {
		return eseguita;
	}

	public void setEseguita(Boolean eseguita) {
		this.eseguita = eseguita;
	}

	public Long getIdContrattoOggetto() {
		return idContrattoOggetto;
	}

	public void setIdContrattoOggetto(Long idContrattoOggetto) {
		this.idContrattoOggetto = idContrattoOggetto;
	}

	public Long getIdContratto() {
		return idContratto;
	}

	public void setIdContratto(Long idContratto) {
		this.idContratto = idContratto;
	}

	public Long getIdTicket() {
		return idTicket;
	}

	public void setIdTicket(Long idTicket) {
		this.idTicket = idTicket;
	}
}
