/**
 * Jobs Plugin for Bukkit
 * Copyright (C) 2011 Zak Ford <zak.j.ford@gmail.com>
 * 
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package com.gamingmesh.jobs.config;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;

import org.bukkit.configuration.file.YamlConfiguration;

import com.google.common.io.Files;

/* 
 * Based on CommentedYamlConfiguration by dumptruckman
 * https://github.com/dumptruckman/PluginBase/blob/master/bukkit/src/main/java/com/dumptruckman/minecraft/pluginbase/config/CommentedYamlConfiguration.java
 */

public class CommentedYamlConfiguration extends YamlConfiguration {
    private HashMap<String, String> comments;
   

    public CommentedYamlConfiguration() {
        super();
        comments = new HashMap<String, String>();
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

        FileWriter writer = new FileWriter(file);

        try {
            writer.write(data);
        } finally {
            writer.close();
        }
    }
    private String insertComments(String yaml) {       
        // if there's comments to add, we need to add comments
        if (!comments.isEmpty()) {
            // String array of each line in the config file
            String[] yamlContents = yaml.split("[" + System.getProperty("line.separator") + "]");

            // This will hold the entire newly formatted config
            StringBuilder newContents = new StringBuilder();
            // This holds the current path the lines are at in the config
            StringBuilder currentPath = new StringBuilder();
            // This tells if the specified path has already been commented
            boolean commentedPath = false;
            // This flags if the line is a node or unknown text.
            boolean node = false;
            // The depth of the path. (number of words separated by periods - 1)
            int depth = 0;

            // This will cause the first line to be ignored.
            boolean firstLine = true;
            // Loop through the config lines
            for (final String line : yamlContents) {
                if (firstLine) {
                    firstLine = false;
                    if (line.startsWith("#")) {
                        continue;
                    }
                }
                // If the line is a node (and not something like a list value)
                if (line.contains(": ") || (line.length() > 1 && line.charAt(line.length() - 1) == ':')) {
                    // This is a new node so we need to mark it for commenting (if there are comments)
                    commentedPath = false;
                    // This is a node so flag it as one
                    node = true;

                    // Grab the index of the end of the node name
                    int index = 0;
                    index = line.indexOf(": ");
                    if (index < 0) {
                        index = line.length() - 1;
                    }
                    // If currentPath is empty, store the node name as the currentPath. (this is only on the first iteration, i think)
                    if (currentPath.toString().isEmpty()) {
                        currentPath = new StringBuilder(line.substring(0, index));
                    } else {
                        // Calculate the whitespace preceding the node name
                        int whiteSpace = 0;
                        for (int n = 0; n < line.length(); n++) {
                            if (line.charAt(n) == ' ') {
                                whiteSpace++;
                            } else {
                                break;
                            }
                        }
                        // Find out if the current depth (whitespace * 2) is greater/lesser/equal to the previous depth
                        if (whiteSpace / 2 > depth) {
                            // Path is deeper.  Add a . and the node name
                            currentPath.append(".").append(line.substring(whiteSpace, index));
                            depth++;
                        } else if (whiteSpace / 2 < depth) {
                            // Path is shallower, calculate current depth from whitespace (whitespace / 2) and subtract that many levels from the currentPath
                            int newDepth = whiteSpace / 2;
                            for (int i = 0; i < depth - newDepth; i++) {
                                currentPath.replace(currentPath.lastIndexOf("."), currentPath.length(), "");
                            }
                            // Grab the index of the final period
                            int lastIndex = currentPath.lastIndexOf(".");
                            if (lastIndex < 0) {
                                // if there isn't a final period, set the current path to nothing because we're at root
                                currentPath = new StringBuilder();
                            } else {
                                // If there is a final period, replace everything after it with nothing
                                currentPath.replace(currentPath.lastIndexOf("."), currentPath.length(), "").append(".");
                            }
                            // Add the new node name to the path
                            currentPath.append(line.substring(whiteSpace, index));
                            // Reset the depth
                            depth = newDepth;
                        } else {
                            // Path is same depth, replace the last path node name to the current node name
                            int lastIndex = currentPath.lastIndexOf(".");
                            if (lastIndex < 0) {
                                // if there isn't a final period, set the current path to nothing because we're at root
                                currentPath = new StringBuilder();
                            } else {
                                // If there is a final period, replace everything after it with nothing
                                currentPath.replace(currentPath.lastIndexOf("."), currentPath.length(), "").append(".");
                            }
                            //currentPath = currentPath.replace(currentPath.substring(currentPath.lastIndexOf(".")), "");
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
                        // If there's a comment for the current path, retrieve it and flag that path as already commented
                        comment = comments.get(currentPath.toString());
                    }
                    if (comment != null && !comment.isEmpty()) {
                        // Add the comment to the beginning of the current line
                        newLine.insert(0, System.getProperty("line.separator")).insert(0, comment);
                        comment = null;
                        commentedPath = true;
                    }
                }
                newLine.append(System.getProperty("line.separator"));
                // Add the (modified) line to the total config String
                newContents.append(newLine.toString());
            }
            
            return newContents.toString();
        }
        return yaml;
    }

    /**
     * Adds a comment just before the specified path.  The comment can be multiple lines.  An empty string will indicate a blank line.
     *
     * @param path         Configuration path to add comment.
     * @param commentLines Comments to add.  One String per line.
     */
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
}
