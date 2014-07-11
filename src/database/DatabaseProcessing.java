package database;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.channels.FileChannel;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;

import tables.Classroom;
import tables.Course;
import tables.Department;
import tables.Instructor;
import tables.Section;
import tables.SectionHasCourseHour;
import tables.Student;
import tables.StudentHasSection;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.os.AsyncTask;
import android.os.Environment;
import android.widget.Toast;

public class DatabaseProcessing extends SQLiteOpenHelper {

	// Database Version
	private static final int DATABASE_VERSION = 12;
	private static Context context = null;
	private static Activity activity = null;
	private static ProgressDialog pDialog = null;
	// Database Name
	private static final String DATABASE_NAME = "etuCalendarDB";

	public DatabaseProcessing(Context context) {
		super(context, DATABASE_NAME, null, DATABASE_VERSION);
		this.context = context;
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		db.execSQL("PRAGMA encoding = \"UTF-8\"");
		String CREATE_TABLE = "CREATE TABLE IF NOT EXISTS student ( studentname TEXT, studentemail TEXT, studentid TEXT PRIMARY KEY, studentdepartment INTEGER, studentyear INTEGER );";
		db.execSQL(CREATE_TABLE);
		CREATE_TABLE = "CREATE TABLE IF NOT EXISTS course ( coursecode TEXT PRIMARY KEY, coursename TEXT );";
		db.execSQL(CREATE_TABLE);
		CREATE_TABLE = "CREATE TABLE IF NOT EXISTS department ( departmentid INTEGER PRIMARY KEY AUTOINCREMENT, departmentname TEXT );";
		db.execSQL(CREATE_TABLE);
		CREATE_TABLE = "CREATE TABLE IF NOT EXISTS classroom ( classroomname TEXT PRIMARY KEY );";
		db.execSQL(CREATE_TABLE);
		CREATE_TABLE = "CREATE TABLE IF NOT EXISTS instructor ( instructorid INTEGER PRIMARY KEY AUTOINCREMENT, instructorname TEXT );";
		db.execSQL(CREATE_TABLE);
		CREATE_TABLE = "CREATE TABLE IF NOT EXISTS section ( sectionid INTEGER PRIMARY KEY AUTOINCREMENT, coursecode TEXT, instructorid INTEGER, sectionnumber INTEGER, FOREIGN KEY(coursecode) REFERENCES course(coursecode), FOREIGN KEY(instructorid) REFERENCES instructor(instructorid)  );";
		db.execSQL(CREATE_TABLE);
		CREATE_TABLE = "CREATE TABLE IF NOT EXISTS studenthassection ( studentid TEXT, sectionid INTEGER, FOREIGN KEY(sectionid) REFERENCES section(sectionid), FOREIGN KEY(studentid) REFERENCES student(studentid) );";
		db.execSQL(CREATE_TABLE);
		CREATE_TABLE = "CREATE TABLE IF NOT EXISTS departmenthassection ( departmentid INTEGER, sectionid INTEGER, year INTEGER, studentcount INTEGER, FOREIGN KEY(departmentid) REFERENCES department(departmentid), FOREIGN KEY(sectionid) REFERENCES section(sectionid) );";
		db.execSQL(CREATE_TABLE);
		CREATE_TABLE = "CREATE TABLE IF NOT EXISTS sectionhascoursehour ( sectionid INTEGER, sectionday INTEGER, sectionhour INTEGER, classroomname TEXT, FOREIGN KEY(classroomname) REFERENCES classroom(classroomname), FOREIGN KEY(sectionid) REFERENCES section(sectionid) );";
		db.execSQL(CREATE_TABLE);
		CREATE_TABLE = "CREATE TABLE IF NOT EXISTS classroomhasreserve ( classroomname TEXT, sectionday INTEGER, sectionhour INTEGER, reservefor character varying(80), FOREIGN KEY(classroomname) REFERENCES classroom(classroomname) );";
		db.execSQL(CREATE_TABLE);
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		String DROP_TABLE = "DROP TABLE IF EXISTS classroomhasreserve;"
				+ "DROP TABLE IF EXISTS studenthassection;"
				+ "DROP TABLE IF EXISTS departmenthassection;"
				+ "DROP TABLE IF EXISTS sectionhascoursehour;"
				+ "DROP TABLE IF EXISTS section;"
				+ "DROP TABLE IF EXISTS course;"
				+ "DROP TABLE IF EXISTS instructor;"
				+ "DROP TABLE IF EXISTS student;"
				+ "DROP TABLE IF EXISTS classroom;"
				+ "DROP TABLE IF EXISTS department;";
		db.execSQL(DROP_TABLE);
		onCreate(db);
	}

	public void fillDatabase(Activity activity) {
		this.activity = activity;
		// dropTables(this.getWritableDatabase());
		// createTables(this.getWritableDatabase());
		deleteTables(this.getWritableDatabase());
		generateClassroomList();
		// generateStudentList();
	}

	public void deleteTables(SQLiteDatabase db) {
		db.delete("classroomhasreserve", null, null);
		db.delete("studenthassection", null, null);
		db.delete("departmenthassection", null, null);
		db.delete("sectionhascoursehour", null, null);
		db.delete("section", null, null);
		db.delete("course", null, null);
		db.delete("instructor", null, null);
		db.delete("student", null, null);
		db.delete("classroom", null, null);
		db.delete("department", null, null);
	}

