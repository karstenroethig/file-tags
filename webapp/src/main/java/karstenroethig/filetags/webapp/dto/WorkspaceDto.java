package karstenroethig.filetags.webapp.dto;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class WorkspaceDto
{
	@NotNull
	@Size(min = 1, max = 256)
	private String pathToDir;
}
