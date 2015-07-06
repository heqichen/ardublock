package com.ardublock.core;

import java.util.LinkedList;
import java.util.List;

public class ExampleReader
{
	public List<Example> getExampleList()
	{
		Example e = new Example();
		e.setName("1. blink");
		e.setFilename("02-blink.abp");
		e.setTutorialLink("http://ardublock.heqichen.cn");
		
		LinkedList<Example> ret = new LinkedList<Example>();
		
		ret.add(e);
		
		return ret;
		
	}
}
