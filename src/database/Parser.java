package database;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

import tables.Classroom;
import tables.Course;
import tables.Department;
import tables.Instructor;
import tables.Section;
import tables.SectionHasCourseHour;
import tables.Student;
import tables.StudentHasSection;


public class Parser {
	
	private final static String USER_AGENT = "Mozilla/5.0";
	public StudentHasSection[] studentsSectionList = null;
//	public SectionHasCourseHour[] sectionHasCourseHourList = null;
	ArrayList<SectionHasCourseHour> newSectionCourseHourList = new ArrayList<SectionHasCourseHour>();
	
	public Classroom[] getClassList() throws Exception {

		String url = "http://kayit.etu.edu.tr/Ders/_Ders_prg_start.php";
		URL obj = new URL(url);
		HttpURLConnection con = (HttpURLConnection) obj.openConnection();
		// optional default is GET
		con.setRequestMethod("GET");

		// add request header
		con.setRequestProperty("User-Agent", USER_AGENT);


		BufferedReader in = new BufferedReader(new InputStreamReader(
				con.getInputStream()));
		String inputLine;
		StringBuffer response = new StringBuffer();

		while ((inputLine = in.readLine()) != null) {
			response.append(inputLine);
		}
		in.close();
		// print result
		String res = response.toString();

		String newRes = res
				.substring(res.indexOf("select name = dd_derslik") + 25);
		newRes = newRes.substring(0, newRes.indexOf("</select>"));
		String newRes2 = newRes.replace("</option>", "</option>\n");

		String[] sinifList = newRes2.split("\n");

		String[][] siniflar = new String[sinifList.length][2];

		for (int i = 0; i < sinifList.length; i++) {
			siniflar[i][0] = sinifList[i].substring(14,
					sinifList[i].indexOf(">"));
			siniflar[i][1] = sinifList[i].substring(
					sinifList[i].indexOf(">") + 1, sinifList[i].length() - 9);
		}

		Classroom[] classList = new Classroom[siniflar.length];
		for (int i = 0; i < siniflar.length; i++) {
			Classroom newClass = new Classroom(siniflar[i][0], siniflar[i][1]);
			classList[i] = newClass;
		}

		return classList;
	}
	
	public Instructor[] getInstructorList() throws Exception {
		String url = "http://kayit.etu.edu.tr/Ders/_Ders_prg_start.php";
		URL obj = new URL(url);
		HttpURLConnection con = (HttpURLConnection) obj.openConnection();
		// optional default is GET
		con.setRequestMethod("GET");

		// add request header
		con.setRequestProperty("User-Agent", USER_AGENT);

		int responseCode = con.getResponseCode();

		BufferedReader in = new BufferedReader(new InputStreamReader(
				con.getInputStream()));
		String inputLine;
		StringBuffer response = new StringBuffer();

		while ((inputLine = in.readLine()) != null) {
			response.append(inputLine);
		}
		in.close();
		// print result
		String res = response.toString();

		String newRes = res
				.substring(res.indexOf("select name = dd_hoca") + 22);
		newRes = newRes.substring(0, newRes.indexOf("</select>"));
		String newRes2 = newRes.replace("</option>", "</option>\n");

		String[] hocaList = newRes2.split("\n");

		String[][] hocalar = new String[hocaList.length][2];

		for (int i = 0; i < hocaList.length; i++) {
			hocalar[i][0] = hocaList[i].substring(14, 21);
			hocalar[i][1] = hocaList[i].substring(22, hocaList[i].length() - 9);
		}

		Instructor[] instructorList = new Instructor[hocalar.length];
		for (int i = 0; i < hocalar.length; i++) {
			try {
				Instructor newInstructor = new Instructor(
						Integer.parseInt(hocalar[i][0]), hocalar[i][1]);
				instructorList[i] = newInstructor;
			} catch (Exception e) {
				// TODO: handle exception
			}

		}

		return instructorList;
	}
	
