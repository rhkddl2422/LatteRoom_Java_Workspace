package arduino.device.vo;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import arduino.device.TempDevice;
import arduino.device.TestClient;

public class Message {
	private String deviceID;
	private String deviceType;
	private String dataType;
	private String jsonData;
	private static Gson gson = new GsonBuilder().setDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS").create();
	
	
	// constructor
	private Message() {
//        this.deviceID = TempDevice.getDeviceID();
//        this.deviceType = TempDevice.getDeviceType();
    }
	
	public Message(TestClient client) {
		this.deviceID = client.getDeviceID();
		this.deviceType = client.getDeviceType();
	}
	
	public Message(SensorData data) {
        this();
        this.dataType = "SensorData";
        this.jsonData = Message.gson.toJson(data);
    }
	
	public Message(Alert data) {
        this();
        this.dataType = "Alert";
        this.jsonData = Message.gson.toJson(data);
    }
	
	public Message(String sensorID) {
		this();
		this.dataType = "Request";
		this.jsonData = sensorID;
	}
	
	public Message(String id, String type, String data) {
		this.deviceID = id;
		this.dataType = type;
		this.jsonData = data;
	}
	
	public Message(TestClient client, String data) {
		this(client);
		this.dataType = "SensorData";
		this.jsonData = data;
	}
	
	public Message(TestClient client, SensorData data) {
		this(client);
		this.dataType = "SensorData";
        this.jsonData = Message.gson.toJson(data);
	}
	
	public Message(String id, SensorData data) {
		this.deviceID = id;
		this.dataType = "SensorData";
        this.jsonData = Message.gson.toJson(data);
	}


	// custom method
	public SensorData getSensorData() {
		return Message.gson.fromJson(this.jsonData, SensorData.class);
	}
	
	public String getRequestData() {
		return this.jsonData;
	}
	
	
	// get, set method
	public String getDeviceID() {
		return deviceID;
	}

	public void setDeviceID(String deviceID) {
		this.deviceID = deviceID;
	}

	public String getDeviceType() {
		return deviceType;
	}

	public void setDeviceType(String deviceType) {
		this.deviceType = deviceType;
	}

	public String getDataType() {
		return dataType;
	}
	
	public void setDataType(String dataType) {
		this.dataType = dataType;
	}

	public String getJsonData() {
		return jsonData;
	}

	public void setJsonData(String jsonData) {
		this.jsonData = jsonData;
	}

	@Override
	public String toString() {
		return "Message [deviceID=" + deviceID + ", dataType=" + dataType + ", jsonData=" + jsonData + "]";
	}
	
}
