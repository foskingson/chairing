package chairing.chairing.controller.wheelchair;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import chairing.chairing.config.JwtUtil;
import chairing.chairing.domain.rental.Rental;
import chairing.chairing.domain.user.User;
import chairing.chairing.domain.wheelchair.Location;
import chairing.chairing.domain.wheelchair.Wheelchair;
import chairing.chairing.repository.user.UserRepository;
import chairing.chairing.repository.wheelchair.WheelchairRepository;
import chairing.chairing.service.rental.RentalService;
import chairing.chairing.service.user.UserService;
import chairing.chairing.service.wheelchair.WheelchairService;
import lombok.RequiredArgsConstructor;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;


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

    @GetMapping("/child")
    public ResponseEntity<Long> getAvailableChildWheelchairCount() {
        long count = wheelchairService.getAvailableChildWheelchairCount();
        return ResponseEntity.ok(count);
    }


    // 휠체어 위치 정보 제공
    @GetMapping("/map")
    public ResponseEntity<?> showMap(@RequestHeader("Authorization") String authorizationHeader) {
        String token = authorizationHeader.substring(7); // "Bearer " 제거
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
        
        // 휠체어 ID로 해당 휠체어를 찾아서 위치 정보 저장
        wheelchairService.saveLocation(wheelchairId, new Location(x, y));
    
        return ResponseEntity.ok("Data received");
    }

    
}

/*  하드웨어 코딩 예시
#include <TinyGPS++.h>  // GPS 라이브러리
#include <SoftwareSerial.h>  // 소프트웨어 시리얼 (GPS 모듈용)
#include <ESP8266WiFi.h>  // ESP8266 WiFi 라이브러리
#include <ESP8266HTTPClient.h>  // HTTPClient 라이브러리

// GPS 설정
TinyGPSPlus gps;
SoftwareSerial ss(4, 5);  // GPS 모듈의 RX, TX 연결 핀 설정

// Wi-Fi 설정
const char* ssid = "your-SSID";
const char* password = "your-PASSWORD";

// 서버 설정
const char* serverUrl = "http://your-server-ip:port/gps";  // 스프링 서버의 URL
int wheelchairId = 1;  // 휠체어 ID

void setup() {
  Serial.begin(9600);  // 시리얼 통신 시작
  ss.begin(9600);  // GPS 모듈과의 통신 시작
  
  // Wi-Fi 연결
  WiFi.begin(ssid, password);
  while (WiFi.status() != WL_CONNECTED) {
    delay(500);
    Serial.println("Connecting to Wi-Fi...");
  }
  Serial.println("Connected to Wi-Fi");
}

void loop() {
  // GPS 데이터를 읽어오기
  while (ss.available() > 0) {
    gps.encode(ss.read());
    if (gps.location.isUpdated()) {
      double latitude = gps.location.lat();
      double longitude = gps.location.lng();
      
      Serial.print("Latitude: ");
      Serial.println(latitude, 6);
      Serial.print("Longitude: ");
      Serial.println(longitude, 6);

      // 서버로 데이터 전송
      if (WiFi.status() == WL_CONNECTED) {
        HTTPClient http;
        String serverPath = String(serverUrl) + "?x=" + String(latitude, 6) + "&y=" + String(longitude, 6) + "&wheelchairId=" + String(wheelchairId);

        http.begin(serverPath);  // 서버 URL 설정
        int httpResponseCode = http.POST("");  // POST 요청

        if (httpResponseCode > 0) {
          Serial.print("HTTP Response code: ");
          Serial.println(httpResponseCode);  // 서버 응답 코드 출력
        } else {
          Serial.print("Error code: ");
          Serial.println(httpResponseCode);
        }
        
        http.end();  // 요청 종료
      }
      delay(5000);  // 5초마다 데이터 전송
    }
  }
}

 */