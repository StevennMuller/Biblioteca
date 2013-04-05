package br.rafiki.bibliotecaAndroid;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.List;

import org.json.JSONException;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnCancelListener;
import android.content.Intent;
import android.os.AsyncTask;
import android.widget.Toast;

public class ParserDetails extends AsyncTask<String, Void, String> {

	private Context context;
	private ProgressDialog progressDialog;

	public ParserDetails(Context context) {
		this.context = context;
	}

	@Override
	protected String doInBackground(String... arg0) {
		try {
			try {
				return detalhar(arg0[0]);
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				cancel(true);
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			Toast.makeText(this.context, "Ocorreu um erro de conexão!",
					Toast.LENGTH_LONG).show();
			cancel(true);
			e.printStackTrace();
			e.getMessage();
		}
		return "";
	}

	private String detalhar(String idLivro) throws IOException, JSONException {
		String link = "http://192.168.1.4:8080/servicoBiblioteca/detalhes/"
				+ idLivro;
		URL url = new URL(link);
		HttpURLConnection conn = (HttpURLConnection) url.openConnection();
		conn.setReadTimeout(130000);
		conn.connect();
		InputStreamReader inputReader = new InputStreamReader(
				conn.getInputStream(), "UTF-8");
		BufferedReader bufferedReader = (new BufferedReader(inputReader));

		String linha, resposta;
		StringBuilder builder = new StringBuilder();
		while ((linha = bufferedReader.readLine()) != null) {
			builder.append(linha);
		}
		resposta = builder.toString();
		System.out.println(resposta);

		return resposta;
	}

	@Override
	protected void onPostExecute(String resposta) {
		Intent intent = new Intent(context, Detalhes.class);
		intent.putExtra(Detalhes.MESSAGE_RESPOSTA, resposta);
		context.startActivity(intent);
		progressDialog.dismiss();
	}

	@Override
	protected void onPreExecute() {
		progressDialog = new ProgressDialog(context);
		progressDialog.setCanceledOnTouchOutside(false);
		// progressDialog.show(context, "Aguarde", "Buscando resultados...");
		progressDialog.setMessage("Buscando resultados!");
		progressDialog.setTitle("Aguarde");
		progressDialog.show();
		progressDialog.setOnCancelListener(new OnCancelListener() {

			@Override
			public void onCancel(DialogInterface arg0) {
				// TODO Auto-generated method stub
				cancel(true);
			}
		});
	}
	
	@Override
	protected void onCancelled(String result) {
		Toast.makeText(this.context, "Cancelado", Toast.LENGTH_SHORT).show();
		
	
	}

}