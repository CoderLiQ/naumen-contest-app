package liq.developers.naumencontestapp.Net;


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

import liq.developers.naumencontestapp.Computer;


/**
 * Created by Michael on 09.05.2017.
 */

public class GetAllComputersOnPage {

    static ArrayList<Computer> bad = new ArrayList<>(); //Будет возвращен, если запрос не был успешен

    public static ArrayList<Computer> getData(int page) throws IOException, ParseException {

        String requestUrl = "http://testwork.nsd.naumen.ru/rest/computers?p="+page;

        URL url = new URL(requestUrl);
        HttpURLConnection httpConnection = (HttpURLConnection) url.openConnection();
        try {
            httpConnection.connect();
        }
        catch (Exception e){

            bad.add(new Computer("Nothing to display", 0, new Computer.Company("Or failed to connect to the server", 0)));
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
            bad.add(new Computer("Nothing to display", 0, new Computer.Company("Or failed to connect to the server", 0)));
            return bad;
        }
    }

    public static ArrayList<Computer> getDataFromJson(String str) throws ParseException { //парсинг

        ArrayList<Computer> computers = new ArrayList<>();


        JSONParser parser = new JSONParser();
        JSONObject object = (JSONObject) parser.parse(str);


        JSONArray array = (JSONArray) object.get("items");

        for (Object s : array) {

            //System.out.println(s.toString());
            computers.add(getComputerFromJson(s.toString()));
        }



        return computers;

    }

    public static Computer getComputerFromJson(String str) throws ParseException{

        JSONParser parser = new JSONParser();
        JSONObject object = (JSONObject) parser.parse(str);

        //проверка свойства "Компания" на NULL
        Computer.Company company;
        if (object.containsKey("company"))
        {
          company = getCompanyFromJson(object.get("company").toString());
        }
        else {
            company =  new Computer.Company("", 0);
        }
        return new Computer(object.get("name").toString(), Integer.parseInt(object.get("id").toString()), company);
    }

    public static Computer.Company getCompanyFromJson(String str) throws ParseException{

        //if (str != null){
        JSONParser parser = new JSONParser();
        JSONObject object = (JSONObject) parser.parse(str);
        return new Computer.Company(object.get("name").toString(),Integer.parseInt(object.get("id").toString()));
        //}


    }


}