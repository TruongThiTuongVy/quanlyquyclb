package DB_Connect;

public class Main {
    static Database connect_db = new Database();

    public static void main(String[] args) {
        connect_db.connect();
    }
}