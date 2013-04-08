package nz.ac.vuw.nwen304_2013t1.p1.allenbenj;

import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

import org.xmlpull.v1.XmlPullParser;

import android.util.Xml;

public class Route {

	private static Routes routes;

	public static class Routes {

		private final Map<Integer, Route> routes = new HashMap<Integer, Route>();

		public Routes(InputStream is) {
			try {
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
			} catch (Exception e) {
				throw new RuntimeException(e);
			}
		}

		public Route routeByID(int id) {
			return routes.get(id);
		}

		public Route[] allRoutes() {
			Route[] routes_arr = new Route[routes.size()];
			routes.values().toArray(routes_arr);
			return routes_arr;
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

	public static Routes parseRoutes(InputStream is) {
		return new Routes(is);
	}

	public static void useRoutes(Routes r) {
		routes = r;
	}

	public static Route routeByID(int id) {
		return routes.routeByID(id);
	}

	public static Route[] allRoutes() {
		return routes.allRoutes();
	}

}
