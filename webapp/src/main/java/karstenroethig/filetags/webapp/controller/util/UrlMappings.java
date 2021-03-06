package karstenroethig.filetags.webapp.controller.util;

public class UrlMappings
{
	private static final String REDIRECT_PREFIX = "redirect:";

	public static final String HOME = "/";
	public static final String STATISTIC = "/statistic";

	public static final String CONTROLLER_API = "/api";
	public static final String CONTROLLER_API_VERSION_1_0 = "/1.0";

	public static final String CONTROLLER_WORKSPACE = "/workspace";
	public static final String CONTROLLER_FILE = "/file";
	public static final String CONTROLLER_GALLERY = "/gallery";
	public static final String CONTROLLER_TAG = "/tag";
	public static final String CONTROLLER_ADMIN = "/admin";

	public static final String ACTION_LIST = "/list";
	public static final String ACTION_SHOW = "/show/{id}";
	public static final String ACTION_CREATE = "/create";
	public static final String ACTION_EDIT = "/edit/{id}";
	public static final String ACTION_DELETE = "/delete/{id}";
	public static final String ACTION_SAVE = "/save";
	public static final String ACTION_UPDATE = "/update";

	public static final String ACTION_OPEN = "/open";
	public static final String ACTION_SYNC = "/sync";

	private UrlMappings()
	{
	}

	public static String redirect( String controllerPath, String actionPath )
	{
		return REDIRECT_PREFIX + controllerPath + actionPath;
	}
}
