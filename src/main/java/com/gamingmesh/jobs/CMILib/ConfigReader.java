package com.gamingmesh.jobs.CMILib;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.nio.charset.Charset;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import org.bukkit.Location;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.YamlConfiguration;

import com.gamingmesh.jobs.Jobs;
import com.google.common.io.Files;

/* 
 * Made by Zrips
 */

public class ConfigReader extends YamlConfiguration {
    private HashMap<String, String> comments;
    private YamlConfiguration config;
    private String p = null;
    private File file = null;

    public ConfigReader(String fileName) throws Exception {
	this(new File(Jobs.getFolder(), fileName));
    }

    public ConfigReader(File file) throws Exception {
	super();
	comments = new HashMap<>();
	this.file = file;
	this.config = getyml(file);
    }

    @Override
    public void save(String file) throws IOException {
	if (file == null) {
	    throw new IllegalArgumentException("File cannot be null");
	}

	save(new File(file));
    }

    @Override
    public void save(File file) throws IOException {
	if (file == null) {
	    throw new IllegalArgumentException("File cannot be null");
	}
	Files.createParentDirs(file);
	String data = insertComments(saveToString());
	PrintWriter writer = new PrintWriter(file, "UTF-8");
	try {
	    writer.write(data);
	} finally {
	    writer.close();
	}
    }

    private String insertComments(String yaml) {
	if (!comments.isEmpty()) {
	    String[] yamlContents = yaml.split("[" + System.getProperty("line.separator") + "]");
	    StringBuilder newContents = new StringBuilder();
	    StringBuilder currentPath = new StringBuilder();
	    boolean commentedPath = false;
	    boolean node = false;
	    int depth = 0;

	    boolean firstLine = true;
	    for (final String line : yamlContents) {
		if (firstLine) {
		    firstLine = false;
		    if (line.startsWith("#")) {
			continue;
		    }
		}

		boolean keyOk = true;
		if (line.contains(": ")) {
		    int index = 0;
		    index = line.indexOf(": ");
		    if (index < 0) {
			index = line.length() - 1;
		    }
		    int whiteSpace = 0;
		    for (int n = 0; n < line.length(); n++) {
			if (line.charAt(n) == ' ') {
			    whiteSpace++;
			} else {
			    break;
			}
		    }
		    String key = line.substring(whiteSpace, index);
		    if (key.contains(" "))
			keyOk = false;
		    else if (key.contains("&"))
			keyOk = false;
		    else if (key.contains("."))
			keyOk = false;
		    else if (key.contains("'"))
			keyOk = false;
		    else if (key.contains("\""))
			keyOk = false;
		}

		if (line.contains(": ") && keyOk || (line.length() > 1 && line.charAt(line.length() - 1) == ':')) {
		    commentedPath = false;
		    node = true;

		    int index = 0;
		    index = line.indexOf(": ");
		    if (index < 0) {
			index = line.length() - 1;
		    }
		    if (currentPath.toString().isEmpty()) {
			currentPath = new StringBuilder(line.substring(0, index));
		    } else {
			int whiteSpace = 0;
			for (int n = 0; n < line.length(); n++) {
			    if (line.charAt(n) == ' ') {
				whiteSpace++;
			    } else {
				break;
			    }
			}
			if (whiteSpace / 2 > depth) {
			    currentPath.append(".").append(line.substring(whiteSpace, index));
			    depth++;
			} else if (whiteSpace / 2 < depth) {
			    int newDepth = whiteSpace / 2;
			    for (int i = 0; i < depth - newDepth; i++) {
				currentPath.replace(currentPath.lastIndexOf("."), currentPath.length(), "");
			    }
			    int lastIndex = currentPath.lastIndexOf(".");
			    if (lastIndex < 0) {
				currentPath = new StringBuilder();
			    } else {
				currentPath.replace(currentPath.lastIndexOf("."), currentPath.length(), "").append(".");
			    }
			    currentPath.append(line.substring(whiteSpace, index));
			    depth = newDepth;
			} else {
			    int lastIndex = currentPath.lastIndexOf(".");
			    if (lastIndex < 0) {
				currentPath = new StringBuilder();
			    } else {
				currentPath.replace(currentPath.lastIndexOf("."), currentPath.length(), "").append(".");
			    }
			    currentPath.append(line.substring(whiteSpace, index));
			}
		    }
		} else {
		    node = false;
		}
		StringBuilder newLine = new StringBuilder(line);
		if (node) {
		    String comment = null;
		    if (!commentedPath) {
			comment = comments.get(currentPath.toString());
		    }
		    if (comment != null && !comment.isEmpty()) {
			newLine.insert(0, System.getProperty("line.separator")).insert(0, comment);
			comment = null;
			commentedPath = true;
		    }
		}
		newLine.append(System.getProperty("line.separator"));
		newContents.append(newLine.toString());
	    }

	    return newContents.toString();
	}
	return yaml;
    }

