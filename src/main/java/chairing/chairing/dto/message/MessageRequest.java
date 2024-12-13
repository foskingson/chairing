package chairing.chairing.dto.message;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class MessageRequest {
    private String targetToken;
    private String title;
    private String body;
    public MessageRequest(String targetToken, String title, String body) {
        this.targetToken = targetToken;
        this.title = title;
        this.body = body;
    }
}