	public void createTables(SQLiteDatabase db) {
		String CREATE_TABLE = "CREATE TABLE IF NOT EXISTS student ( studentname TEXT, studentemail TEXT, studentid TEXT PRIMARY KEY, studentdepartment INTEGER, studentyear INTEGER );";
		db.execSQL(CREATE_TABLE);
		CREATE_TABLE = "CREATE TABLE IF NOT EXISTS course ( coursecode TEXT PRIMARY KEY, coursename TEXT );";
		db.execSQL(CREATE_TABLE);
		CREATE_TABLE = "CREATE TABLE IF NOT EXISTS department ( departmentid INTEGER PRIMARY KEY AUTOINCREMENT, departmentname TEXT );";
		db.execSQL(CREATE_TABLE);
		CREATE_TABLE = "CREATE TABLE IF NOT EXISTS classroom ( classroomname TEXT PRIMARY KEY );";
		db.execSQL(CREATE_TABLE);
		CREATE_TABLE = "CREATE TABLE IF NOT EXISTS instructor ( instructorid INTEGER PRIMARY KEY AUTOINCREMENT, instructorname TEXT );";
		db.execSQL(CREATE_TABLE);
		CREATE_TABLE = "CREATE TABLE IF NOT EXISTS section ( sectionid INTEGER PRIMARY KEY AUTOINCREMENT, coursecode TEXT, instructorid INTEGER, sectionnumber INTEGER, FOREIGN KEY(coursecode) REFERENCES course(coursecode), FOREIGN KEY(instructorid) REFERENCES instructor(instructorid)  );";
		db.execSQL(CREATE_TABLE);
		CREATE_TABLE = "CREATE TABLE IF NOT EXISTS studenthassection ( studentid TEXT, sectionid INTEGER, FOREIGN KEY(sectionid) REFERENCES section(sectionid), FOREIGN KEY(studentid) REFERENCES student(studentid) );";
		db.execSQL(CREATE_TABLE);
		CREATE_TABLE = "CREATE TABLE IF NOT EXISTS departmenthassection ( departmentid INTEGER, sectionid INTEGER, year INTEGER, studentcount INTEGER, FOREIGN KEY(departmentid) REFERENCES department(departmentid), FOREIGN KEY(sectionid) REFERENCES section(sectionid) );";
		db.execSQL(CREATE_TABLE);
		CREATE_TABLE = "CREATE TABLE IF NOT EXISTS sectionhascoursehour ( sectionid INTEGER, sectionday INTEGER, sectionhour INTEGER, classroomname TEXT, FOREIGN KEY(classroomname) REFERENCES classroom(classroomname), FOREIGN KEY(sectionid) REFERENCES section(sectionid) );";
		db.execSQL(CREATE_TABLE);
		CREATE_TABLE = "CREATE TABLE IF NOT EXISTS classroomhasreserve ( classroomname TEXT, sectionday INTEGER, sectionhour INTEGER, reservefor character varying(80), FOREIGN KEY(classroomname) REFERENCES classroom(classroomname) );";
		db.execSQL(CREATE_TABLE);
	}

	public void dropTables(SQLiteDatabase db) {
		String DROP_TABLE = "DROP TABLE IF EXISTS classroomhasreserve;"
				+ "DROP TABLE IF EXISTS studenthassection;"
				+ "DROP TABLE IF EXISTS departmenthassection;"
				+ "DROP TABLE IF EXISTS sectionhascoursehour;"
				+ "DROP TABLE IF EXISTS section;"
				+ "DROP TABLE IF EXISTS course;"
				+ "DROP TABLE IF EXISTS instructor;"
				+ "DROP TABLE IF EXISTS student;"
				+ "DROP TABLE IF EXISTS classroom;"
				+ "DROP TABLE IF EXISTS department;";
		db.execSQL(DROP_TABLE);
	}

	public void generateClassroomList() {
		try {
			class ClassroomTask extends AsyncTask<String, Void, Void> {
				@Override
				protected void onPreExecute() {
					super.onPreExecute();
					// Showing progress dialog
					pDialog = new ProgressDialog(activity);
					pDialog.setMessage("Sınıf Listesi Güncelleniyor...");
					pDialog.setCancelable(false);
					pDialog.show();

				}

				protected Void doInBackground(String... urls) {
					try {
						Parser parser = new Parser();
						Classroom[] classList = parser.getClassList();
						for (int i = 0; i < classList.length; i++) {
							try {
								addClassroom(toUpperCase(classList[i].classroomName));
								System.out.println(classList[i].classroomName);
							} catch (Exception e) {
								// TODO: handle exception
							}
						}
						try {
							addClassroom("NONE");
						} catch (Exception e) {
							// TODO: handle exception
						}
					} catch (Exception e) {
					}
					return null;
				}

				@Override
				protected void onPostExecute(Void result) {
					super.onPostExecute(result);
					// Dismiss the progress dialog
					if (pDialog.isShowing())
						pDialog.dismiss();

					generateInstructorList();

				}
			}
			new ClassroomTask().execute();
		} catch (Exception e) {
		}
	}

