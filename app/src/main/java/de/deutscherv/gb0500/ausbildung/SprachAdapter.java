package de.deutscherv.gb0500.ausbildung;

import android.app.Activity;
import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;
import java.util.Random;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class SprachAdapter extends RecyclerView.Adapter<SprachAdapter.SprachHolder> {

    private final Context mainContext;
    private final List<Sprache> sprachListe;
    private final Activity activity;
    private final AppDatenbank datenbank;
    int[] currentWortIndex = {0};
    int[] currentkapitelIndex = {0};
    private boolean buttonVisible = false;
    private boolean loesungGezeigt = false;
    TextView textViewFrage;
    TextView textViewAbbrechen;
    Button buttonAbgeben;
    Button buttonLoesung;
    EditText editText;
    RadioButton buttonWort1;
    RadioButton buttonWort2;
    RadioButton buttonWort3;
    RadioGroup radioGroup;
    Benutzer localBenutzer;



    public SprachAdapter(Context mainContext, List<Sprache> sprachListe, Activity activity, AppDatenbank datenbank, Benutzer localBenutzer) {
        this.mainContext = mainContext;
        this.sprachListe = sprachListe;
        this.activity = activity;
        this.datenbank = datenbank;
        this.localBenutzer = localBenutzer;
    }

    @NonNull
    @Override
    public SprachHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(this.mainContext).inflate(R.layout.sprachrecycler_layout, parent, false);
        return new SprachHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull SprachHolder holder, int position) {
        holder.SetDetails(sprachListe.get(position));
        Animation fadeOut = AnimationUtils.loadAnimation(mainContext, R.anim.fade_out);
        Animation fadeIn = AnimationUtils.loadAnimation(mainContext, R.anim.fade_in);

        holder.constraintLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (holder.buttonLernen.getVisibility() == View.GONE) {
                    holder.constraintLayout.startAnimation(fadeIn);
                    holder.buttonLernen.setVisibility(View.VISIBLE);
                    holder.buttonPruefen.setVisibility(View.VISIBLE);
                    
                } else {
                    holder.constraintLayout.startAnimation(fadeOut);
                    holder.buttonLernen.setVisibility(View.GONE);
                    holder.buttonPruefen.setVisibility(View.GONE);
                    
                }
                buttonVisible = !buttonVisible;
            }

        });

        holder.buttonLernen.setOnClickListener(v -> {
            starteLernen(true, holder.getAdapterPosition());
        });

        holder.buttonPruefen.setOnClickListener(v -> {
            starteLernen(false, holder.getAdapterPosition());
        });
    }

    @Override
    public int getItemCount() {
        return sprachListe.size();
    }

    public static class SprachHolder extends RecyclerView.ViewHolder {

        private final TextView textViewSprache;
        private final ConstraintLayout constraintLayout;
        private final Button buttonLernen;
        private final Button buttonPruefen;

        public SprachHolder(View itemView) {
            super(itemView);
            textViewSprache = itemView.findViewById(R.id.textViewSprache);
            constraintLayout = itemView.findViewById(R.id.sprachenConstraintLayout);
            buttonLernen = itemView.findViewById(R.id.buttonLernen);
            buttonPruefen = itemView.findViewById(R.id.buttonPruefen);

        }

        void SetDetails(Sprache sprache) {
            textViewSprache.setText(sprache.getName());
        }
    }

    private void starteLernen(boolean lernButtongeklickt, int position) {
        ExecutorService executor = Executors.newSingleThreadExecutor();
        executor.execute(() -> {
            List<Kapitel> kapitelListe = datenbank.kapitelDao().findBySpracheId(sprachListe.get(position).getId());
            if (activity != null) {
                if (kapitelListe.isEmpty()) {
                    activity.runOnUiThread(() -> {
                        Toast.makeText(mainContext, "Keine Kapitel vorhanden", Toast.LENGTH_SHORT).show();
                    });
                } else {
                    activity.runOnUiThread(() -> {
                        if (lernButtongeklickt){
                            Toast.makeText(mainContext, "Lernen gestartet", Toast.LENGTH_SHORT).show();
                            zeigeNaechtesKapitel(kapitelListe, position, lernButtongeklickt);
                        }else {
                            Toast.makeText(mainContext, "Prüfung gestartet", Toast.LENGTH_SHORT).show();
                            zeigeNaechtesKapitel(kapitelListe, position ,lernButtongeklickt);
                        }
                    });
                }
            } else {
                new Handler(Looper.getMainLooper()).post(() -> {
                    Toast.makeText(mainContext.getApplicationContext(), "Fehler! Aktivität wurde zerstört. Bitte Neustarten", Toast.LENGTH_SHORT).show();
                });
            }
        });
    }


    // Methode, um das nächste Wort anzuzeigen
    private void zeigeNaechstesWort(List<Wort> wortListe, List<Kapitel> kapitelListe, int position, boolean wortwaehlenLayout, Kapitel aktuellesKapitel, boolean lernButtonGeklickt) {
        textViewFrage = activity.findViewById(R.id.frage);
        textViewAbbrechen = activity.findViewById(R.id.textViewAbbrechen);
        buttonAbgeben = activity.findViewById(R.id.buttonAbgeben);
        buttonLoesung = activity.findViewById(R.id.buttonLoesung);
        editText = activity.findViewById(R.id.editTextText);
        buttonWort1 = activity.findViewById(R.id.buttonWort1);
        buttonWort2 = activity.findViewById(R.id.buttonWort2);
        buttonWort3 = activity.findViewById(R.id.buttonWort3);
        radioGroup = activity.findViewById(R.id.radioGroup);
        RadioButton richtigerRadioButton;
        if (currentWortIndex[0] < wortListe.size()) {
            Wort aktuellesWort = wortListe.get(currentWortIndex[0]);
            aktuellesWort.setUebersetzung(aktuellesWort.getUebersetzung().replace(",","/"));
            if (lernButtonGeklickt){
                textViewFrage.setText(aktuellesWort.getWort() + " = " + aktuellesWort.getUebersetzung());
                richtigerRadioButton = null;
            }else {
                textViewFrage.setText("Wie übersetzt man das Wort: \n\n" + aktuellesWort.getWort() + "?");
                if (wortwaehlenLayout) {
                    Random random = new Random();
                    int randomIndex = random.nextInt(3);
                    if (randomIndex == 0) {
                        buttonWort1.setText(aktuellesWort.getUebersetzung());
                        wortListe.remove(aktuellesWort);
                        Wort wortIndex2 = wortListe.get(random.nextInt(wortListe.size()));
                        buttonWort2.setText(wortIndex2.getUebersetzung());
                        wortListe.remove(wortIndex2);
                        buttonWort3.setText(wortListe.get(random.nextInt(wortListe.size())).getUebersetzung());
                        wortListe.add(aktuellesWort);
                        wortListe.add(wortIndex2);
                        richtigerRadioButton = buttonWort1;
                    } else if (randomIndex == 1) {
                        buttonWort2.setText(aktuellesWort.getUebersetzung());
                        wortListe.remove(aktuellesWort);
                        Wort wortIndex1 = wortListe.get(random.nextInt(wortListe.size()));
                        buttonWort1.setText(wortIndex1.getUebersetzung());
                        wortListe.remove(wortIndex1);
                        buttonWort3.setText(wortListe.get(random.nextInt(wortListe.size())).getUebersetzung());
                        wortListe.add(aktuellesWort);
                        wortListe.add(wortIndex1);
                        richtigerRadioButton = buttonWort2;
                    } else {
                        buttonWort3.setText(aktuellesWort.getUebersetzung());
                        wortListe.remove(aktuellesWort);
                        Wort wortIndex1 = wortListe.get(random.nextInt(wortListe.size()));
                        buttonWort1.setText(wortIndex1.getUebersetzung());
                        wortListe.remove(wortIndex1);
                        buttonWort2.setText(wortListe.get(random.nextInt(wortListe.size())).getUebersetzung());
                        wortListe.add(aktuellesWort);
                        wortListe.add(wortIndex1);
                        richtigerRadioButton = buttonWort3;
                    }

                } else {
                    richtigerRadioButton = null;
                }
            }

            buttonAbgeben.setOnClickListener(v -> {
                if (!lernButtonGeklickt) {
                    if (wortwaehlenLayout) {
                        if (richtigerRadioButton == buttonWort1 && buttonWort1.isChecked()) {
                            speichereFortschrittUndFuehreFort(wortListe, kapitelListe, position, wortwaehlenLayout, aktuellesWort, aktuellesKapitel, lernButtonGeklickt);

                        } else if (richtigerRadioButton == buttonWort2 && buttonWort2.isChecked()) {
                            speichereFortschrittUndFuehreFort(wortListe, kapitelListe, position, wortwaehlenLayout, aktuellesWort, aktuellesKapitel, lernButtonGeklickt);

                        } else if (richtigerRadioButton == buttonWort3 && buttonWort3.isChecked()) {
                            speichereFortschrittUndFuehreFort(wortListe, kapitelListe, position, wortwaehlenLayout, aktuellesWort, aktuellesKapitel, lernButtonGeklickt);

                        } else {
                            falscheAntwort();
                        }
                    } else {
                        final String[] splittetUebersetzung = aktuellesWort.getUebersetzung().split("/");
                        if (splittetUebersetzung.length > 1) {
                            boolean istRichtigeLoesung = false;
                            for (String uebersetzung : splittetUebersetzung) {
                                if (uebersetzung.equalsIgnoreCase(editText.getText().toString().trim())) {
                                    speichereFortschrittUndFuehreFort(wortListe, kapitelListe, position, wortwaehlenLayout, aktuellesWort, aktuellesKapitel, lernButtonGeklickt);
                                    editText.setText("");
                                    istRichtigeLoesung = true;
                                }
                            }
                            if (!istRichtigeLoesung) {
                                falscheAntwort();
                            }
                        }else {
                            if (aktuellesWort.getUebersetzung().equalsIgnoreCase(editText.getText().toString().trim())) {
                                speichereFortschrittUndFuehreFort(wortListe, kapitelListe, position, wortwaehlenLayout, aktuellesWort, aktuellesKapitel, lernButtonGeklickt);
                                editText.setText("");
                            } else {
                                falscheAntwort();

                            }
                        }
                    }
                }else {
                    currentWortIndex[0]++;
                    zeigeNaechstesWort(wortListe, kapitelListe, position, wortwaehlenLayout, aktuellesKapitel, lernButtonGeklickt);
                }
            });
            buttonLoesung.setOnClickListener(v -> {
                loesungGezeigt = true;
                if (wortwaehlenLayout) {
                    richtigerRadioButton.setChecked(true);
                    buttonLoesung.setVisibility(View.GONE);
                    buttonAbgeben.setText("Weiter");

                } else {
                    final String[] splittetUebersetzung = aktuellesWort.getUebersetzung().split("/");
                    Random random = new Random();
                    int randomIndex = random.nextInt(splittetUebersetzung.length);
                    editText.setText(splittetUebersetzung[randomIndex]);
                    buttonLoesung.setVisibility(View.GONE);
                    buttonAbgeben.setText("Weiter");
                }
            });

            textViewAbbrechen.setOnClickListener(v -> {
                activity.recreate();
            });

        } else {
            currentkapitelIndex[0]++;
            currentWortIndex[0] = 0;
            zeigeNaechtesKapitel(kapitelListe, position, lernButtonGeklickt);
        }

    }

    private void zeigeNaechtesKapitel(List<Kapitel> kapitelListe, int position, boolean lernButtonGeklickt) {
        final Random random = new Random();
        boolean wortwaehlenLayout = random.nextBoolean();
        if (!lernButtonGeklickt) {
            if (wortwaehlenLayout) {
                activity.setContentView(R.layout.wortwaehlen_layout);
            } else {
                activity.setContentView(R.layout.worteingabe_layout);
            }
        }else {
            activity.setContentView(R.layout.lern_layout);
        }
        
        TextView textViewSprache = activity.findViewById(R.id.sprachekapitel);
        TextView textViewBeschreibung = activity.findViewById(R.id.beschreibung);
        if (currentkapitelIndex[0] < kapitelListe.size()) {
            Kapitel aktuellesKapitel = kapitelListe.get(currentkapitelIndex[0]);
            ExecutorService executor1 = Executors.newSingleThreadExecutor();
            executor1.execute(() -> {
                List<Wort> wortListe = datenbank.wortDao().findByKapitelId(aktuellesKapitel.getId());

                activity.runOnUiThread(() -> {
                    textViewSprache.setText(sprachListe.get(position).getName() + " - " + aktuellesKapitel.getTitel());
                    textViewBeschreibung.setText(aktuellesKapitel.getBeschreibung());

                    zeigeNaechstesWort(wortListe, kapitelListe, position, wortwaehlenLayout, aktuellesKapitel, lernButtonGeklickt);
                });
            });


        } else {
            activity.setContentView(R.layout.lernenbeendet_layout);
            Button buttonBeenden = activity.findViewById(R.id.buttonBeenden);
            buttonBeenden.setOnClickListener(v -> {
                activity.recreate();
            });
        }

    }

    private void speichereFortschrittUndFuehreFort(List<Wort> wortListe, List<Kapitel> kapitelListe, int position, boolean wortwaehlenLayout, Wort aktuellesWort, Kapitel aktuellesKapitel, boolean lernButtonGeklickt) {
        currentWortIndex[0]++;
        if (!loesungGezeigt) {
            ExecutorService executor = Executors.newSingleThreadExecutor();
            executor.execute(() -> {
                List<BenutzerKapitel> benutzerKapitelList = datenbank.benutzerKapitelDao().findByBenutzerId(localBenutzer.getId());
                if (benutzerKapitelList.isEmpty()) {
                    datenbank.benutzerKapitelDao().insert(new BenutzerKapitel(localBenutzer.getId(), aktuellesKapitel.getId()));
                } else {
                    boolean benutzerKapitelExistiert = false;
                    for (BenutzerKapitel benutzerKapitel : benutzerKapitelList) {
                        if (benutzerKapitel.getKapitel_id() == aktuellesKapitel.getId()) {
                            benutzerKapitelExistiert = true;
                            break;
                        }
                    }
                    if (!benutzerKapitelExistiert) {
                        datenbank.benutzerKapitelDao().insert(new BenutzerKapitel(localBenutzer.getId(), aktuellesKapitel.getId()));
                    }

                }
                List<BenutzerWort> benutzerWoerter = datenbank.benutzerWortDao().findByBenutzerId(localBenutzer.getId());
                boolean gefunden = false;
                for (BenutzerWort benutzerWort : benutzerWoerter) {
                    if (benutzerWort.getWort_id() == aktuellesWort.getId()) {
                        gefunden = true;
                        if (!benutzerWort.isWiederholt()) {
                            benutzerWort.setWiederholt(true);
                            datenbank.benutzerWortDao().update(benutzerWort);
                        }
                        break;
                    }

                }
                if (!gefunden) {
                    datenbank.benutzerWortDao().insert(new BenutzerWort(localBenutzer.getId(), aktuellesWort.getId(), aktuellesWort.getKapitel_id(), sprachListe.get(position).getId(), false));

                }


            });
        } else {
            loesungGezeigt = false;
        }
        zeigeNaechstesWort(wortListe, kapitelListe, position, wortwaehlenLayout, aktuellesKapitel, lernButtonGeklickt);
        buttonLoesung.setVisibility(View.GONE);
        buttonAbgeben.setText("Abgeben");
    }

    private void falscheAntwort() {
        Toast.makeText(activity, "Falsche Antwort", Toast.LENGTH_SHORT).show();
        buttonLoesung.setVisibility(View.VISIBLE);
    }
}
