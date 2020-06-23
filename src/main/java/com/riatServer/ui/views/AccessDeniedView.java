package com.riatServer.ui.views;

import com.vaadin.flow.component.polymertemplate.PolymerTemplate;
import com.vaadin.flow.router.BeforeEnterEvent;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.templatemodel.TemplateModel;
import javax.servlet.http.HttpServletResponse;
import com.riatServer.exception.AccessDeniedException;
import com.vaadin.flow.component.Tag;
import com.vaadin.flow.component.dependency.JsModule;
import com.vaadin.flow.router.*;

@Tag("access-denied-view")
@JsModule("./src/views/errors/access-denied-view.js")
@Route
public class AccessDeniedView extends PolymerTemplate<TemplateModel> implements HasErrorParameter<AccessDeniedException> {

	@Override
	public int setErrorParameter(BeforeEnterEvent beforeEnterEvent, ErrorParameter<AccessDeniedException> errorParameter) {
		return HttpServletResponse.SC_FORBIDDEN;
	}
}
