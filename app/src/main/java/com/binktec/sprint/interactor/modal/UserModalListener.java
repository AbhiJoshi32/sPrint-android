package com.binktec.sprint.interactor.modal;

import com.binktec.sprint.modal.pojo.User;

public interface UserModalListener {
    void syncDatabaseValues(User user);

    void initSessions();
}
