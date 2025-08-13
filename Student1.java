import java.io.Serializable;
public class Student1 implements Serializable {
    private static final long serialVersionUID = 1L;

    private int id;
    private String name;
    private int age;
    private String contactNumber;
    private String email;

    public Student1(int id, String name, int age, String contactNumber, String email) {
        this.id = id;
        this.name = name;
        this.age = age;
        this.contactNumber = contactNumber;
        this.email = email;
    }

    // Getters and setters
    public int getId() { return id; }
    public void setName(String name) { this.name = name; }
    public String getName() { return name; }
    public void setAge(int age) { this.age = age; }
    public int getAge() { return age; }
    public void setContactNumber(String contactNumber) { this.contactNumber = contactNumber; }
    public String getContactNumber() { return contactNumber; }
    public void setEmail(String email) { this.email = email; }
    public String getEmail() { return email; }

    @Override
    public String toString() {
        return id + ": " + name;
    }

    public String detailedString() {
        return "ID: " + id +
                ", Name: " + name +
                ", Age: " + age +
                ", Contact: " + contactNumber +
                ", Email: " + email;
    }
}
