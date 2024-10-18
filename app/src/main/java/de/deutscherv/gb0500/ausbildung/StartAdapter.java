package de.deutscherv.gb0500.ausbildung;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class StartAdapter extends RecyclerView.Adapter<StartAdapter.StartHolder>{

    private static Context mainContext;
    private final List<BenutzerWort> benutzerWortListe;
    private final List<BenutzerKapitel> benutzerKapitelListe;
    private final AppDatenbank datenbank;
    private static Activity activity;
    private static double progress = 0.0;
    private Boolean isRecyclerViewVisible = false;


    public StartAdapter(Activity activity,Context mainContext, List<BenutzerWort> benutzerWortListe, AppDatenbank datenbank, List<BenutzerKapitel> benutzerKapitelList) {
        this.activity = activity;
        this.mainContext = mainContext;
        this.benutzerWortListe = benutzerWortListe;
        this.datenbank = datenbank;
        this.benutzerKapitelListe = benutzerKapitelList;
    }

    @NonNull
    @Override
    public StartAdapter.StartHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mainContext).inflate(R.layout.startrecycler_layout, parent, false);
        return new StartHolder(view, datenbank);
    }

    @Override
    public void onBindViewHolder(@NonNull StartAdapter.StartHolder holder, int position) {
        KapitelDetailsAdapter kapitelDetailsAdapter = new KapitelDetailsAdapter(mainContext, benutzerKapitelListe.get(holder.getAdapterPosition()), datenbank);
        RecyclerView recyclerView = holder.itemView.findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(mainContext));recyclerView.setAdapter(kapitelDetailsAdapter);
            holder.SetDetails(benutzerWortListe, benutzerKapitelListe.get(position));
            holder.constraintLayout.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View v) {
                    if (recyclerView.getVisibility() == View.GONE) {
                        holder.constraintLayout.startAnimation(holder.fadeIn);
                        recyclerView.setVisibility(View.VISIBLE);
                    }else{
                        holder.constraintLayout.startAnimation(holder.fadeOut);
                        recyclerView.setVisibility(View.GONE);
                    }
                    isRecyclerViewVisible = !isRecyclerViewVisible;
                }
            });

    }

    @Override
    public int getItemCount() {
        return benutzerKapitelListe.size();
    }

    public int getProgress() {
        int prozentZahl = 0;
        if (getItemCount() == 0|| progress == 0.0){
            return prozentZahl;
        }else {
            prozentZahl = (int) (progress / getItemCount());
            progress = 0.0;
            return prozentZahl;
        }
    }

    public static class StartHolder extends RecyclerView.ViewHolder {

        private final ExecutorService executor = Executors.newSingleThreadExecutor();
        private final TextView textViewSprache;
        private final TextView textViewKapitel;
        private final TextView textViewProzent;
        private final AppDatenbank appDatenbank;
        private final ConstraintLayout constraintLayout;
        private final Animation fadeIn;
        private final Animation fadeOut;

        public StartHolder(View itemView, AppDatenbank datenbank) {
            super(itemView);
            this.appDatenbank = datenbank;
            textViewSprache = itemView.findViewById(R.id.textViewSprache);
            textViewKapitel = itemView.findViewById(R.id.textViewKapitel);
            textViewProzent = itemView.findViewById(R.id.textViewProzent);
            constraintLayout = itemView.findViewById(R.id.startConstraintLayout);
            fadeIn = AnimationUtils.loadAnimation(mainContext, R.anim.fade_in);
            fadeOut = AnimationUtils.loadAnimation(mainContext, R.anim.fade_out);
        }

       void SetDetails(List<BenutzerWort> benutzerWortListe, BenutzerKapitel benutzerKapitel) {
            executor.execute(() -> {
                    Kapitel kapitel = appDatenbank.kapitelDao().findById(benutzerKapitel.getKapitel_id());
                    Sprache sprache = appDatenbank.spracheDao().findById(kapitel.getSprache_id());
                    List<Wort> woerterAusKapitel = appDatenbank.wortDao().findByKapitelId(kapitel.getId());
                    int anzahlGelernt = 0;
                    int anzahlWoerter = woerterAusKapitel.size();
                    for (BenutzerWort benutzerWort: benutzerWortListe) {
                        if (benutzerWort.getKapitel_id() == kapitel.getId()) {
                            anzahlGelernt++;
                        }
                    }
                    double prozent = ((double)anzahlGelernt/anzahlWoerter)*100;
                    progress += prozent;
                    activity.runOnUiThread(()->{
                        textViewSprache.setText(sprache.getName());
                        textViewKapitel.setText("Kapitel " +kapitel.getKapitelNummer() +": " + kapitel.getTitel());
                        textViewProzent.setText(String.format("%.2f",prozent) + "%");
                    });
            });

        }
    }

}
