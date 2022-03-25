package solution;

import java.nio.file.Paths;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import baseclasses.Aircraft;
import baseclasses.DataLoadingException;
import baseclasses.IAircraftDAO;
import baseclasses.QualityScoreCalculator;
import baseclasses.Route;
import baseclasses.Schedule;

/**
 * This class allows you to run the code in your classes yourself, for testing and development
 */
public class Main {

	public static void main(String[] args) throws DataLoadingException {	
		IAircraftDAO aircraft = new AircraftDAO();
		
		//Tells your Aircraft DAO to load this particular data file
		//aircraft.loadAircraftData(Paths.get("./data/aircraft.csv"));
		aircraft.loadAircraftData(Paths.get("./data/schedule_aircraft.csv"));
		//aircraft.loadAircraftData(Paths.get("./data/malformed_aircraft1.csv"));
		
		//aircraft.reset();
		
		
		
		//System.out.println(aircraft.getAllAircraft());
		//System.out.println(aircraft.findAircraftByType("B767"));
		//System.out.println(aircraft.findAircraftByTailCode("G-TCXC"));
		//System.out.println(aircraft.findAircraftByStartingPosition("BHX"));
		
		
		//List<Aircraft> found = new ArrayList<>();
		//found =aircraft.getAllAircraft();
		//for(Aircraft find: found)
		//{
			//System.out.println(find.getSeats());
		//}
		
		
		//System.out.println(aircraft.findAircraftBySeats(329));
		
		//List<Aircraft> found = new ArrayList<>();
		//found =aircraft.findAircraftBySeats(329);
		//for(Aircraft find: found)
		//{
			//System.out.println(find.getTailCode());
		//}
		//System.out.println(aircraft.getAllAircraft());
		
		CrewDAO crew = new CrewDAO();
		
		crew.loadCrewData(Paths.get("./data/schedule_crew.json"));
		//System.out.println(crew.getAllCrew().size() + "    1150");
		//System.out.println(crew.getAllCabinCrew().size() + "    800");
		//System.out.println(crew.getAllPilots().size()+ "     350");
		
		RouteDAO route = new RouteDAO();
		
		route.loadRouteData(Paths.get("./data/schedule_routes.xml"));
		//System.out.println(route.getAllRoutes());
		//System.out.println(route.getNumberOfRoutes());
		//System.out.println(route.findRoutesByDepartureAirportAndDay("BFS","Tue"));
		//System.out.println(route.findRoutesDepartingAirport("BFS"));
		//System.out.println(route.findRoutesByDayOfWeek("Tue"));
		//System.out.println(route.findRoutesbyDate(LocalDate.now()));
		
		//List<Route> found = new ArrayList<>();
		//found =route.getAllRoutes();
		//for(Route find: found)
				//{
					//System.out.println("arrival airport: "+ find.getArrivalAirport());
					//System.out.println("arrival airport code: "+ find.getArrivalAirportCode());
					//System.out.println("day of week: "+ find.getDayOfWeek());
					//System.out.println("departure airport: "+ find.getDepartureAirport());
					//System.out.println("departure airport code: "+ find.getDepartureAirportCode());
					//System.out.println("arrival time: "+ find.getArrivalTime());
					//System.out.println("arrival airport: "+ find.getArrivalAirport());
					//System.out.println("flight number: "+ find.getFlightNumber());
					//System.out.println("Duration: " + find.getDuration());
					//System.out.println("");
				//}
		
		PassengerNumbersDAO passenger = new PassengerNumbersDAO();
		passenger.loadPassengerNumbersData(Paths.get("./data/schedule_passengers.db"));
		//System.out.println(passenger.getNumberOfEntries());
		//LocalDate date = LocalDate.of(2020,07,01);
		//System.out.println(passenger.getPassengerNumbersFor(618,date));
		//passenger.reset();
		//System.out.println(passenger.getPassengerNumbersFor(618,date));
		
		Scheduler scheduler = new Scheduler();
		
		LocalDate start = LocalDate.of(2020,04,01);
		LocalDate end = LocalDate.of(2020,05,01);
		
		Schedule schedule = scheduler.generateSchedule(aircraft, crew, route, passenger, start, end);
		
		QualityScoreCalculator score = new QualityScoreCalculator(aircraft, crew, passenger, schedule);
		System.out.println(score.calculateQualityScore());
	}

}
