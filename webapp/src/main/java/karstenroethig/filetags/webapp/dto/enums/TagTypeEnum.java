package karstenroethig.filetags.webapp.dto.enums;

import lombok.Getter;

@Getter
public enum TagTypeEnum
{
	CATEGORY("badge-secondary"),
	PERSON("badge-info");

	private String styleClass;

	private TagTypeEnum(String styleClass)
	{
		this.styleClass = styleClass;
	}
}
