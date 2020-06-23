package com.riatServer.ui.views.list.form;

import com.riatServer.domain.*;
import com.riatServer.service.TaskService;
import com.riatServer.ui.views.LocalDateTimeField;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.ComponentEvent;
import com.vaadin.flow.component.ComponentEventListener;
import com.vaadin.flow.component.Key;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.checkbox.Checkbox;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.textfield.TextArea;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.binder.BeanValidationBinder;
import com.vaadin.flow.data.binder.Binder;
import com.vaadin.flow.shared.Registration;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

public class ListOfEmployeeForm extends FormLayout {

    public ComboBox<Task> task_id = new ComboBox<>("Задача");
    public ComboBox<User> user_id =  new ComboBox<>("Сотрудник");
    public ComboBox<User> owner_id = new ComboBox<>("Поручатель");
    public Checkbox active = new Checkbox("Активно");
    public ComboBox<TaskStatus> taskStatus_id = new ComboBox<>("Статус задачи");
    public LocalDateTimeField createDate = new LocalDateTimeField("Дата создания");
    public LocalDateTimeField updateDate = new LocalDateTimeField("Дата обновления");
    public LocalDateTimeField termDate = new LocalDateTimeField("Срок");
    public LocalDateTimeField taskCreateDate = new LocalDateTimeField("Дата создания задачи");
    public TextArea description = new TextArea("Описание");

    public Button save = new Button("Сохранить");
    public Button delete = new Button("Удалить");
    public Button close = new Button("Отменить");

    Binder<ListOfEmployees> binder = new BeanValidationBinder<>(ListOfEmployees.class);

    @Autowired
    public ListOfEmployeeForm(List<Task> taskList, List<User> userList, List<TaskStatus> taskStatusList, TaskService taskService) {
        addClassName("input-form");

        binder.bind(task_id, "task_id");
        binder.bind(user_id, "user_id");
        binder.bind(taskStatus_id, "taskStatus_id");
        binder.bind(owner_id, "owner_id");

        binder.forField(description).bind(listOfEmployees -> listOfEmployees.getTask_id().getDescription(), null);
        binder.forField(termDate).bind(listOfEmployees -> listOfEmployees.getTask_id().getTermDate(), null);
        binder.forField(taskCreateDate).bind(listOfEmployees -> listOfEmployees.getTask_id().getCreateDate(), null);


        binder.bindInstanceFields(this);

        task_id.setItems(taskList);
        task_id.setItemLabelGenerator(Task::getName);

        user_id.setItems(userList);
        user_id.setItemLabelGenerator(User::getFullName);

        taskStatus_id.setItems(taskStatusList);
        taskStatus_id.setItemLabelGenerator(TaskStatus::getName);

        active.setReadOnly(true);

        owner_id.setItems(userList);
        owner_id.setItemLabelGenerator(User::getFullName);

        description.getStyle().set("maxHeight", "150px");
        description.setPlaceholder("Write here ...");

        taskStatus_id.addValueChangeListener(comboBoxTaskStatusComponentValueChangeEvent -> {
            if(comboBoxTaskStatusComponentValueChangeEvent.getValue() != null){
                if(comboBoxTaskStatusComponentValueChangeEvent.getValue().getName().equals("Завершено") || comboBoxTaskStatusComponentValueChangeEvent.getValue().getName().equals("Не завершено")) {
                    active.setValue(false);
                }
                else {
                    if(comboBoxTaskStatusComponentValueChangeEvent.getValue().getName().equals("В процессе") || comboBoxTaskStatusComponentValueChangeEvent.getValue().getName().equals("В доработке")) {
                        active.setValue(true);
                    }
                }
            }
        });
        task_id.setAllowCustomValue(false);

        task_id.addValueChangeListener(evt -> {
            if (evt.getValue() != null){
                description.setValue(evt.getValue().getDescription());
                termDate.setValue(evt.getValue().getTermDate());
                taskCreateDate.setValue(evt.getValue().getCreateDate());
            }
        });

        createDate.setReadOnly(true);
        updateDate.setReadOnly(true);

//        description.setReadOnly(true);
//        termDate.setReadOnly(true);
//        taskCreateDate.setReadOnly(true);

        this.setWidth("200mm");

        add(
                task_id,
                description,
                user_id,
                owner_id,
                taskStatus_id,
                active,
                termDate,
                taskCreateDate,
                createDate,
                updateDate,
                createButtonsLayout()
        );
    }

    public void setListOfEmployee(ListOfEmployees listOfEmployee) {
        binder.setBean(listOfEmployee);
        if(listOfEmployee == null){
            binder.removeBinding(description);
            binder.removeBinding(termDate);
            binder.removeBinding(taskCreateDate);
            description.setVisible(false);
            termDate.setVisible(false);
            taskCreateDate.setVisible(false);
        }
        else {
            if (listOfEmployee.getTask_id() != null) {
                binder.forField(description).bind(listOfEmployees -> listOfEmployees.getTask_id().getDescription(), null);
                binder.forField(termDate).bind(listOfEmployees -> listOfEmployees.getTask_id().getTermDate(), null);
                binder.forField(taskCreateDate).bind(listOfEmployees -> listOfEmployees.getTask_id().getCreateDate(), null);
                description.setVisible(true);
                termDate.setVisible(true);
                taskCreateDate.setVisible(true);
            }
        }
    }

    private Component createButtonsLayout() {
        save.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        delete.addThemeVariants(ButtonVariant.LUMO_ERROR);
        close.addThemeVariants(ButtonVariant.LUMO_TERTIARY);

        save.addClickListener(click -> validateAndSave());
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
    public static abstract class ListOfEmployeeFormEvent extends ComponentEvent<ListOfEmployeeForm> {
        private ListOfEmployees listOfEmployee;

        protected ListOfEmployeeFormEvent(ListOfEmployeeForm source, ListOfEmployees listOfEmployee) {
            super(source, false);
            this.listOfEmployee = listOfEmployee;
        }

        public ListOfEmployees getListOfEmployee() {
            return listOfEmployee;
        }
    }

    public static class SaveEvent extends ListOfEmployeeForm.ListOfEmployeeFormEvent {
        SaveEvent(ListOfEmployeeForm source, ListOfEmployees listOfEmployee) {
            super(source, listOfEmployee);
        }
    }

    public static class DeleteEvent extends ListOfEmployeeForm.ListOfEmployeeFormEvent {
        DeleteEvent(ListOfEmployeeForm source, ListOfEmployees listOfEmployee) {
            super(source, listOfEmployee);
        }

    }

    public static class CloseEvent extends ListOfEmployeeForm.ListOfEmployeeFormEvent {
        CloseEvent(ListOfEmployeeForm source) {
            super(source, null);
        }
    }

    public <T extends ComponentEvent<?>> Registration addListener(Class<T> eventType,
                                                                  ComponentEventListener<T> listener) {
        return getEventBus().addListener(eventType, listener);
    }


}