	public void generateInstructorList() {
		try {
			class InstructorTask extends AsyncTask<String, Void, Void> {
				@Override
				protected void onPreExecute() {
					super.onPreExecute();
					// Showing progress dialog
					pDialog = new ProgressDialog(activity);
					pDialog.setMessage("Öğretim Üyesi Listesi Güncelleniyor...");
					pDialog.setCancelable(false);
					pDialog.show();

				}

				protected Void doInBackground(String... urls) {
					try {
						Parser parser = new Parser();
						Instructor[] instructorList = parser
								.getInstructorList();
						for (int i = 0; i < instructorList.length; i++) {
							try {
								addInstructor(instructorList[i].instructorName);
								System.out
										.println(instructorList[i].instructorName);
							} catch (Exception e) {
								// TODO: handle exception
							}
						}
						try {
							addInstructor("NONE");
						} catch (Exception e) {
							// TODO: handle exception
						}
					} catch (Exception e) {
					}
					return null;
				}

				@Override
				protected void onPostExecute(Void result) {
					super.onPostExecute(result);
					// Dismiss the progress dialog
					if (pDialog.isShowing())
						pDialog.dismiss();
					generateCourseList();
				}
			}
			new InstructorTask().execute();
		} catch (Exception e) {
		}
	}

	public void generateCourseList() {
		try {
			class CourseTask extends AsyncTask<String, Void, Void> {
				@Override
				protected void onPreExecute() {
					super.onPreExecute();
					// Showing progress dialog
					pDialog = new ProgressDialog(activity);
					pDialog.setMessage("Ders Listesi Güncelleniyor...");
					pDialog.setCancelable(false);
					pDialog.show();

				}

				protected Void doInBackground(String... urls) {
					try {
						Parser parser = new Parser();
						Course[] courseList = parser.getCourseList();
						for (int i = 0; i < courseList.length; i++) {
							try {
								System.out.println(courseList[i].courseCode);
								addCourse(courseList[i].courseCode,
										courseList[i].courseName);
							} catch (Exception e) {
								System.out.println("asdsad");
							}
						}
					} catch (Exception e) {
					}
					return null;
				}

				@Override
				protected void onPostExecute(Void result) {
					super.onPostExecute(result);
					// Dismiss the progress dialog
					if (pDialog.isShowing())
						pDialog.dismiss();
					generateSectionList();

				}
			}
			new CourseTask().execute();
		} catch (Exception e) {
		}
	}

	public void generateSectionList() {
		try {
			final DatabaseProcessing db = new DatabaseProcessing(context);
			class SectionTask extends AsyncTask<String, Void, Void> {
				Parser parser = new Parser();

				@Override
				protected void onPreExecute() {
					super.onPreExecute();
					// Showing progress dialog
					pDialog = new ProgressDialog(activity);
					pDialog.setMessage("Şube Listesi Güncelleniyor...");
					pDialog.setCancelable(false);
					pDialog.show();

				}

				protected Void doInBackground(String... urls) {
					try {
						Course[] courseList = parser.getCourseList();

						for (int i = 0; i < courseList.length; i++) {
							Section[] sectionList = parser
									.getSectionOfSpecificCourse(
											courseList[i].courseCode,
											courseList[i].courseID, db);
							for (int j = 0; j < sectionList.length; j++) {
								try {
									addSection(sectionList[j].courseCode,
											sectionList[j].instructorID,
											sectionList[j].sectionNumber);
								} catch (Exception e) {
									// TODO: handle exception
								}
							}
						}
					} catch (Exception e) {
					}
					return null;
				}

				@Override
				protected void onPostExecute(Void result) {
					super.onPostExecute(result);
					// Dismiss the progress dialog
					if (pDialog.isShowing())
						pDialog.dismiss();

					generateSectionCourseHourList(parser.newSectionCourseHourList);

				}
			}
			new SectionTask().execute();
		} catch (Exception e) {
		}
	}

	public void generateSectionCourseHourList(
			final ArrayList<SectionHasCourseHour> sectionCourseHourList) {
		try {
			class SectionCourseHourTask extends AsyncTask<String, Void, Void> {
				@Override
				protected void onPreExecute() {
					super.onPreExecute();
					// Showing progress dialog
					pDialog = new ProgressDialog(activity);
					pDialog.setMessage("Şube Bilgileri Güncelleniyor...");
					pDialog.setCancelable(false);
					pDialog.show();
				}

				protected Void doInBackground(String... urls) {
					try {
						for (int i = 0; i < sectionCourseHourList.size(); i++) {
							try {
								addSectionCourseHour(
										getSectionID(
												sectionCourseHourList.get(i).courseCode,
												sectionCourseHourList.get(i).sectionID),
										sectionCourseHourList.get(i).sectionDay,
										sectionCourseHourList.get(i).sectionHour,
										sectionCourseHourList.get(i).classroomName);
							} catch (Exception e) {
								// TODO: handle exception
							}

						}
					} catch (Exception e) {
					}
					return null;
				}

				@Override
				protected void onPostExecute(Void result) {
					super.onPostExecute(result);
					// Dismiss the progress dialog
					if (pDialog.isShowing())
						pDialog.dismiss();
					generateDepartmentList();

				}
			}
			new SectionCourseHourTask().execute();
		} catch (Exception e) {
		}
	}

