package lat.sal.zwolabot.dao;

import lat.sal.zwolabot.entity.User;

public interface UserDAO {

    User getUser(long id);

    void saveUser(User user);

    User getUserByUsername(String username);
}