	public Course[] getCourseList() throws Exception {
		String url = "http://kayit.etu.edu.tr/Ders/_Ders_prg_start.php";
		URL obj = new URL(url);
		HttpURLConnection con = (HttpURLConnection) obj.openConnection();
		// optional default is GET
		con.setRequestMethod("GET");

		// add request header
		con.setRequestProperty("User-Agent", USER_AGENT);
		con.setRequestProperty("Accept-Language", "tr-TR,tr;q=0.8");

		BufferedReader in = new BufferedReader(new InputStreamReader(
				con.getInputStream(), "UTF-8"));
		String inputLine;
		StringBuffer response = new StringBuffer();

		while ((inputLine = in.readLine()) != null) {
			response.append(inputLine);
		}
		in.close();
		// print result
		String res = response.toString();
		String newRes = res.substring(res.indexOf("select") + 22,
				res.indexOf("</select><br><b>"));
		String newRes2 = newRes.replace("</option>", "</option>\n");

		String[] dersList = newRes2.split("\n");

		String[][] dersler = new String[dersList.length][2];

		for (int i = 0; i < dersList.length; i++) {
			dersler[i][0] = dersList[i].substring(14, 21);
			dersler[i][1] = dersList[i].substring(22, dersList[i].length() - 9);
		}

		Course[] courseList = new Course[dersler.length];
		for (int i = 0; i < dersler.length; i++) {
			Course newCourse = new Course(
					dersler[i][0],
					dersler[i][1].substring(0, dersler[i][1].indexOf(" ", 4)),
					(dersler[i][1].substring(dersler[i][1].indexOf(" ", 4) + 1))
							.replaceAll("'", "''"));
			courseList[i] = newCourse;
		}
		return courseList;
	}

	public Section[] getSectionOfSpecificCourse(String courseCode,
		String courseID, DatabaseProcessing db) throws Exception {
		String url = "http://kayit.etu.edu.tr/Ders/Ders_prg.php";
		URL obj = new URL(url);
		HttpURLConnection con = (HttpURLConnection) obj.openConnection();

		// add reuqest header
		con.setRequestMethod("POST");
		con.setRequestProperty("User-Agent", USER_AGENT);
		con.setRequestProperty("Accept-Language", "tr-TR,tr;q=0.8");

		String urlParameters = "dd_ders=" + courseID
				+ "&sube=0&btn_ders=\"Seçili Dersin Programýný Göster\"";

		// Send post request
		con.setDoOutput(true);
		DataOutputStream wr = new DataOutputStream(con.getOutputStream());
		wr.writeBytes(urlParameters);
		wr.flush();
		wr.close();

		BufferedReader in = new BufferedReader(new InputStreamReader(
				con.getInputStream()));
		String inputLine;
		StringBuffer response = new StringBuffer();

		while ((inputLine = in.readLine()) != null) {
			response.append(inputLine);
		}
		in.close();

		String res = response.toString();

		String[] subeler = res.split("ube:");

		ArrayList<Section> subeList = new ArrayList<Section>();

		int instructorID = -1;
		String instructorName;
		int sectionID = 1;
		for (int i = 1; i < subeler.length; i++) {
			instructorName = subeler[i].substring(subeler[i].indexOf(" ") + 2,
					subeler[i].indexOf("<tr>"));
			String buffer = subeler[i].substring(subeler[i]
					.indexOf("<center>08:30-09:20</center>"));
			buffer = buffer.substring(0, buffer.indexOf("</table><br>"));
			String[] buf = buffer
					.split("</tr><tr><th bgcolor=#FFCCFF WIDTH='150'>");
			String[][] list = new String[13][6];
			for (int j = 0; j < buf.length; j++) {
				buf[j] = buf[j]
						.substring(buf[j].indexOf("</th><td><center>") + 17);
				list[j] = buf[j].split("<td><center>");
			}
			int sayac = 0;

			for (int j = 0; j < list.length; j++) {
				for (int k = 0; k < list[j].length; k++) {
					if (list[j][k].equals("-")) {
						sayac++;
					} else {
						instructorID = db.getInstructorID(instructorName);
						if (instructorID == -1) {
							if (db.addInstructor(instructorName)) {
								instructorID = db
										.getInstructorID(instructorName);
							} else
								continue;
						}
						String classroomName = list[j][k].substring(9);
						Section newSection = null;
						if (db.findClassroom(classroomName)) {
							String section = sectionID + "";
							try {
								SectionHasCourseHour newSectionHasCourseHour = new SectionHasCourseHour(
										Integer.parseInt(section), k + 1,
										j + 1, classroomName, courseCode);
								newSectionCourseHourList
										.add(newSectionHasCourseHour);
							} catch (Exception e) {
								// TODO: handle exception
							}

						} else {
							if (db.addClassroom(classroomName)) {
								String section = sectionID + "";
								try {
									SectionHasCourseHour newSectionHasCourseHour = new SectionHasCourseHour(
											Integer.parseInt(section), k + 1,
											j + 1, classroomName, courseCode);
									newSectionCourseHourList
											.add(newSectionHasCourseHour);
								} catch (Exception e) {
									// TODO: handle exception
								}
							} else
								continue;
						}
					}
				}
				if (sayac == 78) {
					instructorID = db.getInstructorID(instructorName);
					if (instructorID == -1) {
						if (db.addInstructor(instructorName)) {
							instructorID = db.getInstructorID(instructorName);
						} else
							continue;
					}
					String section = sectionID + "";
					try {
						SectionHasCourseHour newSectionHasCourseHour = new SectionHasCourseHour(
								Integer.parseInt(section), 0, 0, "none",
								courseCode);
						newSectionCourseHourList.add(newSectionHasCourseHour);
					} catch (Exception e) {
						// TODO: handle exception
					}
					sayac = 0;
				}

			}
			sectionID++;
			Section newSection = new Section(sectionID, i, courseCode,
					instructorID);
			subeList.add(newSection);
		}
		Section[] list = new Section[subeList.size()];

		for (int i = 0; i < list.length; i++) {
			list[i] = subeList.get(i);
		}
		return list;
	}
	
