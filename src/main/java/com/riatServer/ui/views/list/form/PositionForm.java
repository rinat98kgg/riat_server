package com.riatServer.ui.views.list.form;

import com.riatServer.domain.Position;
import com.riatServer.ui.views.LocalDateTimeField;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.ComponentEvent;
import com.vaadin.flow.component.ComponentEventListener;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.binder.BeanValidationBinder;
import com.vaadin.flow.data.binder.Binder;
import com.vaadin.flow.shared.Registration;

public class PositionForm extends FormLayout {

    TextField name = new TextField("Должность");
    public LocalDateTimeField createDate = new LocalDateTimeField("Дата создания");
    public LocalDateTimeField updateDate = new LocalDateTimeField("Дата обновления");



    Button save = new Button("Сохранить");
    Button delete = new Button("Удалить");
    Button close = new Button("Отменить");

    Binder<Position> binder = new BeanValidationBinder<>(Position.class);

    public PositionForm() {
        addClassName("input-form");
        binder.bindInstanceFields(this);

        createDate.setReadOnly(true);
        updateDate.setReadOnly(true);


//        binder.forField(createDate)
//                .withConverter(new LocalDateTime())
//                .bind("createDate");
//        binder.forField(createDate).withConverter(new));
//        binder.forField(updateDate).withConverter(new StringToDateConverter());
        HorizontalLayout horizontalLayout = new HorizontalLayout(createDate, updateDate);
        VerticalLayout verticalLayout = new VerticalLayout(name, horizontalLayout, createButtonsLayout());

        this.setWidth("140mm");

        add(
                verticalLayout
        );

    }

    public void setPosition(Position position) {
        binder.setBean(position);
    }

    private Component createButtonsLayout() {
        save.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        delete.addThemeVariants(ButtonVariant.LUMO_ERROR);
        close.addThemeVariants(ButtonVariant.LUMO_TERTIARY);

        save.addClickListener(click -> validateAndSave());
        delete.addClickListener(click -> fireEvent(new DeleteEvent(this, binder.getBean())));
        close.addClickListener(click -> fireEvent(new CloseEvent(this)));

        binder.addStatusChangeListener(evt -> save.setEnabled(binder.isValid()));

        return new HorizontalLayout(save, delete, close);
    }

    private void validateAndSave() {
        if (binder.isValid()) {
            fireEvent(new SaveEvent(this, binder.getBean()));
        }
    }

    // Events
    public static abstract class PositionFormEvent extends ComponentEvent<PositionForm> {
        private Position position;

        protected PositionFormEvent(PositionForm source, Position position) {
            super(source, false);
            this.position = position;
        }

        public Position getPosition() {
            return position;
        }
    }

    public static class SaveEvent extends PositionForm.PositionFormEvent {
        SaveEvent(PositionForm source, Position position) {
            super(source, position);
        }
    }

    public static class DeleteEvent extends PositionForm.PositionFormEvent {
        DeleteEvent(PositionForm source, Position position) {
            super(source, position);
        }

    }

    public static class CloseEvent extends PositionForm.PositionFormEvent {
        CloseEvent(PositionForm source) {
            super(source, null);
        }
    }

    public <T extends ComponentEvent<?>> Registration addListener(Class<T> eventType,
                                                                  ComponentEventListener<T> listener) {
        return getEventBus().addListener(eventType, listener);
    }

}
