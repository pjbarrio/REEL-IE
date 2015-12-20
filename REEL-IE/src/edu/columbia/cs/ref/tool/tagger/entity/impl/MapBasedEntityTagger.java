package edu.columbia.cs.ref.tool.tagger.entity.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import com.google.gdata.util.common.base.Pair;

import edu.columbia.cs.ref.model.Document;
import edu.columbia.cs.ref.model.entity.Entity;
import edu.columbia.cs.ref.tool.tagger.entity.EntityTagger;
import edu.columbia.cs.ref.tool.tagger.span.impl.EntitySpan;

public class MapBasedEntityTagger extends EntityTagger<EntitySpan, Entity> {

	private Map<Integer, List<Pair<Long,Pair<Integer, Integer>>>> entitiesMap;
	private Map<Integer, String> entityTable;

	public MapBasedEntityTagger(Set<String> tags,
			Map<Integer, List<Pair<Long,Pair<Integer, Integer>>>> entitiesMap,
			Map<Integer, String> entityTable) {
		
		super(tags);
		
		this.entitiesMap = entitiesMap;
		
		this.entityTable = entityTable;
			
	}

	@Override
	protected List<EntitySpan> findSpans(Document d) {
		
		List<EntitySpan> ess = new ArrayList<EntitySpan>();
		
		int first,lenght;
		
		long id;
		
		for (Entry<Integer, List<Pair<Long, Pair<Integer,Integer>>>> entry : entitiesMap.entrySet()) {
			
			String tag = entityTable.get(entry.getKey());
			
			for (int i = 0; i < entry.getValue().size(); i++) {
				
				id = entry.getValue().get(i).getFirst();
				
				first = entry.getValue().get(i).getSecond().getFirst();
				
				lenght = entry.getValue().get(i).getSecond().getSecond()-first;
				
				ess.add(new EntitySpan(Long.toString(id), tag, d.getSubstring(first, lenght), first, lenght));
				
			}
			
		}
		
		return ess;
		
	}

	

}
