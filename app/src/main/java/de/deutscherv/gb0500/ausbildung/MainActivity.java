package de.deutscherv.gb0500.ausbildung;

import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.room.Room;

import com.google.android.material.bottomnavigation.BottomNavigationView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.time.LocalTime;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class MainActivity extends AppCompatActivity {

    private AppDatenbank datenbank;
    private ExecutorService executor = Executors.newSingleThreadExecutor();
    private List<Benutzer> benutzerListe;
    private List<Sprache> sprachListe;
    private List<BenutzerWort> benutzerWortList;
    private List<BenutzerKapitel> benutzerKapitelList;
    Benutzer localBenutzer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.loginlayout);

        datenbank = Room.databaseBuilder(getApplicationContext(), AppDatenbank.class, "lernapp.db").build();

        leseDatenbankAusUndMeldeBenutzerAn();
    }

    private void leseDatenbankAusUndMeldeBenutzerAn() {
        executor.execute(() -> {

            benutzerListe = datenbank.benutzerDao().getAll();


            if (datenbank.spracheDao().getAll().isEmpty()) {
                leseJSONAus();
            }

            if (benutzerListe.isEmpty()) {
                runOnUiThread(() -> {
                    Toast.makeText(this, "Kein Konto gefunden", Toast.LENGTH_SHORT).show();
                    registrieren();
                });
            } else {
                for (Benutzer benuter : benutzerListe) {
                    if (benuter.isAutologin()) {
                        localBenutzer = benuter;
                        benutzerWortList = datenbank.benutzerWortDao().findByBenutzerId(localBenutzer.getId());
                        benutzerKapitelList = datenbank.benutzerKapitelDao().findByBenutzerId(localBenutzer.getId());

                        runOnUiThread(() -> {
                            setContentView(R.layout.fragmentlayout);
                            loadFragments(new StartFragment());
                            loadStartFragment();
                        });
                        break;
                    }
                }
            }
        });

        executor.shutdown();

        Button button = findViewById(R.id.button);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                anmelden();
            }
        });

        TextView textView = findViewById(R.id.keinKontoTextView);
        textView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setContentView(R.layout.registrierlayout);
                registrieren();
            }
        });


    }

    private void anmelden() {
        ProgressBar progressBar = findViewById(R.id.loading_spinner);
        progressBar.setVisibility(View.VISIBLE);
        TextView editTextEmailAddress = findViewById(R.id.editTextEmailAddress);
        TextView editTextPassword = findViewById(R.id.editTextPassword);
        String email = editTextEmailAddress.getText().toString();
        String password = hashPassword(editTextPassword.getText().toString());
        executor = Executors.newSingleThreadExecutor();
        executor.execute(() -> {
            Benutzer benutzer = datenbank.benutzerDao().findByEmail(email);
            if (benutzer != null && benutzer.getPasswort().equals(password)) {
                CheckBox checkBox = findViewById(R.id.checkBox);
                benutzer.setAutologin(checkBox.isChecked());
                datenbank.benutzerDao().update(benutzer);
                localBenutzer = benutzer;
                runOnUiThread(() -> {
                    Toast.makeText(MainActivity.this, "Login erfolgreich", Toast.LENGTH_SHORT).show();
                    progressBar.setVisibility(View.GONE);
                    setContentView(R.layout.fragmentlayout);
                    loadFragments(new StartFragment());
                    loadStartFragment();
                });
            } else {
                runOnUiThread(() -> {
                    Toast.makeText(MainActivity.this, "Ungültige E-Mail oder Passwort", Toast.LENGTH_SHORT).show();
                    progressBar.setVisibility(View.GONE);
                });
            }

        });
    }

    // Methode zum Laden von Fragmenten
    private void loadFragment(Fragment fragment) {
        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.fragment_container, fragment)
                .commit();
    }

    private void registrieren() {
        setContentView(R.layout.registrierlayout);
        Button buttonSpeichern = findViewById(R.id.speichernButton);
        buttonSpeichern.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ExecutorService executor2 = Executors.newSingleThreadExecutor();
                EditText editTextName = findViewById(R.id.editTextName);
                EditText editTextEmail = findViewById(R.id.editTextEmailAddress);
                EditText editTextPasswort = findViewById(R.id.editTextPassword);
                EditText editTextPasswortWiederholen = findViewById(R.id.editTextPasswordWiederholen);
                String name = editTextName.getText().toString();
                String email = editTextEmail.getText().toString();
                String passwort = editTextPasswort.getText().toString();
                String passwortWiederholen = editTextPasswortWiederholen.getText().toString();
                ProgressBar progressBar = findViewById(R.id.loading_spinner);
                progressBar.setVisibility(View.VISIBLE);

                executor2.execute(() -> {
                    Benutzer benutzer = datenbank.benutzerDao().findByEmail(email);
                    if (benutzer != null) {
                        runOnUiThread(() -> {
                            Toast.makeText(MainActivity.this, "Ein Account mit dieser E-Mail existiert bereits", Toast.LENGTH_SHORT).show();
                            progressBar.setVisibility(View.GONE);
                        });
                    } else if (name.isEmpty() || email.isEmpty() || passwort.isEmpty() || passwortWiederholen.isEmpty()) {
                        runOnUiThread(() -> {
                            Toast.makeText(MainActivity.this, "Bitte füllen Sie alle Felder aus", Toast.LENGTH_SHORT).show();
                            progressBar.setVisibility(View.GONE);
                        });
                    } else if (email.contains("@") && passwort.equals(passwortWiederholen)) {
                        String hashedPasswort = hashPassword(passwort);
                        localBenutzer = new Benutzer(name, email, hashedPasswort, true);
                        datenbank.benutzerDao().insert(localBenutzer);
                        runOnUiThread(() -> {
                            Toast.makeText(MainActivity.this, "Registrierung erfolgreich", Toast.LENGTH_SHORT).show();
                            progressBar.setVisibility(View.GONE);
                            setContentView(R.layout.activity_main);
                            TextView textViewWillkommen = findViewById(R.id.textViewWillkommen);
                            textViewWillkommen.setText("Willkommen " + localBenutzer.getName());
                            Button startButton = findViewById(R.id.startbutton);
                            startButton.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    setContentView(R.layout.fragmentlayout);
                                    loadFragments(new SprachFragment());
                                    loadSprachFragment();
                                }
                            });
                        });

                    } else {
                        runOnUiThread(() -> {
                            Toast.makeText(MainActivity.this, "Ungültige E-Mail oder Passwörter", Toast.LENGTH_SHORT).show();
                            progressBar.setVisibility(View.GONE);
                        });

                    }
                });

            }
        });
        Button buttonZurueck = findViewById(R.id.button2);
        buttonZurueck.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                recreate();
            }
        });
    }

    private void loadFragments(Fragment fragment) {
        BottomNavigationView bottomNavigationView = findViewById(R.id.bottom_navigation);

        loadFragment(fragment);

        // Listener für die BottomNavigationView-Items
        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {

            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                Fragment selectedFragment = null;

                int id = item.getItemId();

                if (id == R.id.nav_start) {
                    selectedFragment = new StartFragment();
                    loadStartFragment();
                } else if (id == R.id.nav_sprache) {
                    selectedFragment = new SprachFragment();
                    loadSprachFragment();
                } else if (id == R.id.nav_profil) {
                    selectedFragment = new ProfilFragment();
                    loadProfilFragment();

                }

                if (selectedFragment != null) {
                    loadFragment(selectedFragment);
                }
                return true;
            }
        });
    }

    private void loadStartFragment() {
        ExecutorService executor = Executors.newSingleThreadExecutor();
        executor.execute(() -> {
            benutzerWortList = datenbank.benutzerWortDao().findByBenutzerId(localBenutzer.getId());
            benutzerKapitelList = datenbank.benutzerKapitelDao().findByBenutzerId(localBenutzer.getId());
            runOnUiThread(() -> {
                updateGreetingBasedOnTime();
                loadRecyclerViewAndProgressBar();
            });
        });
    }

    private void loadSprachFragment() {
        ExecutorService executor = Executors.newSingleThreadExecutor();
        executor.execute(() -> {
            sprachListe = datenbank.spracheDao().getAll();
            runOnUiThread(() -> {
                SprachAdapter sprachAdapter = new SprachAdapter(this.getApplicationContext(), sprachListe, this, datenbank, localBenutzer);
                RecyclerView sprachRecyclerView = this.findViewById(R.id.sprachRecyclerView);
                if (sprachRecyclerView == null) {                    Toast.makeText(this, "Fehler. Bitte wiederholen", Toast.LENGTH_SHORT).show();
                    Log.w("MainActivity", "Fehler. SprachRecyclerView konnte nicht geladen werden");
                } else {
                    sprachRecyclerView.setLayoutManager(new LinearLayoutManager(this.getApplicationContext()));
                    sprachRecyclerView.setAdapter(sprachAdapter);
                }
            });
        });

    }

    private void loadProfilFragment() {
        Animation fadeOut = AnimationUtils.loadAnimation(this.getApplicationContext(), R.anim.fade_out);
        Animation fadeIn = AnimationUtils.loadAnimation(this.getApplicationContext(), R.anim.fade_in);

        ExecutorService executor = Executors.newSingleThreadExecutor();
        executor.execute(() -> {
            runOnUiThread(() -> {
                if (this.findViewById(R.id.textViewAbmelden) != null) {
                    TextView textViewAbmelden = this.findViewById(R.id.textViewAbmelden);
                    textViewAbmelden.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Button buttonAbmeldenBestaetigen = findViewById(R.id.buttonAbmeldenBestaetigen);
                            if (buttonAbmeldenBestaetigen != null) {
                                if (buttonAbmeldenBestaetigen.getVisibility() == View.VISIBLE) {
                                    buttonAbmeldenBestaetigen.startAnimation(fadeOut);
                                    buttonAbmeldenBestaetigen.setVisibility(View.GONE);
                                } else {
                                    buttonAbmeldenBestaetigen.startAnimation(fadeIn);
                                    buttonAbmeldenBestaetigen.setVisibility(View.VISIBLE);
                                    buttonAbmeldenBestaetigen.setOnClickListener(new View.OnClickListener() {
                                        @Override
                                        public void onClick(View v) {
                                            ExecutorService executor2 = Executors.newSingleThreadExecutor();
                                            executor2.execute(() -> {
                                                localBenutzer.setAutologin(false);
                                                datenbank.benutzerDao().update(localBenutzer);
                                                runOnUiThread(MainActivity.this::recreate);

                                            });
                                        }
                                    });
                                }
                            } else {
                                Toast.makeText(MainActivity.this, "Fehler. Bitte wiederholen", Toast.LENGTH_SHORT).show();
                                Log.w("MainActivity", "Fehler. ButtonAbmeldenBestaetigen konnte nicht geladen werden");
                            }

                        }
                    });

                    TextView textViewName = this.findViewById(R.id.textViewName);
                    textViewName.setText("Name: " + localBenutzer.getName());
                    TextView textViewEmail = this.findViewById(R.id.textViewEmail);
                    textViewEmail.setText("E-Mail: " + localBenutzer.getEmail());
                    CheckBox checkBox = this.findViewById(R.id.checkBoxAutoLogin);
                    checkBox.setChecked(localBenutzer.isAutologin());
                    checkBox.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            ExecutorService executor2 = Executors.newSingleThreadExecutor();
                            executor2.execute(() -> {
                                localBenutzer.setAutologin(checkBox.isChecked());
                                datenbank.benutzerDao().update(localBenutzer);
                            });
                            executor2.shutdown();
                        }
                    });
                    TextView textViewFortschrittLoeschen = this.findViewById(R.id.textViewFortschrittLoeschen);
                    textViewFortschrittLoeschen.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Button buttonFortschrittLoeschenBestaetigen = findViewById(R.id.buttonFortschrittLoeschenBestaetigen);
                            if (buttonFortschrittLoeschenBestaetigen != null) {
                                if (buttonFortschrittLoeschenBestaetigen.getVisibility() == View.VISIBLE) {
                                    buttonFortschrittLoeschenBestaetigen.startAnimation(fadeOut);
                                    buttonFortschrittLoeschenBestaetigen.setVisibility(View.GONE);
                                } else {
                                    buttonFortschrittLoeschenBestaetigen.startAnimation(fadeIn);
                                    buttonFortschrittLoeschenBestaetigen.setVisibility(View.VISIBLE);
                                    buttonFortschrittLoeschenBestaetigen.setOnClickListener(new View.OnClickListener() {
                                        @Override
                                        public void onClick(View v) {
                                            buttonFortschrittLoeschenBestaetigen.setVisibility(View.GONE);
                                            ExecutorService executor2 = Executors.newSingleThreadExecutor();
                                            executor2.execute(() -> {

                                                datenbank.benutzerWortDao().deleteByBenutzerId(localBenutzer.getId());
                                                datenbank.benutzerKapitelDao().deleteByBenutzerId(localBenutzer.getId());
                                            });
                                            executor2.shutdown();
                                        }
                                    });
                                }
                            } else {
                                Toast.makeText(MainActivity.this, "Fehler. Bitte wiederholen", Toast.LENGTH_SHORT).show();
                                Log.w("MainActivity", "Fehler. ButtonFortschrittLoeschenBestaetigen konnte nicht geladen werden");
                            }

                        }
                    });
                } else {
                    Toast.makeText(this, "Fehler in Profil. Bitte wiederholen", Toast.LENGTH_SHORT).show();
                    Log.w("MainActivity", "Fehler. TextViewAbmelden konnte nicht geladen werden");

                }

            });
        });
    }

    private static String hashPassword(String password) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] encodedHash = digest.digest(password.getBytes(StandardCharsets.UTF_8));
            return bytesToHex(encodedHash);
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
            return null;
        }
    }

    private static String bytesToHex(byte[] hash) {
        StringBuilder hexString = new StringBuilder(2 * hash.length);
        for (byte b : hash) {
            String hex = Integer.toHexString(0xff & b);
            if (hex.length() == 1) {
                hexString.append('0');
            }
            hexString.append(hex);
        }
        return hexString.toString();

    }

    public String loadJSONFromAsset() {
        String json = null;
        try (InputStream is = getAssets().open("initial_data.json")) {   // JSON im assets Ordner
            int size = is.available();
            byte[] buffer = new byte[size];
            is.read(buffer);
            is.close();
            json = new String(buffer, StandardCharsets.UTF_8);
        } catch (IOException ex) {
            ex.printStackTrace();
            return null;
        }
        return json;
    }

    private void leseJSONAus() {
        String jsonString = loadJSONFromAsset();
        if (jsonString != null) {
            try {
                JSONObject jsonObj = new JSONObject(jsonString);
                JSONArray sprachen = jsonObj.getJSONArray("sprache");

                // Durch die Sprachen iterieren
                for (int i = 0; i < sprachen.length(); i++) {
                    JSONObject sprache = sprachen.getJSONObject(i);
                    String spracheName = sprache.getString("name");
                    long spracheID = datenbank.spracheDao().insert(new Sprache(spracheName));

                    JSONArray kapitel = sprache.getJSONArray("kapitel");

                    // Durch die Kapitel iterieren
                    for (int j = 0; j < kapitel.length(); j++) {
                        JSONObject kapitelObj = kapitel.getJSONObject(j);
                        String kapitelTitel = kapitelObj.getString("titel");
                        String kapitelBeschreibung = kapitelObj.getString("beschreibung");
                        int kapitelNummer = kapitelObj.getInt("kapitelnummer");
                        long kapitelID = datenbank.kapitelDao().insert(new Kapitel(kapitelTitel, kapitelBeschreibung, kapitelNummer, (int) spracheID));

                        JSONArray woerter = kapitelObj.getJSONArray("wort");

                        // Durch die Wörter iterieren
                        for (int k = 0; k < woerter.length(); k++) {
                            JSONObject wortObj = woerter.getJSONObject(k);
                            String wort = wortObj.getString("wort");
                            String uebersetzung = wortObj.getString("uebersetzung");
                            datenbank.wortDao().insert(new Wort(wort, uebersetzung, (int) kapitelID));
                        }
                    }
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    private void updateGreetingBasedOnTime() {
        TextView textView = this.findViewById(R.id.textView1);
        if (textView != null) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) { // Wird benötigt, da LocalTime API 26 braucht
                LocalTime currentTime = LocalTime.now();
                if (currentTime.getHour() < 12) {
                    textView.setText("Guten Morgen " + localBenutzer.getName());
                } else if (currentTime.getHour() < 18) {
                    textView.setText("Guten Tag " + localBenutzer.getName());
                } else {
                    textView.setText("Guten Abend " + localBenutzer.getName());
                }
            } else {
                textView.setText("Hallo " + localBenutzer.getName());
            }

        } else {
            Toast.makeText(this, "Fehler. Bitte wiederholen", Toast.LENGTH_SHORT).show();
            Log.w("MainActivity", "Fehler. TextViewBegruessung konnte nicht geladen werden");
        }
    }

    private void loadRecyclerViewAndProgressBar() {
        ProgressBar progressbar = this.findViewById(R.id.progressBar);
        StartAdapter startAdapter = new StartAdapter(this, this.getApplicationContext(), benutzerWortList, datenbank, benutzerKapitelList);
        RecyclerView startRecyclerView = this.findViewById(R.id.recyclerView);
        if (startRecyclerView != null) {
            startRecyclerView.setLayoutManager(new LinearLayoutManager(this.getApplicationContext()));
            startRecyclerView.setAdapter(startAdapter);
            progressbar.setProgress(startAdapter.getProgress());
        } else {
            Toast.makeText(this, "Fehler. Bitte wiederholen", Toast.LENGTH_SHORT).show();
            Log.w("MainActivity", "Fehler. StartRecyclerView konnte nicht geladen werden");

        }

    }
}