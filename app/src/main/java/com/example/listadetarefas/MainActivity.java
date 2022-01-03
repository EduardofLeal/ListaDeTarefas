package com.example.listadetarefas;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

   private EditText editText;
   private ListView listView;
   private Button button;

   private ArrayAdapter<String> itensAdaptador;
   private ArrayList<Integer> iDS;
   private  ArrayList<String> itens;

   private SQLiteDatabase bancoDados;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        editText = (EditText) findViewById(R.id.editTextAfazer);
        listView = (ListView) findViewById(R.id.listAfazeres);
        button = (Button) findViewById(R.id.btn1);

        carregaTarefas();

        listView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> adapterView, View view, int i, long l) {
                alertaApagaTarefa(i);
                return false;
            }
        });

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                adicionarNovaTarefa(editText.getText().toString());
            }
        });
    }

    private void carregaTarefas(){
        try
        {
            bancoDados = openOrCreateDatabase("ListaDeTarefas", MODE_PRIVATE, null);
            bancoDados.execSQL("CREATE TABLE IF NOT EXISTS minhasTarefas(id INTEGER PRIMARY KEY AUTOINCREMENT, tarefa VARCHAR)");

            /*String novaTarefa = editText.getText().toString();
            bancoDados.execSQL("INSERT INTO minhasTarefas(tarefa) VALUES('" + novaTarefa + "')");*/

            Cursor cursor = bancoDados.rawQuery("SELECT * FROM minhasTarefas ORDER BY id DESC", null);

            int indiceColunaID = cursor.getColumnIndex("id");
            int indiceColunaTarefa = cursor.getColumnIndex("tarefa");

            itens = new ArrayList<String>();
            iDS = new ArrayList<Integer>();

             itensAdaptador = new ArrayAdapter<String>(getApplicationContext(),
                    android.R.layout.simple_list_item_2,
                    android.R.id.text1,
                    itens);


            listView.setAdapter(itensAdaptador);

            cursor.moveToFirst();
            while(cursor != null){
                Log.i("LogX", "ID: " + cursor.getString(indiceColunaID) + " Tarefa: " + cursor.getString(indiceColunaTarefa) );
                itens.add(cursor.getString(indiceColunaTarefa));
                iDS.add(Integer.parseInt(cursor.getString(indiceColunaID)));
                cursor.moveToNext();
            }
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }

    }
    private void adicionarNovaTarefa(String novaTarefa){
        try
        {
            if(novaTarefa.equals("")){
                Toast.makeText(MainActivity.this, "Type in a Task!", Toast.LENGTH_SHORT).show();
            }
            else{
                Toast.makeText(MainActivity.this, "Task Added!", Toast.LENGTH_SHORT).show();
                editText.setText("");
                bancoDados.execSQL("INSERT INTO minhasTarefas(tarefa) VALUES('" + novaTarefa + "')");
                carregaTarefas();
            }
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }
    private void apagarTarefa(Integer id){
            try
            {
                bancoDados.execSQL("DELETE FROM minhasTarefas WHERE id = " + id);
                carregaTarefas();
                Toast.makeText(MainActivity.this, "Task Removed!", Toast.LENGTH_SHORT).show();
            }
            catch (Exception e)
            {
                e.printStackTrace();
            }
    }
    private void alertaApagaTarefa(Integer idSelecionado){
        String tarefaSelecionada = itens.get(idSelecionado);
        Integer numeroID = idSelecionado;

        new AlertDialog.Builder(MainActivity.this).setTitle("Warning!")
                .setMessage("Do you wish to delete this task?")
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        apagarTarefa(iDS.get(numeroID));
                    }
                }).setNegativeButton("No", null).show();
    }
}