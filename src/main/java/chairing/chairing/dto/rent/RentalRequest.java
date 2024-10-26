package chairing.chairing.dto.rent;

import chairing.chairing.domain.wheelchair.WheelchairType;

import java.time.LocalDate;
import java.time.LocalDateTime;

public class RentalRequest {
    private String wheelchairType; // 문자열로 변경
    private LocalDate returnDate; // ISO 형식의 반납일
    private LocalDate rentalDate; // 대여 시작일 추가

    // Getters and Setters
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

    // 추가: WheelchairType으로 변환하는 메서드
    public WheelchairType getWheelchairTypeEnum() {
        return WheelchairType.valueOf(wheelchairType.toUpperCase());
    }
}
