package fr.heban.contentproviderheban;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;

import android.content.ContentResolver;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.provider.UserDictionary;
import android.util.SparseBooleanArray;
import android.view.View;
import android.widget.AdapterView;
import android.widget.CursorAdapter;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;
import android.widget.Switch;

import java.util.List;

public class MainActivity extends AppCompatActivity {

    SimpleCursorAdapter adapter = null;
    Cursor cursor = null;
    ContentResolver resolver;

    // Ajout du callback pour l'intention
    private final ActivityResultLauncher<Intent> mActivityResult = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                //Si le retour est OK, alors on recharge les données
                if(result.getResultCode() == RESULT_OK) {
                   this.load_data();
                }
            }
    );

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        // Récupération du résolveur
        this.resolver = getContentResolver();
        // Chargement des données
        this.load_data();

        ListView listView = (ListView) findViewById(R.id.listview_dic);
        // Catch quand on clique sur un item dans la liste
        listView.setOnItemClickListener((adapterView, view, i, l) -> {
            this.cursor = (Cursor) adapterView.getAdapter().getItem(i);
        });
    }

    /**
     * Méthode permettant de traiter le click sur le bouton de suppression
     * @param view
     */
    public void onBtnDeleteClicked(View view) {

        ListView listView = (ListView) findViewById(R.id.listview_dic);
        // On vérifie que le cursor ne soit pas null
        if (cursor != null) {
            // On construit le champ de recherche
            String where_clause = UserDictionary.Words.WORD + " = ?";
            // On supprime la donnée
            this.resolver.delete(UserDictionary.Words.CONTENT_URI, where_clause, new String[]{cursor.getString(0)});
            // On recharge les données
            this.load_data();
        }

    }

    /**
     * Méthode permettant de traiter le click sur le bouton d'ajout
     * @param view
     */
    public void onBtnAddClicked(View view) {
        Intent intent = new Intent(this, AddWordActivity.class);
        this.mActivityResult.launch(intent);
    }

    /**
     * Méthode permettant de traiter le click sur le switch
     * @param view
     */
    public void onSwitchClicked(View view) {
        this.load_data();
    }

    public void load_data() {
        // On récupère le switch
        Switch BtnSwitch = findViewById(R.id.switch_alpha);
        // par défaut on ne trie pas
        String sorted;

        if (!BtnSwitch.isChecked()) {
            // Trie descendant, z -> A
            sorted = UserDictionary.Words.WORD + " DESC";
        } else {
            // Tire ascendant A -> z
            sorted = UserDictionary.Words.WORD + " ASC";
        }
        // On récupère les données
        Cursor cursor = this.resolver.query(
                UserDictionary.Words.CONTENT_URI,
                new String[]{UserDictionary.Words.WORD, UserDictionary.Words._ID},
                null,
                null,
                sorted);

        ListView listView = (ListView) findViewById(R.id.listview_dic);
        // On crée notre SimpleCursorAdapter
        this.adapter = new SimpleCursorAdapter(this, android.R.layout.simple_list_item_activated_1, cursor, new String[]{UserDictionary.Words.WORD}, new int[]{android.R.id.text1}, 0);
        // On ajoute notre adpater dans notre liste
        listView.setAdapter(adapter);
    }
}