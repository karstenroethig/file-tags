package karstenroethig.filetags.webapp.service.impl;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.FileVisitOption;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import karstenroethig.filetags.webapp.dto.FileDto;
import karstenroethig.filetags.webapp.dto.FilePageDto;
import karstenroethig.filetags.webapp.dto.FileSearchDto;
import karstenroethig.filetags.webapp.dto.TagDto;
import karstenroethig.filetags.webapp.dto.WorkspaceDto;

@Service
public class WorkspaceServiceImpl
{
	private static final int MAX_ENTRIES_PER_PAGE = 10;

	private static WorkspaceDto SELECTED_WORKSPACE = null;
	private static List<FileDto> FILES = new ArrayList<>();

	public boolean hasWorkspace()
	{
		return SELECTED_WORKSPACE != null;
	}

	public void selectWorkspace(WorkspaceDto workspace)
	{
		Path pathToWorkspace = Paths.get(workspace.getPathToDir());

		if (!Files.exists(pathToWorkspace) || !Files.isDirectory(pathToWorkspace))
			return;

		try {
			FILES.clear();
			FILES.addAll(
					Files.walk(pathToWorkspace, FileVisitOption.FOLLOW_LINKS)
						.sorted(Comparator.reverseOrder())
						.filter(path -> !Files.isDirectory(path))
						.map(path -> transform(pathToWorkspace, path))
						.collect(Collectors.toList()));
		}
		catch (IOException ex)
		{
			// TODO Log exception
			ex.printStackTrace();
		}
	}

	private FileDto transform(Path workspaceDirPath, Path filePath)
	{
		Path relativeFilePath = workspaceDirPath.relativize(filePath);
		Path relativeFileDirPath = relativeFilePath.getParent();

		FileDto fileDto = new FileDto();
		fileDto.setPathToDir(relativeFileDirPath != null ? StringUtils.replace(relativeFileDirPath.toString(), "/", " / ") : null);
		fileDto.setFilename(relativeFilePath.getFileName().toString());

		try (InputStream inputStream = Files.newInputStream(filePath))
		{
			fileDto.setSize(Files.size(filePath));
			fileDto.setHash(DigestUtils.md5Hex(inputStream));
		}
		catch (IOException ex)
		{
			// TODO log exception
		}

		return fileDto;
	}

	public FilePageDto find(FileSearchDto searchParams, int pageNumber)
	{
		List<FileDto> foundFiles = FILES.stream()
			.filter(file -> matchesSearchParams(file, searchParams))
			.sorted(Comparator.comparing(FileDto::getFilename))
			.collect(Collectors.toList());

		int toIndex = MAX_ENTRIES_PER_PAGE * pageNumber;
		int fromIndex = toIndex - MAX_ENTRIES_PER_PAGE;

		if (foundFiles.size() < toIndex)
		{
			toIndex = foundFiles.size();
			fromIndex = toIndex - (foundFiles.size() % MAX_ENTRIES_PER_PAGE);
		}

		FilePageDto page = new FilePageDto();
		page.setCurrentPageNumber(pageNumber);
		page.setMaxPageNumber(foundFiles.size() / MAX_ENTRIES_PER_PAGE + (foundFiles.size() % MAX_ENTRIES_PER_PAGE > 0 ? 1 : 0));
		page.setFiles(foundFiles.isEmpty() ? new ArrayList<>() : foundFiles.subList(fromIndex, toIndex));
		return page;
	}

	private boolean matchesSearchParams(FileDto file, FileSearchDto searchParams)
	{
		if (searchParams.getTags().isEmpty() && StringUtils.isBlank(searchParams.getFilename()))
			return file.isNew();

		if (StringUtils.isNotBlank(searchParams.getFilename())
				&& !StringUtils.containsIgnoreCase(file.getFilename(), searchParams.getFilename()))
			return false;

		if (!searchParams.getTags().isEmpty())
		{
			for (TagDto searchTag : searchParams.getTags())
			{
				boolean hasSearchTag = false;

				for (TagDto fileTag : file.getTags())
				{
					if (fileTag.getId().equals(searchTag.getId()))
					{
						hasSearchTag = true;
						break;
					}
				}

				if (!hasSearchTag)
					return false;
			}
		}

		return true;
	}
}
