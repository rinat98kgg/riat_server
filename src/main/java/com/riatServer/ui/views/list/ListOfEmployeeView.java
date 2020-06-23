package com.riatServer.ui.views.list;

import com.riatServer.domain.*;
import com.riatServer.service.ListOfEmployeeService;
import com.riatServer.service.TaskService;
import com.riatServer.service.TaskStatusService;
import com.riatServer.service.UserService;
import com.riatServer.ui.MainLayout;
import com.riatServer.ui.views.list.form.ListOfEmployeeForm;
import com.riatServer.ui.views.list.form.TaskForm;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.html.H4;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.component.treegrid.TreeGrid;
import com.vaadin.flow.data.provider.hierarchy.HierarchicalQuery;
import com.vaadin.flow.data.renderer.LocalDateTimeRenderer;
import com.vaadin.flow.data.value.ValueChangeMode;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import org.springframework.security.access.annotation.Secured;

import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.stream.Stream;

@Route(value = "list-of-employee", layout = MainLayout.class)
@PageTitle("Задачи сотрудников | RIAT")
@Secured("ROLE_USER")
public class ListOfEmployeeView extends VerticalLayout {
    private final ListOfEmployeeService listOfEmployeeService;
    private final TaskService taskService;
    //    Grid<ListOfEmployees> grid = new Grid<>(ListOfEmployees.class);
    TreeGrid<ListOfEmployees> grid = new TreeGrid<>(ListOfEmployees.class);
    TextField filterText = new TextField();
    H2 title = new H2("Задачи сотрудников");
    private ListOfEmployeeForm listOfEmployeeForm;
    private final TaskForm taskForm;
    Button addSubTask = new Button("Добавить подзадачу");
    Dialog dialog1;
    Dialog dialog2;
    H4 h4 = new H4("Добавление подзадачи к задаче");
    VerticalLayout subTaskLayout = new VerticalLayout();

    private boolean isUpdating;

    public ListOfEmployeeView(ListOfEmployeeService listOfEmployeeService, TaskService taskService,
                              UserService userService, TaskStatusService taskStatusService) {
        this.listOfEmployeeService = listOfEmployeeService;
        this.taskService = taskService;

        addClassName("list-view");
        setSizeFull();
        configureGrid();

        taskForm = new TaskForm(taskService);
        taskForm.addListener(TaskForm.SaveEvent.class, this::saveTask);
        taskForm.addListener(TaskForm.CloseEvent.class, e -> closeTaskEditor());

        listOfEmployeeForm = new ListOfEmployeeForm(taskService.getAll(), userService.getAll(), taskStatusService.getAll(), taskService);
        listOfEmployeeForm.addListener(ListOfEmployeeForm.SaveEvent.class, this::saveListOfEmployee);
        listOfEmployeeForm.addListener(ListOfEmployeeForm.DeleteEvent.class, this::deleteListOfEmployee);
        listOfEmployeeForm.addListener(ListOfEmployeeForm.CloseEvent.class, e -> closeEditor());

        dialog1 = new Dialog();
        dialog1.add(listOfEmployeeForm, addSubTask);
        dialog1.setSizeFull();

        subTaskLayout.add(h4, taskForm);
        subTaskLayout.setDefaultHorizontalComponentAlignment(Alignment.CENTER);
        dialog2 = new Dialog();
        dialog2.add(subTaskLayout);
        dialog2.setSizeFull();

        addSubTask.addThemeVariants(ButtonVariant.LUMO_PRIMARY, ButtonVariant.LUMO_CONTRAST);

        addSubTask.addClickListener(event -> {
            taskForm.createDate.setVisible(false);
            taskForm.updateDate.setVisible(false);
            editTask(new Task());
        });

        Div content = new Div(grid);
        content.addClassName("content");
        content.setSizeFull();

        add(title, getToolbar(), content);
        updateList();
        closeEditor();


    }

    private void deleteListOfEmployee(ListOfEmployeeForm.DeleteEvent event) {
        listOfEmployeeService.delete(event.getListOfEmployee());
        updateList();
        closeEditor();
    }

    private void saveListOfEmployee(ListOfEmployeeForm.SaveEvent event) {
        if (isUpdating) {
            listOfEmployeeService.save(event.getListOfEmployee());
//            Notification.show("Данные пользователя с логином: " + event.getListOfEmployee().getName() + " были изменены");
        } else {
            listOfEmployeeService.create2(event.getListOfEmployee());
//            Notification.show("Добавлен новый пользователь с логином: " + event.getListOfEmployee().getName());
        }
        updateList();
        closeEditor();
    }

    private void closeEditor() {
        listOfEmployeeForm.setListOfEmployee(null);
        removeClassName("editing");
        dialog1.close();
    }


