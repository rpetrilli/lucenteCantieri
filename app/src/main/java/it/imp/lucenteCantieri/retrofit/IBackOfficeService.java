package it.imp.lucenteCantieri.retrofit;

import java.util.Date;
import java.util.List;

import it.imp.lucenteCantieri.model.TaskCantiereEntity;
import okhttp3.MultipartBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface IBackOfficeService {
    @GET("/api/mobile/leggiStruttura/{idClienteSquadra}/{passwd}")
    Call<Cliente> leggiStruttura(@Path("idClienteSquadra") long idClienteSquadra, @Path("passwd") String passwd );

    @GET("/api/mobile/leggiPianoDiLavoro/{idClienteSquadra}/{passwd}")
    Call<List<TaskCantiereEntity>> leggiPianoDiLavoro(@Path("idClienteSquadra") long idClienteSquadra, @Path("passwd") String passwd,
        @Query("inizio") String inizio, @Query("fine") String fine);

    @POST("/api/mobile/leggiPianoDiLavoro/{idClienteSquadra}/{passwd}")
    Call<List<TaskCantiereEntity>> confermaTasks(@Path("idClienteSquadra") long idClienteSquadra, @Path("passwd") String passwd,
        @Body List<TaskCantiereEntity> tasks);

    @Multipart
    @POST("/api/mobile/uploadFile/{idClienteSquadra}/{passwd}/{idTaskCantiere}")
    Call<ResponseBody> uploadAllegato(
            @Path("idClienteSquadra") long idClienteSquadra, @Path("passwd") String passwd,
            @Path("idTaskCantiere") long idTaskCantiere,
            @Part MultipartBody.Part file
    );

}
