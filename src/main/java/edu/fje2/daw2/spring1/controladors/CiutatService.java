package edu.fje2.daw2.spring1.controladors;

import edu.fje2.daw2.spring1.model.Ciutat;
import edu.fje2.daw2.spring1.repositoris.CiutatRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;
import java.net.*;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.util.ArrayList;
import java.util.List;

@Service
public class CiutatService {
    List<Ciutat> ciutats = new ArrayList<>();
    @Autowired
    private CiutatRepository ciutatRepository;

    public void fetchPrevisioCiutats(List<Ciutat> ciutats) {
        for (Ciutat ciutat : ciutats) {
            try {
                URL url = new URL(ciutat.getConsultaFetch());
                //System.out.println("URL: " + ciutat.getConsultaFetch());
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                connection.setRequestMethod("GET");
                connection.setConnectTimeout(5000);
                connection.setReadTimeout(5000);

                int responseCode = connection.getResponseCode();
                if (responseCode == HttpURLConnection.HTTP_OK) {
                    BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                    String inputLine;
                    StringBuffer response = new StringBuffer();
                    while ((inputLine = in.readLine()) != null) {
                        response.append(inputLine);
                    }
                    in.close();
                    String previsioJSON = response.toString();
                    ciutat.setPrevisioJSON(previsioJSON);
                    ciutatRepository.save(ciutat);
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public void actualitzarPrevisioCiutats(){


        Ciutat barcelona = new Ciutat("Barcelona", "https://api.open-meteo.com/v1/forecast?latitude=41.39&longitude=2.16&daily=weathercode&forecast_days=3&timezone=Europe%2FBerlin", null);
        ciutats.add(barcelona);

        Ciutat madrid = new Ciutat("Madrid", "https://api.open-meteo.com/v1/forecast?latitude=40.42&longitude=-3.70&daily=weathercode&forecast_days=3&timezone=Europe%2FBerlin", null);
        ciutats.add(madrid);

        Ciutat almeria = new Ciutat("Almeria", "https://api.open-meteo.com/v1/forecast?latitude=36.84&longitude=-2.46&daily=weathercode&forecast_days=3&timezone=Europe%2FBerlin", null);
        ciutats.add(almeria);

        Ciutat milan = new Ciutat("Milan", "https://api.open-meteo.com/v1/forecast?latitude=45.46&longitude=9.19&daily=weathercode&forecast_days=3&timezone=Europe%2FBerlin", null);
        ciutats.add(milan);

        Ciutat porto = new Ciutat("Porto", "https://api.open-meteo.com/v1/forecast?latitude=41.15&longitude=-8.61&daily=weathercode&forecast_days=3&timezone=Europe%2FBerlin", null);
        ciutats.add(porto);

        fetchPrevisioCiutats(ciutats);
    }
}
