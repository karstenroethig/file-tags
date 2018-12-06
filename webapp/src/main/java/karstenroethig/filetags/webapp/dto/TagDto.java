package karstenroethig.filetags.webapp.dto;

import karstenroethig.filetags.webapp.dto.enums.TagTypeEnum;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class TagDto
{
	private Integer id;
	private String name;
	private TagTypeEnum type;
}
