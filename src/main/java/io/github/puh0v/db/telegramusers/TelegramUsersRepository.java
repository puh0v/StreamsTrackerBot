package io.github.puh0v.db.telegramusers;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TelegramUsersRepository extends JpaRepository<TelegramUsersEntity, Long> {

    boolean existsByTelegramId(Long telegramId);

    TelegramUsersEntity findByTelegramId(Long telegramId);
}
