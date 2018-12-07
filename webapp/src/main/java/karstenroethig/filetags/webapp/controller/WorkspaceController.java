package karstenroethig.filetags.webapp.controller;

import java.io.FileNotFoundException;

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
import karstenroethig.filetags.webapp.util.MessageKeyEnum;
import karstenroethig.filetags.webapp.util.Messages;
import lombok.extern.slf4j.Slf4j;

@Slf4j

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
		if (bindingResult.hasErrors())
		{
			model.addAttribute(Messages.ATTRIBUTE_NAME, Messages.createWithError(MessageKeyEnum.WORKSPACE_OPEN_INVALID));
			return ViewEnum.WORKSPACE_OPEN.getViewName();
		}

		try
		{
			workspaceService.open(workspace);
		}
		catch (FileNotFoundException ex)
		{
			bindingResult.rejectValue("pathToDir", "workspace.open.invalid.directoryNotFound");
			model.addAttribute(Messages.ATTRIBUTE_NAME, Messages.createWithError(MessageKeyEnum.WORKSPACE_OPEN_INVALID));
			return ViewEnum.WORKSPACE_OPEN.getViewName();
		}
		catch (Exception ex)
		{
			log.error("error on opening workspace", ex);
			model.addAttribute(Messages.ATTRIBUTE_NAME, Messages.createWithError(MessageKeyEnum.WORKSPACE_OPEN_ERROR));
			return ViewEnum.WORKSPACE_OPEN.getViewName();
		}

		redirectAttributes.addFlashAttribute(Messages.ATTRIBUTE_NAME, Messages.createWithSuccess(MessageKeyEnum.WORKSPACE_OPEN_SUCCESS));
		return UrlMappings.redirect(UrlMappings.CONTROLLER_FILE, UrlMappings.ACTION_LIST);
	}

	@RequestMapping(value = UrlMappings.ACTION_SYNC, method = RequestMethod.GET)
	public String sync(final RedirectAttributes redirectAttributes, Model model)
	{
		try
		{
			workspaceService.sync();
			redirectAttributes.addFlashAttribute(Messages.ATTRIBUTE_NAME, Messages.createWithSuccess(MessageKeyEnum.WORKSPACE_SYNC_SUCCESS));
		}
		catch (Exception ex)
		{
			log.error("error on synchronizing workspace", ex);
			redirectAttributes.addFlashAttribute(Messages.ATTRIBUTE_NAME, Messages.createWithError(MessageKeyEnum.WORKSPACE_SYNC_ERROR));
		}

		return UrlMappings.redirect(UrlMappings.CONTROLLER_FILE, UrlMappings.ACTION_LIST);
	}

	@RequestMapping(value = UrlMappings.ACTION_SAVE, method = RequestMethod.GET)
	public String save(final RedirectAttributes redirectAttributes, Model model)
	{
		try
		{
			workspaceService.save();
			redirectAttributes.addFlashAttribute(Messages.ATTRIBUTE_NAME, Messages.createWithSuccess(MessageKeyEnum.WORKSPACE_SAVE_SUCCESS));
		}
		catch (Exception ex)
		{
			log.error("error on saving workspace", ex);
			redirectAttributes.addFlashAttribute(Messages.ATTRIBUTE_NAME, Messages.createWithError(MessageKeyEnum.WORKSPACE_SAVE_ERROR));
		}

		return UrlMappings.redirect(UrlMappings.CONTROLLER_FILE, UrlMappings.ACTION_LIST);
	}
}
