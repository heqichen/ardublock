package com.ardublock.core;

import java.util.LinkedList;
import java.util.List;

public class ExampleReader
{
	public List<Example> getExampleList()
	{
		LinkedList<Example> ret = new LinkedList<Example>();
		
		
		Example e = new Example();
		e.setName("1. blink");
		e.setFilename("02-blink.abp");
		e.setTutorialLink("http://ardublock.heqichen.cn");
		ret.add(e);
		
		e = new Example();
		e.setName("2. blink");
		e.setFilename("02-blink.abp");
		//e.setTutorialLink("http://ardublock.heqichen.cn");
		ret.add(e);
		
		return ret;
		
	}
}
