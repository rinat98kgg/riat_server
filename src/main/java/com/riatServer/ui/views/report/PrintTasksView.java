package com.riatServer.ui.views.report;

import com.riatServer.domain.Task;
import com.riatServer.domain.TaskStatus;
import com.riatServer.service.TaskService;
import com.riatServer.service.TaskStatusService;
import com.riatServer.ui.MainLayout;
import com.riatServer.ui.views.LocalDateTimeField;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.html.Label;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.data.renderer.LocalDateTimeRenderer;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import org.springframework.security.access.annotation.Secured;
import java.time.LocalDateTime;
import java.util.List;

@Route(value = "tasks-report", layout = MainLayout.class)
@PageTitle("Отчеты о всех задачах")
@Secured("ROLE_USER")
public class PrintTasksView extends VerticalLayout {
    private final TaskService taskService;
    H2 title = new H2("Отчеты о всех задачах");
    Grid<Task> grid = new Grid<>(Task.class);

    LocalDateTimeField fromCreateDate = new LocalDateTimeField("Дата создания, c");
    LocalDateTimeField toCreateDate = new LocalDateTimeField("Дата создания, до");
    LocalDateTimeField fromTermDate = new LocalDateTimeField("Срок, c");
    LocalDateTimeField toTermDate = new LocalDateTimeField("Срок, до");

    HorizontalLayout layout1 = new HorizontalLayout();
    VerticalLayout createDateLayout = new VerticalLayout();
    VerticalLayout termDateLayout = new VerticalLayout();


    ComboBox<TaskStatus> statusComboBox = new ComboBox<>("Статус задач");
    Dialog dialog;
    int i = 0;
    List<Task> task;

    PrintableTasks printableTasks;
    public PrintTasksView(TaskService taskService, TaskStatusService statusService) {
        this.taskService = taskService;

        addClassName("list-view");
        setSizeFull();
        configureGrid();

        statusComboBox.setItems(statusService.getAll());
        statusComboBox.setItemLabelGenerator(TaskStatus::getName);
        statusComboBox.setAllowCustomValue(false);
        HorizontalLayout horizontalLayout = new HorizontalLayout();
        Button showButton = new Button("Показать");
        Button printButton = new Button("Распечатать",  VaadinIcon.PRINT.create());

        createDateLayout.add(fromCreateDate, toCreateDate);
        termDateLayout.add(fromTermDate, toTermDate);

        layout1.add(statusComboBox, showButton, printButton);
        horizontalLayout.add(createDateLayout, termDateLayout);
        layout1.setDefaultVerticalComponentAlignment(Alignment.END);

        fromCreateDate.setValue(LocalDateTime.now());
        fromTermDate.setValue(LocalDateTime.now());
        toCreateDate.setValue(LocalDateTime.now());
        toTermDate.setValue(LocalDateTime.now());
        showButton.setVisible(false);
        printButton.setVisible(false);

        statusComboBox.addValueChangeListener(event -> {
            if(event.getValue() != null){
                showButton.setVisible(true);

            }
            else {
                showButton.setVisible(false);
                printButton.setVisible(false);
            }
        });

        showButton.addClickListener(event -> {
            showDate();
            this.add(grid);
            if(!task.isEmpty()){
                printButton.setVisible(true);
            }
        });

        printButton.addClickListener(event -> {
            dialog = new Dialog();
            printableTasks = new PrintableTasks(taskService, statusComboBox.getValue().getId(), statusComboBox.getValue().getName(), fromCreateDate.getValue(), toCreateDate.getValue(), fromTermDate.getValue(), toTermDate.getValue());
            dialog.add(printableTasks);
            dialog.setMinWidth("186mm");
            dialog.setHeight("287mm");
            dialog.open();
        });
        add(title, layout1, horizontalLayout);
    }

    private void configureGrid() {
        grid.addClassName("user-grid");
        grid.setSizeFull();
        grid.getStyle().set("display", "block");

        grid.setColumns();
        grid.addComponentColumn(object -> {
            i = i + 1;
            return new Label("" + i);
        }).setHeader("№").setKey("col1");
        grid.addColumn(Task::getName).setHeader("Название").setKey("col2");;
        grid.addColumn(Task::getDescription).setHeader("Описание").setKey("col3");
        grid.addColumn(new LocalDateTimeRenderer<>(Task::getCreateDate, "dd-MM-yyyy HH:mm:ss")).setHeader("Дата создания").setKey("col4");
        grid.addColumn(new LocalDateTimeRenderer<>(Task::getTermDate, "dd-MM-yyyy HH:mm:ss")).setHeader("Срок").setKey("col5");
        grid.getColumns().forEach(col -> {
            if(col.getKey().equals("col1")){
                col.setWidth("10px");
            }
            else {
                col.setAutoWidth(true);
            }
        });
        grid.addThemeNames("row-stripes", "no-header", "wrap-cell-content");
//        grid.setHeightByRows(true);
    }

    private void showDate(){
        i = 0;
        task = taskService.getTasksByStatus(statusComboBox.getValue().getId(), fromCreateDate.getValue(), toCreateDate.getValue(), fromTermDate.getValue(), toTermDate.getValue());
        grid.setItems(task);
    }

}