	public void generateDepartmentList() {
		try {
			class DepartmentTask extends AsyncTask<String, Void, Void> {
				@Override
				protected void onPreExecute() {
					super.onPreExecute();
					// Showing progress dialog
					pDialog = new ProgressDialog(activity);
					pDialog.setMessage("Bölüm Listesi Güncelleniyor...");
					pDialog.setCancelable(false);
					pDialog.show();

				}

				protected Void doInBackground(String... urls) {
					try {
						Parser parser = new Parser();
						Department[] departmentList = parser
								.getDepartmentList();
						for (int i = 0; i < departmentList.length; i++) {
							try {
								addDepartment(toUpperCase(departmentList[i].departmentName));
							} catch (Exception e) {
								// TODO: handle exception
							}
						}
					} catch (Exception e) {
					}
					return null;
				}

				@Override
				protected void onPostExecute(Void result) {
					super.onPostExecute(result);
					// Dismiss the progress dialog
					if (pDialog.isShowing())
						pDialog.dismiss();
					generateStudentList();

				}
			}
			new DepartmentTask().execute();
		} catch (Exception e) {
		}
	}

	public void generateStudentList() {
		try {
			final DatabaseProcessing db = new DatabaseProcessing(context);
			class StudentTask extends AsyncTask<String, Void, Void> {
				Parser parser = new Parser();

				@Override
				protected void onPreExecute() {
					super.onPreExecute();
					// Showing progress dialog
					pDialog = new ProgressDialog(activity);
					pDialog.setMessage("Öğrenci Listesi Güncelleniyor...");
					pDialog.setCancelable(false);
					pDialog.show();

				}

				protected Void doInBackground(String... urls) {
					try {
						Student[] studentList = parser.getStudentList(db);
						for (int i = 0; i < studentList.length; i++) {
							try {
								addStudent(studentList[i].studentID,
										studentList[i].studentName,
										studentList[i].studentEmail,
										studentList[i].studentDepartment,
										studentList[i].studentYear);
							} catch (Exception e) {
								// TODO: handle exception
							}
						}
					} catch (Exception e) {
						System.out.println(e.toString());
					}
					return null;
				}

				@Override
				protected void onPostExecute(Void result) {
					super.onPostExecute(result);
					// Dismiss the progress dialog
					if (pDialog.isShowing())
						pDialog.dismiss();
					generateStudentsSectionList(parser.studentsSectionList);

				}
			}
			new StudentTask().execute();
		} catch (Exception e) {
		}
	}

	public void generateReserveList() {
		try {
			final DatabaseProcessing db = new DatabaseProcessing(context);
			class ReserveClassroomTask extends AsyncTask<String, Void, Void> {
				@Override
				protected void onPreExecute() {
					super.onPreExecute();
					// Showing progress dialog
					pDialog = new ProgressDialog(activity);
					pDialog.setMessage("Reserve Sınıf Bilgileri Güncelleniyor...");
					pDialog.setCancelable(false);
					pDialog.show();

				}

				protected Void doInBackground(String... urls) {
					try {
						Parser parser = new Parser();
						Classroom[] classroomList = parser.getClassList();
						for (int i = 0; i < classroomList.length; i++) {
							String[][] reserves = parser
									.getReservesOfSpecificClassroom(classroomList[i].classroomID);

							for (int j = 0; j < reserves.length; j++) {
								try {
									addClassroomHasReserve(
											toUpperCase(classroomList[i].classroomName),
											reserves[j][0], reserves[j][1],
											reserves[j][2]);
								} catch (Exception e) {
									// TODO: handle exception
								}

							}

						}
					} catch (Exception e) {
					}
					return null;
				}

				@Override
				protected void onPostExecute(Void result) {
					super.onPostExecute(result);
					// Dismiss the progress dialog
					if (pDialog.isShowing())
						pDialog.dismiss();

				}
			}
			new ReserveClassroomTask().execute();
		} catch (Exception e) {
		}
	}

	public void generateStudentsSectionList(
			final StudentHasSection[] studentsSectionList) {

		final DatabaseProcessing db = new DatabaseProcessing(context);
		class StudentSectionTask extends AsyncTask<String, Void, Void> {
			@Override
			protected void onPreExecute() {
				super.onPreExecute();
				// Showing progress dialog
				pDialog = new ProgressDialog(activity);
				pDialog.setMessage("Ders Programları Güncelleniyor...");
				pDialog.setCancelable(false);
				pDialog.show();

			}

			protected Void doInBackground(String... urls) {
				try {
					for (int i = 0; i < studentsSectionList.length; i++) {
						addStudentHasSection(studentsSectionList[i].studentID,
								studentsSectionList[i].sectionID);
					}
				} catch (Exception e) {
				}
				return null;
			}

			@Override
			protected void onPostExecute(Void result) {
				super.onPostExecute(result);
				// Dismiss the progress dialog
				if (pDialog.isShowing())
					pDialog.dismiss();
				// selectAllDepartments();
				generateReserveList();
			}
		}
		new StudentSectionTask().execute();
	}

