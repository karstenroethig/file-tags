package karstenroethig.filetags.webapp.util;

import lombok.Getter;

public enum MessageKeyEnum
{
	APPLICATION_VERSION("application.version"),
	APPLICATION_REVISION("application.revision"),
	APPLICATION_BUILD_DATE("application.buildDate"),

	WORKSPACE_OPEN_INVALID("workspace.open.invalid"),
	WORKSPACE_OPEN_SUCCESS("workspace.open.success"),
	WORKSPACE_OPEN_ERROR("workspace.open.error"),
	WORKSPACE_SAVE_SUCCESS("workspace.save.success"),
	WORKSPACE_SAVE_ERROR("workspace.save.error"),
	WORKSPACE_SYNC_SUCCESS("workspace.sync.success"),
	WORKSPACE_SYNC_ERROR("workspace.sync.error"),

	TAG_SAVE_INVALID("tag.save.invalid"),
	TAG_SAVE_SUCCESS("tag.save.success"),
	TAG_SAVE_ERROR("tag.save.error"),
	TAG_UPDATE_INVALID("tag.update.invalid"),
	TAG_UPDATE_SUCCESS("tag.update.success"),
	TAG_UPDATE_ERROR("tag.update.error"),
	TAG_DELETE_SUCCESS("tag.delete.success"),
	TAG_DELETE_ERROR("tag.delete.error");

	@Getter
	private String key;

	private MessageKeyEnum(String key)
	{
		this.key = key;
	}
}
