package liq.developers.naumencontestapp.Net;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

import liq.developers.naumencontestapp.Computer;

import static liq.developers.naumencontestapp.Net.GetAllComputersOnPage.getCompanyFromJson;

/**
 * Created by Michael on 10.05.2017.
 */

public class GetASingleComputer {

    public static Computer getComp (int id) throws IOException, ParseException {

        String requestUrl = "http://testwork.nsd.naumen.ru/rest/computers/" + id;

        Computer bad = new Computer("Nothing to display\nOr failed to connect to the server", 0); //Будет возвращен, если запрос не был успешен

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

            return getCompFromJson(strBuilder.toString());
        }

        return bad;
    }

    public static Computer getCompFromJson (String str) throws ParseException {

        JSONParser parser = new JSONParser();
        JSONObject object = (JSONObject) parser.parse(str);

        //проверка свойств на NULL
        Computer.Company company;
        String imageUrl;
        String description;

        if (object.containsKey("company"))
        {
            company = getCompanyFromJson(object.get("company").toString());
        }
        else {
            company =  new Computer.Company("", 0);
            }
        if (object.containsKey("imageUrl"))
        {
            imageUrl = object.get("imageUrl").toString();
        }
        else {
            imageUrl = "";
        }
        if (object.containsKey("description"))
        {
            description = object.get("description").toString();
        }
        else
        {
            description = "";
        }

        return new Computer(object.get("name").toString(), Integer.parseInt(object.get("id").toString()), company, imageUrl, description );

    }


}



