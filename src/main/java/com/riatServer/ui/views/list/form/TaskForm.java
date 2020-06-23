package com.riatServer.ui.views.list.form;

import com.riatServer.domain.Task;
import com.riatServer.service.TaskService;
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
import com.vaadin.flow.component.textfield.TextArea;
import com.vaadin.flow.data.binder.BeanValidationBinder;
import com.vaadin.flow.data.binder.Binder;
import com.vaadin.flow.shared.Registration;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.ArrayList;
import java.util.List;

public class TaskForm extends FormLayout {
    ComboBox<String>  name = new ComboBox<>("Название");
    TextArea description = new TextArea("Описание");
    Checkbox templateTask = new Checkbox("Шаблонная задача");
//    TextField procent = new TextField("Выполнено (в %)");
    LocalDateTimeField termDate = new LocalDateTimeField("Срок");
    public LocalDateTimeField createDate = new LocalDateTimeField("Дата создания");
    public LocalDateTimeField updateDate = new LocalDateTimeField("Дата обновления");

    public Button save = new Button("Сохранить");
    public Button delete = new Button("Удалить");
    public Button close = new Button("Отменить");

    HorizontalLayout btnLayout = new HorizontalLayout();

    Binder<Task> binder = new BeanValidationBinder<>(Task.class);

    ConfirmDialogView confirmDialogView = new ConfirmDialogView();

    ConfirmDialog dialog;

    public boolean isChanged = false;

    @Autowired
    public TaskForm(TaskService taskService) {
        List<Task> templateTasks = taskService.getAllTemplateTask();
        List<String> temStringList = new ArrayList<>();
        templateTasks.forEach(task -> {
            temStringList.add(task.getName());
        });

        addClassName("input-form");

//        binder.forField(procent)
//                .withConverter(new StringToFloatConverter("Value must be a float"))
//                .bind(Task::getProcent, Task::setProcent);


        binder.bindInstanceFields(this);

        name.addCustomValueSetListener(
                event -> name.setValue(event.getDetail()));

        name.setRequired(true);
        name.setItems(temStringList);
        name.setPlaceholder("Можете выбрать из списка шаблонов или ввести вручную...");

        description.getStyle().set("maxHeight", "150px");
        description.setPlaceholder("Write here ...");

        createDate.setReadOnly(true);
        updateDate.setReadOnly(true);

        dialog = new ConfirmDialog();

        this.setWidth("200mm");

        add(
                name,
                description,
                termDate,
                templateTask,
//                procent,
                createDate,
                updateDate,
                createButtonsLayout()
        );
    }

    public void setTask(Task task) {
        binder.setBean(task);
    }

    private Component createButtonsLayout() {
        save.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        delete.addThemeVariants(ButtonVariant.LUMO_ERROR);
        close.addThemeVariants(ButtonVariant.LUMO_TERTIARY);

        save.addClickListener(click -> {
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
                    fireEvent(new TaskForm.CloseEvent(this));
                }
            }
        });
        delete.addClickListener(click -> {
            dialog = confirmDialogView.confirmationDialogForDangerousAction();
            dialog.addConfirmListener(event -> fireEvent(new TaskForm.DeleteEvent(this, binder.getBean())));
        });
        close.addClickListener(click -> fireEvent(new CloseEvent(this)));

        binder.addStatusChangeListener(evt -> save.setEnabled(binder.isValid()));
        binder.addValueChangeListener(evt -> isChanged = true);

        btnLayout.add(save, delete, close);

        return new HorizontalLayout(save, delete, close);
    }



    private void validateAndSave() {
        if (binder.isValid()) {
            fireEvent(new SaveEvent(this, binder.getBean()));
        }
    }

    // Events
    public static abstract class TaskFormEvent extends ComponentEvent<TaskForm> {
        private Task task;

        protected TaskFormEvent(TaskForm source, Task task) {
            super(source, false);
            this.task = task;
        }

        public Task getTask() {
            return task;
        }
    }

    public static class SaveEvent extends TaskForm.TaskFormEvent {
        SaveEvent(TaskForm source, Task task) {
            super(source, task);
        }
    }

    public static class DeleteEvent extends TaskForm.TaskFormEvent {
        DeleteEvent(TaskForm source, Task task) {
            super(source, task);
        }

    }

    public static class CloseEvent extends TaskForm.TaskFormEvent {
        CloseEvent(TaskForm source) {
            super(source, null);
        }
    }

    public <T extends ComponentEvent<?>> Registration addListener(Class<T> eventType,
                                                                  ComponentEventListener<T> listener) {
        return getEventBus().addListener(eventType, listener);
    }


}
