package it.imp.lucenteCantieri.servizi;

import android.content.Context;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import org.apache.commons.beanutils.BeanUtilsBean;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.text.SimpleDateFormat;
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
import it.imp.lucenteCantieri.model.SegnalazioneEntity;
import it.imp.lucenteCantieri.model.SegnalazioniDao;
import it.imp.lucenteCantieri.model.TaskCantiereDao;
import it.imp.lucenteCantieri.model.TaskCantiereEntity;
import it.imp.lucenteCantieri.model.TaskCantiereImg;
import it.imp.lucenteCantieri.model.TaskCantiereImgDao;
import it.imp.lucenteCantieri.retrofit.Cliente;
import it.imp.lucenteCantieri.retrofit.ClienteGerarchia;
import it.imp.lucenteCantieri.retrofit.ClienteLivello;
import it.imp.lucenteCantieri.retrofit.IBackOfficeService;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Response;
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
    Context mContext = null;

    public AppService(Context context) {

        Gson gson = new GsonBuilder()
                .setDateFormat("yyyy-MM-dd")
                .create();

        Retrofit retrofit = new Retrofit.Builder().baseUrl(settings.baseUrl)
                .addConverterFactory(GsonConverterFactory.create(gson))
                .build();
        mBackOfficeService = retrofit.create(IBackOfficeService.class);
        mDb = AppDatabase.getInstance(context);
        mContext = context;
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

        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd");

        List<TaskCantiereEntity> tasks = mBackOfficeService
                .leggiPianoDiLavoro(settings.idClienteSquadra, settings.passwd, df.format(inizio), df.format(fine))
                .execute().body();

        TaskCantiereDao taskCantienreDao = mDb.taskCantiereDao();
        taskCantienreDao.deleteAll();
        taskCantienreDao.insertAll(tasks);

        TaskCantiereImgDao taskCantienreDaoImp = mDb.taskCantiereImgDao();
        taskCantienreDaoImp.deleteAll();

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


    public List<AttivitaElenco> leggiTaskCantiere(Date dt, NodoAlbero nodoAlbero){

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
            boolean skip = (nodoAlbero.idLivello1 !=null && nodoAlbero.idLivello1!= nodo.idLivello1)
                    || (nodoAlbero.idLivello2 !=null && nodoAlbero.idLivello2!= nodo.idLivello2)
                    || (nodoAlbero.idLivello3 !=null && nodoAlbero.idLivello3!= nodo.idLivello3)
                    || (nodoAlbero.idLivello4 !=null && nodoAlbero.idLivello4!= nodo.idLivello4)
                    || (nodoAlbero.idLivello5 !=null && nodoAlbero.idLivello5!= nodo.idLivello5)
                    || (nodoAlbero.idLivello6 !=null && nodoAlbero.idLivello6!= nodo.idLivello6);
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


    public List<ClienteGerarchiaEntity> leggiGerachiaPerNodoAlbero(NodoAlbero nodoAlbero){
        if (gerarchia == null){
            ClienteGerarchiaDao clienteGerachiaDao = mDb.clienteGerarchiaDao();
            gerarchia = clienteGerachiaDao.getAll();
        }

        List<ClienteGerarchiaEntity> ret = new ArrayList<>();

        for(ClienteGerarchiaEntity nodo: gerarchia){
            boolean skip = (nodoAlbero.idLivello1 !=null && nodoAlbero.idLivello1!= nodo.idLivello1)
                    || (nodoAlbero.idLivello2 !=null && nodoAlbero.idLivello2!= nodo.idLivello2)
                    || (nodoAlbero.idLivello3 !=null && nodoAlbero.idLivello3!= nodo.idLivello3)
                    || (nodoAlbero.idLivello4 !=null && nodoAlbero.idLivello4!= nodo.idLivello4)
                    || (nodoAlbero.idLivello5 !=null && nodoAlbero.idLivello5!= nodo.idLivello5)
                    || (nodoAlbero.idLivello6 !=null && nodoAlbero.idLivello6!= nodo.idLivello6);
            if (skip)
                continue;

            ret.add(nodo);

        }
        return ret;

    }


    /**
     * Legge se ci sono le descrizioni dei singoli livello
     * @return
     */
    public ArrayList<String> descrizioniFiltro(NodoAlbero nodoAlbero){
        if (valori == null){
            ClienteLivelliDao clienteDao = mDb.clienteLivelloDao();
            valori = clienteDao.getAll();
        }
        ArrayList<String> ret = new ArrayList<>();

        Map<Long, String> mDesc = new HashMap<Long, String>();
        for(ClienteValoreLivelloEntity val: valori){
            if ((nodoAlbero.idLivello1 !=null && val.idClienteLivello == nodoAlbero.idLivello1)
                || (nodoAlbero.idLivello2 !=null && val.idClienteLivello == nodoAlbero.idLivello2)
                    || (nodoAlbero.idLivello3 !=null && val.idClienteLivello == nodoAlbero.idLivello3)
                    || (nodoAlbero.idLivello4 !=null && val.idClienteLivello == nodoAlbero.idLivello4)
                    || (nodoAlbero.idLivello5 !=null && val.idClienteLivello == nodoAlbero.idLivello5)
                    || (nodoAlbero.idLivello6 !=null && val.idClienteLivello == nodoAlbero.idLivello6))
            {
                mDesc.put(val.idClienteLivello, val.descVoceLivello);
            }
        }
        if (nodoAlbero.idLivello1 !=null )
            ret.add(mDesc.get(nodoAlbero.idLivello1));
        if (nodoAlbero.idLivello2 != null)
            ret.add(mDesc.get(nodoAlbero.idLivello2));
        if (nodoAlbero.idLivello3 !=null)
            ret.add(mDesc.get(nodoAlbero.idLivello3));
        if (nodoAlbero.idLivello4 !=null)
            ret.add(mDesc.get(nodoAlbero.idLivello4));
        if (nodoAlbero.idLivello5 !=null)
            ret.add(mDesc.get(nodoAlbero.idLivello5));
        if (nodoAlbero.idLivello6 !=null)
            ret.add(mDesc.get(nodoAlbero.idLivello6));

        return ret;
//        return Arrays.asList("Fab 1", "BAGNO");
    }

    public ArrayList<String> descrizioniFiltro(UbicazioneCantiere ubicazioneCantiere){
        return descrizioniFiltro(new NodoAlbero(ubicazioneCantiere));
    }


    public void closeAttivita(Long idTaskCantiere) {
        TaskCantiereDao taskCantiereDto = mDb.taskCantiereDao();
        TaskCantiereEntity  item = taskCantiereDto.getById(idTaskCantiere);
        item.eseguita = true;
        taskCantiereDto.insert(item);
    }

    public UbicazioneCantiere ubicazionecantiere(NodoAlbero nodoAlbero){
        if (valori == null){
            ClienteLivelliDao clienteDao = mDb.clienteLivelloDao();
            valori = clienteDao.getAll();
        }
        UbicazioneCantiere  ret = new UbicazioneCantiere(nodoAlbero);

        Map<Long, String> mDesc = new HashMap<Long, String>();
        for(ClienteValoreLivelloEntity val: valori){
            if ((nodoAlbero.idLivello1 !=null && val.idClienteLivello == nodoAlbero.idLivello1)
                    || (nodoAlbero.idLivello2 !=null && val.idClienteLivello == nodoAlbero.idLivello2)
                    || (nodoAlbero.idLivello3 !=null && val.idClienteLivello == nodoAlbero.idLivello3)
                    || (nodoAlbero.idLivello4 !=null && val.idClienteLivello == nodoAlbero.idLivello4)
                    || (nodoAlbero.idLivello5 !=null && val.idClienteLivello == nodoAlbero.idLivello5)
                    || (nodoAlbero.idLivello6 !=null && val.idClienteLivello == nodoAlbero.idLivello6))
            {
                mDesc.put(val.idClienteLivello, val.descVoceLivello);
            }
        }
        if (nodoAlbero.idLivello1 !=null )
            ret.descLivello1= mDesc.get(nodoAlbero.idLivello1);
        if (nodoAlbero.idLivello2 != null)
            ret.descLivello2= mDesc.get(nodoAlbero.idLivello2);
        if (nodoAlbero.idLivello3 !=null)
            ret.descLivello3= mDesc.get(nodoAlbero.idLivello3);
        if (nodoAlbero.idLivello4 !=null)
            ret.descLivello4= mDesc.get(nodoAlbero.idLivello4);
        if (nodoAlbero.idLivello5 !=null)
            ret.descLivello5= mDesc.get(nodoAlbero.idLivello5);
        if (nodoAlbero.idLivello6 !=null)
            ret.descLivello6= mDesc.get(nodoAlbero.idLivello6);


        return ret;

    }


    public void sendTaskCantiere(Long idTaskCantiere, String note) throws IOException {
        TaskCantiereDao daoTaskCantiere = mDb.taskCantiereDao();
        TaskCantiereEntity task = daoTaskCantiere.getById(idTaskCantiere);
        task.note = note;

        task.dataPrestazione = new Date();
        List<TaskCantiereEntity> tasksDaConfermare = new ArrayList<>();
        tasksDaConfermare.add(task);

        Response<List<TaskCantiereEntity>> response = mBackOfficeService
                .confermaTasks(settings.idClienteSquadra, settings.passwd, tasksDaConfermare)
                .execute();
        if (!response.isSuccessful()){
            throw new IOException("Comunicazione con il server non possibile");
        }

        TaskCantiereImgDao taskCantiereImgDao = mDb.taskCantiereImgDao();
        List<TaskCantiereImg> immagini = taskCantiereImgDao.getImgByIdTaskCantiere(idTaskCantiere);
        for(TaskCantiereImg img: immagini ){

            File file =new File(img.nomeImmagine);
            RequestBody requestFile = RequestBody.create(MediaType.parse("multipart/form-data"), file);

            MultipartBody.Part multipartBody =MultipartBody.Part.createFormData("file",file.getName(),requestFile);

            mBackOfficeService.uploadAllegato(settings.idClienteSquadra, settings.passwd, idTaskCantiere, multipartBody).execute();
        }
        taskCantiereImgDao.deleteByTaskCantienre(idTaskCantiere);

        task.eseguita = true;
        daoTaskCantiere.deleteById(idTaskCantiere);


    }

    public void sendSegnalazione(Long idSegnalazione) throws IOException {
        SegnalazioniDao segnalazioniDao = mDb.segnalazioniDao();
        SegnalazioneEntity segnalazione = segnalazioniDao.getById(idSegnalazione);

        List<SegnalazioneEntity> segnalazioni = new ArrayList<>();
        segnalazioni.add(segnalazione);

        Response<List<SegnalazioneEntity>> response = mBackOfficeService
                .inviaSegnalazione(settings.idClienteSquadra, settings.passwd, segnalazioni)
                .execute();
        if (!response.isSuccessful()){
            throw new IOException("Comunicazione con il server non possibile");
        }
        segnalazioniDao.deleteById(idSegnalazione);


    }


    public TaskCantiereImg salvaImmagine(Long idTaskCantiere, String imageFilePath) {
        TaskCantiereImg taskCantiereImg = new TaskCantiereImg();
        taskCantiereImg.idTaskCantiere = idTaskCantiere;
        taskCantiereImg.nomeImmagine = imageFilePath;

        mDb.taskCantiereImgDao().insert(taskCantiereImg);
        return taskCantiereImg;

    }

    public List<TaskCantiereImg> leggiImmagini(Long idTaskCantiere) {
        return mDb.taskCantiereImgDao().getImgByIdTaskCantiere(idTaskCantiere);
    }

    public SegnalazioneEntity salvaSegnalazione(SegnalazioneEntity segnalazione) {
        segnalazione.idSegnalazione =  mDb.segnalazioniDao().insert(segnalazione);
        return segnalazione;
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

    public void deletePhoto(TaskCantiereImg photo) {
        File file = new File(photo.nomeImmagine);
        boolean deleted = file.delete();
        mDb.taskCantiereImgDao().deleteById(photo.idTaskCantiereImg);
    }


    public void deleteAllPhotos(Long idTaskCantiere) {
        for(TaskCantiereImg item: mDb.taskCantiereImgDao().getImgByIdTaskCantiere(idTaskCantiere)){
            deletePhoto(item);
        }
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
