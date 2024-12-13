package chairing.chairing;

import org.springframework.integration.annotation.ServiceActivator;
import org.springframework.messaging.Message;
import org.springframework.stereotype.Service;

@Service
public class TcpMessageHandler {

    @ServiceActivator(inputChannel = "requestChannel")
    public void handleMessage(Message<byte[]> message) {
        String receivedMessage = new String(message.getPayload());
        System.out.println("Received message: " + receivedMessage);
    }
}
