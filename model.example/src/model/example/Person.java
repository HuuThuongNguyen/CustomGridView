package model.example;


import javafx.beans.property.BooleanProperty;
import javafx.beans.property.Property;
import javafx.beans.property.SimpleBooleanProperty;


public class Person {
    private String name;
    private String age;
    private BooleanProperty selected = new SimpleBooleanProperty();


    public Person(String name, String age) {
        this.name = name;
        this.age = age;
    }

    public String getName() {
        return name;
    }

    public String getAge() {
        return age;
    }

    public Property<Boolean> selectedProperty() {
        return this.selected;
    }

    public boolean match(String search) {
        String[] parts = search.split("\\s+", -1);
        for (int i=0; i<parts.length; i++) {
            String p = parts[i];
            if (!getName().contains(p) && !getAge().contains(p))
                return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "Person{" +
                "name='" + name + '\'' +
                ", age='" + age + '\'' +
                '}';
    }
}