	public Department[] getDepartmentList() throws Exception {
		String url = "http://kayit.etu.edu.tr/Ders/_Ders_prg_start.php";
		URL obj = new URL(url);
		HttpURLConnection con = (HttpURLConnection) obj.openConnection();
		// optional default is GET
		con.setRequestMethod("GET");

		// add request header
		con.setRequestProperty("User-Agent", USER_AGENT);

		int responseCode = con.getResponseCode();

		BufferedReader in = new BufferedReader(new InputStreamReader(
				con.getInputStream()));
		String inputLine;
		StringBuffer response = new StringBuffer();

		while ((inputLine = in.readLine()) != null) {
			response.append(inputLine);
		}
		in.close();
		// print result
		String res = response.toString();

		String newRes = res
				.substring(res.indexOf("select name = dd_bolum") + 23);
		newRes = newRes.substring(0, newRes.indexOf("</select>"));
		String newRes2 = newRes.replace("</option>", "</option>\n");

		String[] bolumList = newRes2.split("\n");

		String[][] bolumler = new String[bolumList.length][2];

		for (int i = 0; i < bolumList.length; i++) {
			bolumler[i][0] = bolumList[i].substring(14,
					bolumList[i].indexOf(">"));
			bolumler[i][1] = bolumList[i].substring(
					bolumList[i].indexOf(">") + 1, bolumList[i].length() - 9);
		}

		Department[] departmentList = new Department[bolumler.length];
		for (int i = 0; i < bolumler.length; i++) {
			try {
				Department newDepartment = new Department(
						Integer.parseInt(bolumler[i][0]), bolumler[i][1]);
				departmentList[i] = newDepartment;
			} catch (Exception e) {
				// TODO: handle exception
			}
		}

		return departmentList;
	}
	
