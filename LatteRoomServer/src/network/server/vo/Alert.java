package network.server.vo;

public class Alert {
	private String deviceID;
    private int hour;           // 시간
    private int min;            // 분
    private String weeks;       // 알람 수행 요일
    private boolean flag;       // 알람 on/off

    
    // constructor
    public Alert(int hour, int min, String weeks, boolean flag) {
        this.hour = hour;
        this.min = min;
        this.weeks = weeks;
        this.flag = flag;
    }

    
    // get, set
	public String getDeviceID() {
		return deviceID;
	}

	public void setDeviceID(String deviceID) {
		this.deviceID = deviceID;
	}

	public int getHour() {
		return hour;
	}

	public void setHour(int hour) {
		this.hour = hour;
	}

	public int getMin() {
		return min;
	}

	public void setMin(int min) {
		this.min = min;
	}

	public String getWeeks() {
		return weeks;
	}

	public void setWeeks(String weeks) {
		this.weeks = weeks;
	}

	public boolean isFlag() {
		return flag;
	}

	public void setFlag(boolean flag) {
		this.flag = flag;
	}
    
}