	//
	// public void generateDepartmentsSectionList() {
	// try {
	// SQLiteDatabase db = this.getReadableDatabase();
	// Cursor cursor = db.query("department",
	// new String[] { "departmentid" }, null, null, null, null,
	// null);
	// if (cursor.moveToFirst()) {
	// do {
	// for (int i = 1; i < 10; i++) {
	// findStudentsSectionsCountForSpecificDepartmentAndYear(
	// cursor.getInt(0), i);
	// }
	// } while (cursor.moveToNext());
	// }
	// } catch (Exception e) {
	// }
	// }
	//
	// public boolean findStudentsSectionsCountForSpecificDepartmentAndYear(
	// int departmentID, int year) {
	// try {
	// String sqlcommand =
	// "select count(student.studentid), section.sectionid from department, student, studenthassection, "
	// +
	// "section where section.sectionid = studenthassection.sectionid and student.studentdepartment = department.departmentid "
	// + "and department.departmentid = "
	// + departmentID
	// + " and student.studentyear = "
	// + year
	// + " and studenthassection.studentid = student.studentid "
	// + "group by section.sectionid order by section.sectionid";
	//
	// ResultSet res = statement.executeQuery(sqlcommand);
	// // Displaying the output
	//
	// while (res.next()) {
	// try {
	// addDepartmentHasSection(departmentID, year,
	// res.getString(2), res.getInt(1));
	// } catch (Exception e) {
	// // TODO: handle exception
	// }
	// }
	//
	// } catch (Exception e) {
	// // TODO: handle exception
	// }
	//
	// return false;
	// }

	public boolean addClassroom(String classroomName) {
		SQLiteDatabase db = this.getWritableDatabase();
		ContentValues values = new ContentValues();
		values.put("classroomname", classroomName);
		db.insert("classroom", null, values);
		db.close();
		return true;
	}

	public boolean addStudentHasSection(String studentID, int sectionID) {
		SQLiteDatabase db = this.getWritableDatabase();
		ContentValues values = new ContentValues();
		values.put("studentid", studentID);
		values.put("sectionid", sectionID);
		db.insert("studenthassection", null, values);
		db.close();
		return true;
	}

	public boolean addStudent(String studentID, String studentName,
			String studentEmail, int studentDepartment, String studentYear) {
		SQLiteDatabase db = this.getWritableDatabase();
		ContentValues values = new ContentValues();
		values.put("studentid", studentID);
		values.put("studentname", studentName);
		values.put("studentemail", studentEmail);
		values.put("studentdepartment", studentDepartment);
		values.put("studentyear", studentYear);
		db.insert("student", null, values);
		db.close();
		return true;
	}

	public boolean addInstructor(String instructorName) {
		SQLiteDatabase db = this.getWritableDatabase();
		ContentValues values = new ContentValues();
		values.put("instructorname", instructorName);
		db.insert("instructor", null, values);
		db.close();
		return true;
	}

	public boolean addCourse(String courseCode, String courseName) {
		SQLiteDatabase db = this.getWritableDatabase();
		ContentValues values = new ContentValues();
		values.put("coursecode", courseCode);
		values.put("coursename", courseName);
		db.insert("course", null, values);
		db.close();
		return true;
	}

	public boolean addSection(String courseCode, int instructorID,
			int sectionNumber) {
		SQLiteDatabase db = this.getWritableDatabase();
		ContentValues values = new ContentValues();
		values.put("coursecode", courseCode);
		values.put("instructorid", instructorID);
		values.put("sectionnumber", sectionNumber);
		db.insert("section", null, values);
		db.close();
		return true;
	}

	public boolean addSectionCourseHour(int sectionID, int sectionDay,
			int sectionHour, String classroomName) {
		SQLiteDatabase db = this.getWritableDatabase();
		ContentValues values = new ContentValues();
		values.put("sectionid", sectionID);
		values.put("sectionday", sectionDay);
		values.put("sectionhour", sectionHour);
		values.put("classroomname", classroomName);
		db.insert("sectionhascoursehour", null, values);
		db.close();
		return true;
	}

	public boolean addClassroomHasReserve(String classroomName,
			String sectionDay, String sectionHour, String reserveFor) {
		SQLiteDatabase db = this.getWritableDatabase();
		ContentValues values = new ContentValues();
		values.put("classroomname", classroomName);
		values.put("sectionday", sectionDay);
		values.put("sectionhour", sectionHour);
		values.put("reservefor", reserveFor);
		db.insert("classroomhasreserve", null, values);
		db.close();
		return true;
	}

	public boolean addDepartment(String departmentName) {
		SQLiteDatabase db = this.getWritableDatabase();
		ContentValues values = new ContentValues();
		values.put("departmentname", departmentName);
		db.insert("department", null, values);
		db.close();
		return true;
	}

	public int getInstructorID(String instructorName) {
		SQLiteDatabase db = this.getReadableDatabase();
		Cursor cursor = db.query("instructor", new String[] { "instructorid" },
				"instructorname" + "=?", new String[] { instructorName }, null,
				null, null, null);
		if (cursor.moveToFirst()) {
			int result = cursor.getInt(0);
			cursor.close();
			return result;
		} else
			return -1;
	}

