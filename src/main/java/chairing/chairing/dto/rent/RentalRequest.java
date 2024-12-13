package chairing.chairing.dto.rent;

import chairing.chairing.domain.wheelchair.WheelchairType;

import java.time.LocalDate;

public class RentalRequest {
    private String wheelchairType; 
    private LocalDate returnDate;
    private LocalDate rentalDate; 

    public String getWheelchairType() {
        return wheelchairType;
    }

    public void setWheelchairType(String wheelchairType) {
        this.wheelchairType = wheelchairType;
    }

    public LocalDate getReturnDate() {
        return returnDate;
    }

    public void setReturnDate(LocalDate returnDate) {
        this.returnDate = returnDate;
    }

    public LocalDate getRentalDate() {
        return rentalDate;
    }

    public void setRentalDate(LocalDate rentalDate) {
        this.rentalDate = rentalDate;
    }

    public WheelchairType getWheelchairTypeEnum() {
        return WheelchairType.valueOf(wheelchairType.toUpperCase());
    }
}
