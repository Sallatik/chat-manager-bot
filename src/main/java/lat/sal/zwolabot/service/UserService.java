package lat.sal.zwolabot.service;
import lat.sal.zwolabot.entity.User;

public interface UserService {

    void addUser(User user);

    void updateUser(User user);

    void setUserAccessLevel(long id, String accessLevel);

    void setAdmin(long id, boolean admin);

    boolean isAdmin(long id);

    LevelAndChats getLevelAndAvailableChats(long id);
}
