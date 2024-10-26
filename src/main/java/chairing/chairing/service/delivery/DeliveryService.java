package chairing.chairing.service.delivery;

import chairing.chairing.domain.delivery.Delivery;
import chairing.chairing.domain.delivery.DeliveryStatus;
import chairing.chairing.domain.rental.Rental;
import chairing.chairing.repository.delivery.DeliveryRepository;
import chairing.chairing.repository.rental.RentalRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class DeliveryService {

    private final DeliveryRepository deliveryRepository;
    private final RentalRepository rentalRepository;

    public List<Delivery> getAllDeliveries() {
        return deliveryRepository.findAll();
    }

    public Delivery addDelivery(Delivery delivery) {
        Rental rental = rentalRepository.findById(delivery.getRental().getRentalId())
                .orElseThrow(() -> new IllegalArgumentException("Invalid rental ID"));
        delivery.setRental(rental);

        // 초기 배송 상태 설정
        delivery.setStatus(DeliveryStatus.PENDING);

        // rental 정보 설정이 필요한 경우 여기에 추가
        // Rental rental = rentalRepository.findById(delivery.getRental().getRentalId()).orElseThrow(...);
        // delivery.setRental(rental);

        return deliveryRepository.save(delivery);
    }

    public Delivery updateStatus(Long deliveryId, String status) {
        Delivery delivery = deliveryRepository.findById(deliveryId)
                .orElseThrow(() -> new IllegalArgumentException("배송 정보를 찾을 수 없습니다."));

        try {
            DeliveryStatus deliveryStatus = DeliveryStatus.valueOf(status);
            delivery.setStatus(deliveryStatus);
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("유효하지 않은 배송 상태입니다.");
        }

        return deliveryRepository.save(delivery);
    }
}
