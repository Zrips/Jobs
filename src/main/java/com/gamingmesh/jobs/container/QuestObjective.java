package com.gamingmesh.jobs.container;

import java.util.ArrayList;
import java.util.List;

import com.gamingmesh.jobs.Jobs;
import com.gamingmesh.jobs.config.ConfigManager.KeyValues;

import net.Zrips.CMILib.Messages.CMIMessages;

public class QuestObjective {

    private int id;
    private String meta;
    private String name;
    private int amount = Integer.MAX_VALUE;
    private ActionType action = null;

    public static List<QuestObjective> get(String objective, String jobName) {

        String[] split = objective.split(";", 3);

        List<QuestObjective> list = new ArrayList<QuestObjective>();

        if (split.length < 2) {
            CMIMessages.consoleMessage("Job " + jobName + " has incorrect quest objective (" + objective + ")!");
            return list;
        }

        ActionType actionType = ActionType.getByName(split[0]);

        if (actionType == null)
            return list;

        try {

            String mats = split[1].toUpperCase();
            String[] co = mats.split(",");

            int amount = 1;
            if (split.length <= 3)
                amount = Integer.parseInt(split[2]);

            if (co.length > 0) {
                for (String materials : co) {
                    KeyValues kv = Jobs.getConfigManager().getKeyValue(materials, actionType, jobName);

                    if (kv == null)
                        continue;

                    list.add(new QuestObjective(actionType, kv.getId(), kv.getMeta(), (kv.getType() + kv.getSubType()).toUpperCase(), amount));
                }
            } else {
                KeyValues kv = Jobs.getConfigManager().getKeyValue(mats, actionType, jobName);

                if (kv != null) {
                    list.add(new QuestObjective(actionType, kv.getId(), kv.getMeta(), (kv.getType() + kv.getSubType()).toUpperCase(), amount));
                }
            }
        } catch (Exception e) {
            CMIMessages.consoleMessage("Job " + jobName + " has incorrect quest objective (" + objective + ")!");
        }

        return list;
    }

    public QuestObjective(ActionType action, int id, String meta, String name, int amount) {
        this.action = action;
        this.id = id;
        this.meta = meta;
        this.name = name;
        this.amount = amount;
    }

    public int getTargetId() {
        return id;
    }

    public void setTargetId(int id) {
        this.id = id;
    }

    public String getTargetMeta() {
        return meta;
    }

    public void setTargetMeta(String meta) {
        this.meta = meta;
    }

    public String getTargetName() {
        return name;
    }

    public void setTargetName(String name) {
        this.name = name;
    }

    public int getAmount() {
        return amount;
    }

    public void setAmount(int amount) {
        this.amount = amount;
    }

    public ActionType getAction() {
        return action;
    }

    public void setAction(ActionType action) {
        this.action = action;
    }

    public boolean same(QuestObjective obj) {
        return obj.id == this.id && obj.meta.equals(this.meta) && obj.name.equals(this.name) && obj.amount == this.amount && obj.action == this.action;
    }
}
