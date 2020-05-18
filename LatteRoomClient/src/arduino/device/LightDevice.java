package arduino.device;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.InetSocketAddress;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import arduino.device.LightDevice.SerialListener;
import arduino.device.LightDevice.ServerListener;
import arduino.device.vo.*;
import gnu.io.CommPortIdentifier;
import gnu.io.NoSuchPortException;
import gnu.io.SerialPort;
import gnu.io.SerialPortEvent;
import gnu.io.SerialPortEventListener;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.scene.Scene;
import javafx.scene.control.TextArea;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;

public class LightDevice extends Application implements TestClient {

	private static final String DEVICE_ID = "LATTE03";
	private static final String DEVICE_TYPE = "DEVICE";		// App : "USER"
	
	private static final String COMPORT_NAMES = "COM5";
	private static final String SERVER_ADDR = "70.12.60.105";
//	private static final String SERVER_ADDR = "localhost";
	private static final int SERVER_PORT = 55566;
	
	private BorderPane root;
	private TextArea textarea;
	
	private ServerListener toServer = new ServerListener();
	private SerialListener toArduino = new SerialListener();
	private LightSharedObject sharedObject;
	
	private Sensor lightSensor = new Sensor(this, "LIGHT", "LIGHT");
	
	
	private static Gson gson = new GsonBuilder().setDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS").create();
	
	
	// ======================================================
	public void displayText(String msg) {
		Platform.runLater(() -> {
			textarea.appendText(msg + "\n");
		});
	}
	
	public static Gson getGson() {
		return gson;
	}
	
	@Override
	public String getDeviceID() {
		// TODO Auto-generated method stub
		return DEVICE_ID;
	}

	@Override
	public String getDeviceType() {
		// TODO Auto-generated method stub
		return DEVICE_TYPE;
	}

	@Override
	public String getSensorList() {
		List<Sensor> sensorList = new ArrayList<Sensor>();
		sensorList.add(lightSensor);
		return gson.toJson(sensorList);
	}
	
	
	
	// ======================================================
	@Override
	public void start(Stage primaryStage) throws Exception {
		
		// Logic
		toServer.initialize();
		toArduino.initialize();

		// SharedObject
		sharedObject = new LightSharedObject(this, toServer, toArduino);
		
		
		// UI ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
		root = new BorderPane();
		root.setPrefSize(700, 500);
		
		// Center ----------------------------------------------
		textarea = new TextArea();
		textarea.setEditable(false);
		root.setCenter(textarea);
		
		
		Scene scene = new Scene(root);
		primaryStage.setScene(scene);
		primaryStage.setTitle("DeviceTemp");
		primaryStage.setOnCloseRequest((e) -> {
			toServer.close();
			toArduino.close();
		});
		primaryStage.show();
	}// start()
	
	
	
	// ======================================================
	public static void main(String[] args) {
		launch(args);
	}
	
	
	
	// ======================================================
	class ServerListener {
		private Socket socket;
		private BufferedReader serverIn;
		private PrintWriter serverOut;
		private ExecutorService executor;
		
		
		public void initialize() {
			
			executor = Executors.newFixedThreadPool(1);
			
			Runnable runnable = () -> {
				try {
					socket = new Socket();
					socket.connect(new InetSocketAddress(SERVER_ADDR, SERVER_PORT));
					serverIn = new BufferedReader(new InputStreamReader(socket.getInputStream()));
					serverOut = new PrintWriter(socket.getOutputStream());
				} catch (IOException e) {
//					e.printStackTrace();
					close();
					return;
				}
				
				// 
				send(getDeviceID());
				send(getDeviceType());
				send(new Message(getDeviceID()
						, "SENSOR_LIST"
						, getSensorList()));
				
				
				String line = "";
				while(true) {
					try {
						line = serverIn.readLine();
						
						if(line == null) {
							displayText("server error. disconnected");
							throw new IOException();
						} else {
							displayText("Server ] " + line);
							
							Message message = gson.fromJson(line, Message.class);
							
							try {
							SensorData data = gson.fromJson(message.getJsonData(), SensorData.class);
							
							lightSensor.setRecentData(data.getStateDetail());
							
							if(data.getStates().equals("ON")) {
				                toArduino.send(data.getStateDetail());
								
			                }else if(data.getStates().equals("OFF")) {
			                	toArduino.send("0");
			                }
							}catch(Exception e2) {
								displayText(e2.toString());
							}
							
						}
					} catch (IOException e) {
//						e.printStackTrace();
						close();
						break;
					}
				} // while()
			};
			executor.submit(runnable);
		} // startClient()
		
