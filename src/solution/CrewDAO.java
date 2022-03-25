package solution;
import java.io.BufferedReader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONObject;

import baseclasses.CabinCrew;
import baseclasses.Crew;
import baseclasses.DataLoadingException;
import baseclasses.ICrewDAO;
import baseclasses.Pilot;
import baseclasses.Pilot.Rank;

/**
 * The CrewDAO is responsible for loading data from JSON-based crew files 
 * It contains various methods to help the scheduler find the right pilots and cabin crew
 */
public class CrewDAO implements ICrewDAO {

	/**
	 * Loads the crew data from the specified file, adding them to the currently loaded crew
	 * Multiple calls to this function, perhaps on different files, would thus be cumulative
	 * @param p A Path pointing to the file from which data could be loaded
	 * @throws DataLoadingException if anything goes wrong. The exception's "cause" indicates the underlying exception
	 */
	List<Crew> Crew = new ArrayList<>();
	@Override
	public void loadCrewData(Path p) throws DataLoadingException {
		List<Crew> Crew2 = new ArrayList<>();
		try {
			BufferedReader reader = Files.newBufferedReader(p);
			String json = ""; String line = "";
			while((line=reader.readLine()) !=null)
            {json =json + line;}
			
			
			
			JSONObject crew = new JSONObject(json);
			
			JSONArray pilots = crew.getJSONArray("pilots");
			JSONArray cabincrew = crew.getJSONArray("cabincrew");
	            for(int k=0;k<pilots.length();k++) {
	            	JSONObject pilot = pilots.getJSONObject(k);
	            	
	            	Pilot a = new Pilot();
	            	if(isNumeric(pilot.getString("forename"))==true)
	            	{
	            		throw new DataLoadingException();
	            	}
	            	if(isNumeric(pilot.getString("surname"))==true)
	            	{
	            		throw new DataLoadingException();
	            	}
	            	
	            	String forename = pilot.getString("forename");
	            	String surname = pilot.getString("surname");
	            	String homebase = pilot.getString("home_airport");
	            	Rank rank = null;
	            	if(pilot.getString("rank").contentEquals("CAPTAIN"))
	            	{
	            		rank = Pilot.Rank.CAPTAIN;
	            	}
	            	else if(pilot.getString("rank").contentEquals("FIRST_OFFICER"))
	            	{
	            		rank = Pilot.Rank.FIRST_OFFICER;
	            	}
	            	else
	            	{
	            		throw new DataLoadingException();
	            	}
	            	JSONArray typeratings = pilot.getJSONArray("type_ratings");
	            	
	            	a.setForename(forename);
	            	a.setSurname(surname);
	            	a.setHomeBase(homebase);
	            	a.setRank(rank);
	            	for(int l=0;l<typeratings.length();l++) {
	            		 a.setQualifiedFor(typeratings.getString(l));
	            	 }
	            	Crew2.add(a);
	            	}
	            for(int k=0;k<cabincrew.length();k++) {
	            	JSONObject cc = cabincrew.getJSONObject(k);
	            	
	            	CabinCrew a = new CabinCrew();
	            	String forename = cc.getString("forename");
	            	String surname = cc.getString("surname");
	            	String homebase = cc.getString("home_airport");
	            	JSONArray typeratings = cc.getJSONArray("type_ratings");
	            	
	            	a.setForename(forename);
	            	a.setSurname(surname);
	            	a.setHomeBase(homebase);
	            	for(int l=0;l<typeratings.length();l++) {
	            		 a.setQualifiedFor(typeratings.getString(l));
	            	 }
	            	Crew2.add(a);
	            }
	            for(Crew c: Crew2)
	            {
	            	Crew.add(c);
	            }
			
		} catch (Exception ioe) {
			throw new DataLoadingException(ioe);
		}
		
	}
	
	/**
	 * Returns a list of all the cabin crew based at the airport with the specified airport code
	 * @param airportCode the three-letter airport code of the airport to check for
	 * @return a list of all the cabin crew based at the airport with the specified airport code
	 */
	@Override
	public List<CabinCrew> findCabinCrewByHomeBase(String airportCode) {
		List<CabinCrew> found = new ArrayList<>();
		for(Crew person: Crew)
		{
			if(person.getHomeBase().equals(airportCode) && person instanceof CabinCrew)
			{
				found.add((CabinCrew) person);
			}
		}
		return found;
	}
	
	public static boolean isNumeric(String str) { 
		  try {  
		    Double.parseDouble(str);  
		    return true;
		  } catch(NumberFormatException e){  
		    return false;  
		  }  
		}

