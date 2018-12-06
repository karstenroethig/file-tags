package karstenroethig.filetags.webapp.dto;

import java.util.ArrayList;
import java.util.List;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class FilePageDto
{
	private List<FileDto> files = new ArrayList<>();

	private Integer currentPageNumber;
	private Integer maxPageNumber;

	public boolean hasPreviousPage()
	{
		return currentPageNumber > 1;
	}

	public boolean hasNextPage()
	{
		return maxPageNumber > currentPageNumber;
	}

	public boolean hasEntries()
	{
		return !files.isEmpty();
	}
}