		public void close() {
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
			serverOut.println(msg);
			serverOut.flush();
		}
		
		public void send(Message msg) {
			serverOut.println(gson.toJson(msg));
			serverOut.flush();
			displayText("Message전송! "+msg);
		}
		
		public void send(String sensorID, String states) {
			Message message = new Message(new SensorData(sensorID, states));
			send(message);
		}
		
	} // ServerListener
	
	
	
	// ======================================================
	class SerialListener implements SerialPortEventListener {
		
		SerialPort serialPort;
		
		private BufferedReader serialIn;
		private PrintWriter serialOut;
		private static final int TIME_OUT = 2000;
		private static final int DATA_RATE = 9600;
		
		public void initialize() {
			CommPortIdentifier portId = null;
			try {
				portId = CommPortIdentifier.getPortIdentifier(COMPORT_NAMES);
			} catch (NoSuchPortException e1) {
				e1.printStackTrace();
			};
			
			if (portId == null) {
				System.out.println("Could not find COM port.");
				return;
			}

			try {
				// open serial port, and use class name for the appName.
				serialPort = (SerialPort) portId.open(this.getClass().getName(), TIME_OUT);

				// set port parameters
				serialPort.setSerialPortParams(
						DATA_RATE,					// 9600 
						SerialPort.DATABITS_8, 
						SerialPort.STOPBITS_1,
						SerialPort.PARITY_NONE);

				// open the streams
				serialIn = new BufferedReader(new InputStreamReader(serialPort.getInputStream()));
				serialOut = new PrintWriter(serialPort.getOutputStream());

				// add event listeners
				serialPort.addEventListener(this);
				serialPort.notifyOnDataAvailable(true);
			} catch (Exception e) {
				System.err.println(e.toString());
			}
		}

		/**
		 * This should be called when you stop using the port. This will prevent port
		 * locking on platforms like Linux.
		 */
		public synchronized void close() {
			if (serialPort != null) {
				serialPort.removeEventListener();
				serialPort.close();
			}
		}
		
		public synchronized void send(String msg) {
			serialOut.println(msg);
			serialOut.flush();
		}

		/**
		 * Handle an event on the serial port. Read the data and print it.
		 */
		public synchronized void serialEvent(SerialPortEvent oEvent) {
			if (oEvent.getEventType() == SerialPortEvent.DATA_AVAILABLE) {
				try {
					String inputLine = serialIn.readLine();
					displayText("get : " + inputLine);
					
				} catch (Exception e) {
//					System.err.println(e.toString() + "  : prb de lecture");
				}
			}
			// Ignore all the other eventTypes, but you should consider the other ones.
		}
		
	} // SerialListener

} // TempDevice

class LightSharedObject {
	// Temperature & Heat & Cool
	private int hopeStates = 23;
	private int states = 1000;
	private TestClient client;
	private ServerListener toServer;
	private SerialListener toArduino;
	
	
	LightSharedObject(TestClient client, ServerListener toServer, SerialListener toArduino) {
		this.client = client;
		this.toServer = toServer;
		this.toArduino = toArduino;
	}
	
	public synchronized int getHopeStates() {
		return this.hopeStates;
	}
	
	public synchronized void setHopeStates(int hopeStates) {
		this.hopeStates = hopeStates;
		control();
	}
	
	public synchronized int getStates() {
		return states;
	}
	
	public synchronized void setStates(int states) {
		this.states = states;
		control();
	}
	
	private synchronized void control() {
		
	}
	
}