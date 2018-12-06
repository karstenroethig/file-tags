package karstenroethig.filetags.webapp.dto;

import javax.validation.constraints.NotNull;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class WorkspaceDto
{
	@NotNull
	private String pathToDir;
}
