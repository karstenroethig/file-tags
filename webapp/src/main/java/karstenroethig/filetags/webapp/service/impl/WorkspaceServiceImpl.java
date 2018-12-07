package karstenroethig.filetags.webapp.service.impl;

import java.io.IOException;
import java.io.InputStream;
import java.math.BigInteger;
import java.nio.file.FileVisitOption;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;

import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import karstenroethig.filetags.webapp.domain.DirectoryInfo;
import karstenroethig.filetags.webapp.domain.File;
import karstenroethig.filetags.webapp.domain.ObjectFactory;
import karstenroethig.filetags.webapp.domain.Tag;
import karstenroethig.filetags.webapp.domain.TagType;
import karstenroethig.filetags.webapp.domain.Tags;
import karstenroethig.filetags.webapp.dto.FileDto;
import karstenroethig.filetags.webapp.dto.TagDto;
import karstenroethig.filetags.webapp.dto.WorkspaceDto;
import karstenroethig.filetags.webapp.dto.enums.TagTypeEnum;
import karstenroethig.filetags.webapp.service.exceptions.TagAlreadyExistsException;
import lombok.extern.slf4j.Slf4j;

@Slf4j

@Service
public class WorkspaceServiceImpl
{
	private static final String[] FILES_BLACKLIST = new String[] {"[\\.]{1}[\\w\\W]+"};
	private static final String DIRECTORY_INFO_FILENAME = ".directory-info";

	private static final ObjectFactory OBJECT_FACTORY = new ObjectFactory();

	private static WorkspaceDto SELECTED_WORKSPACE = null;

	@Autowired FileServiceImpl fileService;
	@Autowired TagServiceImpl tagService;

	public boolean hasWorkspace()
	{
		return SELECTED_WORKSPACE != null;
	}

	public void open(WorkspaceDto workspace) throws JAXBException
	{
		Path pathToWorkspace = Paths.get(workspace.getPathToDir());
		if (!Files.exists(pathToWorkspace) || !Files.isDirectory(pathToWorkspace))
			return;

		SELECTED_WORKSPACE = workspace;

		Path pathToDirectoryInfo = pathToWorkspace.resolve(DIRECTORY_INFO_FILENAME);
		if (Files.exists(pathToDirectoryInfo))
			open(pathToDirectoryInfo);
		else
		{
			tagService.clear();
			fileService.clear();
			sync();
		}
	}

	private void open(Path pathToDirectoryInfo) throws JAXBException
	{
		JAXBContext context = JAXBContext.newInstance(DirectoryInfo.class);
		Unmarshaller unmarshaller = context.createUnmarshaller();

		DirectoryInfo directoryInfo = (DirectoryInfo)unmarshaller.unmarshal(pathToDirectoryInfo.toFile());

		tagService.clear();
		if (directoryInfo.getTags() != null && directoryInfo.getTags().getTag() != null)
			for (Tag tag : directoryInfo.getTags().getTag())
			{
				try
				{
					tagService.save(transform(tag));
				}
				catch (TagAlreadyExistsException ex)
				{
					log.error("error", ex);
				}
			}

		List<FileDto> newFiles = new ArrayList<>();
		if (directoryInfo.getFiles() != null && directoryInfo.getFiles().getFile() != null)
			for (File file : directoryInfo.getFiles().getFile())
				newFiles.add(transform(file));

		fileService.clear();
		fileService.saveAll(newFiles);
	}

	private TagDto transform(Tag tag)
	{
		TagDto tagDto = new TagDto();
		tagDto.setName(tag.getName());
		if (tag.getType() == TagType.CATEGORY)
			tagDto.setType(TagTypeEnum.CATEGORY);
		else if (tag.getType() == TagType.PERSON)
			tagDto.setType(TagTypeEnum.PERSON);
		else
			throw new IllegalArgumentException("unknown tag type");

		return tagDto;
	}

	private FileDto transform(File file)
	{
		Path pathToFile = Paths.get(SELECTED_WORKSPACE.getPathToDir(), file.getPath(), file.getName());

		FileDto fileDto = new FileDto();
		fileDto.setFilename(file.getName());
		fileDto.setPathToDir(file.getPath());
		fileDto.setSize(file.getSize().longValue());
		fileDto.setHash(file.getHash());
		fileDto.setFileUrl(pathToFile.toUri().toString());

		String[] tagNames = StringUtils.split(file.getTags(), ',');
		if (tagNames != null)
			for (String tagName : tagNames)
			{
				TagDto tag = tagService.findByName(tagName);
				if (tag != null)
					fileDto.addTag(tag);
			}

		return fileDto;
	}

