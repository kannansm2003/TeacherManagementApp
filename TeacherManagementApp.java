import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.util.List;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.Collections;
import java.util.Comparator;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

interface TeacherFilter {
    boolean filter(Teacher teacher);
}

class Teacher {
    private String fullName;
    private int age;
    private String dateOfBirth;
    private int numClasses;

    // Constructor
    public Teacher(String fullName, int age, String dateOfBirth, int numClasses) {
        this.fullName = fullName;
        this.age = age;
        this.dateOfBirth = dateOfBirth;
        this.numClasses = numClasses;
    }

    // Getter methods
    public String getFullName() {
        return fullName;
    }

    public int getAge() {
        return age;
    }

    public String getDateOfBirth() {
        return dateOfBirth;
    }

    public int getNumClasses() {
        return numClasses;
    }
}

public class TeacherManagementApp {
    private static final String FILE_PATH = "teachers.txt";

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        List<Teacher> teachers = loadTeachersFromFile();

        while (true) {
            System.out.println("Teacher Management System");
            System.out.println("1. Show all teachers");
            System.out.println("2. Add a teacher");
            System.out.println("3. Filter teachers");
			System.out.println("4. Sort teachers");
            System.out.println("5. Search for a teacher");
            System.out.println("6. Update a teacher's record");
            System.out.println("7. Delete a teacher");
            System.out.println("0. Exit");

            System.out.print("Enter your choice: ");
            int choice = scanner.nextInt();
            scanner.nextLine(); // Consume the newline character

            switch (choice) {
                case 1:
                    showAllTeachers(teachers);
                    break;
                case 2:
                    addTeacher(scanner, teachers);
                    break;
                case 3:
                    filterTeachers(scanner, teachers);
                    break;
				case 4:
					sortTeachers(teachers, scanner);
					break;
                case 5:
                    searchTeacher(scanner, teachers);
                    break;
                case 6:
                    updateTeacher(scanner, teachers);
                    break;
                case 7:
                    deleteTeacher(scanner, teachers);
                    break;
                case 0:
                    saveTeachersToFile(teachers);
                    System.out.println("Exiting the program. Goodbye!");
                    System.exit(0);
                default:
                    System.out.println("Invalid choice. Please enter a valid option.");
            }
        }
    }

    private static List<Teacher> loadTeachersFromFile() {
        List<Teacher> teachers = new ArrayList<>();

        try (BufferedReader reader = new BufferedReader(new FileReader(FILE_PATH))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] data = line.split(",");
                String fullName = data[0];
                int age = Integer.parseInt(data[1]);
                String dateOfBirth = data[2];
                int numClasses = Integer.parseInt(data[3]);

                teachers.add(new Teacher(fullName, age, dateOfBirth, numClasses));
            }
        } catch (FileNotFoundException e) {
            // Ignore if the file doesn't exist yet
        } catch (IOException e) {
            e.printStackTrace();
        }

        return teachers;
    }

    private static void saveTeachersToFile(List<Teacher> teachers) {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(FILE_PATH))) {
            for (Teacher teacher : teachers) {
                writer.write(String.format("%s,%d,%s,%d%n",
                        teacher.getFullName(), teacher.getAge(), teacher.getDateOfBirth(), teacher.getNumClasses()));
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static void showAllTeachers(List<Teacher> teachers) {
        if (teachers.isEmpty()) {
            System.out.println("No teachers available.");
        } else {
            System.out.println("All Teachers:");
            for (Teacher teacher : teachers) {
                System.out.println(teacher.getFullName() + " | Age: " + teacher.getAge() +
                        " | DOB: " + teacher.getDateOfBirth() + " | Classes: " + teacher.getNumClasses());
            }
        }
    }

    private static void addTeacher(Scanner scanner, List<Teacher> teachers) {
    System.out.print("Enter Full Name: ");
    String fullName = scanner.nextLine();
    
    System.out.print("Enter Date of Birth (dd/mm/yyyy): ");
    String dobString = scanner.nextLine();

    // Parse the provided date of birth
    SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
    Date dob;
    try {
        dob = dateFormat.parse(dobString);
    } catch (ParseException e) {
        System.out.println("Invalid date format. Please enter the date in dd/mm/yyyy format.");
        return;
    }

    // Calculate age based on the parsed date of birth
    int age = calculateAge(dob);

    System.out.print("Enter Number of Classes: ");
    int numClasses;
    while (true) {
        try {
            numClasses = Integer.parseInt(scanner.nextLine());
            break; // Exit the loop if parsing is successful
        } catch (NumberFormatException e) {
            System.out.println("Invalid input. Please enter a valid integer for Number of Classes.");
        }
    }

    Teacher newTeacher = new Teacher(fullName, age, dobString, numClasses);
    teachers.add(newTeacher);
    System.out.println("Teacher added successfully.");
}

	private static int calculateAge(Date dob) {
        Date currentDate = new Date();
        long diffInMillis = currentDate.getTime() - dob.getTime();
        long ageInMillis = diffInMillis;
        ageInMillis += ageInMillis < 0 ? 0 : 86400000; // Add one day if the birthday hasn't occurred yet
        int age = (int) (ageInMillis / 31536000000L); // Approximate milliseconds in a year
        return age;
    }

    private static void filterTeachers(Scanner scanner, List<Teacher> teachers) {
        System.out.println("Filter Options:");
        System.out.println("1. Filter by Age");
        System.out.println("2. Filter by Number of Classes");
        System.out.print("Enter your choice: ");
        int filterChoice = scanner.nextInt();
        scanner.nextLine(); // Consume the newline character

        switch (filterChoice) {
            case 1:
                System.out.print("Enter Age to filter: ");
				int ageFilter = scanner.nextInt();
				System.out.println("Filter by (1) Exact Age, (2) Below The Age, (3) Above The Age");
				int ageFilterType = scanner.nextInt();
				showFilteredTeachers(teachers, t -> applyAgeFilter(t, ageFilter, ageFilterType));
				break;
            case 2:
                System.out.print("Enter Number of Classes to filter: ");
                int classesFilter = scanner.nextInt();
                showFilteredTeachers(teachers, t -> t.getNumClasses() == classesFilter);
                break;
            default:
                System.out.println("Invalid filter choice.");
        }
    }

    private static void showFilteredTeachers(List<Teacher> teachers, Predicate<Teacher> filter) {
    List<Teacher> filteredTeachers = teachers.stream().filter(filter::test).collect(Collectors.toList());
        
        if (filteredTeachers.isEmpty()) {
            System.out.println("No teachers match the filter criteria.");
        } else {
            System.out.println("Filtered Teachers:");
            for (Teacher teacher : filteredTeachers) {
                System.out.println(teacher.getFullName() + " | Age: " + teacher.getAge() +
                        " | DOB: " + teacher.getDateOfBirth() + " | Classes: " + teacher.getNumClasses());
            }
        }
    }
	
	private static boolean applyAgeFilter(Teacher teacher, int ageFilter, int ageFilterType) {
    int teacherAge = teacher.getAge();
    switch (ageFilterType) {
        case 1: // Exact Age
            return teacherAge == ageFilter;
        case 2: // Two Categories Below
            return teacherAge >= ageFilter - 2 && teacherAge <= ageFilter;
        case 3: // Two Categories Above
            return teacherAge >= ageFilter && teacherAge <= ageFilter + 2;
        default:
            throw new IllegalArgumentException("Invalid age filter type");
		}
	}
	private static void sortTeachers(List<Teacher> teachers, Scanner scanner) {
    System.out.println("Sort Options:");
    System.out.println("1. Sort by Name");
    System.out.println("2. Sort by Age");
    System.out.println("3. Sort by Number of Classes");
    System.out.print("Enter your choice: ");
    int sortChoice = scanner.nextInt();
    scanner.nextLine(); // Consume the newline character

    boolean ascending = true; // Default to ascending order
    System.out.print("Sort in (1) Ascending or (2) Descending order: ");
    int orderChoice = scanner.nextInt();
    if (orderChoice == 2) {
        ascending = false;
    }

    List<Teacher> sortedTeachers;
    switch (sortChoice) {
        case 1:
            sortedTeachers = sortTeachersByName(new ArrayList<>(teachers), ascending);
            break;
        case 2:
            sortedTeachers = sortTeachersByAge(new ArrayList<>(teachers), ascending);
            break;
        case 3:
            sortedTeachers = sortTeachersByNumClasses(new ArrayList<>(teachers), ascending);
            break;
        default:
            System.out.println("Invalid sorting choice.");
            return;
    }

    showAllTeachers(sortedTeachers);
}

private static List<Teacher> sortTeachersByName(List<Teacher> teachers, boolean ascending) {
    List<Teacher> sortedTeachers = new ArrayList<>(teachers);
    sortedTeachers.sort((t1, t2) -> ascending ?
            t1.getFullName().compareToIgnoreCase(t2.getFullName()) :
            t2.getFullName().compareToIgnoreCase(t1.getFullName()));
    return sortedTeachers;
}

private static List<Teacher> sortTeachersByAge(List<Teacher> teachers, boolean ascending) {
    List<Teacher> sortedTeachers = new ArrayList<>(teachers);
    sortedTeachers.sort(Comparator.comparingInt(Teacher::getAge));
    if (!ascending) {
        Collections.reverse(sortedTeachers);
    }
    return sortedTeachers;
}

private static List<Teacher> sortTeachersByNumClasses(List<Teacher> teachers, boolean ascending) {
    List<Teacher> sortedTeachers = new ArrayList<>(teachers);
    sortedTeachers.sort(Comparator.comparingInt(Teacher::getNumClasses));
    if (!ascending) {
        Collections.reverse(sortedTeachers);
    }
    return sortedTeachers;
}

	private static void searchTeacher(Scanner scanner, List<Teacher> teachers) {
		System.out.print("Enter Full Name to search: ");
		String searchName = scanner.nextLine();

		boolean found = false;
		for (Teacher teacher : teachers) {
			if (teacher.getFullName().equalsIgnoreCase(searchName)) {
				System.out.println("Teacher found:");
				System.out.println(teacher.getFullName() + " | Age: " + teacher.getAge() +
						" | DOB: " + teacher.getDateOfBirth() + " | Classes: " + teacher.getNumClasses());
				found = true;
				break;
			}
		}

		if (!found) {
			System.out.println("Teacher not found.");
		}
	}

        private static void updateTeacher(Scanner scanner, List<Teacher> teachers) {
        System.out.print("Enter Full Name of the teacher to update: ");
        String updateName = scanner.nextLine();

        boolean found = false;
        for (Teacher teacher : teachers) {
            if (teacher.getFullName().equalsIgnoreCase(updateName)) {
                System.out.print("Enter new Full Name: ");
                String newFullName = scanner.nextLine();
                System.out.print("Enter new Age: ");
                int newAge = scanner.nextInt();
                scanner.nextLine(); // Consume the newline character
                System.out.print("Enter new Date of Birth: ");
                String newDateOfBirth = scanner.nextLine();
                System.out.print("Enter new Number of Classes: ");
                int newNumClasses = scanner.nextInt();
                scanner.nextLine(); // Consume the newline character

                teacher = new Teacher(newFullName, newAge, newDateOfBirth, newNumClasses);
                System.out.println("Teacher updated successfully.");
                found = true;
                break;
            }
        }

        if (!found) {
            System.out.println("Teacher not found for update.");
        }
    }

    private static void deleteTeacher(Scanner scanner, List<Teacher> teachers) {
        System.out.print("Enter Full Name of the teacher to delete: ");
        String deleteName = scanner.nextLine();

        Teacher teacherToRemove = null;
        for (Teacher teacher : teachers) {
            if (teacher.getFullName().equalsIgnoreCase(deleteName)) {
                teacherToRemove = teacher;
                break;
            }
        }

        if (teacherToRemove != null) {
            teachers.remove(teacherToRemove);
            System.out.println("Teacher deleted successfully.");
        } else {
            System.out.println("Teacher not found for deletion.");
        }
    }
}

