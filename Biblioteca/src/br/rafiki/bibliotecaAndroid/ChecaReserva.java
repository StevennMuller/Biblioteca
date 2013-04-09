package br.rafiki.bibliotecaAndroid;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.IBinder;
import android.os.Looper;
import android.os.Message;
//import android.widget.Toast;

public class ChecaReserva extends Service {
	private Looper mServiceLooper;
	private ServiceHandler mServiceHandler;

	// Handler that receives messages from the thread
	private final class ServiceHandler extends Handler {
		private String senha;
		private String matricula;
		private Context context;

		public ServiceHandler(Looper looper, Context context) {
			super(looper);
			this.context=context;
		}

		public void setSenha(String senha)

		{
			this.senha = senha;
		}

		public void setMatricula(String matricula)

		{
			this.matricula = matricula;
		}

		@Override
		public void handleMessage(Message msg) {
			// Normally we would do some work here, like download a file.
			// For our sample, we just sleep for 5 seconds.

			ConnectivityManager connMgr = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
			NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();

			while(true){
			while (networkInfo == null || !networkInfo.isConnected()) {
				//verificar conexao com a internet
				long endTime = System.currentTimeMillis() + 5* 1000;
				while (System.currentTimeMillis() < endTime) {
					synchronized (this) {
						try {
							wait(endTime - System.currentTimeMillis());
						} catch (Exception e) {
						}
					}
				}
			}

			// Stop the service using the startId, so that we don't stop
			// the service in the middle of handling another job
			//System.out.println("INTERNET!");
			//verifica reservas a cada 6h
			ChecaReservas(context, matricula, senha);
			long endTime = System.currentTimeMillis() +  6*3600* 1000; //6 * 1000 * 3600;//6horas
			while (System.currentTimeMillis() < endTime) {
				synchronized (this) {
					try {
						wait(endTime - System.currentTimeMillis());
					} catch (Exception e) {
					}
				}
			}
			}
		}
	}

	@Override
	public void onCreate() {
		// Start up the thread running the service. Note that we create a
		// separate thread because the service normally runs in the process's
		// main thread, which we don't want to block. We also make it
		// background priority so CPU-intensive work will not disrupt our UI.
		HandlerThread thread = new HandlerThread("ServiceChecaReservas",
				HandlerThread.MAX_PRIORITY);
		thread.start();

		// Get the HandlerThread's Looper and use it for our Handler
		mServiceLooper = thread.getLooper();
		mServiceHandler = new ServiceHandler(mServiceLooper, this);
	}

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		//Toast.makeText(this, "service starting", Toast.LENGTH_SHORT).show();

		// For each start request, send a message to start a job and deliver the
		// start ID so we know which request we're stopping when we finish the
		// job
		Message msg = mServiceHandler.obtainMessage();
		msg.arg1 = startId;
		mServiceHandler.sendMessage(msg);
		
		String Senha = intent.getStringExtra("SENHA");
		mServiceHandler.setSenha(Senha);
		String matricula = intent.getStringExtra("MATRICULA");
		mServiceHandler.setMatricula(matricula);
		
		// If we get killed, after returning from here, restart
		return START_REDELIVER_INTENT;
	}

	@Override
	public IBinder onBind(Intent intent) {
		// We don't provide binding, so return null
		return null;
	}

	@Override
	public void onDestroy() {
	//	Toast.makeText(this, "service done", Toast.LENGTH_SHORT).show();
	}

	public void ChecaReservas(Context context,String matricula,String senha) {
		try {
			String resposta=Logar(matricula,senha);
			ResolveJson(resposta,context);
			
			
			
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	private String Logar(String matricula, String senha) throws IOException {
		String link = "http://10.0.2.2:8080/servicoBiblioteca/emprestimo/"
				+ matricula + "/" + senha;
		URL url = new URL(link);
		HttpURLConnection conn = (HttpURLConnection) url.openConnection();
		conn.setReadTimeout(530000);
		conn.connect();
		conn.getInputStream();
		InputStreamReader inputReader = new InputStreamReader(
				conn.getInputStream(), "UTF-8");
		BufferedReader bufferResposta = (new BufferedReader(inputReader));

		String linha, resposta;
		StringBuilder builder = new StringBuilder();
		while ((linha = bufferResposta.readLine()) != null) {
			builder.append(linha);
		}
		resposta = builder.toString();
		
		if("{}".equals(resposta) || "{\"valido\":\"false\"}".equals(resposta))
			stopSelf();

		return resposta;

	}
	
	public void Notifica(String livro)
	{
		// Prepare intent which is triggered if the
		// notification is selected

		Intent intent = new Intent(this, LoginActivity.class);
		PendingIntent pIntent = PendingIntent.getActivity(this, 0, intent, 0);

		// Build notification
		// Actions are just fake
		Notification noti = new Notification.Builder(this)
		        .setContentTitle("Livro: "+livro)
		        .setContentText("Dispon�vel")
		        .setSmallIcon(R.drawable.ic_launcher)
		        .setContentIntent(pIntent)
		       .build();
		    
		  
		NotificationManager notificationManager = 
		  (NotificationManager) getSystemService(NOTIFICATION_SERVICE);

		// Hide the notification after its selected
		noti.flags |= Notification.FLAG_AUTO_CANCEL;

		notificationManager.notify(0, noti); 
	}
	public void ResolveJson(String jsonString,Context context)
	{
		JSONObject emprestimosJson;
		try {
			emprestimosJson = new JSONObject(jsonString);
		
		
		try{
			JSONArray reserva = new JSONArray();
			reserva = emprestimosJson.getJSONArray("reservas");
			for(int i = 0; i < reserva.length(); i++){
				JSONObject reservs = new JSONObject();
				reservs =  reserva.getJSONObject(i);
				
				if(reservs.getString("status").equals("Dispon�vel"))
				{
					//Toast.makeText(context, reservs.getString("livro")+" Dispon�vel", Toast.LENGTH_LONG);
					//System.out.print("Toast Aqui");
					Notifica(reservs.getString("livro"));
				}
			}
		} catch(JSONException e){
			try
			{
			JSONObject reserva = new JSONObject();
			reserva = emprestimosJson.getJSONObject("reservas");
			if(reserva.getString("status")=="Dispon�vel")
				//Toast.makeText(context, reserva.getString("livro")+" Dispon�vel", Toast.LENGTH_LONG);
				Notifica(reserva.getString("livro"));
			}
			catch (JSONException i) {
				// TODO: handle exception
				i.printStackTrace();
				
				
			}
		}
		
		} catch (JSONException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
			
		}
		
		
		
	}
}
