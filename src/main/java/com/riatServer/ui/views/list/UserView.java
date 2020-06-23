package com.riatServer.ui.views.list;

import com.riatServer.domain.Position;
import com.riatServer.domain.User;
import com.riatServer.repo.RolesRepo;
import com.riatServer.service.PositionService;
import com.riatServer.service.UserService;
import com.riatServer.ui.MainLayout;
import com.riatServer.ui.views.list.form.UserForm;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.renderer.LocalDateTimeRenderer;
import com.vaadin.flow.data.value.ValueChangeMode;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import org.springframework.security.access.annotation.Secured;

import com.vaadin.flow.component.notification.Notification;

import java.time.LocalDateTime;
import java.util.Comparator;


@Route(value = "users", layout = MainLayout.class)
@PageTitle("Пользователи | RIAT")
@Secured("ROLE_ADMIN")
public class UserView extends VerticalLayout {
    private final UserService userService;
    Grid<User> grid = new Grid<>(User.class);
    TextField filterText = new TextField();
    private final UserForm userForm;
    H2 title = new H2("Пользователи");
    Dialog dialog;

    private boolean isUpdating;

    public UserView(UserService userService,
                    PositionService positionService, RolesRepo rolesRepo) {
        this.userService = userService;
        addClassName("list-view");
        setSizeFull();
        configureGrid();

        userForm = new UserForm(positionService.getAll(), rolesRepo.findAll(), userService);
        userForm.addListener(UserForm.SaveEvent.class, this::saveUser);
        userForm.addListener(UserForm.DeleteEvent.class, this::deleteUser);
        userForm.addListener(UserForm.CloseEvent.class, e -> closeEditor());


        dialog = new Dialog();
        dialog.add(userForm);
        dialog.setSizeFull();

        Div content = new Div(grid);
        content.addClassName("content");
        content.setSizeFull();

        add(title, getToolbar(), content);
        updateList();
        closeEditor();
    }

    private void deleteUser(UserForm.DeleteEvent event) {
        Notification.show("Данные пользователя с логином: " + event.getUser().getName() + " были удалены");
        userService.delete(event.getUser());
        updateList();
        closeEditor();
    }

    private void saveUser(UserForm.SaveEvent event) {
        if(isUpdating){
            userService.save(event.getUser());
            Notification.show("Данные пользователя с логином: " + event.getUser().getName() + " были изменены");
        }
        else{
            userService.create(event.getUser());
            Notification.show("Добавлен новый пользователь с логином: " + event.getUser().getName());
        }

        updateList();
        closeEditor();
    }

    private void closeEditor() {
        userForm.setUser(null);
//        userForm.setVisible(false);
        removeClassName("editing");
        dialog.close();
    }

    private void configureGrid() {
        grid.addClassName("user-grid");
        grid.setSizeFull();
        //grid.removeColumnByKey("position_id");
        grid.setColumns();
        grid.addColumn(User::getFullName).setHeader("ФИО").setSortable(true);
        grid.addColumn("name").setHeader("Логин");
        grid.addColumn("password").setHeader("Пароль").setVisible(false);
        grid.addColumn(user -> {
            Position position = user.getPosition_id();

            return position == null ? "-" : position.getName();
        }).setHeader("Должность").setSortable(true);

        grid.addColumn(User::getGroup).setHeader("Роли").setSortable(true);
        grid.addColumn(User::isEnabled).setHeader("Активно").setSortable(true);
        grid.addColumn("status").setHeader("Статус");
        grid.addColumn("telephone").setHeader("Телефон");
        grid.addColumn(new LocalDateTimeRenderer<>(User::getCreateDate, "dd-MM-yyyy HH:mm:ss")).setHeader("Дата создания").setSortable(true).setComparator(Comparator.comparing(User::getCreateDate));
        grid.addColumn(new LocalDateTimeRenderer<>(User::getUpdateDate, "dd-MM-yyyy HH:mm:ss")).setHeader("Дата изменения").setSortable(true).setComparator(Comparator.comparing(User::getCreateDate));
        grid.addThemeNames("wrap-cell-content");
        grid.getColumns().forEach(col -> col.setAutoWidth(true));

        grid.asSingleSelect().addValueChangeListener(evt -> {
            userForm.createDate.setVisible(true);
            userForm.updateDate.setVisible(true);
            userForm.changePass.setVisible(true);
            userForm.password.setVisible(false);
            userForm.delete.setVisible(true);
            userForm.isChanged = false;
            editUser(evt.getValue());
        });
    }

    private void editUser(User user) {
        if (user == null) {
            closeEditor();
        } else {
            isUpdating = true;
            userForm.setUser(user);
            dialog.open();
            addClassName("editing");
        }
    }

    private HorizontalLayout getToolbar() {
        filterText.setPlaceholder("Поиск по ФИО");
        filterText.setClearButtonVisible(true);
        filterText.setValueChangeMode(ValueChangeMode.LAZY);
        filterText.addValueChangeListener(e -> updateList());

        Button addUserButton = new Button("Добавить сотрудника");
        addUserButton.addClickListener(click -> addUser());

        HorizontalLayout toolbar = new HorizontalLayout(title, filterText, addUserButton);
        toolbar.addClassName("toolbar");
        return toolbar;
    }

    private void addUser() {
        grid.asSingleSelect().clear();
        userForm.createDate.setVisible(false);
        userForm.updateDate.setVisible(false);
        userForm.changePass.setVisible(false);
        userForm.password.setVisible(true);
        userForm.delete.setVisible(false);
        editUser(new User());
        isUpdating = false;
    }

    private void updateList() {
        grid.setItems(userService.getAll(filterText.getValue()));
        grid.getColumns().forEach(col -> col.setAutoWidth(true));
    }
}