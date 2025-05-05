package com.gamingmesh.jobs.container;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import org.bukkit.Bukkit;
import org.bukkit.event.server.ServerCommandEvent;

import com.gamingmesh.jobs.Jobs;
import com.gamingmesh.jobs.actions.EnchantActionInfo;
import com.gamingmesh.jobs.stuff.Util;

import net.Zrips.CMILib.Version.Schedulers.CMIScheduler;

public class QuestProgression {

    private Quest quest;

    private long validUntil;
    private boolean givenReward = false;

    private final Map<QuestObjective, Integer> done = new HashMap<>();

    public QuestProgression(Quest quest) {
        this.quest = quest;

        validUntil = quest.getValidUntil();
    }

    public Quest getQuest() {
        if (quest == null)
            return null;

        Job job = quest.getJob();
        return job == null ? null : job.getQuest(quest.getConfigName());
    }

    public void setQuest(Quest quest) {
        this.quest = quest;
    }

    public int getTotalAmountNeeded() {
        int amountNeeded = 0;
        for (Map<String, QuestObjective> oneA : quest.getObjectives().values()) {
            for (QuestObjective one : oneA.values()) {
                amountNeeded += one.getAmount();
            }
        }
        return amountNeeded;
    }

    public void reset() {
        validUntil = quest.getValidUntil();
        givenReward = false;
        done.clear();
    }

    public int getTotalAmountDone() {
        int amountDone = 0;

        for (Integer one : done.values()) {
            amountDone += one;
        }

        return amountDone;
    }

    public int getAmountDone(QuestObjective objective) {

        Integer result = done.get(objective);

        if (result != null)
            return result;

        // We might need to readd questObjectives in case config file got reloaded and new objective objects have been created which no longer match previous cache
        Map<String, QuestObjective> objectives = quest.getJob().getQuest(quest.getConfigName()).getObjectives().get(objective.getAction());

        QuestObjective globalObj = null;
        QuestObjective replaceableObj = null;

        for (QuestObjective one : objectives.values()) {
            if (!one.same(objective))
                continue;
            globalObj = one;
            break;
        }

        for (QuestObjective one : done.keySet()) {
            if (!one.same(objective))
                continue;
            replaceableObj = one;
            break;
        }

        if (globalObj == null || replaceableObj == null)
            return 0;

        Integer record = done.get(replaceableObj);

        if (record == null)
            return 0;

        done.remove(replaceableObj);
        done.put(globalObj, record);
        return record;

    }

    public void setAmountDone(QuestObjective objective, int amountDone) {
        if (quest.hasObjective(objective)) {
            done.put(objective, amountDone);
        }
    }

    public long getValidUntil() {
        return validUntil;
    }

    public void setValidUntil(long validUntil) {
        this.validUntil = validUntil;
    }

    public boolean isValid() {
        return !isEnded();
    }

    public boolean isEnded() {
        return validUntil < System.currentTimeMillis();
    }

    public boolean isCompleted() {
        for (Map<String, QuestObjective> oneA : quest.getObjectives().values()) {
            for (QuestObjective one : oneA.values()) {
                Integer amountDone = done.get(one);
                if (amountDone == null || amountDone < one.getAmount())
                    return false;
            }
        }
        return true;
    }

    public void processQuest(JobsPlayer jPlayer, ActionInfo action) {
        if (quest.isStopped() || !quest.hasAction(action.getType()))
            return;

        Map<String, QuestObjective> byAction = quest.getObjectives().get(action.getType());
        QuestObjective objective = objectiveForAction(action);

        if (byAction != null && objective == null)
            return;

        org.bukkit.entity.Player player = jPlayer.getPlayer();

        for (String area : quest.getRestrictedAreas()) {
            for (Entry<String, RestrictedArea> a : Jobs.getRestrictedAreaManager().getRestrictedAreas().entrySet()) {
                if (a.getKey().equalsIgnoreCase(area) && a.getValue().inRestrictedArea(player.getLocation())) {
                    return;
                }
            }
        }

        boolean completed = isCompleted();

        Job questJob = quest.getJob();
        if (questJob != null && completed) {
            int maxQuest = jPlayer.getPlayerMaxQuest(questJob.getName());
            if (maxQuest > 0 && jPlayer.getDoneQuests() >= maxQuest) {
                return;
            }
        }

        if (!completed && objective != null) {
            Integer old = done.getOrDefault(objective, 0);
            done.put(objective, old < objective.getAmount() ? old + 1 : objective.getAmount());
        }

        jPlayer.setSaved(false);

        //needs to re-call isCompleted() because it might have changed above.
        if (!isCompleted() || !player.isOnline() || givenReward)
            return;

        givenReward = true;

        jPlayer.addDoneQuest(questJob);

        if (quest.getRewardAmount() > 0) {
            Jobs.getEconomy().getEconomy().depositPlayer(player, quest.getRewardAmount());
        }

        // Should be run with scheduler to put it on main thread if its not on it
        CMIScheduler.runTask(Jobs.getInstance(), () -> {
            for (String one : quest.getRewardCmds()) {
                ServerCommandEvent ev = new ServerCommandEvent(Bukkit.getConsoleSender(), one.replace("[playerName]", player.getName()));
                Bukkit.getPluginManager().callEvent(ev);
                if (!ev.isCancelled()) {
                    Bukkit.dispatchCommand(Bukkit.getConsoleSender(), ev.getCommand().startsWith("/") ? ev.getCommand().substring(1) : ev.getCommand());
                }
            }
        });
    }

    public boolean isGivenReward() {
        return givenReward;
    }

    public void setGivenReward(boolean givenReward) {
        this.givenReward = givenReward;
    }

    private static boolean objectiveKeyMatches(String objectiveKey, ActionInfo actionInfo) {
        if (actionInfo instanceof EnchantActionInfo) {
            return Util.enchantMatchesActionInfo(objectiveKey, (EnchantActionInfo) actionInfo);
        }

        return (objectiveKey.equalsIgnoreCase(actionInfo.getNameWithSub()) ||
            objectiveKey.equalsIgnoreCase(actionInfo.getName()));
    }

    private QuestObjective objectiveForAction(ActionInfo actionInfo) {
        Map<String, QuestObjective> byAction = quest.getObjectives().get(actionInfo.getType());
        if (byAction == null) {
            return null;
        }

        for (Map.Entry<String, QuestObjective> objectiveEntry : byAction.entrySet()) {
            String objectiveKey = objectiveEntry.getKey();

            if (objectiveKeyMatches(objectiveKey, actionInfo)) {
                return objectiveEntry.getValue();
            }
        }

        return null;
    }
}
