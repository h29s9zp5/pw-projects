package bdbt_bada_project.SpringApplication;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;

public class OracleConnectionExample {
    public static void main(String[] args) {
        // Parametry połączenia
        String url = "jdbc:oracle:thin:@//194.29.170.4:1521/xe"; // Zmień na swoje dane
        String username = "BDBTGRB02"; // Twoja nazwa użytkownika
        String password = "bdbtgrb02"; // Twoje hasło

        try {
            // Rejestracja sterownika
            Class.forName("oracle.jdbc.driver.OracleDriver");

            // Nawiązanie połączenia
            Connection connection = DriverManager.getConnection(url, username, password);
            System.out.println("Połączono z bazą danych!");

            // Wykonanie zapytania SQL
            String query = "SELECT * FROM ADOPCJE"; // Zmień na swoją tabelę
            Statement statement = connection.createStatement();
            ResultSet resultSet = statement.executeQuery(query);

            // Przetwarzanie wyników
            while (resultSet.next()) {
                System.out.println("Kolumna 1: " + resultSet.getString(1));
                // Dodaj inne kolumny według potrzeb
            }

            // Zamknięcie zasobów
            resultSet.close();
            statement.close();
            connection.close();
            System.out.println("Połączenie zamknięte.");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
