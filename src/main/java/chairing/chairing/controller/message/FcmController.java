package chairing.chairing.controller.message;

import chairing.chairing.dto.message.MessageRequest;
import chairing.chairing.service.message.FirebaseCloudMessageService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;

@RestController
@RequiredArgsConstructor
public class FcmController {

    private final FirebaseCloudMessageService firebaseCloudMessageService;

    @PostMapping("/api/fcm")
    public ResponseEntity pushMessage(@RequestBody MessageRequest request) throws IOException {
        System.out.println(request.getTargetToken() + " "
                +request.getTitle() + " " + request.getBody());

        firebaseCloudMessageService.sendMessageTo(
                request.getTargetToken(),
                request.getTitle(),
                request.getBody());
        return ResponseEntity.ok().build();
    }
}