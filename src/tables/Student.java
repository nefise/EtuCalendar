package tables;

public class Student {
	public String studentID;
	public String studentName;
	public String studentEmail;
	public int studentDepartment;
	public String studentYear;
	
	public Student(String studentID, String name, String email, int studentDepartment, String year) {
		this.studentID = studentID;
		this.studentName = name;
		this.studentDepartment = studentDepartment;
		this.studentEmail = email;
		this.studentYear = year;
	}
}