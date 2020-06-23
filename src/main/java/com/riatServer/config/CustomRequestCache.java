package com.riatServer.config;

import com.riatServer.domain.User;
import com.riatServer.repo.UsersRepo;
import com.riatServer.ui.views.login.LoginView;
import com.vaadin.flow.server.VaadinServletRequest;
import com.vaadin.flow.server.VaadinServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.savedrequest.DefaultSavedRequest;
import org.springframework.security.web.savedrequest.HttpSessionRequestCache;
import org.springframework.security.web.savedrequest.SavedRequest;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class CustomRequestCache extends HttpSessionRequestCache {
    @Autowired
    UsersRepo usersRepo;

    @Override
    public void saveRequest(HttpServletRequest request, HttpServletResponse response) {
        if (!SecurityUtils.isFrameworkInternalRequest(request)) {
            super.saveRequest(request, response);
        }
    }
    public String resolveRedirectUrl() {
        SavedRequest savedRequest = getRequest(VaadinServletRequest.getCurrent().getHttpServletRequest(), VaadinServletResponse.getCurrent().getHttpServletResponse());
        if(savedRequest instanceof DefaultSavedRequest) {
            final String requestURI = ((DefaultSavedRequest) savedRequest).getRequestURI(); //
            // check for valid URI and prevent redirecting to the login view
            if (requestURI != null && !requestURI.isEmpty() && !requestURI.contains(LoginView.ROUTE)) { //
                return requestURI.startsWith("/") ? requestURI.substring(1) : requestURI; //
            }
        }

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        User user = usersRepo.findByName(authentication.getName());
        if(user.getPosition_id().getName().equals("Директор") || user.getPosition_id().getName().equals("Управляющий")){
            return "users";
        }
        else {
            if(user.getPosition_id().getName().equals("Менеджер") || user.getPosition_id().getName().equals("Зам. директора")){
                return "list-of-employee";
            }
            else return "";
        }
    }

}