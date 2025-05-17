package com.example.proyectointegradorfinal_vargasfigueroa;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DatabaseHelper extends SQLiteOpenHelper {
    private static final String DATABASE_NAME = "aceite_usado.db";
    private static final int DATABASE_VERSION = 1;

    // Tabla Usuario
    public static final String TABLE_USUARIO = "usuario";
    public static final String COLUMN_USUARIO_ID = "id";
    public static final String COLUMN_USUARIO_NOMBRE = "nombre";
    public static final String COLUMN_USUARIO_SEXO = "sexo";
    public static final String COLUMN_USUARIO_DOMICILIO_ID = "domicilio_id";
    public static final String COLUMN_USUARIO_PTS_ACUMULADOS = "pts_acumulados";
    public static final String COLUMN_USUARIO_CONTRASENIA = "contrasenia";
    public static final String COLUMN_USUARIO_EMAIL = "correo_electronico";

    // Tabla Domicilio
    public static final String TABLE_DOMICILIO = "domicilio";
    public static final String COLUMN_DOMICILIO_ID = "id";
    public static final String COLUMN_DOMICILIO_DIRECCION = "direccion";
    public static final String COLUMN_DOMICILIO_TIPO = "tipo_domicilio";

    // Tabla Recolección
    public static final String TABLE_RECOLECCION = "recoleccion";
    public static final String COLUMN_RECOLECCION_ID = "id";
    public static final String COLUMN_RECOLECCION_DIRECCION = "direccion";
    public static final String COLUMN_RECOLECCION_CANT_ACEITE = "cant_aceite";
    public static final String COLUMN_RECOLECCION_DOMICILIO_ID = "domicilio_id";
    public static final String COLUMN_RECOLECCION_PTS = "pts_por_recoleccion";

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        // Crear tabla Domicilio
        String CREATE_DOMICILIO_TABLE = "CREATE TABLE " + TABLE_DOMICILIO + "("
                + COLUMN_DOMICILIO_ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
                + COLUMN_DOMICILIO_DIRECCION + " TEXT,"
                + COLUMN_DOMICILIO_TIPO + " TEXT"
                + ")";
        db.execSQL(CREATE_DOMICILIO_TABLE);

        // Crear tabla Usuario
        String CREATE_USUARIO_TABLE = "CREATE TABLE " + TABLE_USUARIO + "("
                + COLUMN_USUARIO_ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
                + COLUMN_USUARIO_NOMBRE + " TEXT,"
                + COLUMN_USUARIO_SEXO + " TEXT,"
                + COLUMN_USUARIO_DOMICILIO_ID + " INTEGER,"
                + COLUMN_USUARIO_PTS_ACUMULADOS + " INTEGER DEFAULT 0,"
                + COLUMN_USUARIO_CONTRASENIA + " TEXT,"
                + COLUMN_USUARIO_EMAIL + " TEXT UNIQUE,"
                + "FOREIGN KEY(" + COLUMN_USUARIO_DOMICILIO_ID + ") REFERENCES " + TABLE_DOMICILIO + "(" + COLUMN_DOMICILIO_ID + ")"
                + ")";
        db.execSQL(CREATE_USUARIO_TABLE);

        // Crear tabla Recolección
        String CREATE_RECOLECCION_TABLE = "CREATE TABLE " + TABLE_RECOLECCION + "("
                + COLUMN_RECOLECCION_ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
                + COLUMN_RECOLECCION_DIRECCION + " TEXT,"
                + COLUMN_RECOLECCION_CANT_ACEITE + " REAL,"
                + COLUMN_RECOLECCION_DOMICILIO_ID + " INTEGER,"
                + COLUMN_RECOLECCION_PTS + " INTEGER,"
                + "FOREIGN KEY(" + COLUMN_RECOLECCION_DOMICILIO_ID + ") REFERENCES " + TABLE_DOMICILIO + "(" + COLUMN_DOMICILIO_ID + ")"
                + ")";
        db.execSQL(CREATE_RECOLECCION_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_RECOLECCION);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_USUARIO);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_DOMICILIO);
        onCreate(db);
    }

    // Métodos para Usuario
    public long agregarUsuario(String nombre, String sexo, long domicilioId, String contrasenia, String email) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_USUARIO_NOMBRE, nombre);
        values.put(COLUMN_USUARIO_SEXO, sexo);
        values.put(COLUMN_USUARIO_DOMICILIO_ID, domicilioId);
        values.put(COLUMN_USUARIO_CONTRASENIA, contrasenia);
        values.put(COLUMN_USUARIO_EMAIL, email);
        values.put(COLUMN_USUARIO_PTS_ACUMULADOS, 0);
        return db.insert(TABLE_USUARIO, null, values);
    }

    public boolean verificarUsuario(String email, String contrasenia) {
        SQLiteDatabase db = this.getReadableDatabase();
        String[] columns = {COLUMN_USUARIO_ID};
        String selection = COLUMN_USUARIO_EMAIL + " = ? AND " + COLUMN_USUARIO_CONTRASENIA + " = ?";
        String[] selectionArgs = {email, contrasenia};
        Cursor cursor = db.query(TABLE_USUARIO, columns, selection, selectionArgs, null, null, null);
        int count = cursor.getCount();
        cursor.close();
        return count > 0;
    }

    public int obtenerPuntosUsuario(String email) {
        SQLiteDatabase db = this.getReadableDatabase();
        int puntos = 0;
        Cursor cursor = db.query(TABLE_USUARIO, new String[]{COLUMN_USUARIO_PTS_ACUMULADOS},
                COLUMN_USUARIO_EMAIL + " = ?", new String[]{email}, null, null, null);
        if (cursor.moveToFirst()) {
            puntos = cursor.getInt(cursor.getColumnIndex(COLUMN_USUARIO_PTS_ACUMULADOS));
        }
        cursor.close();
        return puntos;
    }

    public long obtenerUsuarioId(String email) {
        SQLiteDatabase db = this.getReadableDatabase();
        long userId = -1;
        Cursor cursor = db.query(TABLE_USUARIO, new String[]{COLUMN_USUARIO_ID},
                COLUMN_USUARIO_EMAIL + " = ?", new String[]{email}, null, null, null);
        if (cursor.moveToFirst()) {
            userId = cursor.getLong(cursor.getColumnIndex(COLUMN_USUARIO_ID));
        }
        cursor.close();
        return userId;
    }

    // Métodos para Domicilio
    public long agregarDomicilio(String direccion, String tipoDomicilio) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_DOMICILIO_DIRECCION, direccion);
        values.put(COLUMN_DOMICILIO_TIPO, tipoDomicilio);
        return db.insert(TABLE_DOMICILIO, null, values);
    }

    // Métodos para Recolección
    public long agregarRecoleccion(String direccion, double cantAceite, long domicilioId, int puntos) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_RECOLECCION_DIRECCION, direccion);
        values.put(COLUMN_RECOLECCION_CANT_ACEITE, cantAceite);
        values.put(COLUMN_RECOLECCION_DOMICILIO_ID, domicilioId);
        values.put(COLUMN_RECOLECCION_PTS, puntos);
        return db.insert(TABLE_RECOLECCION, null, values);
    }

    public Cursor obtenerRecoleccionesPorDomicilio(long domicilioId) {
        SQLiteDatabase db = this.getReadableDatabase();
        return db.query(TABLE_RECOLECCION, null,
                COLUMN_RECOLECCION_DOMICILIO_ID + " = ?",
                new String[]{String.valueOf(domicilioId)},
                null, null, null);
    }

    public void actualizarPuntosUsuario(long usuarioId, int puntos) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_USUARIO_PTS_ACUMULADOS, puntos);
        db.update(TABLE_USUARIO, values,
                COLUMN_USUARIO_ID + " = ?",
                new String[]{String.valueOf(usuarioId)});
    }
}