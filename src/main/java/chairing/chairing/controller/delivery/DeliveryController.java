package chairing.chairing.controller.delivery;

import chairing.chairing.domain.delivery.Delivery;
import chairing.chairing.domain.delivery.DeliveryStatus;
import chairing.chairing.service.delivery.DeliveryService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/delivery")
@RequiredArgsConstructor
public class DeliveryController {

    private final DeliveryService deliveryService;

    // 택배사 페이지 환영 메시지
    @GetMapping
    public ResponseEntity<String> deliveryPage() {
        return ResponseEntity.ok("Welcome to the delivery page");
    }

    // 모든 배송 목록 확인
    @GetMapping("/list")
    public ResponseEntity<List<Delivery>> getAllDeliveries() {
        List<Delivery> deliveries = deliveryService.getAllDeliveries();
        return ResponseEntity.ok(deliveries);
    }

    // 배송 추가
    @PostMapping(value = "/deliveries", consumes = {"application/json", "application/json;charset=UTF-8"})
    public ResponseEntity<Delivery> addDelivery(@RequestBody Delivery delivery) {
        Delivery savedDelivery = deliveryService.addDelivery(delivery);
        return ResponseEntity.status(HttpStatus.CREATED).body(savedDelivery);
    }

    // 배송 상태 업데이트
    @PutMapping("/deliveries/{deliveryId}/status")
    public ResponseEntity<Delivery> updateDeliveryStatus(
            @PathVariable Long deliveryId,
            @RequestParam String status) {
        DeliveryStatus deliveryStatus;
        try {
            // 전달된 문자열을 DeliveryStatus enum으로 변환
            deliveryStatus = DeliveryStatus.valueOf(status.toUpperCase());
        } catch (IllegalArgumentException e) {
            // 유효하지 않은 상태 값 처리
            return ResponseEntity.badRequest().body(null);
        }

        // 배송 상태 업데이트
        Delivery updatedDelivery = deliveryService.updateStatus(deliveryId, String.valueOf(deliveryStatus));
        return ResponseEntity.ok(updatedDelivery);
    }
}