	public Student[] getStudentList(DatabaseProcessing db) throws Exception {
try {
	Course[] courseList = getCourseList();
	Department[] departmentList = getDepartmentList();

	ArrayList<Student> studentList = new ArrayList<Student>();
	ArrayList<StudentHasSection> newStudentsSectionList = new ArrayList<StudentHasSection>();

	for (int i = 0; i < courseList.length; i++) {
		String url = "http://kayit.etu.edu.tr/Ders/Ders_prg.php";
		URL obj = new URL(url);
		HttpURLConnection con = (HttpURLConnection) obj.openConnection();
		// add reuqest header
		con.setRequestMethod("POST");
		con.setRequestProperty("User-Agent", USER_AGENT);
		con.setRequestProperty("Accept-Language", "en-US,en;q=0.5");

		String urlParameters = "dd_ders=" + courseList[i].courseID
				+ "&sube=0&btn_sube=\"Þube Listesini Göster\"";

		// Send post request
		con.setDoOutput(true);
		DataOutputStream wr = new DataOutputStream(con.getOutputStream());
		wr.writeBytes(urlParameters);
		wr.flush();
		wr.close();

		int responseCode = con.getResponseCode();
		BufferedReader in = new BufferedReader(new InputStreamReader(
				con.getInputStream()));
		String inputLine;
		StringBuffer response = new StringBuffer();

		while ((inputLine = in.readLine()) != null) {
			response.append(inputLine);
			response.append("\n");
		}
		in.close();

		String[] subeler = response.toString().split("ube");

		for (int j = 2; j < subeler.length; j++) {
			String[] deneme = subeler[j].split("</tr>");
			for (int k = 1; k < deneme.length - 1; k++) {
				deneme[k] = deneme[k]
						.substring(deneme[k].indexOf("<td>") + 4);
				String studentID = deneme[k].substring(0,
						deneme[k].indexOf("</td>"));
				deneme[k] = deneme[k]
						.substring(deneme[k].indexOf("</td>") + 10);
				String studentName = deneme[k].substring(0,
						deneme[k].indexOf("</td>"));
				deneme[k] = deneme[k]
						.substring(deneme[k].indexOf("<td>") + 4);
				String studentDepartment = deneme[k].substring(0,
						deneme[k].indexOf("</td>"));
				deneme[k] = deneme[k]
						.substring(deneme[k].indexOf("</td>") + 18);
				String studentClass = deneme[k].substring(0,
						deneme[k].indexOf("</td>"));
				deneme[k] = deneme[k].substring(deneme[k]
						.indexOf("mailto:") + 7);
				String studentEmail = deneme[k].substring(0,
						deneme[k].indexOf("'>"));
				String status = deneme[k].substring(
						deneme[k].indexOf("<td>") + 4,
						deneme[k].length() - 5);
				int departmentId = db.getDepartmentID(studentDepartment);
				if (departmentId == -1) {
					if (db.addDepartment(studentDepartment)) {
						departmentId = db
								.getDepartmentID(studentDepartment);
					} else
						continue;
				}
				Student newStudent = new Student(studentID, studentName,
						studentEmail, departmentId, studentClass);
				String section = db.getSectionID(
						courseList[i].courseCode, (j - 1)) + "";
				try {
					StudentHasSection newStudentsLesson = new StudentHasSection(
							studentID, Integer.parseInt(section));
					newStudentsSectionList.add(newStudentsLesson);
				} catch (Exception e) {
					// TODO: handle exception
				}

				int add = 1;
				for (int l = 0; l < studentList.size(); l++) {
					if (studentList.get(l).studentID
							.equals(newStudent.studentID)) {
						add = 0;
						break;
					}
				}
				if (add == 1)
					studentList.add(newStudent);
			}
		}
		con.disconnect();
	}
	Student[] list = new Student[studentList.size()];
	for (int i = 0; i < list.length; i++) {
		list[i] = studentList.get(i);
	}
	studentsSectionList = new StudentHasSection[newStudentsSectionList
			.size()];
	for (int i = 0; i < studentsSectionList.length; i++) {
		studentsSectionList[i] = newStudentsSectionList.get(i);
	}
	return list;
} catch (Exception e) {
System.out.println(e);}
		return null;
	}

	public String[][] getReservesOfSpecificClassroom(String classroomID)
			throws Exception {
		String url = "http://kayit.etu.edu.tr/Ders/Ders_prg.php";
		URL obj = new URL(url);
		HttpURLConnection con = (HttpURLConnection) obj.openConnection();

		// add reuqest header
		con.setRequestMethod("POST");
		con.setRequestProperty("User-Agent", USER_AGENT);
		con.setRequestProperty("Accept-Language", "en-US,en;q=0.5");

		String urlParameters = "dd_derslik=" + classroomID
				+ "&sube=0&btn_derslik=\"Bu Dersliðin Programý Göster\"";

		// Send post request
		con.setDoOutput(true);
		DataOutputStream wr = new DataOutputStream(con.getOutputStream());
		wr.writeBytes(urlParameters);
		wr.flush();
		wr.close();

		int responseCode = con.getResponseCode();

		BufferedReader in = new BufferedReader(new InputStreamReader(
				con.getInputStream()));
		String inputLine;
		StringBuffer response = new StringBuffer();

		while ((inputLine = in.readLine()) != null) {
			response.append(inputLine);
		}
		in.close();

		String res = response.toString();

		String buffer = res.substring(res
				.indexOf("<center>08:30-09:20</center>"));
		buffer = buffer.substring(0, buffer.indexOf("</table><br>"));
		String[] buf = buffer
				.split("</tr><tr><th bgcolor=#FFCCFF WIDTH='150'>");
		String[][] list = new String[13][6];
		for (int j = 0; j < buf.length; j++) {
			buf[j] = buf[j].substring(buf[j].indexOf("</th><td><center>") + 17);
			list[j] = buf[j].split("<td><center>");
		}
		ArrayList<String[]> schedule = new ArrayList<String[]>();
		for (int i = 0; i < list.length; i++) {
			for (int j = 0; j < list[i].length; j++) {
				if (list[i][j].equals("-")) {
				} else {
					if (list[i][j].substring(0, 7).equals("REZERVE")) {
						schedule.add(new String[] { j + 1 + "", i + 1 + "", list[i][j].substring(list[i][j].indexOf("(") + 1,list[i][j].indexOf(")") ) });
					}
				}
			}
		}

		String[][] newList = new String[schedule.size()][3];

		for (int i = 0; i < schedule.size(); i++) {
			newList[i] = schedule.get(i);
			for (int j = 0; j < newList[i].length; j++) {
			}
		}
		
		return newList;

	}
}