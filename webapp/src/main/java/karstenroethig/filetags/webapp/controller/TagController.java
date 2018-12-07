package karstenroethig.filetags.webapp.controller;

import java.io.IOException;

import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import karstenroethig.filetags.webapp.controller.exceptions.NotFoundException;
import karstenroethig.filetags.webapp.controller.util.UrlMappings;
import karstenroethig.filetags.webapp.controller.util.ViewEnum;
import karstenroethig.filetags.webapp.dto.TagDto;
import karstenroethig.filetags.webapp.service.exceptions.TagAlreadyExistsException;
import karstenroethig.filetags.webapp.service.impl.TagServiceImpl;
import karstenroethig.filetags.webapp.util.MessageKeyEnum;
import karstenroethig.filetags.webapp.util.Messages;

@ComponentScan
@Controller
@RequestMapping(UrlMappings.CONTROLLER_TAG)
public class TagController
{
	@Autowired private TagServiceImpl tagService;

	@RequestMapping(value = UrlMappings.ACTION_LIST, method = RequestMethod.GET)
	public String list(Model model)
	{
		model.addAttribute("tags", tagService.findAll());
		return ViewEnum.TAG_LIST.getViewName();
	}

	@RequestMapping(value = UrlMappings.ACTION_CREATE, method = RequestMethod.GET)
	public String create(Model model)
	{
		model.addAttribute("tag", tagService.newTag());
		return ViewEnum.TAG_CREATE.getViewName();
	}

	@RequestMapping(value = UrlMappings.ACTION_EDIT, method = RequestMethod.GET)
	public String edit(@PathVariable("id") Integer tagId, Model model)
	{
		TagDto tag = tagService.find(tagId);

		if (tag == null)
			throw new NotFoundException(String.valueOf(tagId));

		model.addAttribute("tag", tag);

		return ViewEnum.TAG_EDIT.getViewName();
	}

	@RequestMapping(value = UrlMappings.ACTION_DELETE, method = RequestMethod.GET)
	public String delete(@PathVariable("id") Integer tagId, final RedirectAttributes redirectAttributes, Model model)
	{
		TagDto tag = tagService.find(tagId);

		if (tag == null)
			throw new NotFoundException(String.valueOf(tagId));

		if (tagService.delete(tagId))
			redirectAttributes.addFlashAttribute(Messages.ATTRIBUTE_NAME, Messages.createWithSuccess(MessageKeyEnum.TAG_DELETE_SUCCESS, tag.getName()));
		else
			redirectAttributes.addFlashAttribute(Messages.ATTRIBUTE_NAME, Messages.createWithError(MessageKeyEnum.TAG_DELETE_ERROR, tag.getName()));

		return UrlMappings.redirect(UrlMappings.CONTROLLER_TAG, UrlMappings.ACTION_LIST);
	}

	@RequestMapping(value = UrlMappings.ACTION_SAVE, method = RequestMethod.POST)
	public String save(@ModelAttribute("tag") @Valid TagDto tag, BindingResult bindingResult, final RedirectAttributes redirectAttributes, Model model)
	{
		if (bindingResult.hasErrors())
		{
			model.addAttribute(Messages.ATTRIBUTE_NAME, Messages.createWithError(MessageKeyEnum.TAG_SAVE_INVALID));

			return ViewEnum.TAG_CREATE.getViewName();
		}

		try
		{
			if (tagService.save(tag) != null)
			{
				redirectAttributes.addFlashAttribute(Messages.ATTRIBUTE_NAME, Messages.createWithSuccess(MessageKeyEnum.TAG_SAVE_SUCCESS, tag.getName()));

				return UrlMappings.redirect(UrlMappings.CONTROLLER_TAG, UrlMappings.ACTION_LIST);
			}
		}
		catch (TagAlreadyExistsException ex)
		{
			bindingResult.rejectValue("name", "tag.error.exists");

			model.addAttribute(Messages.ATTRIBUTE_NAME, Messages.createWithError(MessageKeyEnum.TAG_SAVE_INVALID));

			return ViewEnum.TAG_CREATE.getViewName();
		}

		model.addAttribute(Messages.ATTRIBUTE_NAME, Messages.createWithError(MessageKeyEnum.TAG_SAVE_ERROR));

		return ViewEnum.TAG_CREATE.getViewName();
	}

	@RequestMapping(value = UrlMappings.ACTION_UPDATE, method = RequestMethod.POST)
	public String update(@ModelAttribute("tag") @Valid TagDto tag, BindingResult bindingResult, final RedirectAttributes redirectAttributes, Model model)
	{
		if (bindingResult.hasErrors())
		{
			model.addAttribute(Messages.ATTRIBUTE_NAME, Messages.createWithError(MessageKeyEnum.TAG_UPDATE_INVALID));

			return ViewEnum.TAG_EDIT.getViewName();
		}

		try
		{
			if (tagService.edit(tag) != null)
			{
				redirectAttributes.addFlashAttribute(Messages.ATTRIBUTE_NAME, Messages.createWithSuccess(MessageKeyEnum.TAG_UPDATE_SUCCESS, tag.getName()));

				return UrlMappings.redirect(UrlMappings.CONTROLLER_TAG, UrlMappings.ACTION_LIST);
			}
		}
		catch (TagAlreadyExistsException ex)
		{
			bindingResult.rejectValue("name", "tag.error.exists");

			model.addAttribute(Messages.ATTRIBUTE_NAME, Messages.createWithError(MessageKeyEnum.TAG_UPDATE_INVALID));

			return ViewEnum.TAG_EDIT.getViewName();
		}

		model.addAttribute(Messages.ATTRIBUTE_NAME, Messages.createWithError(MessageKeyEnum.TAG_UPDATE_ERROR));

		return ViewEnum.TAG_EDIT.getViewName();
	}

	@ExceptionHandler(NotFoundException.class)
	void handleNotFoundException(HttpServletResponse response, NotFoundException ex) throws IOException
	{
		response.sendError(HttpStatus.NOT_FOUND.value(), String.format("Tag %s does not exist.", ex.getMessage()));
	}
}
