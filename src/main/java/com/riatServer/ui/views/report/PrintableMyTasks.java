package com.riatServer.ui.views.report;

import com.riatServer.domain.ListOfEmployees;
import com.riatServer.domain.Task;
import com.riatServer.service.ListOfEmployeeService;
import com.riatServer.service.TaskService;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.H3;
import com.vaadin.flow.component.html.Label;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.data.renderer.LocalDateTimeRenderer;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class PrintableMyTasks extends VerticalLayout {
    Grid<ListOfEmployees> grid  = new Grid<>(ListOfEmployees.class);
    H3 h3 = new H3("Отчет о задачах сотрудника");
    int i = 0;
    Button printBtn = new Button("Распечатать");
    HorizontalLayout horizontalLayout = new HorizontalLayout();
    VerticalLayout verticalLayout = new VerticalLayout();
    public PrintableMyTasks(ListOfEmployeeService listOfEmployeeService, Long statusId, String status, LocalDateTime fromCreateDate, LocalDateTime toCreateDate,
                            LocalDateTime fromUpdateDate, LocalDateTime toUpdateDate) {
        addClassName("list-view");
        setSizeFull();

        grid.addClassName("user-grid");
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

        grid.addColumn(new LocalDateTimeRenderer<>(ListOfEmployees::getCreateDate, "dd-MM-yyyy HH:mm:ss")).setHeader("Дата создания").setKey("col5");
        grid.addColumn(new LocalDateTimeRenderer<>(ListOfEmployees::getUpdateDate, "dd-MM-yyyy HH:mm:ss")).setHeader("Дата обновления").setKey("col6");

        grid.addThemeNames("row-stripes", "no-header", "wrap-cell-content");
        grid.setHeightByRows(true);
        grid.getStyle().set("font-size", "14px");
        grid.getColumns().forEach(col -> {
            if(col.getKey().equals("col1")){
                col.setWidth("10px");
            }
        });


        grid.setWidth("186mm");
        Div content = new Div(grid);
        content.addClassName("content");
        content.setWidth("186mm");

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        Label currentUser = new Label("Сотрудник: " + authentication.getName());
        currentUser.getStyle().set("font-style", "bold");
        Label taskStatus = new Label("Статус задач: " + status);
        Label currentDate = new Label("Дата и время создания отчета: " + LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm:ss")));
        Label createDate = new Label("Дата создания, с: " + fromCreateDate.format(DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm:ss")) + " до: " + toCreateDate.format(DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm:ss")));
        Label termDate = new Label("Дата обновления, с: " + fromUpdateDate.format(DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm:ss")) + " до: " + toUpdateDate.format(DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm:ss")));

        verticalLayout.add(currentUser, taskStatus, currentDate, createDate, termDate);
        verticalLayout.getStyle().set("font-size", "12px");
        horizontalLayout.add(verticalLayout, printBtn);
        horizontalLayout.setDefaultVerticalComponentAlignment(Alignment.END);
        printBtn.setWidth("100%");
        horizontalLayout.setWidthFull();
        printBtn.addClickListener(event -> printMethod());
        this.setHorizontalComponentAlignment(Alignment.CENTER, h3);
        add(h3, horizontalLayout, content);
        grid.setItems(listOfEmployeeService.getTheUserTasksByStatus(statusId, fromCreateDate, toCreateDate, fromUpdateDate, toUpdateDate));
//        grid.setItems(taskService.getAll());
    }

    private void printMethod(){
        printBtn.setVisible(false);
//        setSizeUndefined2Print();
        UI.getCurrent().getPage().executeJs( "print();" ) ;
    }

//    private void setSizeUndefined2Print()
//    {
//        UI.getCurrent().getPage().executeJs("document.body.style.overflow = \"auto\";" +
//                "document.body.style.height  = \"auto\"");
//
//        this.setSizeUndefined();
//    }

}