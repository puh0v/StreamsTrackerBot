package io.github.puh0v.db.channels;


import io.github.puh0v.db.subscriptions.SubscriptionsEntity;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "channels")
public class ChannelsEntity {
    @Id
    @Getter
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Setter
    @Getter
    @Column(name = "channel_handle", unique = true, nullable = false)
    private String channelHandle;

    @Setter
    @Getter
    @Column(name = "title", nullable = false)
    private String title;

    @Setter
    @Getter
    @Column(name = "channel_id", unique = true, nullable = false)
    private String channelId;

    @Setter
    @Getter
    @Column(name = "platform", nullable = false)
    private String platform;

    @OneToMany(mappedBy = "channel", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<SubscriptionsEntity> subscriptions = new ArrayList<>();
}
