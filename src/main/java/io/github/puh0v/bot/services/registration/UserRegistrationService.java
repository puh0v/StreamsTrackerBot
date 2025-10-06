package io.github.puh0v.bot.services.registration;

import io.github.puh0v.db.telegramusers.TelegramUsersEntity;
import io.github.puh0v.db.telegramusers.TelegramUsersRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

/**
 * Сервис для сохранения нового пользователя бота в БД
 */
@Slf4j
@Service
public class UserRegistrationService {
    private final TelegramUsersRepository telegramUsersRepository;

    public UserRegistrationService(TelegramUsersRepository telegramUsersRepository) {
        this.telegramUsersRepository = telegramUsersRepository;
    }

    public void saveUserToDatabase(Long userId) {
        if(!telegramUsersRepository.existsByTelegramId(userId)) {
            log.info("[UserRegistrationService] Новый пользователь: {}. Начинаю сохранение в БД...", userId);

            TelegramUsersEntity telegramUsersEntity = new TelegramUsersEntity();
            telegramUsersEntity.setTelegramId(userId);
            telegramUsersRepository.save(telegramUsersEntity);

            log.info("[UserRegistrationService] Пользователь {} успешно сохранён!", userId);
        }
    }
}