	public int getDepartmentID(String departmentName) {
		SQLiteDatabase db = this.getReadableDatabase();
		Cursor cursor = db.query("department", new String[] { "departmentid" },
				"departmentname" + "=?", new String[] { departmentName }, null,
				null, null, null);
		if (cursor.moveToFirst()) {
			int result = cursor.getInt(0);
			cursor.close();
			return result;
		} else
			return -1;
	}

	public int getSectionID(String courseCode, int sectionNumber) {
		SQLiteDatabase db = this.getReadableDatabase();
		Cursor cursor = db.query("section", new String[] { "sectionid" },
				"courseCode=? AND sectionNumber=?", new String[] { courseCode,
						sectionNumber + "" }, null, null, null, null);
		if (cursor != null)
			cursor.moveToFirst();

		int result = cursor.getInt(0);
		cursor.close();
		return result;
	}

	public boolean findClassroom(String classroomName) {
		SQLiteDatabase db = this.getReadableDatabase();
		Cursor cursor = db.query("classroom", new String[] { "classroomname" },
				"classroomname" + "=?", new String[] { classroomName }, null,
				null, null, null);
		if (cursor != null) {
			cursor.moveToFirst();
			cursor.close();
			return true;
		} else
			return false;
	}

	public String toUpperCase(String text) {

		if (text.charAt(0) == 'i')
			text = "Ý" + text.substring(1);
		else if (text.charAt(0) == 'ö')
			text = "Ö" + text.substring(1);
		else if (text.charAt(0) == 'ü')
			text = "Ü" + text.substring(1);
		else if (text.charAt(0) == 'ð')
			text = "Ð" + text.substring(1);
		else if (text.charAt(0) == 'þ')
			text = "Þ" + text.substring(1);
		else if (text.charAt(0) == 'ç')
			text = "Ç" + text.substring(1);
		else if (text.charAt(0) == 'u')
			text = "U" + text.substring(1);
		else
			text = Character.toString(Character.toUpperCase(text.charAt(0)))
					+ text.substring(1);

		if (text.charAt(1) == 'i')
			text = Character.toString(text.charAt(0)) + "Ý" + text.substring(2);
		else if (text.charAt(1) == 'ö')
			text = Character.toString(text.charAt(0)) + "Ö" + text.substring(2);
		else if (text.charAt(1) == 'ü')
			text = Character.toString(text.charAt(0)) + "Ü" + text.substring(2);
		else if (text.charAt(1) == 'ð')
			text = Character.toString(text.charAt(0)) + "Ð" + text.substring(2);
		else if (text.charAt(1) == 'þ')
			text = Character.toString(text.charAt(0)) + "Þ" + text.substring(2);
		else if (text.charAt(1) == 'ç')
			text = Character.toString(text.charAt(0)) + "Ç" + text.substring(2);
		else if (text.charAt(1) == 'u')
			text = Character.toString(text.charAt(0)) + "U" + text.substring(2);
		else
			text = Character.toString(text.charAt(0))
					+ Character.toString(Character.toUpperCase(text.charAt(1)))
					+ text.substring(2);

		for (int i = 2; i < text.length() - 1; i++) {
			if (text.charAt(i) == 'i')
				text = text.substring(0, i) + "Ý" + text.substring(i + 1);
			else if (text.charAt(i) == 'ö')
				text = text.substring(0, i) + "Ö" + text.substring(i + 1);
			else if (text.charAt(i) == 'ü')
				text = text.substring(0, i) + "Ü" + text.substring(i + 1);
			else if (text.charAt(i) == 'ð')
				text = text.substring(0, i) + "Ð" + text.substring(i + 1);
			else if (text.charAt(i) == 'þ')
				text = text.substring(0, i) + "Þ" + text.substring(i + 1);
			else if (text.charAt(i) == 'ç')
				text = text.substring(0, i) + "Ç" + text.substring(i + 1);
			else if (text.charAt(i) == 'u')
				text = text.substring(0, i) + "U" + text.substring(i + 1);
			else
				text = text.substring(0, i)
						+ Character.toString(Character.toUpperCase(text
								.charAt(i))) + text.substring(i + 1);

		}

		if (text.charAt(text.length() - 1) == 'i')
			text = text.substring(0, text.length() - 1) + "Ý";
		else if (text.charAt(text.length() - 1) == 'ö')
			text = text.substring(0, text.length() - 1) + "Ö";
		else if (text.charAt(text.length() - 1) == 'ü')
			text = text.substring(0, text.length() - 1) + "Ü";
		else if (text.charAt(text.length() - 1) == 'ð')
			text = text.substring(0, text.length() - 1) + "Ð";
		else if (text.charAt(text.length() - 1) == 'þ')
			text = text.substring(0, text.length() - 1) + "Þ";
		else if (text.charAt(text.length() - 1) == 'ç')
			text = text.substring(0, text.length() - 1) + "Ç";
		else if (text.charAt(text.length() - 1) == 'u')
			text = text.substring(0, text.length() - 1) + "U";
		else
			text = text.substring(0, text.length() - 1)
					+ Character.toString(Character.toUpperCase(text.charAt(text
							.length() - 1)));

		return text;
	}

