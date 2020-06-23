package com.riatServer.ui.views;

import com.vaadin.flow.component.Html;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.confirmdialog.ConfirmDialog;
import com.vaadin.flow.component.icon.VaadinIcon;

public class ConfirmDialogView {

    public ConfirmDialog alertDialog(String header, String text) {

        ConfirmDialog dialog = new ConfirmDialog(header, text, "OK", this::onOK);
        dialog.open();
        return dialog;
    }

    public ConfirmDialog confirmationDialog() {
        ConfirmDialog dialog = new ConfirmDialog("Подвердите действие",
                "Вы уверены, что хотите сохранить эти данные?", "Сохранить", this::onPublish,
                "Отменить", this::onCancel);
        dialog.open();
        return dialog;
    }

    public ConfirmDialog editableConfirmDialog(String header, String text) {
        ConfirmDialog dialog = new ConfirmDialog(header,
                text, "Да", this::onPublish,
                "Отменить", this::onCancel);
        dialog.open();
        return dialog;
    }

    public ConfirmDialog confirmationDialogForDangerousAction() {
        ConfirmDialog dialog = new ConfirmDialog("Подтвердите удаление",
                "Вы уверены, что хотите удалить эти данные?",
                "Удалить", this::onDelete, "Отменить", this::onCancel);
        dialog.setConfirmButtonTheme("error primary");
        dialog.open();
        return dialog;
    }

    public ConfirmDialog confirmationDialogWithRejectOption() {
        ConfirmDialog dialog = new ConfirmDialog("Unsaved changes",
                "Do you want to save or discard your changes before navigating away?",
                "Save", this::onSave, "Discard", this::onDiscard, "Cancel", this::onCancel);
        dialog.open();
        return dialog;
    }

    public ConfirmDialog changePassword() {
        ConfirmDialog dialog = new ConfirmDialog("Подвердите действие",
                "Вы действительно хотите ввести новый пароль?", "Да", this::onPublish,
                "Отменить", this::onCancel);
        dialog.open();
        return dialog;
    }

    public ConfirmDialog customButtons() {
        ConfirmDialog dialog = new ConfirmDialog();
        dialog.setHeader("Unsaved changes");
        String textHtml = "<p>Do you want to <b>save</b> or <b>discard</b> " +
                "your changes before navigating away?</p>";
        dialog.setText(new Html(textHtml).getElement());
        dialog.open();

        Button saveButton = new Button("Save", VaadinIcon.ENVELOPE_OPEN.create());
        saveButton.addClickListener(e -> dialog.close());
        saveButton.getElement().setAttribute("theme", "primary");
        dialog.setConfirmButton(saveButton.getElement());

        Button rejectButton = new Button("Discard", VaadinIcon.TRASH.create());
        rejectButton.addClickListener(e -> dialog.close());
        rejectButton.getElement().setAttribute("theme", "error tertiary");
        dialog.setRejectButton(rejectButton.getElement());

        dialog.setCancelButton("Cancel", this::onCancel);
        dialog.open();
        return dialog;
    }

    private void onOK(ConfirmDialog.ConfirmEvent event) {
    }

    private void onPublish(ConfirmDialog.ConfirmEvent event) {
    }

    private void onSave(ConfirmDialog.ConfirmEvent event) {
    }

    private void onDiscard(ConfirmDialog.RejectEvent event) {
    }

    private void onDelete(ConfirmDialog.ConfirmEvent event) {
    }

    private void onCancel(ConfirmDialog.CancelEvent event) {
    }
}
