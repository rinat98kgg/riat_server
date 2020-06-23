package com.riatServer.ui.views;

import com.vaadin.flow.component.customfield.CustomField;
import com.vaadin.flow.component.datepicker.DatePicker;
import com.vaadin.flow.component.timepicker.TimePicker;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.Locale;
import java.util.Objects;

public class LocalDateTimeField extends CustomField<LocalDateTime> {
    private DatePicker datePicker;
    private TimePicker timePicker;

    public LocalDateTimeField() {
        datePicker = new DatePicker();
        timePicker = new TimePicker();
        add(datePicker, timePicker);
        setLabel("");
    }

    public LocalDateTimeField(String labelName) {
        this();
        setLabel(labelName);
    }

    @Override
    protected LocalDateTime generateModelValue() {
        LocalDate localDate = this.datePicker.getValue();
        LocalTime localTime = this.timePicker.getValue();
        return (Objects.nonNull(localDate) && Objects.nonNull(localTime))
                ? LocalDateTime.of(localDate, localTime)
                : null;
    }

    @Override
    protected void setPresentationValue(LocalDateTime newPresentationValue) {
        datePicker.setValue(newPresentationValue != null ?
                newPresentationValue.toLocalDate() :
                null);
        timePicker.setValue(newPresentationValue != null ?
                newPresentationValue.toLocalTime() :
                null);
    }
}
