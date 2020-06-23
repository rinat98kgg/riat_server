package com.riatServer.ui.views.list;

import com.riatServer.domain.*;
import com.riatServer.service.ListOfEmployeeService;
import com.riatServer.service.TaskService;
import com.riatServer.service.TaskStatusService;
import com.riatServer.service.UserService;
import com.riatServer.ui.MainLayout;
import com.riatServer.ui.views.ConfirmDialogView;
import com.riatServer.ui.views.list.form.ListOfEmployeeForm;
import com.riatServer.ui.views.list.form.TaskForm;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.confirmdialog.ConfirmDialog;
import com.vaadin.flow.component.dialog.Dialog;
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

@Route(value = "", layout = MainLayout.class)
@PageTitle("Мои задачи | RIAT")
@Secured("ROLE_USER")
public class MyTasksView extends VerticalLayout {
    private final ListOfEmployeeService listOfEmployeeService;
    private final TaskService taskService;
    TreeGrid<ListOfEmployees> grid = new TreeGrid<>(ListOfEmployees.class);
    TextField filterText = new TextField();
    H2 title = new H2("Мои задачи");
    private ListOfEmployeeForm listOfEmployeeForm;
    private final TaskForm taskForm;

    H4 h4 = new H4("Добавление подзадачи к задаче");
    VerticalLayout subTaskLayout = new VerticalLayout();

    Button addSubTask = new Button("Добавить подзадачу");
    Button endBtn = new Button("Завершить задачу");
    Button close = new Button("Отменить");
    ComboBox<TaskStatus> statusComboBox = new ComboBox<>();

    Dialog dialog1;
    Dialog dialog2;

    Button show = new Button("Показать");
    Button clear = new Button("Сбросить");

    ConfirmDialogView confirmDialogView = new ConfirmDialogView();

    ConfirmDialog dialog;


    HorizontalLayout btnLayout = new HorizontalLayout();

    private boolean isUpdating;

    public MyTasksView(ListOfEmployeeService listOfEmployeeService, TaskService taskService,
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
        btnLayout.add(endBtn, addSubTask, close);
        btnLayout.setWidth("180mm");
        btnLayout.setDefaultVerticalComponentAlignment(Alignment.CENTER);
        dialog1.add(listOfEmployeeForm, btnLayout);
        dialog1.setSizeFull();

        dialog = new ConfirmDialog();

        subTaskLayout.add(h4, taskForm);
        subTaskLayout.setDefaultHorizontalComponentAlignment(Alignment.CENTER);
        dialog2 = new Dialog();
        dialog2.add(subTaskLayout);
        dialog2.setSizeFull();

        addSubTask.addThemeVariants(ButtonVariant.LUMO_PRIMARY, ButtonVariant.LUMO_CONTRAST);
        endBtn.addThemeVariants(ButtonVariant.LUMO_PRIMARY, ButtonVariant.LUMO_SUCCESS);
        close.addThemeVariants(ButtonVariant.LUMO_TERTIARY);
        show.addThemeVariants(ButtonVariant.LUMO_PRIMARY, ButtonVariant.LUMO_PRIMARY);
        clear.addThemeVariants(ButtonVariant.LUMO_PRIMARY, ButtonVariant.LUMO_PRIMARY);

        statusComboBox.setItems(taskStatusService.getAll());
        statusComboBox.setItemLabelGenerator(TaskStatus::getName);

        statusComboBox.setPlaceholder("Выберите статус задач...");
        statusComboBox.setAllowCustomValue(false);
        statusComboBox.setClearButtonVisible(true);
        statusComboBox.setWidth("100mm");

        addSubTask.addClickListener(event -> {
            taskForm.createDate.setVisible(false);
            taskForm.updateDate.setVisible(false);
            editTask(new Task());
        });

        endBtn.addClickListener(event -> {
            dialog = confirmDialogView.editableConfirmDialog("Подвердите действие", "Вы действиетльно хотите завершить задачу!");
            dialog.addConfirmListener(evt -> {
                listOfEmployeeForm.active.setValue(false);
                TaskStatus taskStatus = taskStatusService.getByName("Завершено");
                listOfEmployeeForm.taskStatus_id.setValue(taskStatus);
                Notification.show("Задача \"" + grid.asSingleSelect().getValue().getTask_id().getName() + "\" была завершена!");
                listOfEmployeeForm.save.click();
            });
        });

        close.addClickListener(event -> {
            listOfEmployeeForm.close.click();
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

//        grid.addColumn(listOfEmployees -> {
//            User user = listOfEmployees.getUser_id();
//
//            return user == null ? "-" : user.getFullName();
//        }).setHeader("Сотрудник").setSortable(true);

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
        });
    }

