package liq.developers.naumencontestapp;

/**
 * Created by Michael on 09.05.2017.
 */

public class Computer {

    public Computer(String name, int id, Company company)
    {
        this.name = name;
        this.id = id;
        this.company = company;
    }

    public Computer(String name, int id)
    {
        this.name = name;
        this.id = id;
    }

    public Computer(String name, int id, Company company, String imageUrl, String description)
    {
        this.name = name;
        this.id = id;
        this.company = company;
        this.imageUrl = imageUrl;
        this.description = description;
    }

    String name;
    int id;
    Company company;
    String imageUrl;
    String description;

    public static class Company {

        public Company(String name, int id){

            this.name = name;
            this.id = id;
        }
        String name;
        int id;
    }
}

