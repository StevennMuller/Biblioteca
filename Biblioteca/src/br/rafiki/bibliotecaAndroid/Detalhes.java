package br.rafiki.bibliotecaAndroid;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.Html;
import android.view.Menu;
import android.widget.TextView;

public class Detalhes extends Activity {
	public static String  MESSAGE_RESPOSTA="RESPOSTA_JSON"; 
	private TextView tvTitValor;
	private TextView tvEdiValor;
	private TextView tvDispValor;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		setContentView(R.layout.activity_detalhes);
		Intent intent=getIntent();
		String resposta=intent.getStringExtra(MESSAGE_RESPOSTA);
		tvDispValor=(TextView)findViewById(R.id.tvDispValor);
		tvEdiValor=(TextView)findViewById(R.id.tvEdiValor);
		tvTitValor=(TextView)findViewById(R.id.tvTitValor);
		
		try {
			JSONObject detalhesJson =new JSONObject(resposta);
			tvTitValor.setText(Html.fromHtml(detalhesJson.get("titulo").toString()));
			tvEdiValor.setText(Html.fromHtml(detalhesJson.get("editora").toString()));
			
			//Disponibilidades
			String strDisp = "";
			
			try{
				JSONArray disponibilidade = new JSONArray();
				disponibilidade = detalhesJson.getJSONArray("disponibilidades");
				
				for(int i = 0; i < disponibilidade.length(); i++){
					JSONObject disp = new JSONObject();
					disp =  disponibilidade.getJSONObject(i);
					
					strDisp += "<b><i>Biblioteca:</i></b> " + disp.getString("nomeBiblioteca") + "<br>";
					strDisp += "<b><i>Localização:</i></b> " + disp.getString("localizacaoEstante") + "<br>";
					strDisp += "<b><i>Disponíveis:</i></b> " + disp.getString("qtdDisponivel") + "<br>";
					strDisp += "<b><i>Emprestados:</i></b> " + disp.getString("qtdEsprestado") + "<br>";
					strDisp += "<b><i>Consultas:</i></b> " + disp.getString("qtdExemplarConsulta") + "<br>";

					strDisp += "<br>";
				}
			} catch(JSONException e){
				JSONObject disponibilidade = new JSONObject();
				disponibilidade = detalhesJson.getJSONObject("disponibilidades");
				
				strDisp += "<b><i>Biblioteca:</i></b> " + disponibilidade.getString("nomeBiblioteca") + "<br>";
				strDisp += "<b><i>Localização:</i></b> " + disponibilidade.getString("localizacaoEstante") + "<br>";
				strDisp += "<b><i>Disponíveis:</i></b> " + disponibilidade.getString("qtdDisponivel") + "<br>";
				strDisp += "<b><i>Emprestados:</i></b> " + disponibilidade.getString("qtdEsprestado") + "<br>";
				strDisp += "<b><i>Consultas:</i></b> " + disponibilidade.getString("qtdExemplarConsulta") + "<br>";
			}
			
			
			tvDispValor.setText(Html.fromHtml(strDisp));
			
			
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.activity_detalhes, menu);
		return true;
	}

}
