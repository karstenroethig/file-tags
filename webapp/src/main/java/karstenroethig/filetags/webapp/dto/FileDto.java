package karstenroethig.filetags.webapp.dto;

import java.util.ArrayList;
import java.util.List;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class FileDto
{
	private String pathToDir;
	private String filename;
	private Long size;
	private String hash;
	private List<TagDto> tags = new ArrayList<>();

	public boolean isNew()
	{
		return tags == null || tags.isEmpty();
	}
}
