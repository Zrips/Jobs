package com.gamingmesh.jobs.util;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.Scanner;
import java.util.UUID;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import com.google.common.base.Charsets;

public class UUIDConverter
{
	public static String getNameFromUUID(String uuid)
	{
		String name = null;
		try
		{
			URL url = new URL("https://sessionserver.mojang.com/session/minecraft/profile/" + uuid.replaceAll("-", ""));
			Scanner jsonScanner = new Scanner(url.openConnection().getInputStream(), "UTF-8");
			String json = jsonScanner.next();
			name = (((JSONObject)new JSONParser().parse(json)).get("name")).toString();
			jsonScanner.close();
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
		return name;
	}
	
	public static String getUUIDFromName(String name, boolean onlinemode)
	{
		return getUUIDFromName(name, onlinemode, true);
	}

	public static String getUUIDFromName(String name, boolean onlinemode, boolean withSeperators)
	{
		String uuid = null;
		if (onlinemode)
		{
			try
	    	{
	    		BufferedReader in = new BufferedReader(new InputStreamReader(new URL("https://api.mojang.com/users/profiles/minecraft/" + name).openStream()));
	    		uuid = (((JSONObject)new JSONParser().parse(in)).get("id")).toString().replaceAll("\"", "");
	    		in.close();
	    	}
	    	catch (Exception e)
	    	{
	    		e.printStackTrace();
	    		uuid = UUID.nameUUIDFromBytes(("OfflinePlayer:" + name).getBytes(Charsets.UTF_8)).toString();
	    	}
		}
		else
		{
			uuid = UUID.nameUUIDFromBytes(("OfflinePlayer:" + name).getBytes(Charsets.UTF_8)).toString();
		}
		if(uuid != null)
		{
			if(withSeperators)
    		{
    			if(!uuid.contains("-"))
    			{
    				return uuid = uuid.replaceAll("(\\w{8})(\\w{4})(\\w{4})(\\w{4})(\\w{12})", "$1-$2-$3-$4-$5");
    			}
    		}
    		else
    		{
    			uuid = uuid.replaceAll("-", "");
    		}
		}
		return uuid;
	}
}
