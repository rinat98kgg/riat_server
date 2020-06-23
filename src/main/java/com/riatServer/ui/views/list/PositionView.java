package com.riatServer.ui.views.list;

import com.riatServer.domain.Position;
import com.riatServer.service.PositionService;
import com.riatServer.ui.MainLayout;
import com.riatServer.ui.views.list.form.PositionForm;
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

import java.time.LocalDateTime;
import java.util.Comparator;

@Route(value = "positions", layout = MainLayout.class)
@PageTitle("Должности | RIAT")
@Secured("ROLE_ADMIN")
public class PositionView extends VerticalLayout {
    private final PositionService positionService;
    Grid<Position> grid = new Grid<>(Position.class);
    TextField filterText = new TextField();
    H2 title = new H2("Должности");
    private final PositionForm positionForm;
    Dialog dialog;

    private boolean isUpdating;

    public PositionView(PositionService positionService) {
        this.positionService = positionService;
        addClassName("list-view");
        setSizeFull();
        configureGrid();

        positionForm = new PositionForm();
        positionForm.addListener(PositionForm.SaveEvent.class, this::savePosition);
        positionForm.addListener(PositionForm.DeleteEvent.class, this::deletePosition);
        positionForm.addListener(PositionForm.CloseEvent.class, e -> closeEditor());


        dialog = new Dialog();
        dialog.add(positionForm);
        dialog.setSizeFull();

        Div content = new Div(grid);
        content.addClassName("content");
        content.setSizeFull();

        add(title, getToolbar(), content);
        updateList();
        closeEditor();
    }

    private void deletePosition(PositionForm.DeleteEvent event) {
        positionService.delete(event.getPosition());



        updateList();
        closeEditor();
    }

    private void savePosition(PositionForm.SaveEvent event) {
        if(isUpdating){
            positionService.save(event.getPosition());
        }
        else positionService.create(event.getPosition());
        updateList();
        closeEditor();
    }

    private void closeEditor() {
        positionForm.setPosition(null);
        removeClassName("editing");
        dialog.close();
    }

    private void updateList() {
        grid.setItems(positionService.getAll(filterText.getValue()));
    }

    private HorizontalLayout getToolbar() {
        filterText.setPlaceholder("Поиск по должности");
        filterText.setClearButtonVisible(true);
        filterText.setValueChangeMode(ValueChangeMode.LAZY);
        filterText.addValueChangeListener(e -> updateList());

        Button addPositionButton = new Button("Добавить сотрудника");
        addPositionButton.addClickListener(click -> addPosition());

        setWidthFull();
        HorizontalLayout toolbar = new HorizontalLayout(title, filterText, addPositionButton);
        toolbar.addClassName("toolbar");
        return toolbar;
    }

    private void addPosition() {
        grid.asSingleSelect().clear();
        positionForm.createDate.setVisible(false);
        positionForm.updateDate.setVisible(false);
        editPosition(new Position());
        isUpdating = false;
    }

    private void configureGrid() {
        grid.addClassName("position-grid");
        grid.setSizeFull();
        //grid.removeColumnByKey("position_id");
        grid.setColumns();
        grid.addColumn("name").setHeader("Должность");

        grid.addColumn(new LocalDateTimeRenderer<>(Position::getCreateDate, "dd-MM-yyyy HH:mm:ss")).setHeader("Дата создания").setSortable(true).setComparator(Comparator.comparing(Position::getCreateDate));
        grid.addColumn(new LocalDateTimeRenderer<>(Position::getUpdateDate, "dd-MM-yyyy HH:mm:ss")).setHeader("Дата изменения").setSortable(true).setComparator(Comparator.comparing(Position::getUpdateDate));

//        Grid.Column<Position> colum3 = grid.addColumn(
//                new LocalDateTimeRenderer<>(Position::getCreateDate, DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm:ss")));
//        colum3.setSortable(true);
//        colum3.setHeader("Renderer");
//        colum3.setComparator(Comparator.comparing(Position::getCreateDate));

        grid.getColumns().forEach(col -> col.setAutoWidth(true));

        grid.asSingleSelect().addValueChangeListener(evt -> {
            positionForm.createDate.setVisible(true);
            positionForm.updateDate.setVisible(true);
            editPosition(evt.getValue());
        });
    }


    private void editPosition(Position position) {
        if (position == null) {
            closeEditor();
        } else {
            isUpdating = true;
            positionForm.setPosition(position);
            addClassName("editing");
            dialog.open();
        }
    }
}