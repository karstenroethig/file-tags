package karstenroethig.filetags.webapp.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import karstenroethig.filetags.webapp.controller.util.UrlMappings;
import karstenroethig.filetags.webapp.service.impl.WorkspaceServiceImpl;

@ComponentScan
@Controller
public class HomeController
{
	@Autowired private WorkspaceServiceImpl workspaceService;

	@RequestMapping(value = UrlMappings.HOME, method = RequestMethod.GET)
	public String home(Model model)
	{
		if (workspaceService.hasWorkspace())
			return UrlMappings.redirect(UrlMappings.CONTROLLER_FILE, UrlMappings.ACTION_LIST);
		else
			return UrlMappings.redirect(UrlMappings.CONTROLLER_WORKSPACE, UrlMappings.ACTION_OPEN);
	}
}
