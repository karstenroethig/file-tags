package karstenroethig.filetags.webapp.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import karstenroethig.filetags.webapp.controller.util.UrlMappings;
import karstenroethig.filetags.webapp.controller.util.ViewEnum;
import karstenroethig.filetags.webapp.dto.info.ServerInfoDto;
import karstenroethig.filetags.webapp.service.impl.ServerInfoServiceImpl;

@ComponentScan
@Controller
@RequestMapping(UrlMappings.CONTROLLER_ADMIN)
public class AdminController
{
	@Autowired
	ServerInfoServiceImpl serverInfoService;

	@RequestMapping(
		value = "/server-info",
		method = RequestMethod.GET
	)
	public String serverInfo( Model model )
	{
		ServerInfoDto serverInfo = serverInfoService.getInfo();

		model.addAttribute( "serverInfo", serverInfo );

		return ViewEnum.ADMIN_SERVER_INFO.getViewName();
	}
}
