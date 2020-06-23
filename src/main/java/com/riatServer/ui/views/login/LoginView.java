package com.riatServer.ui.views.login;

import com.riatServer.config.CustomRequestCache;
import com.vaadin.flow.component.Tag;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.login.LoginI18n;
import com.vaadin.flow.component.login.LoginOverlay;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.i18n.LocaleChangeEvent;
import com.vaadin.flow.i18n.LocaleChangeObserver;
import com.vaadin.flow.router.AfterNavigationEvent;
import com.vaadin.flow.router.AfterNavigationObserver;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;

@Tag("sa-login-view")
@Route(value = LoginView.ROUTE)
@PageTitle("Авторизация | RIAT")
public class LoginView extends VerticalLayout implements LocaleChangeObserver, AfterNavigationObserver {
    public static final String ROUTE = "login";

    private LoginOverlay login = new LoginOverlay();

    @Autowired
    public LoginView(AuthenticationManager authenticationManager,
                     CustomRequestCache requestCache) {
        addClassName("login-view");
        login.setI18n(createTranslatedI18N());
        login.setOpened(true);
        login.getElement().setAttribute("no-forgot-password", true);

        getElement().appendChild(login.getElement());

        login.addLoginListener(e -> { //
            try {
                final Authentication authentication = authenticationManager
                        .authenticate(new UsernamePasswordAuthenticationToken(e.getUsername(), e.getPassword())); //

                System.out.println(authentication);
                SecurityContextHolder.getContext().setAuthentication(authentication); //
                login.close(); //
                UI.getCurrent().navigate(requestCache.resolveRedirectUrl()); //

            } catch (AuthenticationException ex) { //
                login.setError(true);
            }
        });
    }

    private LoginI18n createTranslatedI18N() {
        LoginI18n i18n = LoginI18n.createDefault();
        i18n.setHeader(new LoginI18n.Header());
        i18n.setForm(new LoginI18n.Form());

        i18n.getHeader().setTitle("RIAT");
        i18n.getHeader().setDescription("Система контроля задач сотрудников отеля Maryotel");
        i18n.getForm().setSubmit("Войти");
        //i18n.getForm().setTitle("Авторизация");
        i18n.getForm().setUsername("Имя пользователя");
        i18n.getForm().setPassword("Пароль");
        i18n.getErrorMessage().setTitle("Ошибка!");
        i18n.getErrorMessage().setMessage("Ошибка при авторизации!");
        i18n.setAdditionalInformation("Введите свой логин и пароль для входа в систему!");
        return i18n;
    }

    @Override
    public void localeChange(LocaleChangeEvent event) {
        login.setI18n(createTranslatedI18N());
    }

    @Override
    public void afterNavigation(AfterNavigationEvent event) {
        login.setError(
                event.getLocation().getQueryParameters().getParameters().containsKey("error"));
    }
}