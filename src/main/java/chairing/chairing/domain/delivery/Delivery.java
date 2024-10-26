package chairing.chairing.domain.delivery;

import lombok.*;
import chairing.chairing.domain.rental.Rental;
import jakarta.persistence.*;

@Getter
@Setter
@Entity
@AllArgsConstructor
@NoArgsConstructor
public class Delivery {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long deliveryId;

    @Getter
    @Setter
    @OneToOne
    @JoinColumn(name = "rental_id", nullable = false)
    private Rental rental;      // 배송과 연결

    @Column(nullable = false)
    private String trackingNumber;  // 택배사 운송장 번호

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private DeliveryStatus status;  // 배송 상태를 Enum으로 관리

    @Column(nullable = false)
    private String deliveryAddress;  // 배송지 주소

    @Column(nullable = false)
    private String name;  // 수령인 이름 또는 배송 대상 이름

}
