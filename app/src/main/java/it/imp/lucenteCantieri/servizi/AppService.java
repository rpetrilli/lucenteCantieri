package it.imp.lucenteCantieri.servizi;

import android.content.Context;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import org.apache.commons.beanutils.BeanUtilsBean;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

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
    private Executor executor = Executors.newSingleThreadExecutor();

    List<ClienteGerarchiaEntity> gerarchia = null;
    List<ClienteValoreLivelloEntity> valori = null;

    List<NodoAlbero> mAlbero = null;

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

    /**
     * leggi dal database l'albero visualizzato nel drawer
     * @return
     * @throws IOException
     * @throws IllegalAccessException
     * @throws NoSuchMethodException
     * @throws InvocationTargetException
     */

    public List<NodoAlbero> getAlberoDrawer() throws IOException, IllegalAccessException, NoSuchMethodException, InvocationTargetException {
        List<NodoAlbero> albero = new ArrayList<>();


        ClienteLivelliDao clienteDao = mDb.clienteLivelloDao();
        ClienteGerarchiaDao clienteGerachiaDao = mDb.clienteGerarchiaDao();

        gerarchia = clienteGerachiaDao.getAll();
        valori = clienteDao.getAll();

        BeanUtilsBean beanUtils = new BeanUtilsBean();

        int nrLivelli = 0;
        for(ClienteValoreLivelloEntity  v: valori)
            if(v.livello > nrLivelli)
        nrLivelli = v.livello;
        List<Long> filtri = new ArrayList<>();
        leggiLivello(albero, nrLivelli, 1, filtri, beanUtils);

        this.mAlbero = albero;
        int idx = 0;
        for(NodoAlbero item: albero){
            item.id = idx++;
            item.show = item.livello == 1;
            item.hasChildren = item.livello == 1;
        }
        return albero;
    }


    public List<NodoAlbero> toggleNodo(NodoAlbero item){

        int idx = this.mAlbero.indexOf(item);
        this.mAlbero.get(idx).figliVisibili = !this.mAlbero.get(idx).figliVisibili;

        if (item.hasChildren){
            for(int i = idx + 1; i < this.mAlbero.size(); i++){
                if (this.mAlbero.get(i).livello <= item.livello){
                    break;
                }
                this.mAlbero.get(i).show = this.mAlbero.get(idx).figliVisibili;
            }
        }


        return this.mAlbero;
    }


    public List<AttivitaElenco> toggleNodo(Date dt, Long idLivello1 , Long idLivello2, Long idLivello3,
                                           Long idLivello4, Long idLivello5, Long idLivello6){

        if (gerarchia == null){
            ClienteGerarchiaDao clienteGerachiaDao = mDb.clienteGerarchiaDao();
            gerarchia = clienteGerachiaDao.getAll();
        }
        List<AttivitaElenco> ret = new ArrayList<>();

        TaskCantiereDao taskCantienreDao = mDb.taskCantiereDao();

        //Capisce quali sono i nodi ammessi
        Set<Long> gerarchiaAmmessa = new HashSet<>();
        Map<Long, ClienteGerarchiaEntity> mGerarchia = new HashMap<>();

        for(ClienteGerarchiaEntity nodo: gerarchia){
            boolean skip = (idLivello1 > 0 && idLivello1!= nodo.idLivello1)
                    || (idLivello2 > 0 && idLivello1!= nodo.idLivello2)
                    || (idLivello3 > 0 && idLivello1!= nodo.idLivello3)
                    || (idLivello4 > 0 && idLivello1!= nodo.idLivello4)
                    || (idLivello5 > 0 && idLivello1!= nodo.idLivello5)
                    || (idLivello6 > 0 && idLivello1!= nodo.idLivello6);
            if (skip)
                continue;

            gerarchiaAmmessa.add(nodo.idClienteGerachia);
            mGerarchia.put(nodo.idClienteGerachia, nodo);
        }


        //Creazione dell'elenco vero e proprio
        List<TaskCantiereEntity> tmp = taskCantienreDao.getByDate(dt);
        for(TaskCantiereEntity item: tmp){
            if (!gerarchiaAmmessa.contains(item.idClienteGeranchia))
                continue;

            ClienteGerarchiaEntity node = mGerarchia.get(item.idClienteGeranchia);
            AttivitaElenco nuovaAttivita = new AttivitaElenco(item.idTaskCantiere, node.descLivello, item.descrizione);

            ret.add((nuovaAttivita));
        }
//        ret.add(new AttivitaElenco(20L, "Bagno Primo Piano", "Pulizia"));
//        ret.add(new AttivitaElenco(21L, "Ufficio Direttore", "Svuatamento Cestino"));

        return ret;
    }


    /**
     * Legge se ci sono le descrizioni dei singoli livello
     * @param idLivello1
     * @param idLivello2
     * @param idLivello3
     * @param idLivello4
     * @param idLivello5
     * @param idLivello6
     * @return
     */
    public List<String> descrizioniFiltro(Long idLivello1 , Long idLivello2, Long idLivello3,
                                          Long idLivello4, Long idLivello5, Long idLivello6){
        if (valori == null){
            ClienteLivelliDao clienteDao = mDb.clienteLivelloDao();
            valori = clienteDao.getAll();
        }
        List<String> ret = new ArrayList<>();

        Map<Long, String> mDesc = new HashMap<Long, String>();
        for(ClienteValoreLivelloEntity val: valori){
            if ((idLivello1 !=null && val.idClienteLivello == idLivello1)
                || (idLivello2 !=null && val.idClienteLivello == idLivello2)
                    || (idLivello3 !=null && val.idClienteLivello == idLivello3)
                    || (idLivello4 !=null && val.idClienteLivello == idLivello4)
                    || (idLivello5 !=null && val.idClienteLivello == idLivello5)
                    || (idLivello6 !=null && val.idClienteLivello == idLivello6))
            {
                mDesc.put(val.idClienteLivello, val.descVoceLivello);
            }
        }
        if (idLivello1 > 0)
            ret.add(mDesc.get(idLivello1));
        if (idLivello2 > 0)
            ret.add(mDesc.get(idLivello2));
        if (idLivello3 > 0)
            ret.add(mDesc.get(idLivello3));
        if (idLivello4 > 0)
            ret.add(mDesc.get(idLivello4));
        if (idLivello5 > 0)
            ret.add(mDesc.get(idLivello5));
        if (idLivello6 > 0)
            ret.add(mDesc.get(idLivello6));

        return ret;
//        return Arrays.asList("Fab 1", "BAGNO");
    }

    private boolean leggiLivello(List<NodoAlbero> albero,
                              int nrLivelli, int livello,
                              List<Long> filtri, BeanUtilsBean beanUtils) throws IllegalAccessException, NoSuchMethodException, InvocationTargetException {
        if (livello > nrLivelli)
            return false;

        //Dopo il for key contiene i valori del livallo da inserire
        Set<Long> keys = new HashSet<>();
        for(ClienteGerarchiaEntity nodo: gerarchia){
            //Applica i filtri livello va da 1..6 in particolare controlla i livelli precedenti rispetto a quello considerato
            boolean skip = false;
            for(int i = 1 ; i <= livello -1 ; i ++){
                //legge il valore del livello i
                Long value = getLongValue(nodo, i);
                skip |=  value!= null && filtri.get(i -1) == value;
            }
            if (skip)   continue;
            //Fine FIltri

            Long value = getLongValue(nodo, livello);
            keys.add(value);
        }

        //Scorre tutti i valori e prende solo quelli da aggiungere
        boolean hasFigli = false;
        for(ClienteValoreLivelloEntity valore: valori){
            if (keys.contains(valore.idClienteLivello)){
                //Aggiunta livello
                NodoAlbero daAggiungere = new NodoAlbero(livello, valore.descVoceLivello);
                for(int i = 1 ; i <= livello -1 ; i ++){
                    setLongValue(daAggiungere, i, filtri.get(i -1));
                }
                setLongValue(daAggiungere, livello, valore.idClienteLivello);
                albero.add(daAggiungere);

                //Per il figlio anche il valore corrente fa da filtro
                List<Long> filtriFiglio = new ArrayList<>();
                filtriFiglio.addAll(filtri);
                filtriFiglio.add(valore.idClienteLivello);
                hasFigli = true;
                daAggiungere.hasChildren = leggiLivello(albero, nrLivelli, livello + 1, filtriFiglio, beanUtils);
            }
        }
        return hasFigli;


    }

    private Long getLongValue(ClienteGerarchiaEntity nodo, int idx) {
        switch (idx){
            case 1:
                return nodo.idLivello1;
            case 2:
                return nodo.idLivello2;
            case 3:
                return nodo.idLivello3;
            case 4:
                return nodo.idLivello4;
            case 5:
                return nodo.idLivello5;
            case 6:
                return nodo.idLivello6;
            default:
                return null;
        }
    }

    private void setLongValue(NodoAlbero nodo, int idx, Long value){
        switch (idx){
            case 1:
                nodo.idLivello1 = value;
                break;
            case 2:
                nodo.idLivello2 = value;
                break;
            case 3:
                nodo.idLivello3 = value;
                break;
            case 4:
                nodo.idLivello4 = value;
                break;
            case 5:
                nodo.idLivello5 = value;
                break;
            case 6:
                nodo.idLivello6 = value;
                break;
            default:

        }
    }




}
