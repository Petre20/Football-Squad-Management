package models;

public class Player {
    private int id;
    private String firstName;
    private String lastName;
    private String position;
    private int number;

    public Player(int id, String firstName, String lastName, String position, int number) {
        this.id = id;
        this.firstName = firstName;
        this.lastName = lastName;
        this.position = position;
        this.number = number;
    }

    public int getId() { return id; }
    public String getFirstName() { return firstName; }
    public String getLastName() { return lastName; }
    public String getPosition() { return position; }
    public int getNumber() { return number; }

    public String getFullName() {
        return firstName + " " + lastName;
    }
}