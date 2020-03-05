package it.imp.lucenteCantieri.servizi;

import android.content.Context;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import it.imp.lucenteCantieri.model.AppDatabase;
import it.imp.lucenteCantieri.model.ClienteGerarchiaDao;
import it.imp.lucenteCantieri.model.ClienteLivelliDao;
import it.imp.lucenteCantieri.model.ClienteGerarchiaEntity;
import it.imp.lucenteCantieri.model.ClienteValoreLivelloEntity;
import it.imp.lucenteCantieri.model.TaskCantiereDao;
import it.imp.lucenteCantieri.model.TaskCantiereEntity;
import it.imp.lucenteCantieri.retrofit.Cliente;
import it.imp.lucenteCantieri.retrofit.ClienteGerarchia;
import it.imp.lucenteCantieri.retrofit.ClienteLivello;
import it.imp.lucenteCantieri.retrofit.IBackOfficeService;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class AppService implements  SettingsChangeListener {
    private static AppService ourInstance = null;

    public static AppService getInstance(Context context) {
        if (ourInstance == null)
            ourInstance = new AppService(context);
        return ourInstance;
    }

    IBackOfficeService mBackOfficeService;
    Settings settings = Settings.getInstance();
    private AppDatabase mDb;

    private AppService(Context context) {
        Retrofit retrofit = new Retrofit.Builder().baseUrl(settings.baseUrl)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        mBackOfficeService = retrofit.create(IBackOfficeService.class);
        mDb = AppDatabase.getInstance(context);
    }

    @Override
    public void settingsChanged(Settings settings) {
        Gson gson = new GsonBuilder()
                .setDateFormat("yyyy-MM-dd")
                .create();
        Retrofit retrofit = new Retrofit.Builder().baseUrl(settings.baseUrl)
                .addConverterFactory(GsonConverterFactory.create(gson))
                .build();
        mBackOfficeService = retrofit.create(IBackOfficeService.class);
    }


    public void downloadStruttura() throws IOException {

        Cliente cliente =  mBackOfficeService.leggiStruttura(settings.idClienteSquadra, settings.passwd).execute().body();
        ClienteLivelliDao clienteDao = mDb.clienteLivelloDao();

        TaskCantiereDao taskCantienreDao = mDb.taskCantiereDao();
        taskCantienreDao.deleteAll();

        for(ClienteLivello valore: cliente.valoriLivelli){
            ClienteValoreLivelloEntity entity = new ClienteValoreLivelloEntity(valore);
            clienteDao.insert(entity);
        }

        ClienteGerarchiaDao clienteGerachiaDao = mDb.clienteGerarchiaDao();
        for(ClienteGerarchia valore: cliente.gerarchia){
            ClienteGerarchiaEntity entity = new ClienteGerarchiaEntity(valore);
            clienteGerachiaDao.insert(entity);
        }
    }

    public void scaricaTaskCantiere() throws IOException{
        Date inizio = new Date();
        Calendar cal = Calendar.getInstance();
        cal.setTime(inizio);
        cal.add(Calendar.MONTH, 1);
        Date fine = cal.getTime();

        List<TaskCantiereEntity> tasks = mBackOfficeService
                .leggiPianoDiLavoro(settings.idClienteSquadra, settings.passwd, inizio, fine)
                .execute().body();

        TaskCantiereDao taskCantienreDao = mDb.taskCantiereDao();
        taskCantienreDao.deleteAll();
        taskCantienreDao.insertAll(tasks);
    }


    public List<NodoAlbero> getAlberoDrawer()throws IOException{
        List<NodoAlbero> ret = new ArrayList<>();
        ret.add(new NodoAlbero(1, "Fab 1", 38L, null, null, null, null, null, null));
        ret.add(new NodoAlbero(2, "UFFICI", 38L, 40L, null, null, null, null, null));
        ret.add(new NodoAlbero(2, "BAGNO", 38L, 41L, null, null, null, null, null));
        return ret;
    }

    public List<AttivitaElenco> getElencoAttivita(Date dt, Long idLivello1 , Long idLivello2, Long idLivello3,
                                                  Long idLivello4, Long idLivello5, Long idLivello6){
        List<AttivitaElenco> ret = new ArrayList<>();
        ret.add(new AttivitaElenco(20L, "Bagno Primo Piano", "Pulizia"));
        ret.add(new AttivitaElenco(21L, "Ufficio Direttore", "Svuatamento Cestino"));
        return ret;
    }


    public List<String> descrizioniFiltro(Long idLivello1 , Long idLivello2, Long idLivello3,
                                          Long idLivello4, Long idLivello5, Long idLivello6){
        return Arrays.asList("Fab 1", "BAGNO");
    }

}
