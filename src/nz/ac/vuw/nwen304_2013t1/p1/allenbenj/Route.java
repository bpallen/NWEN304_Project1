package nz.ac.vuw.nwen304_2013t1.p1.allenbenj;

import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

import org.xmlpull.v1.XmlPullParser;

import android.util.Xml;

public class Route {

	private static volatile Routes routes;

	public static class Routes {

		private final Map<Integer, Route> routes;

		public Routes() {
			routes = new HashMap<Integer, Route>();
		}

		public Routes(InputStream is) throws Exception {
			routes = new HashMap<Integer, Route>();
			XmlPullParser parser = Xml.newPullParser();
			parser.setInput(is, null);
			Route r = null;
			for (int event = parser.getEventType(); event != XmlPullParser.END_DOCUMENT; event = parser.next()) {
				switch (event) {
				case XmlPullParser.START_TAG:
					String name = parser.getName();
					if (name.equalsIgnoreCase("RECORD")) {
						r = new Route();
					} else if (name.equalsIgnoreCase("ROUTE_ID")) {
						r.id = Integer.parseInt(parser.nextText());
						routes.put(r.id, r);
					} else if (name.equalsIgnoreCase("AGENCY_ID")) {
						r.agency = parser.nextText();
					} else if (name.equalsIgnoreCase("ROUTE_SHORT_NAME")) {
						r.short_name = parser.nextText();
					} else if (name.equalsIgnoreCase("ROUTE_LONG_NAME")) {
						r.long_name = parser.nextText();
					} else if (name.equalsIgnoreCase("ROUTE_DESC")) {
						r.description = parser.nextText();
					} else if (name.equalsIgnoreCase("ROUTE_TYPE")) {
						r.type = Integer.parseInt(parser.nextText());
					}
				}
			}
			is.close();
		}

		public Route routeByID(int id) {
			return routes.get(id);
		}

		public Route[] allRoutes() {
			Route[] routes_arr = new Route[routes.size()];
			routes.values().toArray(routes_arr);
			return routes_arr;
		}

		/**
		 * Add the information in the specified Routes object to this one. Is not synchronized, so must not be called if
		 * this Routes object is accessible from mutliple threads.
		 * 
		 * @param r
		 */
		public void add(Routes r) {
			routes.putAll(r.routes);
		}
	}

	private int id;
	private String agency;
	private String short_name, long_name;
	private String description;
	private int type;

	private Route() {

	}

	public int getID() {
		return id;
	}

	public String getAgency() {
		return agency;
	}

	public String getShortName() {
		return short_name;
	}

	public String getLongName() {
		return long_name;
	}

	public String getDescription() {
		return description;
	}

	public int getType() {
		return type;
	}

	public Trip[] getTrips() {
		return Trip.tripsByRouteID(id);
	}

	public Trip[] getTripsByDirection(int dir) {
		return Trip.tripsByRouteIDAndDirection(id, dir);
	}

	@Override
	public String toString() {
		return "[" + id + "] " + long_name;
	}

	@Override
	public int hashCode() {
		return id;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) return true;
		if (obj == null) return false;
		if (getClass() != obj.getClass()) return false;
		Route other = (Route) obj;
		if (id != other.id) return false;
		return true;
	}

	public static Routes parseRoutes(InputStream is) throws Exception {
		return new Routes(is);
	}

	public static void useRoutes(Routes r) {
		routes = r;
	}

	public static Route routeByID(int id) {
		if (routes == null) return null;
		return routes.routeByID(id);
	}

	public static Route[] allRoutes() {
		if (routes == null) return new Route[0];
		return routes.allRoutes();
	}

}
