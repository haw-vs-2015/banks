package bank;

import java.util.ArrayList;

import static spark.Spark.port;

public class Main {

    public static int PORT = 4567;
    public static ArrayList<String> BANK_LIST = new ArrayList<>();

    public static void main(String[] args) {
        BANK_LIST.add("http://localhost:4567");
        //BANK_LIST.add("http://localhost:4568");
        //BANK_LIST.add("http://localhost:4569");
        //BANK_LIST.add("http://localhost:4570");

        port(PORT);

        new BankService();
    }

}
