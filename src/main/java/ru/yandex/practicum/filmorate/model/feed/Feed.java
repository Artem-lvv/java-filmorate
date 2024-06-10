package ru.yandex.practicum.filmorate.model.feed;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

import java.sql.Timestamp;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode(of = {"eventId"})
public class Feed {
    private Long eventId;
    private Long entityId;
    private Long userId;
    private EventType eventType;
    private Operation operation;
    @Builder.Default
    private Timestamp timestamp = new Timestamp(System.currentTimeMillis());

    @JsonProperty("timestamp")
    public long getTimestampInSeconds() {
        return timestamp.getTime();
    }
}
