package karstenroethig.filetags.webapp.controller;

import java.io.IOException;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.core.io.UrlResource;
import org.springframework.core.io.support.ResourceRegion;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpRange;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.MediaTypeFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import karstenroethig.filetags.webapp.bean.FilesSearchBean;
import karstenroethig.filetags.webapp.controller.exceptions.NotFoundException;
import karstenroethig.filetags.webapp.controller.util.UrlMappings;
import karstenroethig.filetags.webapp.controller.util.ViewEnum;
import karstenroethig.filetags.webapp.dto.FileDto;
import karstenroethig.filetags.webapp.dto.FileSearchDto;
import karstenroethig.filetags.webapp.service.impl.FileServiceImpl;
import karstenroethig.filetags.webapp.service.impl.TagServiceImpl;

@ComponentScan
@Controller
@RequestMapping(UrlMappings.CONTROLLER_FILE)
public class FileController
{
	@Autowired FileServiceImpl fileService;
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
		model.addAttribute("page", fileService.find(searchParams, page));
		model.addAttribute("allTags", tagService.findAllGroupedByType());
		model.addAttribute("searchParams", searchParams);

		return ViewEnum.FILE_LIST.getViewName();
	}

	@RequestMapping(value = UrlMappings.ACTION_EDIT, method = RequestMethod.GET)
	public String edit(@PathVariable("id") Integer id, Model model)
	{
		FileDto file = fileService.find(id);

		if (file == null)
			throw new NotFoundException(String.valueOf(id));

		model.addAttribute("file", file);
		model.addAttribute("allTags", tagService.findAllGroupedByType());

		return ViewEnum.FILE_EDIT.getViewName();
	}

	@RequestMapping(
		value = UrlMappings.ACTION_UPDATE,
		method = RequestMethod.POST
	)
	public String update(@ModelAttribute("file") @Valid FileDto file, BindingResult bindingResult, Model model)
	{
		fileService.update(file);

		return UrlMappings.redirect(UrlMappings.CONTROLLER_FILE, "/edit/" + file.getId());
	}
	
	@GetMapping(value = "/video/{id}")
	public ResponseEntity<ResourceRegion> video(@PathVariable("id") Integer id, @RequestHeader HttpHeaders headers) throws Exception
	{
		FileDto file = fileService.find(id);

		if (file == null)
			throw new NotFoundException(String.valueOf(id));
		
		UrlResource video = new UrlResource(file.getPathToFile().toUri());
		ResourceRegion region = resourceRegion(video, headers);
		
		return ResponseEntity.status(HttpStatus.PARTIAL_CONTENT)
				.contentType(MediaTypeFactory.getMediaType(video).orElse(MediaType.APPLICATION_OCTET_STREAM))
				.body(region);
	}
	
	private ResourceRegion resourceRegion(UrlResource video, HttpHeaders headers) throws IOException
	{
		long contentLength = video.contentLength();
		HttpRange range = headers.getRange().stream().findFirst().orElse(null);
		
		if (range != null)
		{
			long start = range.getRangeStart(contentLength);
			long end = range.getRangeEnd(contentLength);
			long rangeLength = Long.min(1 * 1024 * 1024,  end - start + 1);
			return new ResourceRegion(video, start, rangeLength);
		}
		else
		{
			long rangeLength = Long.min(1 * 1024 * 1024, contentLength);
			return new ResourceRegion(video, 0, rangeLength);
		}
	}
}
