package com.riatServer.ui.views.report;

import com.riatServer.domain.ListOfEmployees;
import com.riatServer.domain.Task;
import com.riatServer.domain.TaskStatus;
import com.riatServer.service.ListOfEmployeeService;
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


@Route(value = "my-tasks-report", layout = MainLayout.class)
@PageTitle("Отчет о задачах сотрудника")
@Secured("ROLE_USER")
public class PrintMyTasksView extends VerticalLayout {
    private final ListOfEmployeeService listOfEmployeeService;
    H2 title = new H2("Отчет о задачах сотрудника");
    Grid<ListOfEmployees> grid = new Grid<>(ListOfEmployees.class);

    LocalDateTimeField fromCreateDate = new LocalDateTimeField("Дата создания, c");
    LocalDateTimeField toCreateDate = new LocalDateTimeField("Дата создания, до");
    LocalDateTimeField fromUpdateDate = new LocalDateTimeField("Дата обновления, c");
    LocalDateTimeField toUpdateDate = new LocalDateTimeField("Дата обновления, до");

    HorizontalLayout layout1 = new HorizontalLayout();
    VerticalLayout createDateLayout = new VerticalLayout();
    VerticalLayout termDateLayout = new VerticalLayout();


    ComboBox<TaskStatus> statusComboBox = new ComboBox<>("Статус задач");
    Dialog dialog;
    int i = 0;
    List<ListOfEmployees> listOfEmployees;

    PrintableMyTasks printableTasks;
    public PrintMyTasksView(TaskStatusService statusService, ListOfEmployeeService listOfEmployeeService) {
        this.listOfEmployeeService = listOfEmployeeService;

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
        termDateLayout.add(fromUpdateDate, toUpdateDate);

        layout1.add(statusComboBox, showButton, printButton);
        horizontalLayout.add(createDateLayout, termDateLayout);
        layout1.setDefaultVerticalComponentAlignment(Alignment.END);

        fromCreateDate.setValue(LocalDateTime.now());
        fromUpdateDate.setValue(LocalDateTime.now());
        toCreateDate.setValue(LocalDateTime.now());
        toUpdateDate.setValue(LocalDateTime.now());
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
            if(!listOfEmployees.isEmpty()){
                printButton.setVisible(true);
            }
        });

        printButton.addClickListener(event -> {
            dialog = new Dialog();
            printableTasks = new PrintableMyTasks(listOfEmployeeService, statusComboBox.getValue().getId(), statusComboBox.getValue().getName(), fromCreateDate.getValue(), toCreateDate.getValue(), fromUpdateDate.getValue(), toUpdateDate.getValue());
            dialog.add(printableTasks);
            dialog.setMinWidth("186mm");
            dialog.setHeight("287mm");
            dialog.open();
        });
        add(title, layout1, horizontalLayout);
    }

    private void configureGrid() {
        grid.addClassName("user-grid");
        grid.setHeightFull();
        grid.getStyle().set("display", "block");

        grid.setColumns();
        grid.addComponentColumn(object -> {
            i = i + 1;
            return new Label("" + i);
        }).setHeader("№").setKey("col1");

        grid.addColumn(listOfEmployees1 -> {
            return listOfEmployees1.getTask_id().getName();
        }).setHeader("Задача").setKey("col2");

        grid.addColumn(listOfEmployees1 -> {
            return listOfEmployees1.getTask_id().getDescription();
        }).setHeader("Описание").setKey("col3");

        grid.addColumn(new LocalDateTimeRenderer<>((listOfEmployees1 -> {
            return listOfEmployees1.getTask_id().getTermDate();
        }), "dd-MM-yyyy HH:mm:ss")).setHeader("Срок").setKey("col4");

        grid.setWidth("100");
//        grid.setHeightByRows(true);

        grid.addColumn(new LocalDateTimeRenderer<>(ListOfEmployees::getCreateDate, "dd-MM-yyyy HH:mm:ss")).setHeader("Дата создания").setKey("col5");
        grid.addColumn(new LocalDateTimeRenderer<>(ListOfEmployees::getUpdateDate, "dd-MM-yyyy HH:mm:ss")).setHeader("Дата обновления").setKey("col6");

        grid.getColumns().forEach(col -> {
            if(col.getKey().equals("col1")){
                col.setWidth("10px");
            }
            else {
                col.setAutoWidth(true);
            }
        });
        grid.addThemeNames("row-stripes", "wrap-cell-content");
    }

    private void showDate(){
        i = 0;
        listOfEmployees = listOfEmployeeService.getTheUserTasksByStatus(statusComboBox.getValue().getId(), fromCreateDate.getValue(), toCreateDate.getValue(), fromUpdateDate.getValue(), toUpdateDate.getValue());
        grid.setItems(listOfEmployees);
    }

}