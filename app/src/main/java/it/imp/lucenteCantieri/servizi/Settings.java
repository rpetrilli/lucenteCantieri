package it.imp.lucenteCantieri.servizi;

import android.content.Context;
import android.os.Build;

import androidx.annotation.RequiresApi;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.List;

public class Settings {
    private static final Settings ourInstance = new Settings();
    public static final String CONFIG_JSON = "config.json";

    public String baseUrl;
    public String passwd;

    public Long idClienteSquadra;
    public String descSquadra;
    public Long idCliente;
    public String denominazione;

    List<SettingsChangeListener> listeners = new ArrayList<>();

    public static Settings getInstance() {
        return ourInstance;
    }

    private Settings() {
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    public void save(Context context, String json) throws IOException, JSONException {

        OutputStreamWriter outputStreamWriter = new OutputStreamWriter(context.openFileOutput(CONFIG_JSON, Context.MODE_PRIVATE));
        outputStreamWriter.write(json);
        outputStreamWriter.close();

        readJson(json);

        listeners.stream().forEach(x-> x.settingsChanged(this));

    }


    public void read(Context context) throws IOException, JSONException {

        FileInputStream fileInputStream = context.openFileInput(CONFIG_JSON);
        InputStreamReader inputStreamReader = new InputStreamReader(fileInputStream);

        BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
        StringBuilder everything = new StringBuilder();
        String line;
        while( (line = bufferedReader.readLine()) != null) {
            everything.append(line);
        }
        readJson(everything.toString());

    }


    private void readJson(String json) throws JSONException {
        JSONObject jObject = new JSONObject(json);
        this.baseUrl = jObject.getString("baseUrl");
        this.passwd = jObject.getString("passwd");
        this.idClienteSquadra = jObject.getLong("idClienteSquadra");
        this.descSquadra = jObject.getString("descSquadra");
        this.idCliente = jObject.getLong("idCliente");
        this.denominazione = jObject.getString("denominazione");
    }


    public String getBaseUrl() {
        return baseUrl;
    }

    public void setBaseUrl(String baseUrl) {
        this.baseUrl = baseUrl;
    }

    public String getPasswd() {
        return passwd;
    }

    public void setPasswd(String passwd) {
        this.passwd = passwd;
    }

    public Long getIdClienteSquadra() {
        return idClienteSquadra;
    }

    public void setIdClienteSquadra(Long idClienteSquadra) {
        this.idClienteSquadra = idClienteSquadra;
    }

    public String getDescSquadra() {
        return descSquadra;
    }

    public void setDescSquadra(String descSquadra) {
        this.descSquadra = descSquadra;
    }

    public Long getIdCliente() {
        return idCliente;
    }

    public void setIdCliente(Long idCliente) {
        this.idCliente = idCliente;
    }

    public String getDenominazione() {
        return denominazione;
    }

    public void setDenominazione(String denominazione) {
        this.denominazione = denominazione;
    }





}