	public void exportDB() {
		File sd = Environment.getExternalStorageDirectory();
		File data = Environment.getDataDirectory();
		FileChannel source = null;
		FileChannel destination = null;
		String currentDBPath = "/data/" + "info.androidhive.slidingmenu"
				+ "/databases/" + DATABASE_NAME;
		String backupDBPath = DATABASE_NAME;
		File currentDB = new File(data, currentDBPath);
		File backupDB = new File(sd, backupDBPath);
		try {
			source = new FileInputStream(currentDB).getChannel();
			destination = new FileOutputStream(backupDB).getChannel();
			destination.transferFrom(source, 0, source.size());
			source.close();
			destination.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public String getStudentDENEME(String studentID) {
		SQLiteDatabase db = this.getReadableDatabase();
		Cursor cursor = db.query("student", new String[] { "studentname" },
				"studentid" + "=?", new String[] { studentID }, null, null,
				null, null);
		if (cursor != null) {
			cursor.moveToFirst();
			String result = cursor.getString(0);
			cursor.close();
			System.out.println(result + "!!!!!!!!!!!!!!!!!!!");
			return result;
		}
		return "bulunamadi!";
	}
	
	public ArrayList<HashMap<String, String>> getStudents(String typedText){
		
		SQLiteDatabase db = this.getReadableDatabase();
		ArrayList<HashMap<String, String>> studentList = new ArrayList<HashMap<String, String>>();
		HashMap<String, String> students;
				
			String[] a = new String[1];
			a[0]       = '%' + typedText + '%';
					
			Cursor cursor = db.rawQuery("SELECT studentname, studentid FROM student " + 
			        "WHERE studentname LIKE ?",
			        a);	
		
			if(cursor.moveToFirst()){
	
				  do{
					  students = new HashMap<String, String>();
					  students.put("suggestion", cursor.getString(0).toLowerCase());
					  students.put("suggestion_type", "Öğrenci");
					  students.put("id",cursor.getString(1).toLowerCase());
					  studentList.add(students);
				  } while(cursor.moveToNext());
			}
		return studentList;
	}
	
	public ArrayList<HashMap<String, String>> getInstructors(String typedText){
		
		SQLiteDatabase db = this.getReadableDatabase();
		ArrayList<HashMap<String, String>> instructorList = new ArrayList<HashMap<String, String>>();
		HashMap<String, String> instructors;
		
		String[] a = new String[1];
		a[0]       = '%' + typedText + '%';
				
		Cursor cursor = db.rawQuery("SELECT instructorname FROM instructor " + 
		        "WHERE instructorname LIKE ?",
		        a);	
				
				if(cursor.moveToFirst()){

					  do{
						  instructors = new HashMap<String, String>();
						  instructors.put("suggestion", cursor.getString(0).toLowerCase());
						  instructors.put("suggestion_type", "Öğretim Üyesi");
						  instructorList.add(instructors);
					  } while(cursor.moveToNext());
				}
		return instructorList;
	}
	
	public ArrayList<HashMap<String, String>> getClassroom(String typedText){
		
		SQLiteDatabase db = this.getReadableDatabase();
		ArrayList<HashMap<String, String>> classroomList = new ArrayList<HashMap<String, String>>();
		HashMap<String, String> classrooms;

		String[] a = new String[1];
		a[0]       = '%' + typedText + '%';
				
		Cursor cursor = db.rawQuery("SELECT classroomname FROM classroom " + 
		        "WHERE classroomname LIKE ?",
		        a);	
				
				if(cursor.moveToFirst()){

					  do{
						  classrooms = new HashMap<String, String>();
						  classrooms.put("suggestion", cursor.getString(0).toLowerCase());
						  classrooms.put("suggestion_type", "Sınıf");
						  classroomList.add(classrooms);
					  } while(cursor.moveToNext());
				}
		return classroomList;
	}
	
	public ArrayList<HashMap<String, String>> getCourseByName(String typedText){
		
		SQLiteDatabase db = this.getReadableDatabase();
		ArrayList<HashMap<String, String>> courseNameList = new ArrayList<HashMap<String, String>>();
		HashMap<String, String> courseName;
		
		String[] a = new String[1];
		a[0]       = '%' + typedText + '%';
				
		Cursor cursor = db.rawQuery("SELECT coursename FROM course " + 
		        "WHERE coursename LIKE ?",
		        a);	
				
				if(cursor.moveToFirst()){

					  do{
						  courseName = new HashMap<String, String>();
						  courseName.put("suggestion", cursor.getString(0).toLowerCase());
						  courseName.put("suggestion_type", "Ders");
						  courseNameList.add(courseName);
					  } while(cursor.moveToNext());
				}
		return courseNameList;
	}
	
	public ArrayList<HashMap<String, String>> getCourseByCode(String typedText){
		
		SQLiteDatabase db = this.getReadableDatabase();
		ArrayList<HashMap<String, String>> courseCodeList = new ArrayList<HashMap<String, String>>();
		HashMap<String, String> courseCode;
			
		String[] a = new String[1];
		a[0]       = '%' + typedText + '%';
				
		Cursor cursor = db.rawQuery("SELECT coursecode FROM course " + 
		        "WHERE coursecode LIKE ?",
		        a);	

				if(cursor.moveToFirst()){

					  do{
						  courseCode = new HashMap<String, String>();
						  courseCode.put("suggestion", cursor.getString(0).toLowerCase());
						  courseCode.put("suggestion_type", "Ders");	
						  courseCodeList.add(courseCode);
					  } while(cursor.moveToNext());
				}
		return courseCodeList;
	}
	
	public ArrayList<HashMap<String, String>> getDepartment(String typedText){
		
		SQLiteDatabase db = this.getReadableDatabase();
		ArrayList<HashMap<String, String>> departmentList = new ArrayList<HashMap<String, String>>();
		HashMap<String, String> departments;
		
		String[] a = new String[1];
		a[0]       = '%' + typedText + '%';
				
		Cursor cursor = db.rawQuery("SELECT departmentname FROM department " + 
		        "WHERE departmentname LIKE ?",
		        a);		
				
				if(cursor.moveToFirst()){

					  do{
						  departments = new HashMap<String, String>();
						  departments.put("suggestion", cursor.getString(0).toLowerCase());
						  departments.put("suggestion_type", "Bölüm");	
						  departmentList.add(departments);
						  
					  } while(cursor.moveToNext());
				}
		return departmentList;
	}
	
	public ArrayList <ArrayList<HashMap<String, String>>>  getStudentProgram(String id){
		
		SQLiteDatabase db = this.getReadableDatabase();
		
		ArrayList <ArrayList<HashMap<String, String>>> studentPrograms = new ArrayList<ArrayList<HashMap<String, String>>>();
		HashMap<String, String> map;
		ArrayList<HashMap<String, String>> studentProgram;
		
		String query = "SELECT section.coursecode, section.sectionnumber, sectionhascoursehour.sectionday,"
				+ " sectionhascoursehour.classroomname, sectionhascoursehour.sectionhour "
				+ "FROM section INNER JOIN sectionhascoursehour ON "
				+ "(section.sectionid = sectionhascoursehour.sectionid)"
				+ " INNER JOIN studenthassection ON "
				+ "(section.sectionid = studenthassection.sectionid)"
				+ " INNER JOIN student ON"
				+ " (studenthassection.studentid = student.studentid)"
				+ " WHERE studenthassection.studentid = '" + id + "';";
		
	   String[][] stdPrg = new String[7][14];
	   for (String[] row : stdPrg)
		    Arrays.fill(row, "");
	   
	   Cursor cursor = db.rawQuery(query, null);	
				
		if(cursor.moveToFirst()){

			  do{
					 stdPrg[Integer.parseInt(cursor.getString(2))][Integer.parseInt(cursor.getString(4))] += cursor.getString(0) + "-" + cursor.getString(1) + "\nDerslik:" + cursor.getString(3) + "\n";
				
			  } while(cursor.moveToNext());
		}

		for (int i = 1; i < 7; i++) {
			
			studentProgram = new ArrayList<HashMap<String,String>>();
			
			for (int j = 1; j < 14; j++) {
				
				map = new HashMap<String, String>();
				map.put("hour", j + 7 + ":30");
				map.put("prg", stdPrg[i][j]);
				studentProgram.add(map);
			}			
			studentPrograms.add(studentProgram);
		}	
		return studentPrograms;
	}
	
	public ArrayList <ArrayList<HashMap<String, String>>>  getEmptyClasses(){
		
				SQLiteDatabase db = this.getReadableDatabase();
				
				ArrayList <ArrayList<HashMap<String, String>>> emptyLists = new ArrayList<ArrayList<HashMap<String, String>>>();
				HashMap<String, String> map;
				ArrayList<HashMap<String, String>> emptyList;
				
				for (int i = 1; i < 7; i++) {
					
					emptyList = new ArrayList<HashMap<String,String>>();
					
					for (int j = 1; j < 14; j++) {
						
						String hours = "";
						String query = "SELECT classroom.classroomname FROM classroom"
									+ " EXCEPT "
								+ "SELECT classroomname FROM sectionhascoursehour"
								+ " WHERE sectionhascoursehour.sectionday = '" + i
								+ "'" + " AND sectionhascoursehour.sectionhour = '"
								+ j + "' UNION "
								+ " SELECT classroomname FROM classroomhasreserve"
								+ " WHERE classroomhasreserve.sectionday = '" + i
								+ "' AND classroomhasreserve.sectionhour = '"
								+ j + "'";
						Cursor cursor = db.rawQuery(query, null);	
						
						if(cursor.moveToFirst()){

							  do{
								  if(!cursor.getString(0).startsWith("Y") && !cursor.getString(0).contains("NONE"))
									  hours += cursor.getString(0) + " - ";
								
							  } while(cursor.moveToNext());
						}
						
						hours = hours.substring(0, hours.length() -  3);
						map = new HashMap<String, String>();
						map.put("hour", j + 7 + ":30");
						map.put("prg", hours);
						emptyList.add(map);
					}
					emptyLists.add(emptyList);
				}	
				return emptyLists;
			}
			
}