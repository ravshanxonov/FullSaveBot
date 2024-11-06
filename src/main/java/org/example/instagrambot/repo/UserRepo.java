package org.example.instagrambot.repo;



import org.example.instagrambot.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepo extends JpaRepository<User,Integer> {
  User findByChatId(Long chatId);

}
