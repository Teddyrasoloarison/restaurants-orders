package DB;

import io.github.cdimascio.dotenv.Dotenv;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

import io.github.cdimascio.dotenv.Dotenv;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DataSource {
    private final String dbUrl;
    private final String dbUser;
    private final String dbPassword;

    public DataSource() {
        // Charger les variables d'environnement
        Dotenv dotenv = Dotenv.load();

        // Récupérer et afficher les variables d'environnement
        String host = dotenv.get("DB_HOST");
        String dbName = dotenv.get("DB_NAME");
        String port = dotenv.get("DB_PORT");

        System.out.println("DB_HOST: " + host);   // Affiche DB_HOST
        System.out.println("DB_PORT: " + port);   // Affiche DB_PORT
        System.out.println("DB_NAME: " + dbName); // Affiche DB_NAME

        // Vérification de la lecture des variables d'environnement
        if (host == null || dbName == null || port == null) {
            throw new IllegalArgumentException("Une ou plusieurs variables d'environnement sont manquantes !");
        }

        this.dbUser = dotenv.get("DB_USERNAME");
        this.dbPassword = dotenv.get("DB_PASSWORD");

        // Construire l'URL de connexion PostgreSQL
        this.dbUrl = String.format("jdbc:postgresql://%s:%s/%s", host, port, dbName);
    }

    public Connection getConnection() throws SQLException {
        return DriverManager.getConnection(dbUrl, dbUser, dbPassword);
    }

    public static void main(String[] args) {
        // Tester la connexion
        try {
            DataSource dataSource = new DataSource();
            Connection connection = dataSource.getConnection();
            System.out.println("Connexion réussie !");
        } catch (SQLException e) {
            System.out.println("Erreur de connexion : " + e.getMessage());
        }
    }
}
