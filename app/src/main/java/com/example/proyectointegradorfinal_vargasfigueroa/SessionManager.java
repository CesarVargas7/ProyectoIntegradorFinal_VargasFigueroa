package com.example.proyectointegradorfinal_vargasfigueroa;

import android.content.Context;
import android.content.SharedPreferences;

import java.util.HashMap;

public class SessionManager {
    // Variables de SharedPreferences
    SharedPreferences pref;
    SharedPreferences.Editor editor;
    Context context;

    // Modo SharedPreferences
    int PRIVATE_MODE = 0;

    // Nombre de SharedPreferences
    private static final String PREF_NAME = "AceiteUsadoSessionPref";

    // Clave de sesión (hacerlo constante)
    public static final String KEY_EMAIL = "email";
    private static final String IS_LOGIN = "IsLoggedIn";

    // Constructor
    public SessionManager(Context context) {
        this.context = context;
        pref = context.getSharedPreferences(PREF_NAME, PRIVATE_MODE);
        editor = pref.edit();
    }

    /**
     * Crea la sesión de login
     * */
    public void createLoginSession(String email) {
        // Almacenar login como TRUE
        editor.putBoolean(IS_LOGIN, true);

        // Almacenar email en pref
        editor.putString(KEY_EMAIL, email);

        // Confirmar los cambios
        editor.commit();
    }

    /**
     * Verificar estado de login
     * */
    public boolean isLoggedIn() {
        return pref.getBoolean(IS_LOGIN, false);
    }

    /**
     * Obtener datos de la sesión
     * */
    public HashMap<String, String> getUserDetails() {
        HashMap<String, String> user = new HashMap<>();
        user.put(KEY_EMAIL, pref.getString(KEY_EMAIL, null));
        return user;
    }

    /**
     * Cerrar sesión, eliminar todos los datos
     * */
    public void logoutUser() {
        // Limpiar todos los datos de la sesión
        editor.clear();
        editor.commit();
    }
}