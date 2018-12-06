package karstenroethig.filetags.webapp.dto;

import java.util.ArrayList;
import java.util.List;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class FileSearchDto
{
	private List<TagDto> tags = new ArrayList<>();
	private String filename;
}
