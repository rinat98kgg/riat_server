package com.riatServer.ui.views.list;

import com.riatServer.domain.TaskStatus;
import com.riatServer.service.TaskStatusService;
import com.riatServer.ui.MainLayout;
import com.riatServer.ui.views.list.form.TaskStatusForm;
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

import java.util.Comparator;

@Route(value = "task-status", layout = MainLayout.class)
@PageTitle("Статусы задач | RIAT")
@Secured("ROLE_ADMIN")
public class TaskStatusView extends VerticalLayout {
    private final TaskStatusService taskStatusService;
    Grid<TaskStatus> grid = new Grid<>(TaskStatus.class);
    TextField filterText = new TextField();
    H2 title = new H2("Статусы задач");
    private final TaskStatusForm taskStatusForm;
    Dialog dialog;
    private boolean isUpdating;

    public TaskStatusView(TaskStatusService taskStatusService) {
        this.taskStatusService = taskStatusService;
        addClassName("list-view");
        setSizeFull();
        configureGrid();

        taskStatusForm = new TaskStatusForm();
        taskStatusForm.addListener(TaskStatusForm.SaveEvent.class, this::saveTaskStatus);
        taskStatusForm.addListener(TaskStatusForm.DeleteEvent.class, this::deleteTaskStatus);
        taskStatusForm.addListener(TaskStatusForm.CloseEvent.class, e -> closeEditor());

        dialog = new Dialog();
        dialog.add(taskStatusForm);
        dialog.setSizeFull();

        Div content = new Div(grid);
        content.addClassName("content");
        content.setSizeFull();

        add(title, getToolbar(), content);
        updateList();
        closeEditor();


    }

    private void deleteTaskStatus(TaskStatusForm.DeleteEvent event) {
        taskStatusService.delete(event.getTaskStatus());
        updateList();
        closeEditor();
    }

    private void saveTaskStatus(TaskStatusForm.SaveEvent event) {
        if(isUpdating){
            taskStatusService.save(event.getTaskStatus());
//            Notification.show("Данные пользователя с логином: " + event.getTaskStatus().getName() + " были изменены");
        }
        else{
            taskStatusService.create(event.getTaskStatus());
//            Notification.show("Добавлен новый пользователь с логином: " + event.getTaskStatus().getName());
        }
        updateList();
        closeEditor();
    }

    private void closeEditor() {
        taskStatusForm.setTaskStatus(null);
        removeClassName("editing");
        dialog.close();
    }

    private void configureGrid() {
        grid.addClassName("taskStatus-grid");
        grid.setSizeFull();
        grid.setColumns();

        grid.addColumn("name").setHeader("Название");
        grid.addColumn(new LocalDateTimeRenderer<>(TaskStatus::getCreateDate, "dd-MM-yyyy HH:mm:ss")).setHeader("Дата создания").setSortable(true).setComparator(Comparator.comparing(TaskStatus::getCreateDate));
        grid.addColumn(new LocalDateTimeRenderer<>(TaskStatus::getUpdateDate, "dd-MM-yyyy HH:mm:ss")).setHeader("Дата изменения").setSortable(true).setComparator(Comparator.comparing(TaskStatus::getCreateDate));

        grid.getColumns().forEach(col -> col.setAutoWidth(true));

        grid.asSingleSelect().addValueChangeListener(evt -> {
            taskStatusForm.createDate.setVisible(true);
            taskStatusForm.updateDate.setVisible(true);
            editTaskStatus(evt.getValue());
        });
    }

    private void editTaskStatus(TaskStatus taskStatus) {
        if (taskStatus == null) {
            closeEditor();
        } else {

            isUpdating = true;
            taskStatusForm.setTaskStatus(taskStatus);
            dialog.open();
            addClassName("editing");

        }
    }

    private HorizontalLayout getToolbar() {
        filterText.setPlaceholder("Поиск по названию");
        filterText.setClearButtonVisible(true);
        filterText.setValueChangeMode(ValueChangeMode.LAZY);
        filterText.addValueChangeListener(e -> updateList());

        Button addTaskStatusButton = new Button("Добавить");
        addTaskStatusButton.addClickListener(click -> addTaskStatus());

        HorizontalLayout toolbar = new HorizontalLayout(filterText, addTaskStatusButton);
        toolbar.addClassName("toolbar");
        return toolbar;
    }

    private void addTaskStatus() {
        grid.asSingleSelect().clear();
        taskStatusForm.createDate.setVisible(false);
        taskStatusForm.updateDate.setVisible(false);
        editTaskStatus(new TaskStatus());
        isUpdating = false;
    }

    private void updateList() {
        grid.setItems(taskStatusService.getAll(filterText.getValue()));
    }
}
