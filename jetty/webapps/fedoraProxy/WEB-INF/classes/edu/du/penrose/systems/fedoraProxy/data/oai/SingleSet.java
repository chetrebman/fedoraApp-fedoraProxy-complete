package edu.du.penrose.systems.fedoraProxy.data.oai;

/**
 * Represents a single OAI set, containing the set name and it's uniqueID. For example
 * the "Carson Brierly Photograph Collection" with the feodra PID codu:38045
 * 
 * @author chet
 *
 */
public class SingleSet {

	private String name;
	private String uniqueID;	
	
	//  default constructor
	public SingleSet(){
		
	}
	
	/**
	 * Create a single set, containing the OAI name and unique id..
	 * 
	 * @param name the OAI set name ie "Carson Brierly Photograph Collection"
	 * @param uniqueID the OAI set uniqueID ie codu:38045
	 */
	public SingleSet( String name, String uniqueID )
	{
		this.name        = name;
		this.uniqueID    = uniqueID;
	}
	
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getUniqueID() {
		return uniqueID;
	}
	public void setUniqueID(String uniqueID) {
		this.uniqueID = uniqueID;
	}
	
}
