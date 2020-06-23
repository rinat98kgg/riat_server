package com.riatServer.ui.views.list.form;

import com.riatServer.domain.TaskStatus;
import com.riatServer.ui.views.LocalDateTimeField;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.ComponentEvent;
import com.vaadin.flow.component.ComponentEventListener;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.binder.BeanValidationBinder;
import com.vaadin.flow.data.binder.Binder;
import com.vaadin.flow.shared.Registration;
import org.springframework.beans.factory.annotation.Autowired;

public class TaskStatusForm extends FormLayout {
    TextField name = new TextField("Название");
    public LocalDateTimeField createDate = new LocalDateTimeField("Дата создания");
    public LocalDateTimeField updateDate = new LocalDateTimeField("Дата обновления");


    Button save = new Button("Сохранить");
    Button delete = new Button("Удалить");
    Button close = new Button("Отменить");

    Binder<TaskStatus> binder = new BeanValidationBinder<>(TaskStatus.class);

    @Autowired
    public TaskStatusForm() {
        addClassName("input-form");

        binder.bindInstanceFields(this);

        createDate.setReadOnly(true);
        updateDate.setReadOnly(true);

        this.setWidth("140mm");

        add(
                name,
                createDate,
                updateDate,
                createButtonsLayout()
        );
    }

    public void setTaskStatus(TaskStatus taskStatus) {
        binder.setBean(taskStatus);
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
    public static abstract class TaskStatusFormEvent extends ComponentEvent<TaskStatusForm> {
        private TaskStatus taskStatus;

        protected TaskStatusFormEvent(TaskStatusForm source, TaskStatus taskStatus) {
            super(source, false);
            this.taskStatus = taskStatus;
        }

        public TaskStatus getTaskStatus() {
            return taskStatus;
        }
    }

    public static class SaveEvent extends TaskStatusForm.TaskStatusFormEvent {
        SaveEvent(TaskStatusForm source, TaskStatus taskStatus) {
            super(source, taskStatus);
        }
    }

    public static class DeleteEvent extends TaskStatusForm.TaskStatusFormEvent {
        DeleteEvent(TaskStatusForm source, TaskStatus taskStatus) {
            super(source, taskStatus);
        }

    }

    public static class CloseEvent extends TaskStatusForm.TaskStatusFormEvent {
        CloseEvent(TaskStatusForm source) {
            super(source, null);
        }
    }

    public <T extends ComponentEvent<?>> Registration addListener(Class<T> eventType,
                                                                  ComponentEventListener<T> listener) {
        return getEventBus().addListener(eventType, listener);
    }


}

