package com.riatServer.ui.views.list.form;

import com.riatServer.domain.Department;
import com.riatServer.domain.DepartmentStaff;
import com.riatServer.domain.User;
import com.riatServer.service.DepartmentStaffService;
import com.riatServer.service.UserService;
import com.riatServer.ui.views.ConfirmDialogView;
import com.riatServer.ui.views.LocalDateTimeField;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.ComponentEvent;
import com.vaadin.flow.component.ComponentEventListener;
import com.vaadin.flow.component.Key;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.confirmdialog.ConfirmDialog;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.data.binder.BeanValidationBinder;
import com.vaadin.flow.data.binder.Binder;
import com.vaadin.flow.shared.Registration;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

public class DepartmentStaffForm extends FormLayout {
    private final DepartmentStaffService departmentStaffService;
    ComboBox<Department> department_id = new ComboBox<>("Отдел");
    ComboBox<User> user_id = new ComboBox<>("Сотрудник");
    public LocalDateTimeField createDate = new LocalDateTimeField("Дата создания");
    public LocalDateTimeField updateDate = new LocalDateTimeField("Дата обновления");


    Button save = new Button("Сохранить");
    Button delete = new Button("Удалить");
    Button close = new Button("Отменить");

    Binder<DepartmentStaff> binder = new BeanValidationBinder<>(DepartmentStaff.class);
    ConfirmDialogView confirmDialogView = new ConfirmDialogView();
    ConfirmDialog dialog;
    @Autowired
    public DepartmentStaffForm(List<Department> departmentList, List<User> userList, DepartmentStaffService departmentStaffService, UserService userService) {
        this.departmentStaffService = departmentStaffService;
        addClassName("input-form");

        binder.bind(department_id, "department_id");
        binder.bind(user_id, "user_id");

        binder.bindInstanceFields(this);

        department_id.setItems(departmentList);
        department_id.setItemLabelGenerator(Department::getName);

//        user_id.setItems(userList);
        user_id.setItemLabelGenerator(User::getFullName);

        department_id.addValueChangeListener(evt -> {
            if(evt.getValue() != null){
                user_id.setItems(userService.getListById(evt.getValue().getId()));
            }
        });

        createDate.setReadOnly(true);
        updateDate.setReadOnly(true);

        dialog = new ConfirmDialog();
        this.setWidth("140mm");

        add(
                department_id,
                user_id,
                createDate,
                updateDate,
                createButtonsLayout()
        );
    }

    public void setDepartmentStaff(DepartmentStaff departmentStaff) {
        binder.setBean(departmentStaff);
    }

    private Component createButtonsLayout() {
        save.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        delete.addThemeVariants(ButtonVariant.LUMO_ERROR);
        close.addThemeVariants(ButtonVariant.LUMO_TERTIARY);

        save.addClickListener(click -> {
            if(user_id.getValue() == null || department_id.getValue() == null){
                if(user_id.getValue() == null){
                    user_id.setInvalid(true);
                    user_id.setErrorMessage("Выберите отдел!");
                }
                if(department_id.getValue() == null){
                    department_id.setInvalid(true);
                    department_id.setErrorMessage("Выберите сотрудника!");
                }
            }
            else {
                if(departmentStaffService.getByUserId(user_id.getValue().getId()) != null){

                    dialog = confirmDialogView.editableConfirmDialog("Подтвердите действие",
                            "\"" + user_id.getValue().getFullName() + "\" уже состоит в отделе \"" + departmentStaffService.getByUserId(user_id.getValue().getId()).getDepartment_id().getName()
                                    + "\". Переназначить его(ее) в отдел \"" + department_id.getValue().getName() + "\"?");

                    dialog.addConfirmListener(event -> {
                        departmentStaffService.delete(departmentStaffService.getByUserId(user_id.getValue().getId()));
                        validateAndSave();
                    });
                }
                else {
                    validateAndSave();
                }
            }
        });
        delete.addClickListener(click -> fireEvent(new DeleteEvent(this, binder.getBean())));
        close.addClickListener(click -> fireEvent(new CloseEvent(this)));

        binder.addStatusChangeListener(evt -> save.setEnabled(binder.isValid()));

        return new HorizontalLayout(save, delete, close);
    }


    private void validateAndSave() {
        if (binder.isValid()) {
            fireEvent(new SaveEvent(this, binder.getBean()));
        }
    }

    // Events
    public static abstract class DepartmentStaffFormEvent extends ComponentEvent<DepartmentStaffForm> {
        private DepartmentStaff departmentStaff;

        protected DepartmentStaffFormEvent(DepartmentStaffForm source, DepartmentStaff departmentStaff) {
            super(source, false);
            this.departmentStaff = departmentStaff;
        }

        public DepartmentStaff getDepartmentStaff() {
            return departmentStaff;
        }
    }

    public static class SaveEvent extends DepartmentStaffForm.DepartmentStaffFormEvent {
        SaveEvent(DepartmentStaffForm source, DepartmentStaff departmentStaff) {
            super(source, departmentStaff);
        }
    }

    public static class DeleteEvent extends DepartmentStaffForm.DepartmentStaffFormEvent {
        DeleteEvent(DepartmentStaffForm source, DepartmentStaff departmentStaff) {
            super(source, departmentStaff);
        }

    }

    public static class CloseEvent extends DepartmentStaffForm.DepartmentStaffFormEvent {
        CloseEvent(DepartmentStaffForm source) {
            super(source, null);
        }
    }

    public <T extends ComponentEvent<?>> Registration addListener(Class<T> eventType,
                                                                  ComponentEventListener<T> listener) {
        return getEventBus().addListener(eventType, listener);
    }


}

