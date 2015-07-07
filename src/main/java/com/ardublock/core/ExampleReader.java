package com.ardublock.core;

import java.io.InputStream;
import java.util.LinkedList;
import java.util.List;
import java.util.ResourceBundle;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

public class ExampleReader
{
	public List<Example> getExampleList()
	{
		List<Example> ret = new LinkedList<Example>();
		
		try
		{
			ResourceBundle exampleNameBundle = ResourceBundle.getBundle("com/ardublock/examples/examples");
			
			
			InputStream exampleConfigInputStream = this.getClass().getResourceAsStream("/com/ardublock/examples/examples.xml");
			DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
			DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
			Document doc = dBuilder.parse(exampleConfigInputStream);
			doc.getDocumentElement().normalize();
			
			NodeList exampleList = doc.getElementsByTagName("example");
			
			for (int i=0; i<exampleList.getLength(); ++i)
			{
				Element elem = (Element)exampleList.item(i);
				Example e = new Example();
				e.setName(exampleNameBundle.getString((elem.getElementsByTagName("name").item(0).getTextContent())));
				e.setFilename(elem.getElementsByTagName("filename").item(0).getTextContent());
				
				NodeList linkList = elem.getElementsByTagName("link");
				if (linkList.getLength() > 0)
				{
					e.setTutorialLink(linkList.item(0).getTextContent());
				}
				
				ret.add(e);
				
			}
		}
		catch (Exception e)
		{
			e.printStackTrace();
			return ret;
		}
		
		return ret;
		
	}
}
