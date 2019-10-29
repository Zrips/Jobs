package com.gamingmesh.jobs.CMIGUI;

import com.gamingmesh.jobs.CMIGUI.GUIManager.CommandType;

class GUIButtonCommand {
    private String command;
    private CommandType vis = CommandType.gui;

    public GUIButtonCommand(String command) {
	this.command = command;
    }

    public GUIButtonCommand(String command, CommandType vis) {
	this.command = command;
	this.vis = vis;
    }

    public String getCommand() {
	return command;
    }

    public void setCommand(String command) {
	this.command = command;
    }

    public CommandType getCommandType() {
	return vis;
    }

    public void setCommandType(CommandType vis) {
	this.vis = vis;
    }

}
