package it.imp.lucenteCantieri.retrofit;

import java.util.Date;
import java.util.List;

import it.imp.lucenteCantieri.model.TaskCantiereEntity;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface IBackOfficeService {
    @GET("/api/mobile/leggiStruttura/{idClienteSquadra}/{passwd}")
    Call<Cliente> leggiStruttura(@Path("idClienteSquadra") long idClienteSquadra, @Path("passwd") String passwd );

    @GET("/api/mobile/leggiPianoDiLavoro/{idClienteSquadra}/{passwd}")
    Call<List<TaskCantiereEntity>> leggiPianoDiLavoro(@Path("idClienteSquadra") long idClienteSquadra, @Path("passwd") String passwd,
        @Query("inizio") Date inizio, @Query("fine") Date fine);
}
