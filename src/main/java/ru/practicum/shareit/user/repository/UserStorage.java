package ru.practicum.shareit.user.repository;

import ru.practicum.shareit.user.User;

import java.util.List;

public interface UserStorage {
    /**
     * Метод для получения списка всех пользователей
     *
     * @return Список экземпляров User
     */
    List<User> getAllUsers();

    /**
     * Метод для получения пользователя по его id
     *
     * @param userId - идентификатор пользователя
     * @return экземпляр User
     */
    User getUser(Long userId);

    /**
     * Метод для создание пользователя
     *
     * @param user экземпляр User
     * @return созданный экземпляр User
     */
    User addUser(User user);

    /**
     * Метод для обновления пользователя
     *
     * @param userId идентификатор пользователя в системе, данные которого нужно обновить
     * @param user   экземпляр User, содержащий данные для обновления
     * @return обновленный экземпляр User
     */
    User updateUser(Long userId, User user);

    /**
     * Метод для удаления пользователя
     *
     * @param userId идентификатор пользователя, которого нужно удалить
     * @return Boolean результат удаления
     */
    Boolean deleteUser(Long userId);
}
