package lk.techmart.web.listener;

import jakarta.inject.Inject;
import jakarta.servlet.annotation.WebListener;
import jakarta.servlet.http.HttpSessionEvent;
import jakarta.servlet.http.HttpSessionListener;
import lk.techmart.core.dto.AdminSessionDTO;
import lk.techmart.ejb.beans.ActiveAdminRegistry;

@WebListener
public class AdminSessionListener implements HttpSessionListener {
    @Inject
    private ActiveAdminRegistry activeAdminRegistry;

    @Override
    public void sessionDestroyed(HttpSessionEvent se) {
        Object adminObj = se.getSession().getAttribute("admin");

        if (adminObj instanceof AdminSessionDTO adminSession) {
            activeAdminRegistry.removeAdmin(adminSession.getId());
        }
    }
}
