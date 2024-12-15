package data;

import models.Admin;
import models.Customer;
import models.User;

import java.io.*;
import java.util.concurrent.ConcurrentHashMap;

public class UserDataManager {
    private static UserDataManager instance;
    private final ConcurrentHashMap<String, User> users;
    private static final String FILE_PATH = "src/resources/userdata.csv";

    private UserDataManager() {
        users = new ConcurrentHashMap<>();
        loadUsersFromFile();
    }

    public static synchronized UserDataManager getInstance() {
        if (instance == null) {
            instance = new UserDataManager();
        }
        return instance;
    }

    private void loadUsersFromFile() {
        try (BufferedReader reader = new BufferedReader(new FileReader(FILE_PATH))) {
            String line = reader.readLine(); // Skippar första raden
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split(",");
                if (parts.length >= 5) {
                    String id = parts[0];
                    String name = parts[1];
                    String email = parts[2];
                    String phoneNumber = parts[3];
                    String password = parts[4];
                    String userType = parts.length == 6 ? parts[5] : "Customer";

                    User user = userType.equalsIgnoreCase("Admin") ?
                            new Admin(id, name, email, phoneNumber, password) :
                            new Customer(id, name, email, phoneNumber, password);

                    users.put(id, user); // Add to the map
                }
            }
        } catch (IOException e) {
            throw new RuntimeException("Error loading users from file", e);
        }
    }

    public boolean registerUser(User newUser, String userType) {
        String userId = newUser instanceof Customer ? ((Customer) newUser).getPID() : ((Admin) newUser).getAdminID();
        if (users.containsKey(userId)) {
            return false; // User finns redan med det personnumret
        }

        try (BufferedWriter writer = new BufferedWriter(new FileWriter(FILE_PATH, true))) {
            writer.write(String.format("%s,%s,%s,%s,%s,%s\n",
                    userId,
                    newUser.getName(),
                    newUser.getEmail(),
                    newUser.getPhoneNumber(),
                    newUser.getPassword(),
                    userType));
        } catch (IOException e) {
            return false;
        }

        users.put(userId, newUser);
        return true;
    }

    public User authenticateUser(String id, String password) {
        User user = users.get(id);
        return (user != null && user.getPassword().equals(password)) ? user : null;
    }

    public User getUser(String id) {
        return users.get(id);
    }

}