    public void addComment(String path, String... commentLines) {
	StringBuilder commentstring = new StringBuilder();
	String leadingSpaces = "";
	for (int n = 0; n < path.length(); n++) {
	    if (path.charAt(n) == '.') {
		leadingSpaces += "  ";
	    }
	}
	for (String line : commentLines) {
	    if (!line.isEmpty()) {
		line = leadingSpaces + "# " + line;
	    }
	    if (commentstring.length() > 0) {
		commentstring.append(System.getProperty("line.separator"));
	    }
	    commentstring.append(line);
	}
	comments.put(path, commentstring.toString());
    }

    public YamlConfiguration getyml(File file) throws Exception {
	YamlConfiguration config = new YamlConfiguration();
	FileInputStream fileinputstream = null;

	try {
	    fileinputstream = new FileInputStream(file);
	    InputStreamReader str = new InputStreamReader(fileinputstream, Charset.forName("UTF-8"));
	    config.load(str);
	    str.close();
	} catch (FileNotFoundException e) {
	} catch (InvalidConfigurationException | IOException e) {
	    e.printStackTrace();
	    saveToBackup();
	    throw e;
	} finally {
	    if (fileinputstream != null)
		try {
		    fileinputstream.close();
		} catch (IOException e) {
		    e.printStackTrace();
		}
	}

	return config;
    }

    public void saveToBackup() {
	File cc = new File(Jobs.getFolder(), "FileBackups");
	if (!cc.isDirectory())
	    cc.mkdir();

	Date date = new Date();
	SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH-mm-ss ");
	String newFileName = dateFormat.format(date) + file.getName();

	Jobs.consoleMsg("&cFailed to load " + file.getName() + "! Backup have been saved into " + Jobs.getFolder().getPath() + File.separator + "FileBackups"
	    + File.separator + newFileName);

	File f = new File(Jobs.getFolder(), "FileBackups" + File.separator + newFileName);
//	file.renameTo(f);
	try {
	    Files.copy(file, f);
	} catch (IOException e) {
	    e.printStackTrace();
	}
    }

    public void save() {
	try {
	    save(file);
	} catch (IOException e) {
	    e.printStackTrace();
	}
    }

    private static void newLn(StringBuilder header) {
	header.append(System.lineSeparator());
    }

    private static StringBuilder formStringBuilder(List<String> list) {
	StringBuilder header = new StringBuilder();
	for (String one : list) {
	    header.append(one);
	    newLn(header);
	}
	return header;
    }

    public void header(List<String> list) {
	options().header(formStringBuilder(list).toString());
    }

    String[] waitingComment = null;

    private void checkWaitingComment(String path) {
	if (waitingComment == null)
	    return;
	addComment(path, waitingComment);
	waitingComment = null;
    }

    public YamlConfiguration getC() {
	return config;
    }

    public void copyDefaults(boolean value) {
	getC().options().copyDefaults(value);
    }

    private String process(String path, Object value) {
	if (this.p != null)
	    path = this.p + path;
	checkWaitingComment(path);
	config.addDefault(path, value);
	copySetting(path);
	return path;
    }

    public Boolean get(String path, Boolean boo) {
	path = process(path, boo);
	return config.getBoolean(path);
    }

    public Object get(String path, Location boo) {
	path = process(path, boo);
	return config.get(path);
    }

    public int get(String path, int boo) {
	path = process(path, boo);
	return config.getInt(path);
    }

    public int get(String path, Integer boo) {
	path = process(path, boo);
	return config.getInt(path);
    }

    public List<Integer> getIntList(String path, List<Integer> list) {
	path = process(path, list);
	return config.getIntegerList(path);
    }

    public List<String> get(String path, List<String> list) {
	path = process(path, list);
	return config.getStringList(path);
    }

    public String get(String path, String boo) {
	path = process(path, boo);
	return config.getString(path);
    }

    public Double get(String path, Double boo) {
	path = process(path, boo);
	return config.getDouble(path);
    }

    private synchronized void copySetting(String path) {
	set(path, config.get(path));
    }

    public void resetP() {
	p = null;
    }

    public void setP(String cmd) {
	this.p = "command." + cmd + ".info.";
    }
}
