package nz.ac.vuw.nwen304_2013t1.p1.allenbenj;

import java.io.InputStream;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.xmlpull.v1.XmlPullParser;

import android.util.Xml;

public class StopTime {

	private static StopTimes stop_times;

	public static class StopTimes {

		private final Map<Integer, Set<StopTime>> stoptimes_by_tripid = new HashMap<Integer, Set<StopTime>>();
		private final Map<Integer, Set<StopTime>> stoptimes_by_stopid = new HashMap<Integer, Set<StopTime>>();

		public StopTimes(InputStream is) {
			stoptimes_by_tripid.clear();
			stoptimes_by_stopid.clear();
			try {
				XmlPullParser parser = Xml.newPullParser();
				parser.setInput(is, null);
				StopTime s = null;
				for (int event = parser.getEventType(); event != XmlPullParser.END_DOCUMENT; event = parser.next()) {
					switch (event) {
					case XmlPullParser.START_TAG:
						String name = parser.getName();
						if (name.equalsIgnoreCase("RECORD")) {
							s = new StopTime();
						} else if (name.equalsIgnoreCase("TRIP_ID")) {
							s.trip_id = Integer.parseInt(parser.nextText());
							Set<StopTime> stimes = stoptimes_by_tripid.get(s.trip_id);
							if (stimes == null) {
								stimes = new HashSet<StopTime>();
								stoptimes_by_tripid.put(s.trip_id, stimes);
							}
							stimes.add(s);
						} else if (name.equalsIgnoreCase("STOP_ID")) {
							s.stop_id = Integer.parseInt(parser.nextText());
							Set<StopTime> stimes = stoptimes_by_stopid.get(s.stop_id);
							if (stimes == null) {
								stimes = new HashSet<StopTime>();
								stoptimes_by_stopid.put(s.stop_id, stimes);
							}
							stimes.add(s);
						} else if (name.equalsIgnoreCase("STOP_SEQUENCE")) {
							s.stop_seq = Integer.parseInt(parser.nextText());
						} else if (name.equalsIgnoreCase("ARRIVAL_TIME")) {
							s.arrival_time = parser.nextText();
						} else if (name.equalsIgnoreCase("DEPARTURE_TIME")) {
							s.departure_time = parser.nextText();
						} else if (name.equalsIgnoreCase("PICKUP_TYPE")) {
							s.pickup_type = Integer.parseInt(parser.nextText());
						} else if (name.equalsIgnoreCase("DROP_OFF_TYPE")) {
							s.dropoff_type = Integer.parseInt(parser.nextText());
						}
					}
				}
			} catch (Exception e) {
				throw new RuntimeException(e);
			}
		}

		public StopTime[] stopTimesByTripID(int id) {
			Set<StopTime> stimes = stoptimes_by_tripid.get(id);
			if (stimes == null) return new StopTime[0];
			StopTime[] stimes_arr = new StopTime[stimes.size()];
			stimes.toArray(stimes_arr);
			Comparator<StopTime> comparator_seq = new Comparator<StopTime>() {

				@Override
				public int compare(StopTime lhs, StopTime rhs) {
					return lhs.stop_seq - rhs.stop_seq;
				}

			};
			Arrays.sort(stimes_arr, comparator_seq);
			return stimes_arr;
		}

		public StopTime[] stopTimesByStopID(int id) {
			Set<StopTime> stimes = stoptimes_by_stopid.get(id);
			if (stimes == null) return new StopTime[0];
			StopTime[] stimes_arr = new StopTime[stimes.size()];
			stimes.toArray(stimes_arr);
			return stimes_arr;
		}

	}

	private int trip_id;
	private int stop_id;
	private int stop_seq;
	private String arrival_time, departure_time;
	private int pickup_type, dropoff_type;

	private StopTime() {

	}

	public int getTripID() {
		return trip_id;
	}

	public int getStopID() {
		return stop_id;
	}

	public int getStopSequence() {
		return stop_seq;
	}

	public String getArrivalTime() {
		return arrival_time;
	}

	public String getDepartureTime() {
		return departure_time;
	}

	public int getPickupTtype() {
		return pickup_type;
	}

	public int getDropoffType() {
		return dropoff_type;
	}

	public Trip getTrip() {
		return Trip.tripByID(trip_id);
	}

	public Route getRoute() {
		return getTrip().getRoute();
	}

	public Stop getStop() {
		return Stop.stopByID(stop_id);
	}

	public static StopTimes parseStopTimes(InputStream is) {
		return new StopTimes(is);
	}

	public static void useStopTimes(StopTimes st) {
		stop_times = st;
	}

	public static StopTime[] stopTimesByTripID(int id) {
		return stop_times.stopTimesByTripID(id);
	}

	public static StopTime[] stopTimesByStopID(int id) {
		return stop_times.stopTimesByStopID(id);
	}

}
