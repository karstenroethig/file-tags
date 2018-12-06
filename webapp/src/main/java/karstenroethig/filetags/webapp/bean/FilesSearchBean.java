package karstenroethig.filetags.webapp.bean;

import org.springframework.context.annotation.Scope;
import org.springframework.context.annotation.ScopedProxyMode;
import org.springframework.stereotype.Component;
import org.springframework.web.context.WebApplicationContext;

import karstenroethig.filetags.webapp.dto.FileSearchDto;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter

@Component
@Scope(value=WebApplicationContext.SCOPE_SESSION, proxyMode=ScopedProxyMode.TARGET_CLASS)
public class FilesSearchBean
{
	private FileSearchDto searchParams;
	private int currentPage;

	public FilesSearchBean()
	{
		clear();
	}

	public void clear()
	{
		searchParams = null;
		currentPage = 1;
	}
}
