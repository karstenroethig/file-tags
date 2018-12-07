package karstenroethig.filetags.webapp.dto.enums;

import lombok.Getter;

@Getter
public enum TagTypeEnum
{
	PERSON("badge-primary"),
	CATEGORY("badge-light");

	private String styleClass;

	private TagTypeEnum(String styleClass)
	{
		this.styleClass = styleClass;
	}
}
