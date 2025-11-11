package com.example.letmecook.cache;

import android.content.Context;
import android.content.SharedPreferences;

import com.example.letmecook.Model.User;
import com.google.gson.Gson;

public class CachedUserManager {
    private static final String PREF_NAME = "user_cache";
    private static final String KEY_USER = "current_user";
    private static User cachedUser;

    public static void saveUser(Context context, User user) {
        SharedPreferences prefs = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        Gson gson = new Gson();
        editor.putString(KEY_USER, gson.toJson(user));
        editor.apply();
        cachedUser = user;
    }

    public static User getCurrentUser(Context context) {
        if (cachedUser != null) return cachedUser;

        SharedPreferences prefs = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        String json = prefs.getString(KEY_USER, null);

        if (json != null) {
            cachedUser = new Gson().fromJson(json, User.class);
            return cachedUser;
        }
        return null;
    }

    public static void clearUser(Context context) {
        SharedPreferences prefs = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        prefs.edit().remove(KEY_USER).apply();
        cachedUser = null;
    }
}
