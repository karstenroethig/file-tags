package karstenroethig.filetags.webapp.dto;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import karstenroethig.filetags.webapp.dto.enums.TagTypeEnum;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class TagDto
{
	private Integer id;

	@NotNull
	@Size(min = 1, max = 256)
	private String name;

	@NotNull
	private TagTypeEnum type;
}
