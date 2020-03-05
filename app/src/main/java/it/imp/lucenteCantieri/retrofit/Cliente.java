package it.imp.lucenteCantieri.retrofit;

import java.util.ArrayList;
import java.util.List;


public class Cliente {
	public Long idCliente;
	public Long idSoggetto;
	public String denominazione;
	public String indirizzo;
	public String codiceGestionale;
	public String denominazioneSoggetto;
	public List<ClienteRiferimento> riferimenti = new ArrayList<>();
    
	public String capoArea;
	public String capoAreaDenominazione;
	public String  coordinatore;
	public String coordinatoreDenominazione;
	public String supervisore;
	public String supervisoreDenominazione;
	public String capoCantiere;
	public String capoCantiereDenominazione;
	public Integer nrLivelli = 1;
	public List<ClienteLivello> valoriLivelli = new ArrayList<>();
    public List<ClienteGerarchia> gerarchia = new ArrayList<>();
    
    public String codIstat;
    public List<ClienteSquadra> squadre = new ArrayList<>();
    


	public Long getIdSoggetto() {
		return idSoggetto;
	}

	public void setIdSoggetto(Long idSoggetto) {
		this.idSoggetto = idSoggetto;
	}

	public String getDenominazione() {
		return denominazione;
	}

	public void setDenominazione(String denominazione) {
		this.denominazione = denominazione;
	}

	public List<ClienteRiferimento> getRiferimenti() {
		return riferimenti;
	}

	public void setRiferimenti(List<ClienteRiferimento> riferimenti) {
		this.riferimenti = riferimenti;
	}

	public Long getIdCliente() {
		return idCliente;
	}

	public void setIdCliente(Long idCliente) {
		this.idCliente = idCliente;
	}

	public String getIndirizzo() {
		return indirizzo;
	}

	public void setIndirizzo(String indirizzo) {
		this.indirizzo = indirizzo;
	}

	public String getCodiceGestionale() {
		return codiceGestionale;
	}

	public void setCodiceGestionale(String codiceGestionale) {
		this.codiceGestionale = codiceGestionale;
	}

	public String getDenominazioneSoggetto() {
		return denominazioneSoggetto;
	}

	public void setDenominazioneSoggetto(String denominazioneSoggetto) {
		this.denominazioneSoggetto = denominazioneSoggetto;
	}


	public String getCapoArea() {
		return capoArea;
	}

	public void setCapoArea(String capoArea) {
		this.capoArea = capoArea;
	}

	public String getCoordinatore() {
		return coordinatore;
	}

	public void setCoordinatore(String coordinatore) {
		this.coordinatore = coordinatore;
	}

	public String getSupervisore() {
		return supervisore;
	}

	public void setSupervisore(String supervisore) {
		this.supervisore = supervisore;
	}

	public String getCapoCantiere() {
		return capoCantiere;
	}

	public void setCapoCantiere(String capoCantiere) {
		this.capoCantiere = capoCantiere;
	}

	public Integer getNrLivelli() {
		return nrLivelli;
	}

	public void setNrLivelli(Integer nrLivelli) {
		this.nrLivelli = nrLivelli;
	}

	public List<ClienteLivello> getValoriLivelli() {
		return valoriLivelli;
	}

	public void setValoriLivelli(List<ClienteLivello> valoriLivelli) {
		this.valoriLivelli = valoriLivelli;
	}

	public List<ClienteGerarchia> getGerarchia() {
		return gerarchia;
	}

	public void setGerarchia(List<ClienteGerarchia> gerarchia) {
		this.gerarchia = gerarchia;
	}

	public String getCapoAreaDenominazione() {
		return capoAreaDenominazione;
	}

	public void setCapoAreaDenominazione(String capoAreaDenominazione) {
		this.capoAreaDenominazione = capoAreaDenominazione;
	}

	public String getCoordinatoreDenominazione() {
		return coordinatoreDenominazione;
	}

	public void setCoordinatoreDenominazione(String coordinatoreDenominazione) {
		this.coordinatoreDenominazione = coordinatoreDenominazione;
	}

	public String getSupervisoreDenominazione() {
		return supervisoreDenominazione;
	}

	public void setSupervisoreDenominazione(String supervisoreDenominazione) {
		this.supervisoreDenominazione = supervisoreDenominazione;
	}

	public String getCapoCantiereDenominazione() {
		return capoCantiereDenominazione;
	}

	public void setCapoCantiereDenominazione(String capoCantiereDenominazione) {
		this.capoCantiereDenominazione = capoCantiereDenominazione;
	}

	public String getCodIstat() {
		return codIstat;
	}

	public void setCodIstat(String codIstat) {
		this.codIstat = codIstat;
	}

	public List<ClienteSquadra> getSquadre() {
		return squadre;
	}

	public void setSquadre(List<ClienteSquadra> squadre) {
		this.squadre = squadre;
	}


	
	
	
	
}
