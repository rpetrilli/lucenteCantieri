package it.imp.lucenteCantieri.servizi;

class UbicazioneCantiere {
    public Long idLivello1;
    public String descLivello1;

    public Long idLivello2;
    public String descLivello2;

    public Long idLivello3;
    public String descLivello3;

    public Long idLivello4;
    public String descLivello4;

    public Long idLivello5;
    public String descLivello5;

    public Long idLivello6;
    public String descLivello6;


    public UbicazioneCantiere(){
    }

    public UbicazioneCantiere(NodoAlbero nodoAlbero){
        this.idLivello1 = nodoAlbero.idLivello1;
        this.idLivello2 = nodoAlbero.idLivello2;
        this.idLivello3 = nodoAlbero.idLivello3;
        this.idLivello4 = nodoAlbero.idLivello4;
        this.idLivello5 = nodoAlbero.idLivello5;
        this.idLivello6 = nodoAlbero.idLivello6;

    }

    public Long getIdLivello1() {
        return idLivello1;
    }

    public void setIdLivello1(Long idLivello1) {
        this.idLivello1 = idLivello1;
    }

    public String getDescLivello1() {
        return descLivello1;
    }

    public void setDescLivello1(String descLivello1) {
        this.descLivello1 = descLivello1;
    }

    public Long getIdLivello2() {
        return idLivello2;
    }

    public void setIdLivello2(Long idLivello2) {
        this.idLivello2 = idLivello2;
    }

    public String getDescLivello2() {
        return descLivello2;
    }

    public void setDescLivello2(String descLivello2) {
        this.descLivello2 = descLivello2;
    }

    public Long getIdLivello3() {
        return idLivello3;
    }

    public void setIdLivello3(Long idLivello3) {
        this.idLivello3 = idLivello3;
    }

    public String getDescLivello3() {
        return descLivello3;
    }

    public void setDescLivello3(String descLivello3) {
        this.descLivello3 = descLivello3;
    }

    public Long getIdLivello4() {
        return idLivello4;
    }

    public void setIdLivello4(Long idLivello4) {
        this.idLivello4 = idLivello4;
    }

    public String getDescLivello4() {
        return descLivello4;
    }

    public void setDescLivello4(String descLivello4) {
        this.descLivello4 = descLivello4;
    }

    public Long getIdLivello5() {
        return idLivello5;
    }

    public void setIdLivello5(Long idLivello5) {
        this.idLivello5 = idLivello5;
    }

    public String getDescLivello5() {
        return descLivello5;
    }

    public void setDescLivello5(String descLivello5) {
        this.descLivello5 = descLivello5;
    }

    public Long getIdLivello6() {
        return idLivello6;
    }

    public void setIdLivello6(Long idLivello6) {
        this.idLivello6 = idLivello6;
    }

    public String getDescLivello6() {
        return descLivello6;
    }

    public void setDescLivello6(String descLivello6) {
        this.descLivello6 = descLivello6;
    }
}
