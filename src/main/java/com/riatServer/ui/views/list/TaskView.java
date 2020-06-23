package com.riatServer.ui.views.list;

import com.riatServer.domain.ListOfEmployees;
import com.riatServer.domain.Task;
import com.riatServer.domain.User;
import com.riatServer.service.ListOfEmployeeService;
import com.riatServer.service.TaskService;
import com.riatServer.service.UserService;
import com.riatServer.ui.MainLayout;
import com.riatServer.ui.views.ConfirmDialogView;
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
import java.time.format.DateTimeFormatter;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Stream;

@Route(value = "tasks", layout = MainLayout.class)
@PageTitle("Задачи | RIAT")
@Secured("ROLE_USER")
public class TaskView extends VerticalLayout {
    private final TaskService taskService;
    private final UserService userService;
    private final ListOfEmployeeService listOfEmployeeService;
    TreeGrid<Task> grid = new TreeGrid<>(Task.class);
    TextField filterText = new TextField();
    H2 title = new H2("Задачи");
    private final TaskForm taskForm;
    Dialog dialog1;
    Dialog dialog2;
    Button instructButton = new Button("Поручить задачу");
    ComboBox<User> user_id =  new ComboBox<>("Сотрудник");

    Button save = new Button("Поручить");
    Button close = new Button("Отменить");
    H4 h4 = new H4("Поручение задачи");


    private boolean isUpdating;
    VerticalLayout layout = new VerticalLayout();
    HorizontalLayout btnLayout = new HorizontalLayout();
    ConfirmDialogView confirmDialogView = new ConfirmDialogView();
    ConfirmDialog confirmDialog;

    public TaskView(TaskService taskService, UserService userService, ListOfEmployeeService listOfEmployeeService) {
        this.taskService = taskService;
        this.userService = userService;
        this.listOfEmployeeService = listOfEmployeeService;
        addClassName("list-view");
        setSizeFull();
        configureGrid();


        taskForm = new TaskForm(taskService);
        taskForm.addListener(TaskForm.SaveEvent.class, this::saveTask);
        taskForm.addListener(TaskForm.DeleteEvent.class, this::deleteTask);
        taskForm.addListener(TaskForm.CloseEvent.class, e -> closeEditor());

        dialog1 = new Dialog();
        dialog1.add(taskForm, instructButton);
        dialog1.setSizeFull();



        save.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        close.addThemeVariants(ButtonVariant.LUMO_TERTIARY);
        instructButton.addThemeVariants(ButtonVariant.LUMO_PRIMARY, ButtonVariant.LUMO_CONTRAST);
        instructButton.setVisible(false);

        dialog2 = new Dialog(layout);
        dialog2.setWidth("100mm");

        btnLayout.add(save, close);
        layout.add(h4, user_id, btnLayout);
        layout.setDefaultHorizontalComponentAlignment(Alignment.CENTER);
        user_id.setWidthFull();
        layout.setWidth("100mm");


        save.addClickListener(event -> {
            if(user_id.getValue() == null) {
                user_id.setInvalid(true);
                user_id.setErrorMessage("Выберите сотрудника!");

            }
            else {
                if(checkIsUnique()){
                    ListOfEmployees list = new ListOfEmployees();
                    list.setUser_id(user_id.getValue());
                    list.setUserId(user_id.getValue().getId());
                    list.setTaskId(grid.asSingleSelect().getValue().getId());
                    list.setTask_id(grid.asSingleSelect().getValue());
                    listOfEmployeeService.create2(list);
                    Notification.show("Задача \"" + grid.asSingleSelect().getValue().getName() + "\" была поручена сотруднику \"" + user_id.getValue().getFullName() + "\"");
                    dialog2.close();
                    dialog1.close();
                }
            }
        });

        close.addClickListener(event -> {
            dialog2.close();
        });

        instructButton.addClickListener(event -> {
            if(grid.asSingleSelect().getValue() != null){
                user_id.setItems(userService.getAll());
                user_id.setItemLabelGenerator(User::getFullName);
                user_id.setAllowCustomValue(false);
                user_id.setInvalid(false);
                dialog2.open();
            }
        });

        Div content = new Div(grid);
        content.addClassName("content");
        content.setSizeFull();

        add(title, getToolbar(), content);
        updateList();
        closeEditor();
    }

