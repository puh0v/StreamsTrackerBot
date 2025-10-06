package io.github.puh0v.db.channels;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ChannelsRepository extends JpaRepository<ChannelsEntity, Long> {

    boolean existsByChannelId(String channelId);

    ChannelsEntity findByChannelId(String channelId);
}