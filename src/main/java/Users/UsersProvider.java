package Users;

import Ability.CityData;
import Ability.DBProvider;

import java.io.*;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Objects;
import java.util.logging.Logger;

public class UsersProvider {
    private ArrayList<User> users = new ArrayList<>();

    public void getUsersFromBase() {
        Logger log = Logger.getLogger("get users");
        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream("users.txt"))) {
            users = (ArrayList<User>) ois.readObject();
        } catch (ClassNotFoundException | IOException e) {
            log.warning(e.getMessage());
        }
    }

    public void saveUsersToBase() {
        Logger log = Logger.getLogger("save users to base");
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream("users.txt", false))) {
            oos.writeObject(users);
        } catch (IOException e) {
            log.warning(e.getMessage());
        }
    }


    public void addUserToList(User user) {
        users.add(user);
        saveUsersToBase();
    }

    public ArrayList<User> getUsers() {
        return users;
    }

    public User getUserByID(Long id) {
        if (!users.isEmpty()) {
            for (User item : users) {
                if (Objects.equals(item.getUserID(), id)) {
                    return item;
                }
            }
        }
        return null;
    }

    public void refreshUser(Long userID, CityData data) {
        User refreshable = getUserByID(userID);
        users.remove(refreshable);
        refreshable.setCurrentCityData(data);
        refreshable.addCityDataToList(data);
        addUserToList(refreshable);
    }

    public void refreshNotificationTime(Long userID, String chatID, LocalTime time) {
        User refreshable = getUserByID(userID);
        users.remove(refreshable);
        refreshable.setNotificationTime(time);
        refreshable.setChatID(chatID);
        addUserToList(refreshable);
    }
    //working with postgre



}