	public void sync()
	{
		Path pathToWorkspace = Paths.get(SELECTED_WORKSPACE.getPathToDir());
		if (!Files.exists(pathToWorkspace) || !Files.isDirectory(pathToWorkspace))
			return;

		try {
			List<FileDto> newFiles = Files.walk(pathToWorkspace, FileVisitOption.FOLLOW_LINKS)
					.sorted(Comparator.reverseOrder())
					.filter(path -> include(path))
					.peek(path -> log.info("loading file " + path.toString()))
					.map(path -> transform(pathToWorkspace, path))
					.collect(Collectors.toList());

			fileService.clear();
			fileService.saveAll(newFiles);
		}
		catch (IOException ex)
		{
			log.error("error", ex);
		}
	}

	private boolean include(Path path)
	{
		if (Files.isDirectory(path))
			return false;

		for (String blacklistPattern : FILES_BLACKLIST)
		{
			if (path.getFileName().toString().matches(blacklistPattern))
				return false;
		}

		return true;
	}

	public void save() throws JAXBException
	{
		if (SELECTED_WORKSPACE == null)
			return;

		Path pathToWorkspace = Paths.get(SELECTED_WORKSPACE.getPathToDir());
		Path pathToDirectoryInfo = pathToWorkspace.resolve(DIRECTORY_INFO_FILENAME);

		DirectoryInfo directoryInfo = createDirectoryInfo();

		JAXBContext context = JAXBContext.newInstance(DirectoryInfo.class);
		Marshaller marshaller = context.createMarshaller();
		marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE);

		marshaller.marshal(directoryInfo, pathToDirectoryInfo.toFile());
	}

	private DirectoryInfo createDirectoryInfo()
	{
		DirectoryInfo directoryInfo = OBJECT_FACTORY.createDirectoryInfo();

		Tags tags = OBJECT_FACTORY.createTags();
		for (TagDto tagDto : tagService.findAll())
			tags.getTag().add(transformTag(tagDto));
		directoryInfo.setTags(tags);

		karstenroethig.filetags.webapp.domain.Files files = OBJECT_FACTORY.createFiles();
		for (FileDto fileDto : fileService.findAll())
			files.getFile().add(transformFile(fileDto));
		directoryInfo.setFiles(files);

		return directoryInfo;
	}

	private Tag transformTag(TagDto tagDto)
	{
		Tag tag = OBJECT_FACTORY.createTag();
		tag.setName(tagDto.getName());
		if (tagDto.getType() == TagTypeEnum.CATEGORY)
			tag.setType(TagType.CATEGORY);
		else if (tagDto.getType() == TagTypeEnum.PERSON)
			tag.setType(TagType.PERSON);
		else
			throw new IllegalArgumentException("unknown tag type");

		return tag;
	}

	private File transformFile(FileDto fileDto)
	{
		File file = OBJECT_FACTORY.createFile();
		file.setName(fileDto.getFilename());
		file.setPath(fileDto.getPathToDir());
		file.setSize(BigInteger.valueOf(fileDto.getSize()));
		file.setHash(fileDto.getHash());
		file.setTags(String.join(",", fileDto.getTags().stream().map(tag -> tag.getName()).collect(Collectors.toList())));

		return file;
	}

	private FileDto transform(Path workspaceDirPath, Path filePath)
	{
		Path relativeFilePath = workspaceDirPath.relativize(filePath);
		Path relativeFileDirPath = relativeFilePath.getParent();

		FileDto fileDto = new FileDto();
		fileDto.setPathToDir(relativeFileDirPath != null ? StringUtils.replace(relativeFileDirPath.toString(), "/", " / ") : null);
		fileDto.setFilename(relativeFilePath.getFileName().toString());
		fileDto.setFileUrl(filePath.toUri().toString());

		try (InputStream inputStream = Files.newInputStream(filePath))
		{
			fileDto.setSize(Files.size(filePath));
			fileDto.setHash(DigestUtils.md5Hex(inputStream));
		}
		catch (IOException ex)
		{
			log.error("error", ex);
		}

		FileDto oldFileDto = fileService.findByHash(fileDto.getHash());
		if (oldFileDto != null)
		{
			for (TagDto tag : oldFileDto.getTags())
				fileDto.addTag(tag);
		}

		return fileDto;
	}
}
