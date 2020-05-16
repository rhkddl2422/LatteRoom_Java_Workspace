package network.server.vo;

import network.server.dao.Device;

public class Sensor implements Cloneable {
	private String deviceID;
	private String sensorID;
	private String sensorType;
	private SensorData recentData;
	
	
	// constructor
	public Sensor() {
	}
	
	public Sensor(String sensorType) {
		this.sensorType = sensorType;
	}
	
	public Sensor(Device device, String sensorType) {
		this.deviceID = device.getDeviceID();
		this.sensorType = sensorType;
	}
	
	public Sensor(String deviceID, String sensorID, String sensorType) {
		this();
		this.deviceID = deviceID;
		this.sensorID = sensorID;
		this.sensorType = sensorType;
	}
	
	// get, set
	public String getDeviceID() {
		return deviceID;
	}
	
	public void setDeviceID(String deviceID) {
		this.deviceID = deviceID;
	}
	
	public String getSensorID() {
		return sensorID;
	}
	
	public void setSensorID(String sensorID) {
		this.sensorID = sensorID;
	}
	
	public String getSensorType() {
		return sensorType;
	}
	
	public void setSensorType(String sensorType) {
		this.sensorType = sensorType;
	}
	
	public SensorData getRecentData() {
		if(this.recentData == null)
			this.recentData = new SensorData(this.sensorID);
		return recentData;
	}
	
	public SensorData setRecentData(SensorData recentData) {
		this.recentData = recentData;
		return this.recentData;
	}
	
	
	
	// custom method
	public String getStates() {
        return this.recentData.getStates();
    }


	public String getStateDetail() {
        return this.recentData.getStateDetail();
    }
    
    // 지정된 센서에 최신 데이터 업데이트 (states)
    public SensorData setRecentData(String states) {
//        this.recentData = new SensorData(this.sensorID, states);
    	if(this.recentData == null)
    		this.recentData = new SensorData(this.sensorID);
    	this.recentData.update(states);
        return this.recentData;
    }

    // 지정된 센서에 최신 데이터 업데이트 (states, stateDetail)
    public SensorData setRecentData(String states, String stateDetail) {
//        this.recentData = new SensorData(this.sensorID, states, stateDetail);
    	if(this.recentData == null)
    		this.recentData = new SensorData(this.sensorID);
        this.recentData.update(states, stateDetail);
        return this.recentData;
    }

	@Override
	public String toString() {
		return "Sensor [deviceID=" + deviceID + ", sensorID=" + sensorID + ", sensorType=" + sensorType
				+ ", recentData=" + recentData + "]";
	}
	
	public Sensor clone() {
		Sensor copy = null;
		try {
			copy = (Sensor) super.clone();
		} catch (CloneNotSupportedException e) {
			e.printStackTrace();
		}
		return copy;
	}
	
}
