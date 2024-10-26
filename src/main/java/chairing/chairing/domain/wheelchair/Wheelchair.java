package chairing.chairing.domain.wheelchair;

import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonBackReference;

import chairing.chairing.domain.rental.Rental;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Embedded;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import lombok.Getter;
import lombok.RequiredArgsConstructor;


@Getter
@RequiredArgsConstructor
@Entity
public class Wheelchair {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long wheelchairId;

    @Enumerated(EnumType.STRING)
    private WheelchairStatus status;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private WheelchairType type;

    @Embedded
    @Column(nullable = true)
    private Location location = new Location(0, 0); // TODO => POINT 타입을 String으로 매핑
    

    @OneToMany(mappedBy = "wheelchair", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonBackReference
    private List<Rental> rentals = new ArrayList<>();

    public Wheelchair(WheelchairStatus status, WheelchairType type, Location location) {
        this.status = status;
        this.type = type;
        this.location = location;
    }


    public void changeStatus(WheelchairStatus newStatus){
        status=newStatus;
    }

    public void updateLocation(Location location){
        this.location = location;
    }
}