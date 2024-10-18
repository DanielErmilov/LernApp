package de.deutscherv.gb0500.ausbildung;

import android.app.Activity;
import android.content.Context;
import android.graphics.drawable.Icon;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.res.ResourcesCompat;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class KapitelDetailsAdapter extends RecyclerView.Adapter<KapitelDetailsAdapter.KapitelDetailsHolder> {

    private final Context mainContext;
    private final BenutzerKapitel benutzerKapitel;
    private List<Wort> wortListe;
    private final AppDatenbank datenbank;
    private ExecutorService executor = Executors.newSingleThreadExecutor();

    public KapitelDetailsAdapter(Context mainContext,BenutzerKapitel benutzerKapitel, AppDatenbank datenbank) {
        this.mainContext = mainContext;
        this.benutzerKapitel = benutzerKapitel;
        this.datenbank = datenbank;
        executor.execute(() -> {
            wortListe = datenbank.wortDao().findByKapitelId(benutzerKapitel.getKapitel_id());
        });

    }

    @NonNull
    @Override
    public KapitelDetailsAdapter.KapitelDetailsHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(this.mainContext).inflate(R.layout.kapiteldetailsrecycler_layout, parent, false);
        return new KapitelDetailsAdapter.KapitelDetailsHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull KapitelDetailsAdapter.KapitelDetailsHolder holder, int position) {
        executor.execute(() -> {
                    List<BenutzerWort> benutzerWortliste = datenbank.benutzerWortDao().findWoerterByBenutzerAndKapitelId(benutzerKapitel.getBenutzer_id(), benutzerKapitel.getKapitel_id());
                    holder.SetDetails(wortListe.get(position), benutzerWortliste);
                });
    }

    @Override
    public int getItemCount() {
        return wortListe.size();
    }

    public static class KapitelDetailsHolder extends RecyclerView.ViewHolder {

        private final ExecutorService executor = Executors.newSingleThreadExecutor();
        private final TextView textViewWort;
        private final TextView textViewGelernt;
        private final TextView textViewWiederholt;

        public KapitelDetailsHolder(View itemView) {
            super(itemView);
            textViewWort = itemView.findViewById(R.id.wort);
            textViewGelernt = itemView.findViewById(R.id.textViewGelernt);
            textViewWiederholt = itemView.findViewById(R.id.textViewWiederholt);
        }

        void SetDetails(Wort wort, List<BenutzerWort> benutzerWortliste) {
            textViewWort.setText(wort.getWort());
            for (BenutzerWort benutzerWort:benutzerWortliste) {
                if (benutzerWort.getWort_id() == wort.getId()) {
                    textViewGelernt.setText("True");
                    if (benutzerWort.isWiederholt()) {
                        textViewWiederholt.setText("True");
                    }
                    break;
                }

            }
        }

    }
}

