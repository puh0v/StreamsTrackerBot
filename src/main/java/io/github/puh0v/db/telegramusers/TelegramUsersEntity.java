package io.github.puh0v.db.telegramusers;

import io.github.puh0v.db.subscriptions.SubscriptionsEntity;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "registered_users")
public class TelegramUsersEntity {
    @Getter
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Setter
    @Getter
    @Column(name = "telegram_id", unique = true, nullable = false)
    private Long telegramId;

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<SubscriptionsEntity> subscriptions = new ArrayList<>();
}
