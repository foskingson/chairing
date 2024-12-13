package chairing.chairing.controller.wheelchair;

import chairing.chairing.domain.wheelchair.WheelchairStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import chairing.chairing.config.JwtUtil;
import chairing.chairing.domain.rental.Rental;
import chairing.chairing.domain.user.User;
import chairing.chairing.domain.wheelchair.Location;
import chairing.chairing.domain.wheelchair.Wheelchair;
import chairing.chairing.service.rental.RentalService;
import chairing.chairing.service.user.UserService;
import chairing.chairing.service.wheelchair.WheelchairService;
import lombok.RequiredArgsConstructor;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestHeader;



import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequiredArgsConstructor
@RequestMapping("/wheelchair")
public class WheelchairController {

    private final WheelchairService wheelchairService;
    private final UserService userService;
    private final RentalService rentalService;
    private final JwtUtil jwtUtil;

    @GetMapping("/adult")
    public ResponseEntity<Long> getAvailableAdultWheelchairCount() {
        long count = wheelchairService.getAvailableAdultWheelchairCount();
        return ResponseEntity.ok(count);
    }

    @GetMapping
    public List<Wheelchair> getWheelchairsByStatus(@RequestParam(required = false) String status) {
        if ("ALL".equalsIgnoreCase(status)) {
            return wheelchairService.getAllWheelchairs();
        } else {
            return wheelchairService.findByStatus(WheelchairStatus.valueOf(status)); 
        }
    }

    @GetMapping("/count")
    public Map<String, Integer> getWheelchairCounts() {
        int total = wheelchairService.countAll();
        int available = wheelchairService.countByStatus(WheelchairStatus.AVAILABLE);
        int broken = wheelchairService.countByStatus(WheelchairStatus.BROKEN);
        int rented = wheelchairService.countByStatus(WheelchairStatus.RENTED);
        int waiting = wheelchairService.countByStatus(WheelchairStatus.WAITING);

        Map<String, Integer> counts = new HashMap<>();
        counts.put("total", total);
        counts.put("available", available);
        counts.put("broken", broken);
        counts.put("rented", rented);
        counts.put("waiting", waiting);

        return counts;
    }

    @GetMapping("/child")
    public ResponseEntity<Long> getAvailableChildWheelchairCount() {
        long count = wheelchairService.getAvailableChildWheelchairCount();
        return ResponseEntity.ok(count);
    }


    // 휠체어 위치 정보 제공
    @GetMapping("/map")
    public ResponseEntity<?> showMap(@RequestHeader("Authorization") String authorizationHeader) {
        String token = authorizationHeader.substring(7); 
        Long userId = jwtUtil.getUserIdFromToken(token);
        System.out.println(userId);
        User user = userService.getCurrentUser(userId);

        Rental rental = rentalService.findByRentalCode(user.getGuardianCode());
        Wheelchair wheelchair = rental.getWheelchair();
        System.out.println(wheelchair.getLocation().getX());
        
        return ResponseEntity.ok(wheelchair.getLocation());
    }

    // gps센서로부터 위치정보를 받아와 휠체어 엔티티에 저장
    @PostMapping("/gps")
    public ResponseEntity<String> receiveGpsData(@RequestParam("x") double x,
                                                 @RequestParam("y") double y,
                                                 @RequestParam("wheelchairId") Long wheelchairId) {
        System.out.println("Received GPS Data - Latitude: " + x + ", Longitude: " + y + ", Wheelchair ID: " + wheelchairId);
        
        wheelchairService.saveLocation(wheelchairId, new Location(x, y));
    
        return ResponseEntity.ok("Data received");
    }

    
}

