package com.riatServer.config;

import com.riatServer.ui.views.AccessDeniedView;
import com.riatServer.ui.views.login.LoginView;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.router.BeforeEnterEvent;
import com.vaadin.flow.router.NotFoundException;
import com.vaadin.flow.server.ServiceInitEvent;
import com.vaadin.flow.server.VaadinServiceInitListener;
import org.springframework.stereotype.Component;

@Component
public class ConfigureUIServiceInitListener implements VaadinServiceInitListener  {

    @Override
    public void serviceInit(ServiceInitEvent event) {
        event.getSource().addUIInitListener(uiEvent -> {
            final UI ui = uiEvent.getUI();
            ui.addBeforeEnterListener(this::beforeEnter);
        });
    }

    /**
     * Reroutes the user if they're not authorized to access the view.
     *
     * @param event
     *            before navigation event with event details
     */
    private void beforeEnter(BeforeEnterEvent event) {
//        if (!LoginView.class.equals(event.getNavigationTarget())
//                && !SecurityUtils.isUserLoggedIn()) {
//            event.rerouteTo(LoginView.class);
//        }
        if(!SecurityUtils.isAccessGranted(event.getNavigationTarget())) { //
            if(SecurityUtils.isUserLoggedIn()) { //
                event.rerouteTo(AccessDeniedView.class); //
            } else {
                event.rerouteTo(LoginView.class); //
            }
        }

    }
}
