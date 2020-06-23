package com.riatServer.ui;

import com.riatServer.config.SecurityUtils;
import com.riatServer.domain.ListOfEmployees;
import com.riatServer.domain.Task;
import com.riatServer.domain.TaskStatus;
import com.riatServer.domain.User;
import com.riatServer.service.UserService;
import com.riatServer.ui.views.list.PositionView;
import com.riatServer.ui.views.list.*;
import com.riatServer.ui.views.list.UserView;
import com.riatServer.ui.views.report.PrintMyTasksView;
import com.riatServer.ui.views.report.PrintTasksView;
import com.vaadin.flow.component.applayout.AppLayout;
import com.vaadin.flow.component.applayout.DrawerToggle;
import com.vaadin.flow.component.contextmenu.MenuItem;
import com.vaadin.flow.component.contextmenu.SubMenu;
import com.vaadin.flow.component.dependency.CssImport;
import com.vaadin.flow.component.html.Anchor;
import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.html.Hr;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.menubar.MenuBar;
import com.vaadin.flow.component.menubar.MenuBarVariant;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.tabs.Tab;
import com.vaadin.flow.component.tabs.Tabs;
import com.vaadin.flow.router.HighlightConditions;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.router.RouterLink;
import com.vaadin.flow.theme.Theme;
import com.vaadin.flow.theme.lumo.Lumo;
import org.springframework.security.access.annotation.Secured;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;


@CssImport("./styles/shared-styles.css")
@Theme(value = Lumo.class, variant = Lumo.DARK)
public class MainLayout extends AppLayout {
    private final UserService userService;
    public MainLayout(UserService userService) {
        this.userService = userService;
        createHeader();
        createDrawer();
    }


    private void createHeader() {
        H1 logo = new H1("RIAT");
        logo.addClassName("logo");
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        Anchor logout = new Anchor("/logout", "Выйти из " + authentication.getName());

        MenuBar menuProfile = new MenuBar();
        MenuItem profile = menuProfile.addItem(authentication.getName());
        profile.addComponentAsFirst(new Icon(VaadinIcon.USER));
        //profile.getSubMenu().addItem("Мои данные", null);
        //profile.getSubMenu().add(new Hr());
        profile.getSubMenu().addItem(logout);

//        MenuItem item = menuProfile.addItem(new Icon(VaadinIcon.BELL));
//        item.getSubMenu().addItem("Notifications"
////                , e -> selected.setText("Notifications")
//        );
//        item.getSubMenu().addItem("Mark as Read"
////                , e -> selected.setText("Mark as Read")
//        );


        HorizontalLayout header = new HorizontalLayout(new DrawerToggle(), logo, menuProfile);
        header.expand(logo);
        header.addClassName("header");
        header.setWidth("100%");
        header.setDefaultVerticalComponentAlignment(FlexComponent.Alignment.CENTER);

        addToNavbar(header);
    }

    private void createDrawer() {
        VerticalLayout layout = new VerticalLayout();
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        User currentUser = userService.getByName(authentication.getName());

//        links.getElement().setAttribute("theme", Lumo.DARK);
        Tabs tabs = new Tabs();
        Tab tab1 = new Tab();
        Tab tab2 = new Tab();
        Tab tab3 = new Tab();
        Tab tab4 = new Tab();
        Tab tab5 = new Tab();
        Tab tab6 = new Tab();
        Tab tab7 = new Tab();


        RouterLink link1 = new RouterLink("Пользователи", UserView.class);
        link1.setHighlightCondition(HighlightConditions.sameLocation());
        link1.getStyle().set("font-size","18px");


        RouterLink link2 = new RouterLink("Отделы", DepartmentView.class);
        link2.setHighlightCondition(HighlightConditions.sameLocation());
        link2.getStyle().set("font-size","18px");

        RouterLink link3 = new RouterLink("Задачи сотрудников", ListOfEmployeeView.class);
        link3.setHighlightCondition(HighlightConditions.sameLocation());
        link3.getStyle().set("font-size","18px");

        RouterLink link4 = new RouterLink("Все задачи", TaskView.class);
        link4.setHighlightCondition(HighlightConditions.sameLocation());
        link4.getStyle().set("font-size","18px");

        RouterLink link5 = new RouterLink("Мои задачи", MyTasksView.class);
        link5.setHighlightCondition(HighlightConditions.sameLocation());
        link5.getStyle().set("font-size","18px");

        RouterLink link6 = new RouterLink("Должности", PositionView.class);
        link2.setHighlightCondition(HighlightConditions.sameLocation());
        link2.getStyle().set("font-size","18px");

        RouterLink link7 = new RouterLink("Статусы задач", TaskStatusView.class);
        link7.setHighlightCondition(HighlightConditions.sameLocation());
        link7.getStyle().set("font-size","18px");

        RouterLink link8 = new RouterLink("Отчеты о всех задачах", PrintTasksView.class);
        link8.setHighlightCondition(HighlightConditions.sameLocation());
        link8.getStyle().set("font-size","18px");

        RouterLink link9 = new RouterLink("Отчеты о задачах сотрудника", PrintMyTasksView.class);
        link9.setHighlightCondition(HighlightConditions.sameLocation());
        link9.getStyle().set("font-size","18px");

        if(SecurityUtils.isAccessGranted(UserView.class)){
            tab1.add(link1);
        }
        if(SecurityUtils.isAccessGranted(DepartmentView.class)){
            tab2.add(link2);
        }
        if(SecurityUtils.isAccessGranted(ListOfEmployeeView.class)){
            tab3.add(link3);
        }
        if(SecurityUtils.isAccessGranted(TaskView.class)){
            tab4.add(link4);
        }
        if(SecurityUtils.isAccessGranted(MyTasksView.class)){
            tab5.add(link5);
        }
        if(SecurityUtils.isAccessGranted(PositionView.class)){
            tab6.add(link6);
        }
        if(SecurityUtils.isAccessGranted(TaskStatusView.class)){
            tab7.add(link7);
        }

        tabs.setOrientation(Tabs.Orientation.VERTICAL);

        MenuBar menuReports = new MenuBar();
        //menuReports.addThemeVariants(MenuBarVariant.LUMO_TERTIARY);
        MenuItem listOfReports = menuReports.addItem("Отчеты");
        menuReports.setWidthFull();

        listOfReports.addComponentAsFirst(new Icon(VaadinIcon.FILE_TABLE));
        SubMenu reportsSubMenu = listOfReports.getSubMenu();

        if(SecurityUtils.isAccessGranted(PrintTasksView.class)){
            reportsSubMenu.addItem(link8);
            link8.setVisible(false);
        }
        if(SecurityUtils.isAccessGranted(PrintMyTasksView.class)){
            reportsSubMenu.addItem(link9);
            link9.setVisible(false);
        }

        if(currentUser.getPosition_id().getName().equals("Директор") || currentUser.getPosition_id().getName().equals("Управляющий")){
            tabs.add(tab1, tab2, tab3, tab4, tab5, tab6, tab7);
            link8.setVisible(true);
        }
        else {
            if(currentUser.getPosition_id().getName().equals("Менеджер")){
                tabs.add(tab3, tab4, tab5);
                link8.setVisible(true);
            }
            else {
                tabs.add(tab5);
            }
        }
        link9.setVisible(true);

        layout.add(tabs, menuReports);
        addToDrawer(layout);
    }
}


