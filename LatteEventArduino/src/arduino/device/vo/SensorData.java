package arduino.device.vo;

import java.sql.Date;

public class SensorData {
	private String sensorID;
	private Date time;
	private String states;
	private String stateDetail;
	
	
	// constructor
	private SensorData() {
		this.time = new Date(System.currentTimeMillis());
	}
	
	public SensorData(String sensorID) {
		this.sensorID = sensorID;
	}
	
	public SensorData(String sensorID, String states) {
		this();
		this.sensorID = sensorID;
		this.states = states;
	}
	
	public SensorData(String sensorID, String states, String stateDetail) {
		this(sensorID, states);
		this.stateDetail = stateDetail;
	}
	

	// custom method
	public void update(String states) {
		this.time = new Date(System.currentTimeMillis());
		this.states = states;
	}
	
	public void update(String states, String stateDetail) {
		this.update(states);
		this.stateDetail = stateDetail;
	}
	
	
	// get, set
	public String getSensorID() {
		return sensorID;
	}
	
	public void setSensorID(String sensorID) {
		this.sensorID = sensorID;
	}
	
	public Date getTime() {
		return time;
	}
	
	public void setTime(Date time) {
		this.time = time;
	}
	
	public String getStates() {
		return states;
	}
	
	public void setStates(String states) {
		this.states = states;
	}
	
	public String getStateDetail() {
		return stateDetail;
	}
	
	public void setStateDetail(String stateDetail) {
		this.stateDetail = stateDetail;
	}

	@Override
	public String toString() {
		return "SensorData [sensorID=" + sensorID + ", time=" + time + ", states=" + states
				+ ", stateDetail=" + stateDetail + "]";
	}
	
}
