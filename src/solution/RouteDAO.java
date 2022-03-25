package solution;

import java.nio.file.Path;
import java.time.DayOfWeek;
import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.TextStyle;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import baseclasses.DataLoadingException;
import baseclasses.IRouteDAO;
import baseclasses.Route;

/**
 * The RouteDAO parses XML files of route information, each route specifying
 * where the airline flies from, to, and on which day of the week
 */
public class RouteDAO implements IRouteDAO {

	/**
	 * Finds all flights that depart on the specified day of the week
	 * @param dayOfWeek A three letter day of the week, e.g. "Tue"
	 * @return A list of all routes that depart on this day
	 */
	
	String[] days = {"Mon", "Tus", "Wed", "Thu", "Fri", "Sat", "Sun"};
	List<Route> Route = new ArrayList<>();
	@Override
	public List<Route> findRoutesByDayOfWeek(String dayOfWeek) {
		List<Route> found = new ArrayList<>();
		for(Route r: Route)
		{
			if (dayOfWeek.compareTo((r.getDayOfWeek()))==0)
			{
				found.add(r);
			}
		}
		return found;
	}

	/**
	 * Finds all of the flights that depart from a specific airport on a specific day of the week
	 * @param airportCode the three letter code of the airport to search for, e.g. "MAN"
	 * @param dayOfWeek the three letter day of the week code to searh for, e.g. "Tue"
	 * @return A list of all routes from that airport on that day
	 */
	@Override
	public List<Route> findRoutesByDepartureAirportAndDay(String airportCode, String dayOfWeek) {
		List<Route> found = new ArrayList<>();
		for(Route r: Route)
		{
			if (r.getDepartureAirportCode().compareTo(airportCode)==0 && dayOfWeek.compareTo((r.getDayOfWeek()))==0)
			{
				found.add(r);
			}
		}
		return found;
	}

	/**
	 * Finds all of the flights that depart from a specific airport
	 * @param airportCode the three letter code of the airport to search for, e.g. "MAN"
	 * @return A list of all of the routes departing the specified airport
	 */
	@Override
	public List<Route> findRoutesDepartingAirport(String airportCode) {
		List<Route> found = new ArrayList<>();
		for(Route r: Route)
		{
			if (r.getDepartureAirportCode().compareTo(airportCode)==0)
			{
				found.add(r);
			}
		}
		return found;
	}

	/**
	 * Finds all of the flights that depart on the specified date
	 * @param date the date to search for
	 * @return A list of all routes that dpeart on this date
	 */
	@Override
	public List<Route> findRoutesbyDate(LocalDate date) {
		List<Route> found = new ArrayList<>();
		
		DayOfWeek a = date.getDayOfWeek();
		Locale locale = Locale.forLanguageTag("en-GB");
		String day = a.getDisplayName(TextStyle.SHORT, locale);

		for(Route r: Route)
		{
			if (day.compareTo((r.getDayOfWeek()))==0)
			{
				found.add(r);
			}
		}
		return found;
	}

	/**
	 * Returns The full list of all currently loaded routes
	 * @return The full list of all currently loaded routes
	 */
	@Override
	public List<Route> getAllRoutes() {
		List<Route> route2 = new ArrayList<>();
		for(Route e:Route)
		{
			route2.add(e);
		}
		return route2;
	}

	/**
	 * Returns The number of routes currently loaded
	 * @return The number of routes currently loaded
	 */
	@Override
	public int getNumberOfRoutes() {
		return Route.size();
	}

