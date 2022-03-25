package solution;
import java.io.BufferedReader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

import baseclasses.Aircraft;
import baseclasses.Aircraft.Manufacturer;
import baseclasses.DataLoadingException;
import baseclasses.IAircraftDAO;

/**
 * The AircraftDAO class is responsible for loading aircraft data from CSV files
 * and contains methods to help the system find aircraft when scheduling
 */
public class AircraftDAO implements IAircraftDAO {
	
	//The data structure we'll use to store the aircraft we've loaded
	List<Aircraft> aircraft = new ArrayList<>();
	@Override
	public void loadAircraftData(Path p) throws DataLoadingException {	
		List<Aircraft> aircraft2 = new ArrayList<>();
		try {
			//open the file
			BufferedReader reader = Files.newBufferedReader(p);
			
			//read the file line by line
			String line = "";
			
			//skip the first line of the file - headers
			reader.readLine();
			
			while( (line = reader.readLine()) != null) {
				//each line has fields separated by commas, split into an array of fields
				String[] fields = line.split(",");
				
				//put some of the fields into variables: check which fields are where atop the CSV file itself
				String tailcode = fields[0].toUpperCase();
				String[] tails = tailcode.split(String.valueOf("-"));
				
				if(tails[0]=="" || tails[1]=="")
				{
					throw new DataLoadingException();
				}
				String model = fields[1].toUpperCase();
				String type = fields[2].toUpperCase();
				
				char ch = type.charAt(0);
				String[] parts = type.split(String.valueOf(ch));
				String part = parts[1];
				if(isNumeric(part)==false)
				{
					throw new DataLoadingException();
				}
				if(part.compareTo(String.valueOf(model))==1)
				{
					if(type.compareTo(String.valueOf(model))==1)
					{
						throw new DataLoadingException();
					}
				}
				
				Manufacturer manufacturer = null;
				String manufact = fields[3].toUpperCase();
				if(manufact.equals("BOEING"))
				{
					manufacturer = Aircraft.Manufacturer.BOEING;
				}
				else if(manufact.equals("AIRBUS"))
				{
					manufacturer = Aircraft.Manufacturer.AIRBUS;
				}
				else if(manufact.equals("EMBRAER"))
				{
					manufacturer = Aircraft.Manufacturer.EMBRAER;
				}
				else if(manufact.equals("BOMBARDIER"))
				{
					manufacturer = Aircraft.Manufacturer.BOMBARDIER;
				}
				else if(manufact.equals("ATR"))
				{
					manufacturer = Aircraft.Manufacturer.ATR;
				}
				else if(manufact.equals("FOKKER"))
				{
					manufacturer = Aircraft.Manufacturer.FOKKER;
				}
				else
				{
					throw new DataLoadingException();
				}
				String startingPosition = fields[4].toUpperCase();
				if(isNumeric(startingPosition)==true || Integer.parseInt(fields[5])<=-1)
				{
					throw new DataLoadingException();
				}
				int seats = Integer.parseInt(fields[5]);
				if(Integer.parseInt(fields[6])<=-1)
				{
					throw new DataLoadingException();
				}
				int cabinCrewRequired = Integer.parseInt(fields[6]);
				
				//create an Aircraft object, and set (some of) its properties
				Aircraft a = new Aircraft();
				a.setTailCode(tailcode);
				a.setModel(model);
				a.setTypeCode(type);
				//a.setManufacturer(manufacturer);
				a.setManufacturer(manufacturer);
				a.setStartingPosition(startingPosition);
				a.setSeats(seats);
				a.setCabinCrewRequired(cabinCrewRequired);
				
				//add the aircraft to our list
				aircraft2.add(a);
				
			}
			for(Aircraft add: aircraft2)
			{
				aircraft.add(add);
			}
		}
		
		catch (Exception ioe) {
			//There was a problem reading the file
			throw new DataLoadingException();
		}

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
	 * Returns a list of all the loaded Aircraft with at least the specified number of seats
	 * @param seats the number of seats required
	 * @return a List of all the loaded aircraft with at least this many seats
	 */
	@Override
	public List<Aircraft> findAircraftBySeats(int seats) {
		List<Aircraft> found = new ArrayList<>();
		for(Aircraft ac: aircraft)
		{
			if (ac.getSeats()>=seats)
			{
				found.add(ac);
			}
		}
		return found;
	}

	/**
	 * Returns a list of all the loaded Aircraft that start at the specified airport code
	 * @param startingPosition the three letter airport code of the airport at which the desired aircraft start
	 * @return a List of all the loaded aircraft that start at the specified airport
	 */
	@Override
	public List<Aircraft> findAircraftByStartingPosition(String startingPosition) {
		List<Aircraft> found = new ArrayList<>();
		for(Aircraft aircraft: aircraft)
		{
			if (aircraft.getStartingPosition().equals(startingPosition))
			{
				found.add(aircraft);
			}
		}
		return found;
	}

	/**
	 * Returns the individual Aircraft with the specified tail code.
	 * @param tailCode the tail code for which to search
	 * @return the aircraft with that tail code, or null if not found
	 */
	@Override
	public Aircraft findAircraftByTailCode(String tailCode) {
		for(Aircraft aircraft: aircraft)
		{
			if (aircraft.getTailCode().equals(tailCode))
			{
				return aircraft;
			}
		}
		return null;
	}

	/**
	 * Returns a List of all the loaded Aircraft with the specified type code
	 * @param typeCode the type code of the aircraft you wish to find
	 * @return a List of all the loaded Aircraft with the specified type code
	 */
	@Override
	public List<Aircraft> findAircraftByType(String typeCode) {
		List<Aircraft> found = new ArrayList<>();
		for(Aircraft aircraft: aircraft)
		{
			if (aircraft.getTypeCode().equals(typeCode))
			{
				found.add(aircraft);
			}
		}
		return found;
	}

	/**
	 * Returns a List of all the currently loaded aircraft
	 * @return a List of all the currently loaded aircraft
	 */
	@Override
	public List<Aircraft> getAllAircraft() {
		List<Aircraft> aircraft2 = new ArrayList<>();
		for(Aircraft e:aircraft)
		{
			aircraft2.add(e);
		}
		return aircraft2;
	}

	/**
	 * Returns the number of aircraft currently loaded 
	 * @return the number of aircraft currently loaded
	 */
	@Override
	public int getNumberOfAircraft() {
		return aircraft.size();
	}

	/**
	 * Unloads all of the aircraft currently loaded, ready to start again if needed
	 */
	@Override
	public void reset() {
		aircraft.clear();

	}

}
