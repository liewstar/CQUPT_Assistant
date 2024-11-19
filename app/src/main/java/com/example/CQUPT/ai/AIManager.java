package com.example.CQUPT.ai;

import android.content.Context;
import android.content.SharedPreferences;
import androidx.preference.PreferenceManager;
import androidx.security.crypto.EncryptedSharedPreferences;
import androidx.security.crypto.MasterKey;
import java.io.IOException;
import java.security.GeneralSecurityException;

public class AIManager {
    private static AIManager instance;
    private final Context context;
    private SharedPreferences encryptedPrefs;
    private static final String ENCRYPTED_PREFS_FILE = "encrypted_ai_prefs";

    private AIManager(Context context) {
        this.context = context.getApplicationContext();
        initializeEncryptedPrefs();
    }

    public static synchronized AIManager getInstance(Context context) {
        if (instance == null) {
            instance = new AIManager(context);
        }
        return instance;
    }

    private void initializeEncryptedPrefs() {
        try {
            MasterKey masterKey = new MasterKey.Builder(context)
                    .setKeyScheme(MasterKey.KeyScheme.AES256_GCM)
                    .build();

            encryptedPrefs = EncryptedSharedPreferences.create(
                    context,
                    ENCRYPTED_PREFS_FILE,
                    masterKey,
                    EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
                    EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
            );
        } catch (GeneralSecurityException | IOException e) {
            e.printStackTrace();
        }
    }

    public boolean isAIEnabled() {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        return prefs.getBoolean("enable_ai", false);
    }

    public String getSelectedProvider() {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        return prefs.getString("ai_provider", "openai");
    }

    public String getApiKey() {
        if (encryptedPrefs != null) {
            return encryptedPrefs.getString("encrypted_api_key", "");
        }
        return "";
    }

    public void saveApiKey(String apiKey) {
        if (encryptedPrefs != null) {
            encryptedPrefs.edit().putString("encrypted_api_key", apiKey).apply();
        }
    }

    public boolean isOCREnabled() {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        return prefs.getBoolean("enable_ocr", false);
    }

    public boolean isVoiceEnabled() {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        return prefs.getBoolean("enable_voice", false);
    }
}
