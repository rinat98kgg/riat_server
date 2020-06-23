package com.riatServer.ui.views.list.form;

import com.riatServer.domain.Position;
import com.riatServer.domain.Role;
import com.riatServer.domain.User;
import com.riatServer.service.UserService;
import com.riatServer.ui.views.ConfirmDialogView;
import com.riatServer.ui.views.LocalDateTimeField;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.ComponentEvent;
import com.vaadin.flow.component.ComponentEventListener;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.checkbox.Checkbox;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.confirmdialog.ConfirmDialog;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.textfield.PasswordField;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.binder.BeanValidationBinder;
import com.vaadin.flow.data.binder.Binder;
import com.vaadin.flow.shared.Registration;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.vaadin.gatanaso.MultiselectComboBox;

import java.util.List;

public class UserForm extends FormLayout {
    private final UserService userService;
    TextField firstName = new TextField("Имя");
    TextField lastName = new TextField("Фамилия");
    TextField patronymic = new TextField("Отчество");
    TextField name = new TextField("Логин");
    public PasswordField password = new PasswordField("Пароль");
    ComboBox<Position> position_id = new ComboBox<>("Должность");

    MultiselectComboBox<Role> roles = new MultiselectComboBox<>("Роли");
    //ComboBox<Role> roles = new ComboBox<>("Роли");
    Checkbox enabled = new Checkbox("Активно");
    ComboBox<Role.Status> status = new ComboBox<>("Статус");
    TextField telephone = new TextField("Телефон");


    public LocalDateTimeField createDate = new LocalDateTimeField("Дата создания");
    public LocalDateTimeField updateDate = new LocalDateTimeField("Дата обновления");

    ConfirmDialogView confirmDialogView = new ConfirmDialogView();

    ConfirmDialog dialog;

    Button save = new Button("Сохранить");
    public Button delete = new Button("Удалить");
    Button close = new Button("Отменить");
    public Button changePass = new Button("Изменить пароль");

    Binder<User> binder = new BeanValidationBinder<>(User.class);

    public boolean isChanged = false;

    public UserForm(List<Position> positions, List<Role> roleList, UserService userService) {
        this.userService = userService;
        addClassName("user-form");

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        User currentUser = userService.getByName(authentication.getName());

        //binder.bindInstanceFields(this);
        dialog = new ConfirmDialog();

        binder.bind(firstName, "firstName");
        binder.bind(lastName, "lastName");
        binder.bind(patronymic, "patronymic");

        binder.bind(name, "name");
        binder.bind(password, "password");
        binder.forField(position_id)
                .bind(User::getPosition_id, User::setPosition_id);
        //        binder.bind(position_id, "position_id");
        binder.forField(roles)
                .bind(User::getRoles, User::setRoles);
        //binder.bind(roles, "roles");
        binder.bind(enabled, "enabled");
        binder.bind(status, "status");
        binder.bind(telephone, "telephone");
        binder.bind(createDate, "createDate");
        binder.bind(updateDate, "updateDate");

        password.setVisible(false);
        roles.setPlaceholder("Выбрать...");
        status.setItems(Role.Status.values());
        roles.setItems(roleList);
        roles.setItemLabelGenerator(Role::getName);
        position_id.setItems(positions);
        position_id.setItemLabelGenerator(Position::getName);

        createDate.setReadOnly(true);
        updateDate.setReadOnly(true);

        this.setWidth("200mm");

        add(
                firstName,
                lastName,
                patronymic,
                name,
                password,
                position_id,
                roles,
                enabled,
                status,
                telephone,
                createDate,
                updateDate,
                createButtonsLayout(),
                changePass
        );
    }

    public void setUser(User user) {
        binder.setBean(user);
    }

    private Component createButtonsLayout() {
        save.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        delete.addThemeVariants(ButtonVariant.LUMO_ERROR);
        close.addThemeVariants(ButtonVariant.LUMO_TERTIARY);
        changePass.addThemeVariants(ButtonVariant.LUMO_PRIMARY, ButtonVariant.LUMO_CONTRAST);

        save.addClickListener(click -> {
            if(userService.getByName(name.getValue()) != null && binder.getBean().getId() == 0){
                name.setInvalid(true);
                name.setErrorMessage("Пользователь с таким логином уже сушествует!");
            }
            else {
                if(binder.getBean().getId() == 0){
                    dialog = confirmDialogView.confirmationDialog();
                    dialog.addConfirmListener(event -> validateAndSave());
                }
                else{
                    if(isChanged){
                        dialog = confirmDialogView.confirmationDialog();
                        dialog.addConfirmListener(event -> validateAndSave());
                    }
                    else {
                        fireEvent(new CloseEvent(this));
                    }
                }
            }
        });


        delete.addClickListener(click -> {
            dialog = confirmDialogView.confirmationDialogForDangerousAction();
            dialog.addConfirmListener(event -> fireEvent(new DeleteEvent(this, binder.getBean())));
        });
        close.addClickListener(click -> {
            fireEvent(new CloseEvent(this));
        });

        changePass.addClickListener(click -> {
            dialog = confirmDialogView.changePassword();
            dialog.addConfirmListener(event -> {
                password.setVisible(true);
                changePass.setVisible(false);
                password.setValue("");
                password.focus();
            } );
        });



        binder.addStatusChangeListener(evt -> {
            boolean isValid = evt.getBinder().isValid();
            save.setEnabled(isValid);
        });

        binder.addValueChangeListener(evt -> isChanged = true);

        return new HorizontalLayout(save, delete, close);
    }


    private void validateAndSave() {
        if (binder.isValid()) {
            fireEvent(new SaveEvent(this, binder.getBean()));
        }
    }

    // Events
    public static abstract class UserFormEvent extends ComponentEvent<UserForm> {
        private User user;

        protected UserFormEvent(UserForm source, User user) {
            super(source, false);
            this.user = user;
        }

        public User getUser() {
            return user;
        }
    }

    public static class SaveEvent extends UserFormEvent {
        SaveEvent(UserForm source, User user) {
            super(source, user);
        }
    }

    public static class DeleteEvent extends UserFormEvent {
        DeleteEvent(UserForm source, User user) {
            super(source, user);
        }

    }

    public static class CloseEvent extends UserFormEvent {
        CloseEvent(UserForm source) {
            super(source, null);
        }
    }

    public <T extends ComponentEvent<?>> Registration addListener(Class<T> eventType,
                                                                  ComponentEventListener<T> listener) {
        return getEventBus().addListener(eventType, listener);
    }


}
