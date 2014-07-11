package tables;

public class Section {
	public int sectionID;
	public int sectionNumber;
	public String courseCode;
	public int instructorID;
	
	public Section(int sectionID, int sectionNumber, String courseCode, int instructorID) {
		this.sectionID = sectionID;
		this.sectionNumber = sectionNumber;
		this.courseCode = courseCode;
		this.instructorID = instructorID;
	}
}