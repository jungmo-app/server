package jungmo.server.domain.repository.chat;

import java.util.List;
import java.util.Optional;
import jungmo.server.domain.entity.chat.ChattingMessage;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface ChattingMessageRepository extends MongoRepository<ChattingMessage, String>, ChattingMessageRepositoryCustom {

}
