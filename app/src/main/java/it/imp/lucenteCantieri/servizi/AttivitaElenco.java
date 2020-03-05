package it.imp.lucenteCantieri.servizi;

class AttivitaElenco {
    public AttivitaElenco(Long idTaskCantiere, String descLivello, String descrizione) {
        this.idTaskCantiere = idTaskCantiere;
        this.descLivello = descLivello;
        this.descrizione = descrizione;
    }

    public Long idTaskCantiere;
    public String descLivello;
    public String descrizione;

    public Long idClienteGeranchia;
    public Long idTicket;

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

    public String getDescLivello() {
        return descLivello;
    }

    public void setDescLivello(String descLivello) {
        this.descLivello = descLivello;
    }

    public String getDescrizione() {
        return descrizione;
    }

    public void setDescrizione(String descrizione) {
        this.descrizione = descrizione;
    }

    public Long getIdTicket() {
        return idTicket;
    }

    public void setIdTicket(Long idTicket) {
        this.idTicket = idTicket;
    }
}
