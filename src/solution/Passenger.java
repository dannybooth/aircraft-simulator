package solution;

import java.time.LocalDate;

public class Passenger {
	int flightnum;
	LocalDate date;
	int estimate;
	Passenger()
	{
		
	}
	
	void setflightnum(int flightnum)
	{
		this.flightnum=flightnum;
	}
	int getflightnum()
	{
		return flightnum;
	}
	void setdate(LocalDate date)
	{
		this.date=date;
	}
	LocalDate getdate()
	{
		return date;
	}
	void setestimate(int load)
	{
		this.estimate=load;
	}
	int getestimate()
	{
		return estimate;
	}
}
