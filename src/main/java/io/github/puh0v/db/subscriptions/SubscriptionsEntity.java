package io.github.puh0v.db.subscriptions;

import io.github.puh0v.db.channels.ChannelsEntity;
import io.github.puh0v.db.telegramusers.TelegramUsersEntity;
import jakarta.persistence.*;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

@Entity
@EqualsAndHashCode(of = {"user", "channel"})
@Table(name = "subscriptions", uniqueConstraints = @UniqueConstraint(
            name = "uk_notifications_entity",
            columnNames = {"user_id", "channel_id"})
)
public class SubscriptionsEntity {
    @Getter
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Setter
    @Getter
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "user_id", nullable = false)
    private TelegramUsersEntity user;

    @Setter
    @Getter
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "channel_id", nullable = false)
    private ChannelsEntity channel;
}