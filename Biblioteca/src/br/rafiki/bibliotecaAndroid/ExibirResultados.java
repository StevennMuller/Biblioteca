package br.rafiki.bibliotecaAndroid;

import java.util.List;


import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;

public class ExibirResultados extends Activity {

	private ResultadosAdapter listAdapter;
	private List<Resultado> resultados;
	private ListView lvResultado;
	Intent intent;

	@SuppressWarnings("unchecked")
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_exibir_resultados);
		lvResultado = (ListView) findViewById(R.id.lvResultados);
		intent = getIntent();

		resultados = (List<Resultado>) intent
				.getSerializableExtra(MainActivity.MESSAGE_RESULTADOS);
		listAdapter = new ResultadosAdapter(this, resultados);
		lvResultado.setAdapter(listAdapter);
		lvResultado.setOnItemClickListener(new OnItemClickListener(
				
				
				) {

					@Override
					public void onItemClick(AdapterView<?> arg0, View arg1,
							int arg2, long arg3) {
						// TODO Auto-generated method stub
					new ParserDetails(ExibirResultados.this).execute("oi");
						
					}
		});
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.activity_exibir_resultados, menu);
		return true;
	}
	
	

}
