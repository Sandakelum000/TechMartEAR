package lk.techmart.web.util;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import lk.techmart.core.dto.CartItemDTO;
import lk.techmart.core.dto.UserSessionDTO;

import java.util.List;

public class SessionUtil {
    public static UserSessionDTO getSessionUser(HttpServletRequest request){
        HttpSession session = request.getSession(false);
        if (session == null) {
            return null;
        }
        return (UserSessionDTO) session.getAttribute("user");
    }

    public static void setSessionUser(HttpServletRequest request,
                                      UserSessionDTO user) {
        HttpSession session = request.getSession();
        session.setAttribute("user", user);
    }

    public static boolean invalidate(HttpServletRequest request) {
        HttpSession session = request.getSession(false);

        if (session != null && session.getAttribute("user") != null) {
            session.invalidate();
            return true;
        }
        return false;
    }
}
