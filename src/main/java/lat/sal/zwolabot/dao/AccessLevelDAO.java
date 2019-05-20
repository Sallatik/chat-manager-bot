package lat.sal.zwolabot.dao;

import lat.sal.zwolabot.entity.AccessLevel;

public interface AccessLevelDAO {

    AccessLevel getAccessLevel(String name);

    void saveAccessLevel(AccessLevel accessLevel);
}
