package it.imp.lucenteCantieri.retrofit;


public class ClienteRiferimento implements Cloneable {
    public Long idClienteRiferimento;
    public String denominazione;
    public String telefono;
    public String email;
    public String funzione;
    public String note;

    public Integer ordinamento;


    public ClienteRiferimento() {
    }

    public Long getIdClienteRiferimento() {
        return idClienteRiferimento;
    }

    public void setIdClienteRiferimento(Long idClienteRiferimento) {
        this.idClienteRiferimento = idClienteRiferimento;
    }

    public String getDenominazione() {
        return denominazione;
    }

    public void setDenominazione(String denominazione) {
        this.denominazione = denominazione;
    }

    public String getTelefono() {
        return telefono;
    }

    public void setTelefono(String telefono) {
        this.telefono = telefono;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public Integer getOrdinamento() {
        return ordinamento;
    }

    public void setOrdinamento(Integer ordinamento) {
        this.ordinamento = ordinamento;
    }

    public String getFunzione() {
        return funzione;
    }

    public void setFunzione(String funzione) {
        this.funzione = funzione;
    }

    public String getNote() {
        return note;
    }

    public void setNote(String note) {
        this.note = note;
    }

    @Override
    public ClienteRiferimento clone() {
        try {
            return (ClienteRiferimento) super.clone();
        } catch (CloneNotSupportedException e) {
            return null;
        }
    }

}
