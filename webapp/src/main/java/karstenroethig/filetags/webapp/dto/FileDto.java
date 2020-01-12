package karstenroethig.filetags.webapp.dto;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class FileDto
{
	private Integer id;
	private String pathToDirFormatted;
	private String filename;
	private Long size;
	private String sizeFormatted;
	private String hash;
	private Path pathToFile;
	private List<TagDto> tags = new ArrayList<>();

	public boolean isNew()
	{
		return tags == null || tags.isEmpty();
	}

	public void addTag(TagDto tag)
	{
		tags.add(tag);
	}

	public void clearTags()
	{
		tags.clear();
	}
}
