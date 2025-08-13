import javax.swing.*;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.util.*;
import java.util.List;
import java.util.stream.Collectors;

class Course implements Serializable {
    private static final long serialVersionUID = 1L;
    private String courseName;
    private String instructorName;

    public Course(String courseName) {
        this.courseName = courseName;
    }

    public String getCourseName() { return courseName; }
    public void setCourseName(String courseName) { this.courseName = courseName; }

    public String getInstructorName() { return instructorName; }
    public void setInstructorName(String instructorName) { this.instructorName = instructorName; }

    @Override
    public String toString() {
        return courseName;
    }

    public String detailedString() {
        return "Course: " + courseName +
                (instructorName != null ? ", Instructor: " + instructorName : ", No instructor assigned");
    }
}

class Enrollment implements Serializable {
    private static final long serialVersionUID = 1L;
    private Student1 student;
    private Course course;

    public Enrollment(Student1 student, Course course) {
        this.student = student;
        this.course = course;
    }

    public Student1 getStudent() { return student; }
    public Course getCourse() { return course; }

    @Override
    public String toString() {
        return student.getName() + " enrolled in " + course.getCourseName();
    }
}

// -------------- MAIN SYSTEM CLASS --------------
public class CoachingCenterManagementSystem extends JFrame {

    private List<Student1> students = new ArrayList<>();
    private List<Course> courses = new ArrayList<>();
    private List<Enrollment> enrollments = new ArrayList<>();

    private int nextStudentId = 1;
    private static final String DATA_FILE = "coaching_center_data.ser";

    // UI Components for Students Tab
    private DefaultListModel<Student1> studentListModel = new DefaultListModel<>();
    private JList<Student1> studentJList = new JList<>(studentListModel);
    private JTextField studentNameField = new JTextField(20);
    private JTextField studentAgeField = new JTextField(5);
    private JTextField studentContactField = new JTextField(15);
    private JTextField studentEmailField = new JTextField(20);
    private JButton addStudentButton = new JButton("Add Student");
    private JButton updateStudentButton = new JButton("Update Student");
    private JButton removeStudentButton = new JButton("Remove Student");

    // UI Components for Courses Tab
    private DefaultListModel<Course> courseListModel = new DefaultListModel<>();
    private JList<Course> courseJList = new JList<>(courseListModel);
    private JTextField courseNameField = new JTextField(20);
    private JTextField instructorNameField = new JTextField(20);
    private JButton addCourseButton = new JButton("Add Course");
    private JButton updateCourseButton = new JButton("Update Course");
    private JButton assignInstructorButton = new JButton("Assign Instructor");
    private JButton removeCourseButton = new JButton("Remove Course");

    // UI Components for Enrollment Tab
    private JComboBox<Student1> enrollmentStudentComboBox = new JComboBox<>();
    private JComboBox<Course> enrollmentCourseComboBox = new JComboBox<>();
    private JButton enrollButton = new JButton("Enroll");
    private DefaultListModel<String> enrollmentListModel = new DefaultListModel<>();
    private JList<String> enrollmentJList = new JList<>(enrollmentListModel);

    // UI Components for Data Overview Tab
    private JTextArea overviewTextArea = new JTextArea(25, 50);

    public CoachingCenterManagementSystem() {
        setTitle("Coaching Center Management System");
        setSize(700, 700);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        loadData();
        updateNextStudentId();

        initUI();

        refreshStudentList();
        refreshCourseList();
        refreshEnrollmentList();
        refreshEnrollmentCombos();
        displayOverview();
    }

