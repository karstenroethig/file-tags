package karstenroethig.filetags.webapp.service.impl;

import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import karstenroethig.filetags.webapp.dto.TagDto;
import karstenroethig.filetags.webapp.dto.TagTypeWrapper;
import karstenroethig.filetags.webapp.service.exceptions.TagAlreadyExistsException;

@Service
public class TagServiceImpl
{
	private static AtomicInteger ID_GENARATOR = new AtomicInteger();

	private static Map<Integer, TagDto> TAGS_BY_ID = new HashMap<>();
	private static Map<String, TagDto> TAGS_BY_NAME = new HashMap<>();

	public TagDto newTag()
	{
		return new TagDto();
	}

	public TagDto save(TagDto tag) throws TagAlreadyExistsException
	{
		if (TAGS_BY_NAME.containsKey(tag.getName()))
			throw new TagAlreadyExistsException();

		tag.setId(ID_GENARATOR.incrementAndGet());

		TAGS_BY_ID.put(tag.getId(), tag);
		TAGS_BY_NAME.put(tag.getName(), tag);

		return tag;
	}

	public boolean delete(Integer id)
	{
		if (TAGS_BY_ID.containsKey(id))
		{
			TagDto tag = TAGS_BY_ID.get(id);

			TAGS_BY_ID.remove(id);
			TAGS_BY_NAME.remove(tag.getName());

			return true;
		}

		return false;
	}

	public TagDto edit(TagDto tag) throws TagAlreadyExistsException
	{
		if (TAGS_BY_NAME.containsKey(tag.getName())
				&& !TAGS_BY_NAME.get(tag.getName()).getId().equals(tag.getId()))
			throw new TagAlreadyExistsException();

		TAGS_BY_ID.put(tag.getId(), tag);
		TAGS_BY_NAME.put(tag.getName(), tag);

		return tag;
	}

	public TagDto find(Integer id)
	{
		return TAGS_BY_ID.get(id);
	}

	public TagDto findByName(String name)
	{
		return TAGS_BY_NAME.get(name);
	}

	public List<TagDto> findAll()
	{
		return TAGS_BY_ID.values().stream().sorted(Comparator.comparing(TagDto::getType).thenComparing(TagDto::getName)).collect(Collectors.toList());
	}

	public TagTypeWrapper findAllGroupedByType()
	{
		TagTypeWrapper tagTypeWrapper = new TagTypeWrapper();

		for (TagDto tag : TAGS_BY_ID.values())
		{
			tagTypeWrapper.add(tag);
		}

		return tagTypeWrapper;
	}

	protected void clear()
	{
		TAGS_BY_ID.clear();
		TAGS_BY_NAME.clear();
		ID_GENARATOR.set(0);
	}

	protected void sort(List<TagDto> tags)
	{
		Collections.sort(tags, Comparator.comparing(TagDto::getType).thenComparing(TagDto::getName));
	}
}