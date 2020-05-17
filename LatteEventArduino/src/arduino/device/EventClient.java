package arduino.device;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketTimeoutException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import arduino.device.vo.Message;
import arduino.device.vo.SensorData;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.geometry.Orientation;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.FlowPane;
import javafx.stage.Stage;

public class EventClient extends Application implements TestClient {
	
	private BorderPane root;
	private FlowPane bottom, right;
	private TextArea textarea;
	private Button conn, disconn;
	private Button tempHeat, tempCool, tempUp, tempDw, lightSwitch, lightUp, lightDw;
	private Button bed0, bed45, bed90;
	private TextField currTempField, currLightField, currBedField;
	private double currTemp = 24.0;
	private int currLight = 50;
	private boolean currLightSwitch = false;
	private int currBed = 0;
	
	private ServerListener connLatte;
	
	private static Gson gson = new GsonBuilder().setDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS").create();
	
	@Override
	public String getDeviceID() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getDeviceType() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getSensorList() {
		// TODO Auto-generated method stub
		return null;
	}
	
	private void displayText(String msg) {
		Platform.runLater(() -> {
			textarea.appendText(msg + "\n");
		});
	}
	
	@Override
	public void start(Stage primaryStage) throws Exception {
		
		connLatte = new ServerListener();
		
		// UI ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
		root = new BorderPane();
		root.setPrefSize(700, 500);
		
		// Center ----------------------------------------------
		textarea = new TextArea();
		textarea.setEditable(false);
		root.setCenter(textarea);
		
		// Buttom ----------------------------------------------
		bottom = new FlowPane();
		bottom.setPrefSize(700, 50);
		
		// -----
		Label labelTemp = new Label("Temp");
		labelTemp.setPrefSize(80, 40);
		labelTemp.setAlignment(Pos.CENTER);
		
		currTempField = new TextField(String.valueOf(currTemp));
		currTempField.setPrefSize(70, 40);
		
		tempHeat = new Button("Heat\nOff");
		tempHeat.setPrefSize(50, 40);
		
		tempCool = new Button("Cool\nOff");
		tempCool.setPrefSize(50, 40);
		
		tempUp = new Button("+");
		tempUp.setPrefSize(50, 40);
		tempUp.setOnAction((e) -> {
			currTemp += Math.random();
			currTempField.setText(String.valueOf(currTemp));
			connLatte.send(String.valueOf(currTemp));
		});
		tempDw = new Button("-");
		tempDw.setPrefSize(50, 40);
		tempDw.setOnAction((e) -> {
			currTemp -= Math.random();
			currTempField.setText(String.valueOf(currTemp));
			connLatte.send(String.valueOf(currTemp));
		});
		
		bottom.getChildren().addAll(labelTemp, currTempField, tempHeat, tempCool, tempUp, tempDw);
		
		// -----
		Label labelLight = new Label("Light");
		labelLight.setPrefSize(80, 40);
		labelLight.setAlignment(Pos.CENTER);
		
		currLightField = new TextField(String.valueOf(currLight));
		currLightField.setPrefSize(70, 40);
		
		lightSwitch = new Button("Off");
		lightSwitch.setPrefSize(50, 40);
		lightSwitch.setOnAction((e) -> {
			currLightSwitch = !currLightSwitch;
			if(currLightSwitch) {
				lightSwitch.setText("On");
			} else {
				lightSwitch.setText("Off");
			}
		});
		
		lightUp = new Button("+");
		lightUp.setPrefSize(50, 40);
		lightUp.setOnAction((e) -> {
			currLight += Math.random()*10;
			currLightField.setText(String.valueOf(currLight));
		});
		lightDw = new Button("-");
		lightDw.setPrefSize(50, 40);
		lightDw.setOnAction((e) -> {
			currLight -= Math.random()*10;
			currLightField.setText(String.valueOf(currLight));
		});
		
		bottom.getChildren().addAll(labelLight, currLightField, lightSwitch, lightUp, lightDw);

		// -----
		Label labelBed = new Label("Bed");
		labelBed.setPrefSize(80, 40);
		labelBed.setAlignment(Pos.CENTER);
		
		currBedField = new TextField("0");
		currBedField.setPrefSize(70, 40);
		
		bed0 = new Button("0");
		bed0.setPrefSize(50, 40);
		bed0.setOnAction((e) -> {
			currBed = 0;
			currBedField.setText(String.valueOf(currBed));
		});
		bed45 = new Button("45");
		bed45.setPrefSize(50, 40);
		bed45.setOnAction((e) -> {
			currBed = 45;
			currBedField.setText(String.valueOf(currBed));
		});
		bed90 = new Button("90");
		bed90.setPrefSize(50, 40);
		bed90.setOnAction((e) -> {
			currBed = 90;
			currBedField.setText(String.valueOf(currBed));
		});
		
		
		bottom.getChildren().addAll(labelBed, currBedField, bed0, bed45, bed90);
		root.setBottom(bottom);
		
		// Right ----------------------------------------------
		right = new FlowPane(Orientation.VERTICAL);
		right.setPrefSize(100, 450);
		
		conn = new Button("Start");
		conn.setPrefSize(100, 60);
		conn.setOnAction((e) -> {
			connLatte.connect();
		});
		disconn = new Button("Stop");
		disconn.setPrefSize(100, 60);
		disconn.setOnAction((e) -> {
			connLatte.disconnect();
		});
			
		right.getChildren().addAll(conn, disconn);
		root.setRight(right);
		
		// ------------------------------------------------------
		
		Scene scene = new Scene(root);
		primaryStage.setScene(scene);
		primaryStage.setTitle("EventClient");
		primaryStage.setOnCloseRequest((e) -> {
			connLatte.disconnect();
		});
		primaryStage.show();
		
	}
	
