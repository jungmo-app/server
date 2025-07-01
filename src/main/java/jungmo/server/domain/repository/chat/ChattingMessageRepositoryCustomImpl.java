package jungmo.server.domain.repository.chat;

import java.util.List;
import jungmo.server.domain.dto.response.chat.ChatMessageResponse;
import jungmo.server.domain.entity.chat.ChattingMessage;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.support.PageableExecutionUtils;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class ChattingMessageRepositoryCustomImpl implements ChattingMessageRepositoryCustom {

    private final MongoTemplate mongoTemplate;

    @Override
    public Page<ChatMessageResponse> findChattingMessagesByRoomIdWithPagingAndFiltering(Pageable page, Long roomId) {

        Pageable pageable = PageRequest.of(page.getPageNumber(), page.getPageSize(), Sort.by("sendTime").descending());

        Query query = new Query()
                .with(pageable)
                .skip(page.getOffset())
                .limit(page.getPageSize());

        // Add filter
        query.addCriteria(Criteria.where("roomId").is(roomId));

        List<ChattingMessage> result = mongoTemplate.find(query, ChattingMessage.class);
        List<ChatMessageResponse> content = result.stream()
                .map(this::toResponse)
                .toList();

        return PageableExecutionUtils.getPage(
                content,
                pageable,
                () -> mongoTemplate.count(
                        Query.of(query).limit(-1).skip(-1),
                        ChatMessageResponse.class
                )
        );
    }

    private ChatMessageResponse toResponse(ChattingMessage entity) {
        return new ChatMessageResponse(
            entity.getRoomId(),
            entity.getType(),
            entity.getSenderId(),
            entity.getSenderName(),
            entity.getContent(),
            entity.getImageUrls(),
            entity.getSendTime().toLocalDate(),
            entity.getSendTime().toLocalTime()
        );
    }
}