    private void initUI() {
        JTabbedPane tabbedPane = new JTabbedPane();

        // Students Tab
        JPanel studentsPanel = new JPanel(new BorderLayout());
        studentsPanel.add(new JScrollPane(studentJList), BorderLayout.WEST);

        JPanel studentFormPanel = new JPanel();
        studentFormPanel.setLayout(new BoxLayout(studentFormPanel, BoxLayout.Y_AXIS));
        studentFormPanel.add(new JLabel("Name:"));
        studentFormPanel.add(studentNameField);
        studentFormPanel.add(new JLabel("Age:"));
        studentFormPanel.add(studentAgeField);
        studentFormPanel.add(new JLabel("Contact Number:"));
        studentFormPanel.add(studentContactField);
        studentFormPanel.add(new JLabel("Email:"));
        studentFormPanel.add(studentEmailField);
        studentFormPanel.add(Box.createRigidArea(new Dimension(0,10)));

        JPanel studentButtonsPanel = new JPanel();
        studentButtonsPanel.add(addStudentButton);
        studentButtonsPanel.add(updateStudentButton);
        studentButtonsPanel.add(removeStudentButton);
        studentFormPanel.add(studentButtonsPanel);

        studentsPanel.add(studentFormPanel, BorderLayout.CENTER);

        tabbedPane.addTab("Students", studentsPanel);

        // Courses Tab
        JPanel coursesPanel = new JPanel(new BorderLayout());
        coursesPanel.add(new JScrollPane(courseJList), BorderLayout.WEST);

        JPanel courseFormPanel = new JPanel();
        courseFormPanel.setLayout(new BoxLayout(courseFormPanel, BoxLayout.Y_AXIS));
        courseFormPanel.add(new JLabel("Course Name:"));
        courseFormPanel.add(courseNameField);
        courseFormPanel.add(new JLabel("Instructor Name:"));
        courseFormPanel.add(instructorNameField);
        courseFormPanel.add(Box.createRigidArea(new Dimension(0,10)));

        JPanel courseButtonsPanel = new JPanel();
        courseButtonsPanel.add(addCourseButton);
        courseButtonsPanel.add(updateCourseButton);
        courseButtonsPanel.add(assignInstructorButton);
        courseButtonsPanel.add(removeCourseButton);
        courseFormPanel.add(courseButtonsPanel);

        coursesPanel.add(courseFormPanel, BorderLayout.CENTER);

        tabbedPane.addTab("Courses", coursesPanel);

        // Enrollment Tab
        JPanel enrollmentPanel = new JPanel(new BorderLayout());

        JPanel enrollFormPanel = new JPanel();
        enrollFormPanel.setLayout(new BoxLayout(enrollFormPanel, BoxLayout.Y_AXIS));
        enrollFormPanel.add(new JLabel("Select Student:"));
        enrollFormPanel.add(enrollmentStudentComboBox);
        enrollFormPanel.add(new JLabel("Select Course:"));
        enrollFormPanel.add(enrollmentCourseComboBox);
        enrollFormPanel.add(Box.createRigidArea(new Dimension(0,10)));
        enrollFormPanel.add(enrollButton);

        enrollmentPanel.add(enrollFormPanel, BorderLayout.NORTH);
        enrollmentPanel.add(new JScrollPane(enrollmentJList), BorderLayout.CENTER);

        tabbedPane.addTab("Enrollments", enrollmentPanel);

        // Overview Tab
        JPanel overviewPanel = new JPanel(new BorderLayout());
        overviewTextArea.setEditable(false);
        overviewTextArea.setFont(new Font("Monospaced", Font.PLAIN, 12));
        overviewPanel.add(new JScrollPane(overviewTextArea), BorderLayout.CENTER);

        tabbedPane.addTab("Overview", overviewPanel);

        add(tabbedPane);

        // -------------------- ACTION LISTENERS --------------------

        // Student List Selection: populate fields
        studentJList.addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                Student1 selected = studentJList.getSelectedValue();
                if (selected != null) {
                    studentNameField.setText(selected.getName());
                    studentAgeField.setText(String.valueOf(selected.getAge()));
                    studentContactField.setText(selected.getContactNumber());
                    studentEmailField.setText(selected.getEmail());
                }
            }
        });

        addStudentButton.addActionListener(e -> addStudent());
        updateStudentButton.addActionListener(e -> updateStudent());
        removeStudentButton.addActionListener(e -> removeStudent());

        // Course List Selection: populate fields
        courseJList.addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                Course selected = courseJList.getSelectedValue();
                if (selected != null) {
                    courseNameField.setText(selected.getCourseName());
                    instructorNameField.setText(selected.getInstructorName() != null ? selected.getInstructorName() : "");
                }
            }
        });

        addCourseButton.addActionListener(e -> addCourse());
        updateCourseButton.addActionListener(e -> updateCourse());
        assignInstructorButton.addActionListener(e -> assignInstructor());
        removeCourseButton.addActionListener(e -> removeCourse());

        enrollButton.addActionListener(e -> enrollStudentInCourse());
    }

    // ----------------- STUDENT METHODS -----------------
    private void addStudent() {
        String name = studentNameField.getText().trim();
        String ageStr = studentAgeField.getText().trim();
        String contact = studentContactField.getText().trim();
        String email = studentEmailField.getText().trim();

        if (name.isEmpty() || ageStr.isEmpty() || contact.isEmpty() || email.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please fill all student fields.");
            return;
        }

        int age;
        try {
            age = Integer.parseInt(ageStr);
            if (age <= 0) throw new NumberFormatException();
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "Please enter a valid positive integer for age.");
            return;
        }

        students.add(new Student1(nextStudentId++, name, age, contact, email));
        saveData();
        refreshStudentList();
        refreshEnrollmentCombos();
        displayOverview();
        clearStudentFields();
    }

    private void updateStudent() {
        Student1 selected = studentJList.getSelectedValue();
        if (selected == null) {
            JOptionPane.showMessageDialog(this, "Select a student to update.");
            return;
        }

        String name = studentNameField.getText().trim();
        String ageStr = studentAgeField.getText().trim();
        String contact = studentContactField.getText().trim();
        String email = studentEmailField.getText().trim();

        if (name.isEmpty() || ageStr.isEmpty() || contact.isEmpty() || email.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please fill all student fields.");
            return;
        }

        int age;
        try {
            age = Integer.parseInt(ageStr);
            if (age <= 0) throw new NumberFormatException();
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "Please enter a valid positive integer for age.");
            return;
        }

        selected.setName(name);
        selected.setAge(age);
        selected.setContactNumber(contact);
        selected.setEmail(email);

        saveData();
        refreshStudentList();
        refreshEnrollmentCombos();
        displayOverview();
        clearStudentFields();
    }

    private void removeStudent() {
        Student1 selected = studentJList.getSelectedValue();
        if (selected == null) {
            JOptionPane.showMessageDialog(this, "Select a student to remove.");
            return;
        }

        // Remove enrollments for this student
        enrollments.removeIf(enrollment -> enrollment.getStudent().getId() == selected.getId());

        students.remove(selected);
        saveData();
        refreshStudentList();
        refreshEnrollmentList();
        refreshEnrollmentCombos();
        displayOverview();
        clearStudentFields();
    }

    private void clearStudentFields() {
        studentNameField.setText("");
        studentAgeField.setText("");
        studentContactField.setText("");
        studentEmailField.setText("");
        studentJList.clearSelection();
    }

    private void refreshStudentList() {
        studentListModel.clear();
        students.forEach(studentListModel::addElement);
    }

    // ----------------- COURSE METHODS -----------------
    private void addCourse() {
        String courseName = courseNameField.getText().trim();
        if (courseName.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Course name cannot be empty.");
            return;
        }

        if (courses.stream().anyMatch(c -> c.getCourseName().equalsIgnoreCase(courseName))) {
            JOptionPane.showMessageDialog(this, "Course already exists.");
            return;
        }

        courses.add(new Course(courseName));
        saveData();
        refreshCourseList();
        refreshEnrollmentCombos();
        displayOverview();
        clearCourseFields();
    }

    private void updateCourse() {
        Course selected = courseJList.getSelectedValue();
        if (selected == null) {
            JOptionPane.showMessageDialog(this, "Select a course to update.");
            return;
        }

        String courseName = courseNameField.getText().trim();
        if (courseName.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Course name cannot be empty.");
            return;
        }

        // Check if new name conflicts with existing courses except the selected one
        boolean conflict = courses.stream()
                .filter(c -> c != selected)
                .anyMatch(c -> c.getCourseName().equalsIgnoreCase(courseName));
        if (conflict) {
            JOptionPane.showMessageDialog(this, "Another course with this name already exists.");
            return;
        }

        selected.setCourseName(courseName);
        saveData();
        refreshCourseList();
        refreshEnrollmentCombos();
        displayOverview();
        clearCourseFields();
    }

    private void assignInstructor() {
        Course selected = courseJList.getSelectedValue();
        if (selected == null) {
            JOptionPane.showMessageDialog(this, "Select a course to assign an instructor.");
            return;
        }

        String instructorName = instructorNameField.getText().trim();
        if (instructorName.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Instructor name cannot be empty.");
            return;
        }

        selected.setInstructorName(instructorName);
        saveData();
        refreshCourseList();
        displayOverview();
        clearCourseFields();
    }

    private void removeCourse() {
        Course selected = courseJList.getSelectedValue();
        if (selected == null) {
            JOptionPane.showMessageDialog(this, "Select a course to remove.");
            return;
        }

        // Remove enrollments for this course
        enrollments.removeIf(enrollment -> enrollment.getCourse().equals(selected));

        courses.remove(selected);
        saveData();
        refreshCourseList();
        refreshEnrollmentList();
        refreshEnrollmentCombos();
        displayOverview();
        clearCourseFields();
    }

    private void clearCourseFields() {
        courseNameField.setText("");
        instructorNameField.setText("");
        courseJList.clearSelection();
    }

    private void refreshCourseList() {
        courseListModel.clear();
        courses.forEach(courseListModel::addElement);
    }

    // -------------- ENROLLMENT METHODS --------------
    private void enrollStudentInCourse() {
        Student1 student = (Student1) enrollmentStudentComboBox.getSelectedItem();
        Course course = (Course) enrollmentCourseComboBox.getSelectedItem();

        if (student == null || course == null) {
            JOptionPane.showMessageDialog(this, "Select both student and course.");
            return;
        }

        boolean alreadyEnrolled = enrollments.stream()
                .anyMatch(enrollment -> enrollment.getStudent().getId() == student.getId() &&
                        enrollment.getCourse().getCourseName().equalsIgnoreCase(course.getCourseName()));

        if (alreadyEnrolled) {
            JOptionPane.showMessageDialog(this, "Student already enrolled in this course.");
            return;
        }

        enrollments.add(new Enrollment(student, course));
        saveData();
        refreshEnrollmentList();
        displayOverview();
    }

    private void refreshEnrollmentList() {
        enrollmentListModel.clear();
        for (Enrollment e : enrollments) {
            enrollmentListModel.addElement(e.toString());
        }
    }

    private void refreshEnrollmentCombos() {
        enrollmentStudentComboBox.removeAllItems();
        for (Student1 s : students) enrollmentStudentComboBox.addItem(s);

        enrollmentCourseComboBox.removeAllItems();
        for (Course c : courses) enrollmentCourseComboBox.addItem(c);
    }

    // ---------------- DATA OVERVIEW -----------------
    private void displayOverview() {
        StringBuilder sb = new StringBuilder();

        sb.append("=== STUDENTS ===\n");
        if (students.isEmpty()) sb.append("No students registered.\n");
        else students.forEach(s -> sb.append(s.detailedString()).append("\n"));

        sb.append("\n=== COURSES ===\n");
        if (courses.isEmpty()) sb.append("No courses added.\n");
        else courses.forEach(c -> sb.append(c.detailedString()).append("\n"));

        sb.append("\n=== ENROLLMENTS ===\n");
        if (enrollments.isEmpty()) sb.append("No enrollments yet.\n");
        else enrollments.forEach(e -> sb.append(e.toString()).append("\n"));

        overviewTextArea.setText(sb.toString());
    }

    // ---------------- DATA PERSISTENCE -----------------
    private void saveData() {
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(DATA_FILE))) {
            oos.writeObject(students);
            oos.writeObject(courses);
            oos.writeObject(enrollments);
            oos.writeInt(nextStudentId);
        } catch (IOException e) {
            JOptionPane.showMessageDialog(this, "Error saving data: " + e.getMessage());
        }
    }

    @SuppressWarnings("unchecked")
    private void loadData() {
        File file = new File(DATA_FILE);
        if (!file.exists()) return;

        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(file))) {
            students = (List<Student1>) ois.readObject();
            courses = (List<Course>) ois.readObject();
            enrollments = (List<Enrollment>) ois.readObject();
            nextStudentId = ois.readInt();
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Error loading data: " + e.getMessage());
        }
    }

    private void updateNextStudentId() {
        nextStudentId = students.stream()
                .mapToInt(Student1::getId)
                .max()
                .orElse(0) + 1;
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            CoachingCenterManagementSystem app = new CoachingCenterManagementSystem();
            app.setVisible(true);
        });
    }
}
