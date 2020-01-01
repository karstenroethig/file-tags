package karstenroethig.filetags.webapp.dto;

import java.util.Objects;

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

	@Override
	public int hashCode()
	{
		int hash = 5;
		hash = 83 * hash + Objects.hashCode(this.id);
		return hash;
	}

	@Override
	public boolean equals(Object obj)
	{
		if (obj == null)
		{
			return false;
		}

		if (getClass() != obj.getClass())
		{
			return false;
		}

		final TagDto other = (TagDto) obj;
		if (!Objects.equals(this.id, other.id))
		{
			return false;
		}

		return true;
	}
}
