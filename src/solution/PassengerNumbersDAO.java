package solution;
import java.nio.file.Path;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import baseclasses.DataLoadingException;
import baseclasses.IPassengerNumbersDAO;

/**
 * The PassengerNumbersDAO is responsible for loading an SQLite database
 * containing forecasts of passenger numbers for flights on dates
 */
public class PassengerNumbersDAO implements IPassengerNumbersDAO {
	List<Passenger> passenger = new ArrayList<>();
	/**
	 * Returns the number of passenger number entries in the cache
	 * @return the number of passenger number entries in the cache
	 */ 
	@Override
	public int getNumberOfEntries() {
		int count = passenger.size();
		return count;
	}

	/**
	 * Returns the predicted number of passengers for a given flight on a given date, or -1 if no data available
	 * @param flightNumber The flight number of the flight to check for
	 * @param date the date of the flight to check for
	 * @return the predicted number of passengers, or -1 if no data available
	 */
	@Override
	public int getPassengerNumbersFor(int flightNumber, LocalDate date) {
		for(Passenger pa: this.passenger)
		{
			if(pa.getflightnum()==flightNumber && pa.getdate().compareTo(date)==0)
			{
				return pa.getestimate();
			}
		}
		return -1;
		}

	/**
	 * Loads the passenger numbers data from the specified SQLite database into a cache for future calls to getPassengerNumbersFor()
	 * Multiple calls to this method are additive, but flight numbers/dates previously cached will be overwritten
	 * The cache can be reset by calling reset() 
	 * @param p The path of the SQLite database to load data from
	 * @throws DataLoadingException If there is a problem loading from the database
	 */
	@Override
	public void loadPassengerNumbersData(Path p) throws DataLoadingException {
		try {
			List<Passenger> passenger2 = new ArrayList<>();
			Connection c = DriverManager.getConnection("jdbc:sqlite:"+p.toString());
			Statement s=c.createStatement();
			ResultSet passenger = s.executeQuery("SELECT * FROM PassengerNumbers;");
			while(passenger.next()){
				Passenger a = new Passenger();
				
				int flightnum = passenger.getInt("FlightNumber");
				LocalDate date = LocalDate.parse(passenger.getString("Date"));
				int load = passenger.getInt("LoadEstimate");
				boolean found = false;
				for(Passenger pa: this.passenger)
				{
					if(pa.getflightnum()==flightnum && pa.getdate().compareTo(date)==0)
					{
						pa.setestimate(load);
						found=true;
						break;
					}
				}
				
				
				if(found==false)
				{
					a.setdate(date);
					a.setflightnum(flightnum);
					a.setestimate(load);
					passenger2.add(a);
				}
			}
			this.passenger=passenger2;
		}
		catch(Exception ioe)
		{
			throw new DataLoadingException();
		}
	}

	/**
	 * Removes all data from the DAO, ready to start again if needed
	 */
	@Override
	public void reset() {
		passenger.clear();
	}

}