	/**
	 * Returns a list of all the cabin crew based at a specific airport AND qualified to fly a specific aircraft type
	 * @param typeCode the type of plane to find cabin crew for
	 * @param airportCode the three-letter airport code of the airport to check for
	 * @return a list of all the cabin crew based at a specific airport AND qualified to fly a specific aircraft type
	 */
	@Override
	public List<CabinCrew> findCabinCrewByHomeBaseAndTypeRating(String typeCode, String airportCode) {
		List<CabinCrew> found = new ArrayList<>();
		List<CabinCrew> cabincrew = this.getAllCabinCrew();
		for(CabinCrew crew: cabincrew)
		{
			if(crew.getHomeBase().equals(airportCode) && crew.getTypeRatings().contains(typeCode))
			{
				found.add(crew);
			}
		}
		return found;
	}

	/**
	 * Returns a list of all the cabin crew currently loaded who are qualified to fly the specified type of plane
	 * @param typeCode the type of plane to find cabin crew for
	 * @return a list of all the cabin crew currently loaded who are qualified to fly the specified type of plane
	 */
	@Override
	public List<CabinCrew> findCabinCrewByTypeRating(String typeCode) {
		List<CabinCrew> found = new ArrayList<>();
		List<CabinCrew> cabincrew = this.getAllCabinCrew();
		for(CabinCrew crew: cabincrew)
		{
			if(crew.getTypeRatings().contains(typeCode))
			{
				found.add(crew);
			}
		}
		return found;
	}

	/**
	 * Returns a list of all the pilots based at the airport with the specified airport code
	 * @param airportCode the three-letter airport code of the airport to check for
	 * @return a list of all the pilots based at the airport with the specified airport code
	 */
	@Override
	public List<Pilot> findPilotsByHomeBase(String airportCode) {
		List<Pilot> found = new ArrayList<>();
		List<Pilot> pilots = this.getAllPilots();
		for(Pilot pilot: pilots)
		{
			if(pilot.getHomeBase().equals(airportCode))
			{
				found.add(pilot);
			}
		}
		return found;
	}

	/**
	 * Returns a list of all the pilots based at a specific airport AND qualified to fly a specific aircraft type
	 * @param typeCode the type of plane to find pilots for
	 * @param airportCode the three-letter airport code of the airport to check for
	 * @return a list of all the pilots based at a specific airport AND qualified to fly a specific aircraft type
	 */
	@Override
	public List<Pilot> findPilotsByHomeBaseAndTypeRating(String typeCode, String airportCode) {
		List<Pilot> found = new ArrayList<>();
		List<Pilot> pilots = this.getAllPilots();
		for(Pilot pilot: pilots)
		{
			if(pilot.getHomeBase().equals(airportCode) && pilot.getTypeRatings().contains(typeCode))
			{
				found.add(pilot);
			}
		}
		return found;
	}

	/**
	 * Returns a list of all the pilots currently loaded who are qualified to fly the specified type of plane
	 * @param typeCode the type of plane to find pilots for
	 * @return a list of all the pilots currently loaded who are qualified to fly the specified type of plane
	 */
	@Override
	public List<Pilot> findPilotsByTypeRating(String typeCode) {
		List<Pilot> found = new ArrayList<>();
		List<Pilot> pilots = this.getAllPilots();
		for(Pilot pilot: pilots)
		{
			if(pilot.getTypeRatings().contains(typeCode))
			{
				found.add(pilot);
			}
		}
		return found;
	}

	/**
	 * Returns a list of all the cabin crew currently loaded
	 * @return a list of all the cabin crew currently loaded
	 */
	@Override
	public List<CabinCrew> getAllCabinCrew() {
		List<CabinCrew> found = new ArrayList<>();
		for(Crew person: Crew)
		{
			if(person instanceof CabinCrew)
			{
				found.add((CabinCrew) person);
			}
		}
		return found;
	}

	/**
	 * Returns a list of all the crew, regardless of type
	 * @return a list of all the crew, regardless of type
	 */
	@Override
	public List<Crew> getAllCrew() {
		return Crew;
	}

	/**
	 * Returns a list of all the pilots currently loaded
	 * @return a list of all the pilots currently loaded
	 */
	@Override
	public List<Pilot> getAllPilots() {
		List<Pilot> found = new ArrayList<>();
		for(Crew person: Crew)
		{
			if(person instanceof Pilot)
			{
				found.add((Pilot) person);
			}
		}
		return found;
	}

	@Override
	public int getNumberOfCabinCrew() {
		int size = this.getAllCabinCrew().size();
		return size;
	}

	/**
	 * Returns the number of pilots currently loaded
	 * @return the number of pilots currently loaded
	 */
	@Override
	public int getNumberOfPilots() {
		int size = this.getAllPilots().size();
		return size;
	}

	/**
	 * Unloads all of the crew currently loaded, ready to start again if needed
	 */
	@Override
	public void reset() {
		Crew.clear();
	}

}
