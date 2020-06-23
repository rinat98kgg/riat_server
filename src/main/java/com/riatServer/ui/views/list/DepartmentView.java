package com.riatServer.ui.views.list;

import com.riatServer.domain.Department;
import com.riatServer.service.DepartmentService;
import com.riatServer.ui.MainLayout;
import com.riatServer.ui.views.list.form.DepartmentForm;
import com.vaadin.flow.component.ClickEvent;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.renderer.LocalDateTimeRenderer;
import com.vaadin.flow.data.value.ValueChangeMode;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import org.springframework.security.access.annotation.Secured;

import java.util.Comparator;

@Route(value = "departments", layout = MainLayout.class)
@PageTitle("Отделы | RIAT")
@Secured("ROLE_USER")
public class DepartmentView extends VerticalLayout {
    private final DepartmentService departmentService;
    Grid<Department> grid = new Grid<>(Department.class);
    TextField filterText = new TextField();
    H2 title = new H2("Отделы");
    private final DepartmentForm departmentForm;
    Dialog dialog;

    private boolean isUpdating;

    public DepartmentView(DepartmentService departmentService) {
        this.departmentService = departmentService;
        addClassName("list-view");
        setSizeFull();
        configureGrid();

        departmentForm = new DepartmentForm();
        departmentForm.addListener(DepartmentForm.SaveEvent.class, this::saveDepartment);
        departmentForm.addListener(DepartmentForm.DeleteEvent.class, this::deleteDepartment);
        departmentForm.addListener(DepartmentForm.CloseEvent.class, e -> closeEditor());

        dialog = new Dialog();
        dialog.add(departmentForm);
        dialog.setSizeFull();

        Div content = new Div(grid);
        content.addClassName("content");
        content.setSizeFull();

        add(title, getToolbar(), content);
        updateList();
        closeEditor();
    }

    private void deleteDepartment(DepartmentForm.DeleteEvent event) {
        Notification.show("Данные отдела: " + event.getDepartment().getName() + " были удалены");
        departmentService.delete(event.getDepartment());
        updateList();
        closeEditor();
    }

    private void saveDepartment(DepartmentForm.SaveEvent event) {
        if(isUpdating){
            departmentService.save(event.getDepartment());
            Notification.show("Данные отдела: " + event.getDepartment().getName() + " были изменены");
        }
        else{
            departmentService.create(event.getDepartment());
            Notification.show("Добавлен новый отдел: " + event.getDepartment().getName());
        }
        updateList();
        closeEditor();
    }

    private void closeEditor() {
        departmentForm.setDepartment(null);
        removeClassName("editing");
        dialog.close();
    }

    private void configureGrid() {
        grid.addClassName("department-grid");
        grid.setSizeFull();
        grid.setColumns();

        grid.addColumn("name").setHeader("Название");
        grid.addColumn(new LocalDateTimeRenderer<>(Department::getCreateDate, "dd-MM-yyyy HH:mm:ss")).setHeader("Дата создания").setSortable(true).setComparator(Comparator.comparing(Department::getCreateDate));
        grid.addColumn(new LocalDateTimeRenderer<>(Department::getUpdateDate, "dd-MM-yyyy HH:mm:ss")).setHeader("Дата изменения").setSortable(true).setComparator(Comparator.comparing(Department::getCreateDate));

        grid.getColumns().forEach(col -> col.setAutoWidth(true));

        grid.asSingleSelect().addValueChangeListener(evt -> {
            departmentForm.createDate.setVisible(true);
            departmentForm.updateDate.setVisible(true);
            editDepartment(evt.getValue());
        });
    }

    private void editDepartment(Department department) {
        if (department == null) {
            closeEditor();
        } else {

            isUpdating = true;
            departmentForm.setDepartment(department);
            addClassName("editing");
            dialog.open();
        }
    }

    private HorizontalLayout getToolbar() {
        filterText.setPlaceholder("Поиск по названию");
        filterText.setClearButtonVisible(true);
        filterText.setValueChangeMode(ValueChangeMode.LAZY);
        filterText.addValueChangeListener(e -> updateList());

        Button addDepartmentButton = new Button("Добавить отдел");
        addDepartmentButton.addClickListener(click -> addDepartment());

        Button depStaff = new Button("Состав отделов");
        depStaff.addClickListener(( ClickEvent< Button > clickEvent ) -> {
            UI.getCurrent().navigate(DepartmentStaffView.class);
        });

        HorizontalLayout toolbar = new HorizontalLayout(filterText, addDepartmentButton, depStaff);
        toolbar.addClassName("toolbar");
        return toolbar;
    }

    private void addDepartment() {
        grid.asSingleSelect().clear();
        departmentForm.createDate.setVisible(false);
        departmentForm.updateDate.setVisible(false);
        editDepartment(new Department());
        isUpdating = false;
    }

    private void updateList() {
        grid.setItems(departmentService.getAll(filterText.getValue()));
    }
}
