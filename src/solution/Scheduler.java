package solution;
import java.time.LocalDate;
import java.util.List;

import baseclasses.Aircraft;
import baseclasses.CabinCrew;
import baseclasses.DoubleBookedException;
import baseclasses.FlightInfo;
import baseclasses.IAircraftDAO;
import baseclasses.ICrewDAO;
import baseclasses.IPassengerNumbersDAO;
import baseclasses.IRouteDAO;
import baseclasses.IScheduler;
import baseclasses.InvalidAllocationException;
import baseclasses.Pilot;
import baseclasses.Schedule;

/**
 * The Scheduler class is responsible for deciding which aircraft and crew will be
 * used for each of an airline's flights in a specified period of time, referred to
 * as a "scheduling horizon". A schedule must have an aircraft, two pilots, and 
 * sufficient cabin crew for the aircraft allocated to every flight in the horizon 
 * to be valid.
 */
	public class Scheduler implements IScheduler {

		/**
		 * Generates a schedule, providing you with ready-loaded DAO objects to get your data from
		 * @param aircraftDAO the DAO for the aircraft to be used when scheduling
		 * @param crewDAO the DAO for the crew to be used when scheduling
		 * @param routeDAO the DAO to use for routes when scheduling
		 * @param passengerNumbersDAO the DAO to use for passenger numbers when scheduling
		 * @param startDate the start of the scheduling horizon
		 * @param endDate the end of the scheduling horizon
		 * @return The generated schedule - which must happen inside 2 minutes
		 */
		@Override
		public Schedule generateSchedule(IAircraftDAO aircraftDAO, ICrewDAO crewDAO, IRouteDAO routeDAO, 
				IPassengerNumbersDAO passengerNumbersDAO, LocalDate startDate, LocalDate endDate) {
			Schedule schedule = new Schedule(routeDAO, startDate, endDate);
			
			List<Aircraft> aircraft = aircraftDAO.getAllAircraft();
			List<Pilot> pilots = crewDAO.getAllPilots();
			List<CabinCrew> cabincrew = crewDAO.getAllCabinCrew();
			
			List<Pilot> captains = crewDAO.getAllPilots();
			List<Pilot> firstofficers = crewDAO.getAllPilots();
			
			captains.clear();
			firstofficers.clear();
			
			for(Pilot pilot: pilots)
			{
				if(pilot.getRank()==Pilot.Rank.CAPTAIN)
				{
					captains.add(pilot);
				}
				else if(pilot.getRank()==Pilot.Rank.FIRST_OFFICER)
				{
					firstofficers.add(pilot);
				}
			}

			List<FlightInfo> info = schedule.getRemainingAllocations();

			while(!schedule.isCompleted())
			{
			for(FlightInfo flight: info)
			{
				while(!schedule.isValid(flight))
				{
					//flight
					boolean has = false;
					String flightstart = flight.getFlight().getDepartureAirport().toUpperCase();
					flightstart = flightstart.substring(0, Math.min(flightstart.length(), 3));
					String planestart = "";
					for(Aircraft plane: aircraft)
					{
						planestart = plane.getStartingPosition();
						
						if(planestart.compareTo(flightstart)==0)
						{
							has=true;
						}
					}
					
					for(Aircraft plane: aircraft)
					{
						if(has==false)
						{
					if(schedule.hasConflict(plane, flight) == false)
					{
						try {
							schedule.allocateAircraftTo(plane, flight);
							break;
						} catch (DoubleBookedException e) {
							e.printStackTrace();
						}
					}
						}
						else
						{
							planestart = plane.getStartingPosition();
							if(schedule.hasConflict(plane, flight) == false && planestart.compareTo(flightstart)==0)
							{
								try {
									schedule.allocateAircraftTo(plane, flight);
									String n = flight.getFlight().getArrivalAirport().toUpperCase();
									n = n.substring(0, Math.min(n.length(), 3));
									plane.setStartingPosition(n);
									break;
								} catch (DoubleBookedException e) {
									e.printStackTrace();
								}
							}
						}
					}
					//first officer
					
					boolean qual = false;
					
					for(Pilot firstofficer: firstofficers)
					{
						if(firstofficer.isQualifiedFor(schedule.getAircraftFor(flight)))
						{
							qual=true;
						}
					}
					
					if(qual==false)
					{
					for(Pilot firstofficer: firstofficers)
					{
						if(schedule.hasConflict(firstofficer, flight) == false)
						{
							try {
								schedule.allocateFirstOfficerTo(firstofficer, flight);
								break;}
							catch (DoubleBookedException e) {e.printStackTrace();}
						}
					}
					}
					else
					{
						for(Pilot firstofficer: firstofficers)
						{
							if(schedule.hasConflict(firstofficer, flight) == false && firstofficer.isQualifiedFor(schedule.getAircraftFor(flight)))
							{
								try {
									schedule.allocateFirstOfficerTo(firstofficer, flight);
									break;}
								catch (DoubleBookedException e) {e.printStackTrace();}
							}
						}
					}
					
					//captain
					
					qual = false;
					
					for(Pilot captain: captains)
					{
						if(captain.isQualifiedFor(schedule.getAircraftFor(flight)))
						{
							qual=true;
						}
					}
					
					if(qual==false)
					{
					for(Pilot captain: captains)
					{
						if(schedule.hasConflict(captain, flight) == false)
						{
							try {
								schedule.allocateCaptainTo(captain, flight);
								break;}
							catch (DoubleBookedException e) {e.printStackTrace();}
						}
					}
					}
					else
					{
						for(Pilot captain: captains)
						{
							if(schedule.hasConflict(captain, flight) == false && captain.isQualifiedFor(schedule.getAircraftFor(flight)))
							{
								try {
									schedule.allocateCaptainTo(captain, flight);
									break;}
								catch (DoubleBookedException e) {e.printStackTrace();}
							}
						}
					}
					
					//cabin crew
					
					qual = false;
					
					for(CabinCrew cc: cabincrew)
					{
						if(cc.isQualifiedFor(schedule.getAircraftFor(flight)));
						{
							qual=true;
						}
					}
					
					if(qual==false)
					{
					for(CabinCrew cc: cabincrew)
					{
						if(schedule.getAircraftFor(flight).getCabinCrewRequired() == schedule.getCabinCrewOf(flight).size())
						{
							break;
						}
						
						if(schedule.hasConflict(cc, flight) == false && schedule.getCabinCrewOf(flight).contains(cc) == false)
						{
							try {
									schedule.allocateCabinCrewTo(cc, flight);}
								catch (DoubleBookedException e) {e.printStackTrace();}
							}
						}
						try {
					schedule.completeAllocationFor(flight);} catch (InvalidAllocationException e) {e.printStackTrace();
					}
					}
					else
					{
						for(CabinCrew cc: cabincrew)
						{
							if(schedule.getAircraftFor(flight).getCabinCrewRequired() == schedule.getCabinCrewOf(flight).size())
							{
								break;
							}
							
							if(schedule.hasConflict(cc, flight) == false && schedule.getCabinCrewOf(flight).contains(cc) == false && cc.isQualifiedFor(schedule.getAircraftFor(flight)))
							{
								try {
										schedule.allocateCabinCrewTo(cc, flight);}
									catch (DoubleBookedException e) {e.printStackTrace();}
								}
							}
							try {
						schedule.completeAllocationFor(flight);} catch (InvalidAllocationException e) {e.printStackTrace();
						}
						}
				}
			}
		}
			return schedule;
		}
	}

	// && captain.isQualifiedFor(schedule.getAircraftFor(flight))
	// && firstofficer.isQualifiedFor(schedule.getAircraftFor(flight))
