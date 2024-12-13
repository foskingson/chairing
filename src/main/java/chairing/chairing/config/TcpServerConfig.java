package chairing.chairing.config;

import chairing.chairing.controller.message.FcmController;
import chairing.chairing.domain.wheelchair.Location;
import chairing.chairing.dto.message.MessageRequest;
import chairing.chairing.service.wheelchair.WheelchairService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.integration.annotation.ServiceActivator;
import org.springframework.integration.channel.DirectChannel;
import org.springframework.integration.ip.tcp.TcpInboundGateway;
import org.springframework.integration.ip.tcp.connection.TcpNetServerConnectionFactory;
import org.springframework.integration.ip.tcp.serializer.ByteArrayLfSerializer;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.MessageHandler;

@Configuration
public class TcpServerConfig {

    @Value("10.210.4.245")
    private String ip;

    @Autowired
    private WheelchairService wheelchairService;

    @Autowired
    private FcmController fcmController;

    @Bean
    public TcpNetServerConnectionFactory serverConnectionFactory() {
        TcpNetServerConnectionFactory factory = new TcpNetServerConnectionFactory(8093); // 포트 설정
        factory.setLocalAddress(ip);
        factory.setSerializer(new ByteArrayLfSerializer());
        factory.setDeserializer(new ByteArrayLfSerializer());
        return factory;
    }

    @Bean
    public TcpInboundGateway tcpInboundGateway() {
        TcpInboundGateway gateway = new TcpInboundGateway();
        gateway.setConnectionFactory(serverConnectionFactory());
        gateway.setRequestChannel(requestChannel());
        return gateway;
    }

    @Bean
    public MessageChannel requestChannel() {
        return new DirectChannel();
    }

    @Bean
    @ServiceActivator(inputChannel = "requestChannel")
    public MessageHandler messageHandler() {
        return message -> {
            String payload = new String((byte[]) message.getPayload());
            System.out.println("Received data: " + payload);

            try {
                if (payload.contains("wheelchairId")) {
                    String[] parts = payload.split(",");
                    Long wheelchairId = Long.parseLong(parts[0].split(":")[1].trim());
                    double x = Double.parseDouble(parts[1].split(":")[1].trim());
                    double y = Double.parseDouble(parts[2].split(":")[1].trim());

                    wheelchairService.saveLocation(wheelchairId, new Location(x, y));
                    System.out.println("Location updated for wheelchair ID: " + wheelchairId);
                } else if (payload.contains("Token")) {
                    String[] parts = payload.split(",");
                    String fcmToken = parts[0].split("Token:")[1].trim();  
                    String title = parts[1].split(":")[1].trim();      
                    String desc = parts[2].split(":")[1].trim();     

                    System.out.println("fcm : "+fcmToken+"\n title: "+title+", desc: "+desc);

                    fcmController.pushMessage(new MessageRequest(fcmToken, title, desc));
                }
            } catch (Exception e) {
                System.err.println("Error processing received data: " + e.getMessage());
            }
        };
    }
}