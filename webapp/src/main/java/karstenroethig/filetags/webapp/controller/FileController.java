package karstenroethig.filetags.webapp.controller;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import karstenroethig.filetags.webapp.bean.FilesSearchBean;
import karstenroethig.filetags.webapp.controller.util.UrlMappings;
import karstenroethig.filetags.webapp.controller.util.ViewEnum;
import karstenroethig.filetags.webapp.dto.FileSearchDto;
import karstenroethig.filetags.webapp.service.impl.TagServiceImpl;
import karstenroethig.filetags.webapp.service.impl.WorkspaceServiceImpl;

@ComponentScan
@Controller
@RequestMapping(UrlMappings.CONTROLLER_FILE)
public class FileController
{
	@Autowired WorkspaceServiceImpl workspaceService;
	@Autowired TagServiceImpl tagService;

	@Autowired FilesSearchBean filesSearchBean;

	@RequestMapping(value = UrlMappings.ACTION_LIST, method = RequestMethod.GET)
	public String list(Model model)
	{
		if (filesSearchBean.getSearchParams() == null)
			return executeNewSearch(new FileSearchDto(), 1);

		if (filesSearchBean.getCurrentPage() == 1)
			return executeSearch(model, filesSearchBean.getSearchParams(), filesSearchBean.getCurrentPage());

		return UrlMappings.redirect(UrlMappings.CONTROLLER_FILE, "/page/" + filesSearchBean.getCurrentPage());
	}

	@RequestMapping(value = "/page/{page}", method = RequestMethod.GET)
	public String page(@PathVariable("page") Integer page, Model model)
	{
		if (filesSearchBean.getSearchParams() == null)
			return executeNewSearch(new FileSearchDto(), page);

		filesSearchBean.setCurrentPage(page);

		return executeSearch(model, filesSearchBean.getSearchParams(), page);
	}

	@RequestMapping(value = "/search", method = RequestMethod.POST)
	public String search(@ModelAttribute("searchParams") @Valid FileSearchDto searchParams, BindingResult bindingResult, RedirectAttributes redirectAttributes, Model model)
	{
		return executeNewSearch(searchParams, 1);
	}

	private String executeNewSearch(FileSearchDto searchParams, int page)
	{
		filesSearchBean.clear();
		filesSearchBean.setSearchParams(searchParams);
		filesSearchBean.setCurrentPage(page);

		return UrlMappings.redirect(UrlMappings.CONTROLLER_FILE, UrlMappings.ACTION_LIST);
	}

	private String executeSearch(Model model, FileSearchDto searchParams, int page)
	{
		model.addAttribute("page", workspaceService.find(searchParams, page));
		model.addAttribute("allTags", tagService.getAllTagsByType());
		model.addAttribute("searchParams", searchParams);

		return ViewEnum.FILE_LIST.getViewName();
	}
}
