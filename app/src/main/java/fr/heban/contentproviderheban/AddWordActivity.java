package fr.heban.contentproviderheban;

import androidx.appcompat.app.AppCompatActivity;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.provider.UserDictionary;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

public class AddWordActivity extends AppCompatActivity {

    public final static String INTENT_DATA_ADD_WORD = "fr.heban.contentproviderheban.addwordActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_word);
    }

    /**
     * Méthode permettant de traiter le click sur le bouton d'annulation
     * @param view
     */
    public void onBtnCancelClicked(View view) {
        setResult(RESULT_CANCELED);
        this.finish();
    }

    /**
     * Méthode permettant de traiter le bouton d'ajout d'un mot
     * @param view
     */
    public void onBtnCreateClicked(View view) {
        //On récupère le champ
        EditText word_to_add = (EditText) findViewById(R.id.input_word);
        //On récupère le resolver
        ContentResolver contentResolver = this.getContentResolver();
        //On crée le champ de recherche
        String where_clause = UserDictionary.Words.WORD + " = ?";
        //On crée la query pour savoir si le mot existe ou non
        Cursor cursor = contentResolver.query(UserDictionary.Words.CONTENT_URI, new String[]{
                UserDictionary.Words.WORD
        }, where_clause, new String[]{word_to_add.getText().toString()}, null);
        //S'il n'existe pas
        if (cursor.getCount() == 0) {
            // On a pas de doublon !

            //On crée un ContentValues nécessaire pour l'ajout
            ContentValues contentValues = new ContentValues();
            contentValues.put(UserDictionary.Words.WORD, word_to_add.getText().toString());
            //On ajoute notre donnée dans la base de données
            contentResolver.insert(UserDictionary.Words.CONTENT_URI, contentValues);

            //On renvoi le résultat
            setResult(RESULT_OK);
            //On termine l'activité
            this.finish();

        } else {
            //Un doublon existe déjà !
            // afficher message erreur
            Toast.makeText(this,R.string.app_error_already, Toast.LENGTH_SHORT).show();
        }


    }


}