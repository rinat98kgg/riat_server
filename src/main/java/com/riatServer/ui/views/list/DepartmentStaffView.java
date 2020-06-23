package com.riatServer.ui.views.list;

import com.riatServer.domain.Department;
import com.riatServer.domain.DepartmentStaff;
import com.riatServer.domain.User;
import com.riatServer.service.DepartmentService;
import com.riatServer.service.DepartmentStaffService;
import com.riatServer.service.UserService;
import com.riatServer.ui.MainLayout;
import com.riatServer.ui.views.list.form.DepartmentStaffForm;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.combobox.ComboBox;
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

@Route(value = "department-staff", layout = MainLayout.class)
@PageTitle("Состав отделов | RIAT")
@Secured("ROLE_USER")
public class DepartmentStaffView extends VerticalLayout {
    private final DepartmentStaffService departmentStaffService;
    Grid<DepartmentStaff> grid = new Grid<>(DepartmentStaff.class);
    TextField filterText = new TextField();
    ComboBox<Department> departmentComboBox = new ComboBox<>();
    H2 title = new H2("Состав отделов");
    private final DepartmentStaffForm departmentStaffForm;
    Dialog dialog;

    private boolean isUpdating;

    public DepartmentStaffView(DepartmentStaffService departmentStaffService,
                               DepartmentService departmentService, UserService userService) {
        this.departmentStaffService = departmentStaffService;
        addClassName("list-view");
        setSizeFull();
        configureGrid();

        departmentStaffForm = new DepartmentStaffForm(departmentService.getAll(), userService.getAll(), departmentStaffService, userService);
        departmentStaffForm.addListener(DepartmentStaffForm.SaveEvent.class, this::saveDepartmentStaff);
        departmentStaffForm.addListener(DepartmentStaffForm.DeleteEvent.class, this::deleteDepartmentStaff);
        departmentStaffForm.addListener(DepartmentStaffForm.CloseEvent.class, e -> closeEditor());

        dialog = new Dialog();
        dialog.add(departmentStaffForm);
        dialog.setSizeFull();

        Div content = new Div(grid);
        content.addClassName("content");
        content.setSizeFull();

        departmentComboBox.setItems(departmentService.getAll());
        departmentComboBox.setItemLabelGenerator(Department::getName);

        departmentComboBox.setPlaceholder("Выберите отдел...");
        departmentComboBox.setAllowCustomValue(false);
        departmentComboBox.setClearButtonVisible(true);
        departmentComboBox.setWidth("100mm");

        add(title, getToolbar(), content);
        updateList();
        closeEditor();


    }

    private void deleteDepartmentStaff(DepartmentStaffForm.DeleteEvent event) {
        departmentStaffService.delete(event.getDepartmentStaff());
        updateList();
        closeEditor();
    }

    private void saveDepartmentStaff(DepartmentStaffForm.SaveEvent event) {
        if (isUpdating) {
            departmentStaffService.save(event.getDepartmentStaff());
            Notification.show("К отделу \"" + event.getDepartmentStaff().getDepartment_id().getName() + "\" был добавлен сотрудник \"" + event.getDepartmentStaff().getUser_id().getFullName() +"\"");
        } else {
            departmentStaffService.create2(event.getDepartmentStaff());
            Notification.show("К отделу \"" + event.getDepartmentStaff().getDepartment_id().getName() + "\" был добавлен сотрудник \"" + event.getDepartmentStaff().getUser_id().getFullName() +"\"");
        }
        updateList();
        closeEditor();
    }

    private void closeEditor() {
        departmentStaffForm.setDepartmentStaff(null);
        removeClassName("editing");
        dialog.close();
    }

    private void configureGrid() {
        grid.addClassName("departmentStaff-grid");
        grid.setSizeFull();
        grid.setColumns();

        grid.addColumn(departmentStaff -> {
            Department department = departmentStaff.getDepartment_id();

            return department == null ? "-" : department.getName();
        }).setHeader("Отдел").setSortable(true);

        grid.addColumn(departmentStaff -> {
            User user = departmentStaff.getUser_id();

            return user == null ? "-" : user.getFullName();
        }).setHeader("Сотрудник").setSortable(true);

        grid.addColumn(new LocalDateTimeRenderer<>(DepartmentStaff::getCreateDate, "dd-MM-yyyy HH:mm:ss")).setHeader("Дата создания").setSortable(true).setComparator(Comparator.comparing(DepartmentStaff::getCreateDate));
        grid.addColumn(new LocalDateTimeRenderer<>(DepartmentStaff::getUpdateDate, "dd-MM-yyyy HH:mm:ss")).setHeader("Дата изменения").setSortable(true).setComparator(Comparator.comparing(DepartmentStaff::getCreateDate));

        grid.getColumns().forEach(col -> col.setAutoWidth(true));

        grid.asSingleSelect().addValueChangeListener(
                evt -> {
                    departmentStaffForm.createDate.setVisible(true);
                    departmentStaffForm.updateDate.setVisible(true);
                    editDepartmentStaff(evt.getValue());
                }
        );
    }

    private void editDepartmentStaff(DepartmentStaff departmentStaff) {
        if (departmentStaff == null) {
            closeEditor();
        } else {
            isUpdating = true;
            departmentStaffForm.setDepartmentStaff(departmentStaff);
            dialog.open();
            addClassName("editing");
        }
    }

    private HorizontalLayout getToolbar() {
        filterText.setPlaceholder("Поиск по названию");
        filterText.setClearButtonVisible(true);
        filterText.setValueChangeMode(ValueChangeMode.LAZY);
        filterText.addValueChangeListener(e -> updateList());

        Button addDepartmentStaffButton = new Button("Добавить сотрудника в отдел");
        addDepartmentStaffButton.addClickListener(click -> addDepartmentStaff());

        departmentComboBox.addValueChangeListener(comboBoxDepartmentComponentValueChangeEvent -> {

            if (comboBoxDepartmentComponentValueChangeEvent.getValue() == null) {
                updateList();
            } else {
                grid.setItems(departmentStaffService.getAll(departmentComboBox.getValue().getName()));
            }
        });

        HorizontalLayout toolbar = new HorizontalLayout(filterText, departmentComboBox, addDepartmentStaffButton);
        toolbar.addClassName("toolbar");
        return toolbar;
    }

    private void addDepartmentStaff() {
        grid.asSingleSelect().clear();
        departmentStaffForm.createDate.setVisible(false);
        departmentStaffForm.updateDate.setVisible(false);
        editDepartmentStaff(new DepartmentStaff());
        isUpdating = false;
    }

    private void updateList() {
        grid.setItems(departmentStaffService.getAll(filterText.getValue()));
    }
}
