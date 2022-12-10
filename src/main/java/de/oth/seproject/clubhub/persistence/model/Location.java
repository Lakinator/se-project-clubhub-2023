package de.oth.seproject.clubhub.persistence.model;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Table(name = "locations")
@Entity
public class Location {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    private String country;

    private String city;

    @Column(length = 32)
    private String postalCode;

    private String street;

    @Column(length = 64)
    private String streetNumber;

    private String description;

    @OneToMany(mappedBy = "location", cascade = CascadeType.PERSIST, orphanRemoval = true)
    private List<GenericEvent> genericEvents = new ArrayList<>();

    public Location() {
        // -- //
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getPostalCode() {
        return postalCode;
    }

    public void setPostalCode(String postalCode) {
        this.postalCode = postalCode;
    }

    public String getStreet() {
        return street;
    }

    public void setStreet(String street) {
        this.street = street;
    }

    public String getStreetNumber() {
        return streetNumber;
    }

    public void setStreetNumber(String streetNumber) {
        this.streetNumber = streetNumber;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public List<GenericEvent> getGenericEvents() {
        return genericEvents;
    }

    public void setGenericEvents(List<GenericEvent> genericEvents) {
        this.genericEvents = genericEvents;
    }

    @Override
    public String toString() {
        return country + ", " + city + ", " + postalCode
                + ", " + street + ", " + streetNumber + ", " + description;
    }
}
