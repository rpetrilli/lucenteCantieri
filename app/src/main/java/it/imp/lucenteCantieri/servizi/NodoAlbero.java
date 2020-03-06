package it.imp.lucenteCantieri.servizi;

public class NodoAlbero {
    public int livello = 0;
    public String descrizione ="";
    public Long idLivello1;
    public Long idLivello2;
    public Long idLivello3;
    public Long idLivello4;
    public Long idLivello5;
    public Long idLivello6;

    public NodoAlbero(int livello, String descrizione,
                      Long idLivello1 , Long idLivello2, Long idLivello3,
                      Long idLivello4, Long idLivello5, Long idLivello6,
                      Long idClienteGerachia) {
        this.livello = livello;
        this.descrizione = descrizione;
        this.idLivello1 = idLivello1;
        this.idLivello2 = idLivello2;
        this.idLivello3 = idLivello3;
        this.idLivello4 = idLivello4;
        this.idLivello5 = idLivello5;
        this.idLivello6 = idLivello6;
        this.idClienteGerachia = idClienteGerachia;
    }

    public NodoAlbero() {
    }




    public Long idClienteGerachia;



    public int getLivello() {
        return livello;
    }

    public void setLivello(int livello) {
        this.livello = livello;
    }

    public String getDescrizione() {
        return descrizione;
    }

    public void setDescrizione(String descrizione) {
        this.descrizione = descrizione;
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

    public Long getIdClienteGerachia() {
        return idClienteGerachia;
    }

    public void setIdClienteGerachia(Long idClienteGerachia) {
        this.idClienteGerachia = idClienteGerachia;
    }
}