	/**
	 * Loads the route data from the specified file, adding them to the currently loaded routes
	 * Multiple calls to this function, perhaps on different files, would thus be cumulative
	 * @param p A Path pointing to the file from which data could be loaded
	 * @throws DataLoadingException if anything goes wrong. The exception's "cause" indicates the underlying exception
	 */
	@Override
	public void loadRouteData(Path p) throws DataLoadingException {
		try {
			boolean day=false;
			DocumentBuilder db = DocumentBuilderFactory.newInstance().newDocumentBuilder();
			Document doc = db.parse(p.toString());
			
			Element root = doc.getDocumentElement();
			NodeList children = root.getChildNodes();
			
			for(int i=0;i<children.getLength();i++)
			{
				Node c = children.item(i);
				
				if(c.getNodeName().equals("Route")) {
					NodeList grandchildren = c.getChildNodes();
					Route a = new Route();
					boolean flightnum = true;
					boolean deptime = true;
					boolean arrivaltime = true;
					for(int j=0;j<grandchildren.getLength();j++)
					{
						Node d = grandchildren.item(j);
						if(d.getNodeName().equals("#text")) {
							
						}
						else if(d.getNodeName().equals("FlightNumber")) {
							int flightNumber = Integer.parseInt(d.getChildNodes().item(0).getNodeValue());
							if(flightnum==false)
							{
								throw new DataLoadingException();
							}
							flightnum=false;
							a.setFlightNumber(flightNumber);
						}
						else if(d.getNodeName().equals("DayOfWeek")) {
							String dayOfWeek = d.getChildNodes().item(0).getNodeValue();
							for(String d1:days)
							{
								if(d1==dayOfWeek)
								{
									day=true;
								}
							}
							if(day==true || a.getDayOfWeek()!=null)
							{
								throw new DataLoadingException();
							}
							a.setDayOfWeek(dayOfWeek);
						}
						else if(d.getNodeName().equals("DepartureTime")) {
							LocalTime departuretime = LocalTime.parse(d.getChildNodes().item(0).getNodeValue());
							if(deptime==false)
							{
								throw new DataLoadingException();
							}
							deptime=false;
							a.setDepartureTime(departuretime);
						}
						else if(d.getNodeName().equals("DepartureAirport")) {
							String departureAirport = d.getChildNodes().item(0).getNodeValue();
							if(a.getDepartureAirport()!=null)
							{
								throw new DataLoadingException();
							}
							a.setDepartureAirport(departureAirport);
						}
						else if(d.getNodeName().equals("DepartureAirportIATACode")) {
							String departureAirportCode = d.getChildNodes().item(0).getNodeValue();
							if(a.getDepartureAirportCode()!=null)
							{
								throw new DataLoadingException();
							}
							a.setDepartureAirportCode(departureAirportCode);
						}
						else if(d.getNodeName().equals("ArrivalTime")) {
							if(arrivaltime==false)
							{
								throw new DataLoadingException();
							}
							arrivaltime=false;
							LocalTime arrivalTime = LocalTime.parse(d.getChildNodes().item(0).getNodeValue());
							a.setArrivalTime(arrivalTime);
						}
						else if(d.getNodeName().equals("ArrivalAirport")) {
							String arrivalAirport = d.getChildNodes().item(0).getNodeValue();
							if(a.getArrivalAirport()!=null)
							{
								throw new DataLoadingException();
							}
							a.setArrivalAirport(arrivalAirport);
						}
						else if(d.getNodeName().equals("ArrivalAirportIATACode")) {
							String arrivalAirportCode = d.getChildNodes().item(0).getNodeValue();
							if (a.getArrivalAirportCode()!=null)
							{
								throw new DataLoadingException();
							}
							a.setArrivalAirportCode(arrivalAirportCode);
						}
						else if(d.getNodeName().equals("Duration")) {
							Duration duration = (Duration.parse(d.getChildNodes().item(0).getNodeValue()));
							a.setDuration(duration);
						}
						else {
							throw new DataLoadingException();
						}
						
						}
					if(a.getArrivalAirport()==null || a.getArrivalAirportCode()==null)
					{
						throw new DataLoadingException();
					}
					Route.add(a);
				}
			}
			
		}
		catch(Exception ioe)
		{
			throw new DataLoadingException();
		}

	}

	/**
	 * Unloads all of the crew currently loaded, ready to start again if needed
	 */
	@Override
	public void reset() {
		Route.clear();
	}

}
