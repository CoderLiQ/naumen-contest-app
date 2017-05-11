package liq.developers.naumencontestapp.Net;

/**
 * Created by Michael on 11.05.2017.
 */

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import liq.developers.naumencontestapp.Computer;

public class GetSimilarComputers {

    public static List<Computer> getData(int id) throws IOException, ParseException {

        String requestUrl = "http://testwork.nsd.naumen.ru/rest/computers/" + id + "/similar";

        ArrayList<Computer> bad = new ArrayList<>(); //Будет возвращен, если запрос не был успешен

        URL url = new URL(requestUrl);
        HttpURLConnection httpConnection = (HttpURLConnection) url.openConnection();
        try {
            httpConnection.connect();
        }
        catch (Exception e){
            return bad;
        }
        int rc = httpConnection.getResponseCode();

        if (rc == 200) {
            String line;
            BufferedReader buffReader = new BufferedReader(new InputStreamReader(httpConnection.getInputStream()));
            StringBuilder strBuilder = new StringBuilder();
            while ((line = buffReader.readLine()) != null) {
                strBuilder.append(line + '\n');
            }

            return getDataFromJson(strBuilder.toString());
        }

        else {
            bad.add(new Computer("Nothing to display\nOr failed to connect to the server", 0)); return bad;
        }

    }


    public static List<Computer> getDataFromJson(String str) throws ParseException { //парсинг

        List<Computer> computers = new ArrayList<>();
        JSONParser parser = new JSONParser();
        JSONArray array = (JSONArray) parser.parse(str);

        for (Object s : array) {
            System.out.println(s.toString());
            computers.add(getComputerFromJson(s.toString()));
        }

        return computers;
    }
    public static Computer getComputerFromJson(String str) throws ParseException{

        JSONParser parser = new JSONParser();
        JSONObject object = (JSONObject) parser.parse(str);

        return new Computer(object.get("name").toString(), Integer.parseInt(object.get("id").toString()));
    }
}