    private void deleteTask(TaskForm.DeleteEvent event) {
        Notification.show("Информация о задаче \"" + event.getTask().getName() + "\" была удалена");
        taskService.delete(event.getTask());
        updateList();
        closeEditor();
    }

    private void saveTask(TaskForm.SaveEvent event) {
        if(isUpdating){
            taskService.save(event.getTask());
            Notification.show("Информация о задаче \"" + event.getTask().getName() + "\" была изменена");
        }
        else{
            taskService.create(event.getTask());
            Notification.show("Добавлена новая задача \"" + event.getTask().getName() + "\"");
        }
        updateList();
        closeEditor();
    }

    private void closeEditor() {
        taskForm.setTask(null);
        removeClassName("editing");
        dialog1.close();
    }

    private void configureGrid() {
        grid.setWidthFull();
        grid.setMinHeight("100%");
        grid.setColumns();

        grid.addHierarchyColumn(Task::getName).setHeader("Название");
        grid.addColumn(Task::getDescription).setHeader("Описание").setSortable(true);
        grid.addColumn(Task::isTemplateTask).setHeader("Шаблонная задача").setSortable(true);
//        grid.addColumn(new NumberRenderer<>(Task::getProcent, NumberFormat.getIntegerInstance())).setHeader("Выполнено (в %)").setSortable(true);
        grid.addColumn(new LocalDateTimeRenderer<>(Task::getTermDate, "dd-MM-yyyy HH:mm:ss")).setHeader("Срок").setSortable(true).setComparator(Comparator.comparing(Task::getCreateDate));
        grid.addColumn(new LocalDateTimeRenderer<>(Task::getCreateDate, "dd-MM-yyyy HH:mm:ss")).setHeader("Дата создания")
                .setSortable(true).setComparator(Comparator.comparing(Task::getCreateDate));
        grid.addColumn(new LocalDateTimeRenderer<>(Task::getUpdateDate, "dd-MM-yyyy HH:mm:ss")).setHeader("Дата изменения").setSortable(true).setComparator(Comparator.comparing(Task::getCreateDate));
        grid.addThemeNames("wrap-cell-content");
        grid.getColumns().forEach(col -> col.setAutoWidth(true));

        grid.asSingleSelect().addValueChangeListener(evt -> {
            taskForm.createDate.setVisible(true);
            taskForm.updateDate.setVisible(true);
            instructButton.setVisible(true);
            taskForm.delete.setVisible(true);
            taskForm.isChanged = false;
            editTask(evt.getValue(), false);
        });
    }

    private void editTask(Task task, boolean isAdd) {
        if (task == null) {
            closeEditor();
        } else {
            isUpdating = true;

            if(isAdd) task.setTermDate(LocalDateTime.now().plusMinutes(30));
            taskForm.setTask(task);
            addClassName("editing");
            dialog1.open();
        }
    }

    private HorizontalLayout getToolbar() {
        filterText.setPlaceholder("Поиск по названию");
        filterText.setClearButtonVisible(true);
        filterText.setValueChangeMode(ValueChangeMode.LAZY);
        filterText.addValueChangeListener(e -> updateList());

        Button addTaskButton = new Button("Добавить задачу");
        addTaskButton.addClickListener(click -> {
            addTask();
        });

        HorizontalLayout toolbar = new HorizontalLayout(filterText, addTaskButton);
        toolbar.addClassName("toolbar");
        return toolbar;
    }

    private void addTask() {
        grid.asSingleSelect().clear();
        taskForm.createDate.setVisible(false);
        taskForm.updateDate.setVisible(false);
        instructButton.setVisible(false);
        taskForm.delete.setVisible(false);
        editTask(new Task(), true);
        isUpdating = false;
    }

    private void updateList() {
        grid.setItems(taskService.getRootTasks(filterText.getValue()), task -> taskService.getChildTasks(task, filterText.getValue()));
        final Stream<Task> rootNodes = grid.getDataProvider().fetchChildren(new HierarchicalQuery<>(null, null));
        grid.expandRecursively(rootNodes, 10);
    }

    private boolean checkIsUnique(){
        List<ListOfEmployees> list = listOfEmployeeService.checkUniqueInstruct(grid.asSingleSelect().getValue().getId(), user_id.getValue().getId());

        if(!list.isEmpty())
        {
            confirmDialogView.alertDialog("Уведомление об ошибке", "Ошибка, эта задача уже назначена выбранному сотруднику!");
            return false;
        }
        else{
            return true;
        }
    }
}
