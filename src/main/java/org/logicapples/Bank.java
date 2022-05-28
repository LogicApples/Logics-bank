package org.logicapples;

import com.mongodb.client.*;
import io.github.cdimascio.dotenv.Dotenv;
import org.bson.Document;

import java.util.*;

import static com.mongodb.client.model.Filters.in;
import static com.mongodb.client.model.Updates.set;

public class Bank {
    public static void main(String[] args) {
        // yes
        Dotenv dotenv = Dotenv.load();
        String uri = dotenv.get("uri");

        // Logger stuff eh
        java.util.logging.Logger mongoLogger = java.util.logging.Logger.getLogger("org.mongodb.driver");
        mongoLogger.setLevel(java.util.logging.Level.SEVERE);

        // Scanner
        Scanner sc = new Scanner(System.in);
        System.out.println("app");
        System.out.println("r - register");
        System.out.println("l - login");
        System.out.print("> ");
        String loginOrRegister = sc.next();
        if (Objects.equals(loginOrRegister, "r")) {
            System.out.print("your username > ");
            String name = sc.next();
            System.out.print("your age > ");
            byte age = sc.nextByte();
            System.out.print("your email > ");
            String email = sc.next();
            System.out.print("your password (Must be atleast 8 characters long) > ");
            String password = sc.next();
            if (password.length() <= 8)
                System.out.println("Please use more characters in your password to make it more secure!");
            int randomNumber = (int) Math.round(Math.random() * 1000000000);
            int user_id = (int) Math.round(Math.random() * age * randomNumber * name.length());
            try (MongoClient mongoClient = MongoClients.create(uri)) {
                boolean accountExists = doesTheAccountExist(mongoClient, name, password, email);
                if (!accountExists) createAccount(mongoClient, user_id, name, age, 0, password, email);
                else System.out.println("Account already exists");
            }
        } else if (Objects.equals(loginOrRegister, "l")) {
            System.out.print("your username > ");
            String name = sc.next();
            System.out.print("your password > ");
            String password = sc.next();
            String dbName;
            String dbPassword;
            String balance;
            try (MongoClient mongoClient = MongoClients.create(uri)) {
                balance = getAccountBalance(mongoClient, name, password);
                dbName = getAccountUsername(mongoClient, name, password);
                dbPassword = getAccountPassword(mongoClient, name, password);
            }
            if (!Objects.equals(dbName, name) && !Objects.equals(dbPassword, password)) {
                System.out.println("Wrong username or password");
                return;
            }
            System.out.println("w - withdraw");
            System.out.println("d - deposit");
            System.out.println("j - work");
            System.out.println("b - balance");
            System.out.print("> ");
            String selection = sc.next();
            if (Objects.equals(selection, "w")) {

            } else if (Objects.equals(selection, "d")) {

            } else if (Objects.equals(selection, "j")) {
                try (MongoClient mongoClient = MongoClients.create(uri)) {
                    updateAccountBalance(mongoClient, name, password, balance);
                }
            } else if (Objects.equals(selection, "b")) {
                System.out.println("Your bank account balance is: " + balance + "€");
            }
        }
    }

    public static void createAccount(MongoClient mongoClient, int user_id, String name, byte age, int balance, String password, String email) {
        MongoCollection<Document> cookies = mongoClient.getDatabase("bank").getCollection("userData");
        List<Document> cookiesList = new ArrayList<>();
        cookiesList.add(new Document("user_id", user_id).append("name", name).append("age", age).append("email", email).append("password", password).append("balance", balance));
        cookies.insertMany(cookiesList);
        System.out.println("Account successfully created. Please login.");
    }

    public static boolean doesTheAccountExist(MongoClient mongoClient, String nameOfTheUser, String passwordOfTheUser, String emailOfTheUser) {
        MongoCollection<Document> cookies = mongoClient.getDatabase("bank").getCollection("userData");
        List<Document> lists = cookies.find().into(new ArrayList<>());
        boolean accountExists = false;
        for (Document c : lists) {
            Object name1 = c.get("name"), password1 = c.get("password"), email1 = c.get("email");
            String name = (String) name1, password = (String) password1, email = (String) email1;
            if (Objects.equals(name, nameOfTheUser) && Objects.equals(password, passwordOfTheUser) && Objects.equals(email, emailOfTheUser)) {
                System.out.println("This user already has an account");
                accountExists = true;
            }
        }
        return accountExists;
    }

    public static String getAccountBalance(MongoClient mongoClient, String nameOfTheUser, String passwordOfTheUser) {
        MongoCollection<Document> cookies = mongoClient.getDatabase("bank").getCollection("userData");
        List<Document> lists = cookies.find().into(new ArrayList<>());
        String balanceToReturn = null;
        for (Document c : lists) {
            Object name1 = c.get("name"), password1 = c.get("password"), email1 = c.get("email"), balance1 = c.get("balance");
            String name = (String) name1, password = (String) password1, email = (String) email1, balance = balance1.toString();
            if (Objects.equals(name, nameOfTheUser) && Objects.equals(password, passwordOfTheUser)) {
                balanceToReturn = balance;
            }
        }
        return balanceToReturn;
    }

    public static String getAccountUsername(MongoClient mongoClient, String nameOfTheUser, String passwordOfTheUser) {
        MongoCollection<Document> cookies = mongoClient.getDatabase("bank").getCollection("userData");
        List<Document> lists = cookies.find().into(new ArrayList<>());
        String nameToReturn = null;
        for (Document c : lists) {
            Object name1 = c.get("name"), password1 = c.get("password"), email1 = c.get("email"), balance1 = c.get("balance");
            String name = (String) name1, password = (String) password1, email = (String) email1;
            if (Objects.equals(name, nameOfTheUser) && Objects.equals(password, passwordOfTheUser)) {
                nameToReturn = name;
            }
        }
        return nameToReturn;
    }

    public static String getAccountPassword(MongoClient mongoClient, String nameOfTheUser, String passwordOfTheUser) {
        MongoCollection<Document> cookies = mongoClient.getDatabase("bank").getCollection("userData");
        List<Document> lists = cookies.find().into(new ArrayList<>());
        String passwordToReturn = null;
        for (Document c : lists) {
            Object name1 = c.get("name"), password1 = c.get("password"), email1 = c.get("email"), balance1 = c.get("balance");
            String name = (String) name1, password = (String) password1, email = (String) email1;
            if (Objects.equals(name, nameOfTheUser) && Objects.equals(password, passwordOfTheUser)) {
                passwordToReturn = password;
            }
        }
        return passwordToReturn;
    }

    public static void updateAccountBalance(MongoClient mongoClient, String name, String password, String balance) {
        MongoCollection<Document> cookies = mongoClient.getDatabase("bank").getCollection("userData");
        List<Document> lists = cookies.find().into(new ArrayList<>());
        for (Document c : lists) {
            Object password1 = c.get("password");
            cookies.findOneAndUpdate(new Document("password", password), set("balance", balance + Math.round(Math.random() * 69)));
        }
    }

    public static void deleteProfile(MongoClient mongoClient) {
        MongoCollection<Document> cookies = mongoClient.getDatabase("bank").getCollection("userData");
        // cookies.deleteOne(in("name", ));
    }

}