	public static void main(String[] args) {
		launch(args);
	}
	
	class ServerListener {
		private ServerSocket server;
		private Socket socket;
		private BufferedReader serverIn;
		private PrintWriter serverOut;
		private ExecutorService executor;
		
		
		public void connect() {
			
			executor = Executors.newFixedThreadPool(1);
			
			Runnable runnable = () -> {
				try {
					server = new ServerSocket();
					server.bind(new InetSocketAddress(60000));
					server.setSoTimeout(3000);
					
					displayText("ready to accept()");
					while(true) {
						try {
							socket = server.accept();
							break;
						} catch (SocketTimeoutException e) {
							if(Thread.interrupted())
								break;
							else continue;
						}
					}
					
					serverIn = new BufferedReader(new InputStreamReader(socket.getInputStream()));
					serverOut = new PrintWriter(socket.getOutputStream());
				} catch (IOException e) {
//					e.printStackTrace();
					disconnect();
					return;
				}
				displayText("Latte Connected");
				
				String line = "";
				while(true) {
					try {
						line = serverIn.readLine();
						
						if(line == null) {
							displayText("server error. disconnected");
							throw new IOException();
						} else {
							displayText("get ] " + line);
							
							if(line.equals("COOLOFF")) {
								Platform.runLater(() -> {
									tempCool.setText("Cool\nOff");
								});
							} else if(line.equals("COOLON")) {
								Platform.runLater(() -> {
									tempCool.setText("Cool\nOn");
								});
							} else if(line.equals("HEATOFF")) {
								Platform.runLater(() -> {
									tempHeat.setText("Heat\nOff");
								});
							} else if(line.equals("HEATOFF")) {
								Platform.runLater(() -> {
									tempHeat.setText("Heat\nOn");
								});
							} else if(line.equals("BOTHOFF")) {
								Platform.runLater(() -> {
									tempCool.setText("Cool\nOff");
									tempHeat.setText("Heat\nOff");
								});
							}
							
						}
					} catch (IOException e) {
//						e.printStackTrace();
						disconnect();
						break;
					}
				} // while()
			};
			executor.submit(runnable);
		} // startClient()
		
		public void disconnect() {
			try {
				if(socket != null && !socket.isClosed()) {
					socket.close();
					if(serverIn != null) serverIn.close();
					if(serverOut != null) serverOut.close();
				}
				if(executor != null && !executor.isShutdown()) {
					executor.shutdownNow();
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		} // stopClient()
		
		public void send(String msg) {
			if(socket != null && !socket.isClosed()) {
				serverOut.println(msg);
				serverOut.flush();
			}
		}
		
		public void send(Message msg) {
			serverOut.println(gson.toJson(msg));
//			serverOut.println("서버야 좀 받아라~!");
			serverOut.flush();
			displayText("서버로 보냈다!!! "+msg);
		}
		
		public void send(String sensorID, String states) {
			Message message = new Message(new SensorData(sensorID, states));
			send(message);
		}
		
	} // ServerListener

}
