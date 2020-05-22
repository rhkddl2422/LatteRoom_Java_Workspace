package arduino.device;

import java.util.ArrayList;
import java.util.List;

public class AvgTemp {

		private List<Float> list = new ArrayList<Float>();
		
		public void add(float temp) {
			if(list.size() != 5) {
				this.list.add(temp);
				return;
			}
			list.remove(0);
			this.list.add(temp);
		}
		
		public float getAvg() {
			float avg = -1f;
			for(float i : list) {
				avg += i;
			}
			if(list.size() != 0) avg = avg/list.size();
			return Math.round((avg*100))/100.0f;
		}

		public int size() {
			return list.size();
		}
}