    private void editListOfEmployee(ListOfEmployees listOfEmployee) {
        if (listOfEmployee == null) {
            closeEditor();
        } else {
            isUpdating = true;
            listOfEmployeeForm.setListOfEmployee(listOfEmployee);
            listOfEmployeeForm.task_id.setReadOnly(true);
            listOfEmployeeForm.description.setReadOnly(true);
            listOfEmployeeForm.user_id.setReadOnly(true);
            listOfEmployeeForm.owner_id.setReadOnly(true);
            listOfEmployeeForm.taskStatus_id.setReadOnly(true);
            listOfEmployeeForm.active.setReadOnly(true);
            listOfEmployeeForm.termDate.setReadOnly(true);

            listOfEmployeeForm.taskCreateDate.setReadOnly(true);
            listOfEmployeeForm.createDate.setReadOnly(true);
            listOfEmployeeForm.updateDate.setReadOnly(true);
            listOfEmployeeForm.save.setVisible(false);
            listOfEmployeeForm.delete.setVisible(false);
            listOfEmployeeForm.close.setVisible(false);
            if(listOfEmployee.getTaskStatus_id().getName().equals("Завершено") || listOfEmployee.getTaskStatus_id().getName().equals("Не выполнено")){
                endBtn.setEnabled(false);
            }
            else endBtn.setEnabled(true);
            addClassName("editing");
            dialog1.open();
        }
    }

    private HorizontalLayout getToolbar() {
        filterText.setPlaceholder("Поиск по задаче");
        filterText.setClearButtonVisible(true);
        filterText.setValueChangeMode(ValueChangeMode.LAZY);
        filterText.addValueChangeListener(e -> {
            statusComboBox.clear();
            updateList();
        });
        filterText.setWidth("100mm");
//        filterText.setWidthFull();

        show.addClickListener(event -> updateWithFilters(statusComboBox.getValue()));

        clear.addClickListener(event -> {
            filterText.clear();
            statusComboBox.clear();
            updateList();
        });

        HorizontalLayout toolbar = new HorizontalLayout(filterText, statusComboBox, show, clear);
        toolbar.addClassName("toolbar");
        return toolbar;
    }

    private void updateWithFilters(TaskStatus taskStatus){
        if(taskStatus != null){
            grid.setItems(listOfEmployeeService.getRootTasksForCurrentUserByTaskStatus(statusComboBox.getValue()), listOfEmployees -> listOfEmployeeService.getChildTasksForCurrentUserByTaskStatus(listOfEmployees, statusComboBox.getValue()));
            final Stream<ListOfEmployees> rootNodes = grid.getDataProvider().fetchChildren(new HierarchicalQuery<>(null, null));
            grid.expandRecursively(rootNodes, 10);
        }
        else {
            updateList();
        }
    }

    private void updateList() {
        grid.setItems(listOfEmployeeService.getRootTasksForCurrentUser(filterText.getValue()), listOfEmployees -> listOfEmployeeService.getChildTasksForCurrentUser(listOfEmployees, filterText.getValue()));
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
