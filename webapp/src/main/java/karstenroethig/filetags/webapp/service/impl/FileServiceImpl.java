package karstenroethig.filetags.webapp.service.impl;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import karstenroethig.filetags.webapp.dto.FileAddTagsDto;
import karstenroethig.filetags.webapp.dto.FileDto;
import karstenroethig.filetags.webapp.dto.FilePageDto;
import karstenroethig.filetags.webapp.dto.FileSearchDto;
import karstenroethig.filetags.webapp.dto.TagDto;
import karstenroethig.filetags.webapp.service.exceptions.FileAlreadyExistsException;
import lombok.extern.slf4j.Slf4j;

@Slf4j

@Service
public class FileServiceImpl
{
	private static final int MAX_ENTRIES_PER_PAGE = 10;

	private static AtomicInteger ID_GENARATOR = new AtomicInteger();

	private static Map<Integer, FileDto> FILES_BY_ID = new HashMap<>();
	private static Map<String, FileDto> FILES_BY_HASH = new HashMap<>();

	@Autowired private TagServiceImpl tagService;

	public FileDto newFile()
	{
		return new FileDto();
	}

	public FileDto save(FileDto file) throws FileAlreadyExistsException
	{
		if (FILES_BY_HASH.containsKey(file.getHash()))
			throw new FileAlreadyExistsException();

		file.setId(ID_GENARATOR.incrementAndGet());

		FILES_BY_ID.put(file.getId(), file);
		FILES_BY_HASH.put(file.getHash(), file);

		return file;
	}

	public void saveAll(List<FileDto> files)
	{
		for (FileDto file : files)
		{
			try
			{
				save(file);
			}
			catch (FileAlreadyExistsException ex)
			{
				log.warn(String.format("file already exists [%s, %s, %s]", file.getFilename(), file.getHash(), file.getPathToDir()));
			}
		}
	}

	public FileDto find(Integer id)
	{
		return FILES_BY_ID.get(id);
	}

	public FileDto findByHash(String hash)
	{
		return FILES_BY_HASH.get(hash);
	}

	public List<FileDto> findAll()
	{
		return FILES_BY_ID.values().stream().sorted(Comparator.comparing(FileDto::getFilename)).collect(Collectors.toList());
	}

	public FilePageDto find(FileSearchDto searchParams, int pageNumber)
	{
		List<FileDto> foundFiles = FILES_BY_ID.values().stream()
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

	protected void clear()
	{
		FILES_BY_ID.clear();
		FILES_BY_HASH.clear();
		ID_GENARATOR.set(0);
	}

	public void addTags(FileAddTagsDto fileAddTags)
	{
		FileDto fileDto = find(fileAddTags.getId());
		if (fileDto == null)
			return;

		String[] tagNames = StringUtils.split(fileAddTags.getTagNames(), ',');
		if (tagNames != null)
			for (String tagName : tagNames)
			{
				TagDto tag = tagService.findByName(StringUtils.trim(tagName));
				if (tag != null)
					fileDto.addTag(tag);
			}
		tagService.sort(fileDto.getTags());
	}

	public void update(FileDto fileChangedDto)
	{
		FileDto fileDto = find(fileChangedDto.getId());
		if (fileDto == null)
			return;

		fileDto.clearTags();

		for (TagDto tagDto : fileChangedDto.getTags())
		{
			fileDto.addTag(tagService.find(tagDto.getId()));
		}

		tagService.sort(fileDto.getTags());
	}
}
