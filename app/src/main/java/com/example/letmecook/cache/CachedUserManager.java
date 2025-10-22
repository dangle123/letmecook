package com.example.letmecook.cache;

import com.example.letmecook.Model.User;

public class CachedUserManager {
    private static User currentUser;

    public static void setCurrentUser(User user) {
        currentUser = user;
    }

    public static User getCurrentUser() {
        return currentUser;
    }

    public static boolean hasUser() {
        return currentUser != null;
    }

    public static void clearCache() {
        currentUser = null;
    }
}
