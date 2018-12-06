package karstenroethig.filetags.webapp.controller;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import karstenroethig.filetags.webapp.controller.util.UrlMappings;
import karstenroethig.filetags.webapp.controller.util.ViewEnum;
import karstenroethig.filetags.webapp.dto.WorkspaceDto;
import karstenroethig.filetags.webapp.service.impl.WorkspaceServiceImpl;

@ComponentScan
@Controller
@RequestMapping(UrlMappings.CONTROLLER_WORKSPACE)
public class WorkspaceController
{
	@Autowired private WorkspaceServiceImpl workspaceService;

	@RequestMapping(value = UrlMappings.ACTION_OPEN, method = RequestMethod.GET)
	public String open(Model model)
	{
		model.addAttribute("workspace", new WorkspaceDto());

		return ViewEnum.WORKSPACE_OPEN.getViewName();
	}

	@RequestMapping(value = UrlMappings.ACTION_OPEN, method = RequestMethod.POST)
	public String open(@ModelAttribute("workspace") @Valid WorkspaceDto workspace, BindingResult bindingResult, final RedirectAttributes redirectAttributes, Model model)
	{
		workspaceService.selectWorkspace(workspace);

		return UrlMappings.redirect(UrlMappings.CONTROLLER_FILE, UrlMappings.ACTION_LIST);
	}
}
