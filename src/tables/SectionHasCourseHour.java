package tables;

public class SectionHasCourseHour {
	public int sectionID;
	public int sectionDay;
	public int sectionHour;
	public String classroomName;
	public String courseCode;
	
	public SectionHasCourseHour(int sectionID, int sectionDay, int sectionHour, String classroomName, String courseCode) {
		this.sectionID = sectionID;
		this.sectionDay = sectionDay;
		this.sectionHour = sectionHour;
		this.classroomName = classroomName;
		this.courseCode = courseCode;
	}
}