    private void configureGrid() {
        grid.addClassName("listOfEmployee-grid");

        grid.setSizeFull();
        grid.setColumns();

//        grid.addColumn(listOfEmployees -> {
//            User user = listOfEmployees.getUser_id();
//
//            return user == null ? "-" : user.getFullName();
//        }).setHeader("Сотрудник").setSortable(true);

        grid.addHierarchyColumn(listOfEmployees -> {
            Task task = listOfEmployees.getTask_id();

            return task == null ? "-" : task.getName();
        }).setHeader("Задача").setSortable(true);

        grid.addColumn(listOfEmployees -> {
            User user = listOfEmployees.getUser_id();

            return user == null ? "-" : user.getFullName();
        }).setHeader("Сотрудник").setSortable(true);



        grid.addColumn(listOfEmployees -> {
            User user = listOfEmployees.getOwner_id();

            return user == null ? "-" : user.getFullName();
        }).setHeader("Поручатель").setSortable(true);

        grid.addColumn(ListOfEmployees::isActive).setHeader("Активно").setSortable(true);

        grid.addColumn(listOfEmployees -> {
            TaskStatus taskStatus = listOfEmployees.getTaskStatus_id();

            return taskStatus == null ? "-" : taskStatus.getName();
        }).setHeader("Статус задачи").setSortable(true);

        grid.addColumn(new LocalDateTimeRenderer<>(ListOfEmployees::getCreateDate, "dd-MM-yyyy HH:mm:ss")).setHeader("Дата создания").setSortable(true).setComparator(Comparator.comparing(ListOfEmployees::getCreateDate));
        grid.addColumn(new LocalDateTimeRenderer<>(ListOfEmployees::getUpdateDate, "dd-MM-yyyy HH:mm:ss")).setHeader("Дата изменения").setSortable(true).setComparator(Comparator.comparing(ListOfEmployees::getCreateDate));
        grid.addThemeNames("wrap-cell-content");
        grid.getColumns().forEach(col -> col.setAutoWidth(true));

        grid.asSingleSelect().addValueChangeListener(evt -> {
            listOfEmployeeForm.createDate.setVisible(true);
            listOfEmployeeForm.updateDate.setVisible(true);
            editListOfEmployee(evt.getValue());
//            listOfEmployeeForm = new ListOfEmployeeForm(taskService.getAll(), userService.getAll(), taskStatusService.getAll(), taskService, );
        });
    }

    private void editListOfEmployee(ListOfEmployees listOfEmployee) {
        if (listOfEmployee == null) {
            closeEditor();
        } else {
            isUpdating = true;
            listOfEmployeeForm.setListOfEmployee(listOfEmployee);
            addClassName("editing");
            dialog1.open();
        }
    }

    private HorizontalLayout getToolbar() {
        filterText.setPlaceholder("Поиск по задаче");
        filterText.setClearButtonVisible(true);
        filterText.setValueChangeMode(ValueChangeMode.LAZY);
        filterText.addValueChangeListener(e -> updateList());
        filterText.setWidthFull();

//        Button addListOfEmployeeButton = new Button("Поручить задачу");
//        addListOfEmployeeButton.addClickListener(click -> addListOfEmployee());

        HorizontalLayout toolbar = new HorizontalLayout(filterText);
        toolbar.addClassName("toolbar");
        return toolbar;
    }

//    private void addListOfEmployee() {
//        grid.asSingleSelect().clear();
//        listOfEmployeeForm.createDate.setVisible(false);
//        listOfEmployeeForm.updateDate.setVisible(false);
//        editListOfEmployee(new ListOfEmployees());
//        isUpdating = false;
//    }

    private void updateList() {
//        grid.setItems(listOfEmployeeService.getAll(filterText.getValue()));
        grid.setItems(listOfEmployeeService.getRootTasks(filterText.getValue()), listOfEmployees -> listOfEmployeeService.getChildTasks(listOfEmployees, filterText.getValue()));
        //grid.sort(SortDirection.ASCENDING);
        final Stream<ListOfEmployees> rootNodes = grid.getDataProvider().fetchChildren(new HierarchicalQuery<>(null, null));
        grid.expandRecursively(rootNodes, 10);
    }

    private void editTask(Task task) {
        if (task == null) {
            closeTaskEditor();
        } else {
            task.setTermDate(LocalDateTime.now().plusMinutes(30));
            taskForm.setTask(task);
            taskForm.delete.setVisible(false);
            taskForm.save.setText("Добавить");
            addClassName("editing");
            dialog2.open();
        }
    }

    private void saveTask(TaskForm.SaveEvent event) {
        taskService.addCreatedSubTaskToTaskAndInstructToUserThis(event.getTask(), grid.asSingleSelect().getValue());
        Notification.show("Добавлена подзадача \"" + event.getTask().getName() + "\" к задаче \"" + taskService.getById(grid.asSingleSelect().getValue().getTaskId()).getName() + "\"");
        updateList();
        closeTaskEditor();
        dialog2.close();
    }

    private void closeTaskEditor() {
        taskForm.setTask(null);
        removeClassName("editing");
        dialog2.close();
    }
}
