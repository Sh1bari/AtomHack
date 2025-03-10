package ru.noxly.websocket.redis;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import lombok.val;
import org.springframework.stereotype.Service;
import ru.noxly.websocket.models.socket.PipeUpdateSocketDto;
import ru.noxly.websocket.models.socket.ReservoirUpdateSocketDto;
import ru.noxly.websocket.services.SocketMessageSender;

import java.io.IOException;

@Service
@Slf4j
@RequiredArgsConstructor
public class PipeSubscriber {

    private final SocketMessageSender sender;

    private final ObjectMapper objectMapper;

    public void receiveMessage(String message, String channel) {
        try {
            val pipeDto = objectMapper.readValue(message, PipeUpdateSocketDto.class);
            val spaceId = pipeDto.getSpaceId();
            sender.sendPipeInfo(spaceId.toString(), pipeDto);
            log.info("🔔 Получено обновление для spaceId={} из канала {}: {}", spaceId, channel, pipeDto);

        } catch (IOException e) {
            log.error("Ошибка десериализации сообщения: {}", e.getMessage(), e);
        }
    }
}
