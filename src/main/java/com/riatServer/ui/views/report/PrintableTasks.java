package com.riatServer.ui.views.report;

import com.riatServer.domain.Task;
import com.riatServer.service.TaskService;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.dependency.JavaScript;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.H3;
import com.vaadin.flow.component.html.Label;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.data.renderer.LocalDateTimeRenderer;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import java.net.ContentHandler;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class PrintableTasks extends VerticalLayout {
    Grid<Task> grid  = new Grid<>(Task.class);
    H3 h3 = new H3("Отчет о задачах");
    int i = 0;
    Button printBtn = new Button("Распечатать");
    HorizontalLayout horizontalLayout = new HorizontalLayout();
    VerticalLayout verticalLayout = new VerticalLayout();
    public PrintableTasks(TaskService taskService, Long statusId, String status, LocalDateTime fromCreateDate, LocalDateTime toCreateDate,
                          LocalDateTime fromTermDate, LocalDateTime toTermDate) {
        addClassName("list-view");
        setSizeFull();

        grid.addClassName("user-grid");
        grid.setColumns();
        grid.addComponentColumn(object -> {
            i = i + 1;
            return new Label("" + i);
        }).setHeader("№").setKey("col1");
        grid.addColumn(Task::getName).setHeader("Название").setKey("col2");;
        grid.addColumn(Task::getDescription).setHeader("Описание").setKey("col3");
        grid.addColumn(new LocalDateTimeRenderer<>(Task::getCreateDate, "dd-MM-yyyy HH:mm:ss")).setHeader("Дата создания").setKey("col4");
        grid.addColumn(new LocalDateTimeRenderer<>(Task::getTermDate, "dd-MM-yyyy HH:mm:ss")).setHeader("Срок").setKey("col5");
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

        Label taskStatus = new Label("Статус задач: " + status);
        Label currentDate = new Label("Дата и время создания отчета: " + LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm:ss")));
        Label createDate = new Label("Дата создания, с: " + fromCreateDate.format(DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm:ss")) + " до: " + toCreateDate.format(DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm:ss")));
        Label termDate = new Label("Срок, с: " + fromTermDate.format(DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm:ss")) + " до: " + toTermDate.format(DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm:ss")));

        verticalLayout.add(taskStatus, currentDate, createDate, termDate);
        verticalLayout.getStyle().set("font-size", "12px");
        horizontalLayout.add(verticalLayout, printBtn);
        horizontalLayout.setDefaultVerticalComponentAlignment(Alignment.END);
        printBtn.setWidth("100%");
        horizontalLayout.setWidthFull();
        printBtn.addClickListener(event -> printMethod());
        this.setHorizontalComponentAlignment(Alignment.CENTER, h3);
        add(h3, horizontalLayout, content);
        grid.setItems(taskService.getTasksByStatus(statusId, fromCreateDate, toCreateDate, fromTermDate, toTermDate));
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
