package com.gamingmesh.jobs.dao;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map.Entry;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.OfflinePlayer;
import org.bukkit.World;
import org.bukkit.entity.Player;

import com.gamingmesh.jobs.Jobs;
import com.gamingmesh.jobs.container.ArchivedJobs;
import com.gamingmesh.jobs.container.BlockProtection;
import com.gamingmesh.jobs.container.Convert;
import com.gamingmesh.jobs.container.CurrencyType;
import com.gamingmesh.jobs.container.DBAction;
import com.gamingmesh.jobs.container.ExploreChunk;
import com.gamingmesh.jobs.container.ExploreRegion;
import com.gamingmesh.jobs.container.Job;
import com.gamingmesh.jobs.container.JobProgression;
import com.gamingmesh.jobs.container.JobsPlayer;
import com.gamingmesh.jobs.container.JobsWorld;
import com.gamingmesh.jobs.container.Log;
import com.gamingmesh.jobs.container.LogAmounts;
import com.gamingmesh.jobs.container.PlayerInfo;
import com.gamingmesh.jobs.container.PlayerPoints;
import com.gamingmesh.jobs.container.TopList;
import com.gamingmesh.jobs.dao.JobsManager.DataBaseType;
import com.gamingmesh.jobs.economy.PaymentData;
import com.gamingmesh.jobs.stuff.TimeManage;
import com.gamingmesh.jobs.stuff.Util;

public abstract class JobsDAO {

    private JobsConnectionPool pool;
    private static String prefix;
    private Jobs plugin;

    private static DataBaseType dbType = DataBaseType.SqLite;

    // Not in use currently
    public enum TablesFieldsType {
	decimal, number, text, varchar, stringList, stringLongMap, stringIntMap, locationMap, state, location, longNumber;
    }

    public enum worldsTableFields implements JobsTableInterface {
	name("varchar(36)", true);

	private String type;

	private boolean unique = false;

	worldsTableFields(String type) {
	    this(type, false);
	}

	worldsTableFields(String type, boolean unique) {
	    this.type = type;
	    this.unique = unique;
	}

	@Override
	public String getCollumn() {
	    return this.name();
	}

	@Override
	public String getType() {
	    return type;
	}

	@Override
	public boolean isUnique() {
	    return unique;
	}
    }

    public enum jobsNameTableFields implements JobsTableInterface {
	name("varchar(36)", true);

	private String type;

	private boolean unique = false;

	jobsNameTableFields(String type) {
	    this(type, false);
	}

	jobsNameTableFields(String type, boolean unique) {
	    this.type = type;
	    this.unique = unique;
	}

	@Override
	public String getCollumn() {
	    return this.name();
	}

	@Override
	public String getType() {
	    return type;
	}

	@Override
	public boolean isUnique() {
	    return unique;
	}
    }

    public enum UserTableFields implements JobsTableInterface {
	player_uuid("varchar(36)"),
	username("text"),
	seen("bigint"),
	donequests("int"),
	quests("text");

	private String type;

	UserTableFields(String type) {
	    this.type = type;
	}

	@Override
	public String getCollumn() {
	    return this.name();
	}

	@Override
	public String getType() {
	    return type;
	}

	@Override
	public boolean isUnique() {
	    return false;
	}
    }

    public enum JobsTableFields implements JobsTableInterface {
	userid("int"),
	job("text"),
	experience("double"),
	level("int"),
	jobid("int");

	private String type;

	JobsTableFields(String type) {
	    this.type = type;
	}

	@Override
	public String getCollumn() {
	    return this.name();
	}

	@Override
	public String getType() {
	    return type;
	}

	@Override
	public boolean isUnique() {
	    return false;
	}
    }

    public enum ArchiveTableFields implements JobsTableInterface {
	userid("int"),
	job("text"),
	experience("int"),
	level("int"),
	left("bigint"),
	jobid("int");

	private String type;

	ArchiveTableFields(String type) {
	    this.type = type;
	}

	@Override
	public String getCollumn() {
	    return this.name();
	}

	@Override
	public String getType() {
	    return type;
	}

	@Override
	public boolean isUnique() {
	    return false;
	}
    }

    public enum BlockTableFields implements JobsTableInterface {
	world("varchar(36)"),
	x("int"),
	y("int"),
	z("int"),
	recorded("bigint"),
	resets("bigint"),
	worldid("int");

	private String type;

	BlockTableFields(String type) {
	    this.type = type;
	}

	@Override
	public String getCollumn() {
	    return this.name();
	}

	@Override
	public String getType() {
	    return type;
	}

	@Override
	public boolean isUnique() {
	    return false;
	}
    }

    public enum LimitTableFields implements JobsTableInterface {
	userid("int"),
	type("varchar(36)"),
	collected("double"),
	started("bigint"),
	typeid("int");

	private String ttype;

	LimitTableFields(String type) {
	    this.ttype = type;
	}

	@Override
	public String getCollumn() {
	    return this.name();
	}

	@Override
	public String getType() {
	    return ttype;
	}

	@Override
	public boolean isUnique() {
	    return false;
	}
    }

    public enum LogTableFields implements JobsTableInterface {
	userid("int"),
	time("bigint"),
	action("varchar(20)"),
	itemname("text"),
	count("int"),
	money("double"),
	exp("double"),
	points("double");

	private String type;

	LogTableFields(String type) {
	    this.type = type;
	}

	@Override
	public String getCollumn() {
	    return this.name();
	}

	@Override
	public String getType() {
	    return type;
	}

	@Override
	public boolean isUnique() {
	    return false;
	}
    }

    public enum PointsTableFields implements JobsTableInterface {
	userid("int"),
	totalpoints("double"),
	currentpoints("double");

	private String type;

	PointsTableFields(String type) {
	    this.type = type;
	}

	@Override
	public String getCollumn() {
	    return this.name();
	}

	@Override
	public String getType() {
	    return type;
	}

	@Override
	public boolean isUnique() {
	    return false;
	}
    }

    public enum ExploreDataTableFields implements JobsTableInterface {
	worldname("varchar(64)"),
	chunkX("int"),
	chunkZ("int"),
	playerNames("text"),
	worldid("int");

	private String type;

	ExploreDataTableFields(String type) {
	    this.type = type;

	}

	@Override
	public String getCollumn() {
	    return this.name();
	}

	@Override
	public String getType() {
	    return type;
	}

	@Override
	public boolean isUnique() {
	    return false;
	}
    }

    public enum DBTables {
	JobNameTable("jobNames",
	    "CREATE TABLE IF NOT EXISTS `[tableName]` (`id` int NOT NULL AUTO_INCREMENT PRIMARY KEY[fields]);",
	    "CREATE TABLE IF NOT EXISTS `[tableName]` (`id` INTEGER PRIMARY KEY AUTOINCREMENT[fields]);", jobsNameTableFields.class),
	WorldTable("worlds",
	    "CREATE TABLE IF NOT EXISTS `[tableName]` (`id` int NOT NULL AUTO_INCREMENT PRIMARY KEY[fields]);",
	    "CREATE TABLE IF NOT EXISTS `[tableName]` (`id` INTEGER PRIMARY KEY AUTOINCREMENT[fields]);", worldsTableFields.class),
	UsersTable("users",
	    "CREATE TABLE IF NOT EXISTS `[tableName]` (`id` int NOT NULL AUTO_INCREMENT PRIMARY KEY[fields]);",
	    "CREATE TABLE IF NOT EXISTS `[tableName]` (`id` INTEGER PRIMARY KEY AUTOINCREMENT[fields]);", UserTableFields.class),
	JobsTable("jobs",
	    "CREATE TABLE IF NOT EXISTS `[tableName]` (`id` int NOT NULL AUTO_INCREMENT PRIMARY KEY[fields]);",
	    "CREATE TABLE IF NOT EXISTS `[tableName]` (`id` INTEGER PRIMARY KEY AUTOINCREMENT[fields]);", JobsTableFields.class),
	ArchiveTable("archive",
	    "CREATE TABLE IF NOT EXISTS `[tableName]` (`id` int NOT NULL AUTO_INCREMENT PRIMARY KEY[fields]);",
	    "CREATE TABLE IF NOT EXISTS `[tableName]` (`id` INTEGER PRIMARY KEY AUTOINCREMENT[fields]);", ArchiveTableFields.class),
	BlocksTable("blocks",
	    "CREATE TABLE IF NOT EXISTS `[tableName]` (`id` int NOT NULL AUTO_INCREMENT PRIMARY KEY[fields]);",
	    "CREATE TABLE IF NOT EXISTS `[tableName]` (`id` INTEGER PRIMARY KEY AUTOINCREMENT[fields]);", BlockTableFields.class),
	LimitsTable("limits",
	    "CREATE TABLE IF NOT EXISTS `[tableName]` (`id` int NOT NULL AUTO_INCREMENT PRIMARY KEY[fields]);",
	    "CREATE TABLE IF NOT EXISTS `[tableName]` (`id` INTEGER PRIMARY KEY AUTOINCREMENT[fields]);", LimitTableFields.class),
	LogTable("log",
	    "CREATE TABLE IF NOT EXISTS `[tableName]` (`id` int NOT NULL AUTO_INCREMENT PRIMARY KEY[fields]);",
	    "CREATE TABLE IF NOT EXISTS `[tableName]` (`id` INTEGER PRIMARY KEY AUTOINCREMENT[fields]);", LogTableFields.class),
	ExploreDataTable("exploreData",
	    "CREATE TABLE IF NOT EXISTS `[tableName]` (`id` int NOT NULL AUTO_INCREMENT PRIMARY KEY[fields]);",
	    "CREATE TABLE IF NOT EXISTS `[tableName]` (`id` INTEGER PRIMARY KEY AUTOINCREMENT[fields]);", ExploreDataTableFields.class),
	PointsTable("points",
	    "CREATE TABLE IF NOT EXISTS `[tableName]` (`id` int NOT NULL AUTO_INCREMENT PRIMARY KEY[fields]);",
	    "CREATE TABLE IF NOT EXISTS `[tableName]` (`id` INTEGER PRIMARY KEY AUTOINCREMENT[fields]);", PointsTableFields.class);

	private String mySQL;
	private String sQlite;
	private String tableName;
	private JobsTableInterface[] c;

	DBTables(String tableName, String MySQL, String SQlite, Class<?> cc) {
	    this.tableName = tableName;
	    this.mySQL = MySQL;
	    this.sQlite = SQlite;
	    this.c = (JobsTableInterface[]) cc.getEnumConstants();
	}

	private String getQR() {
	    switch (dbType) {
	    case MySQL:
		return this.mySQL.replace("[tableName]", prefix + this.tableName);
	    case SqLite:
		return this.sQlite.replace("[tableName]", this.tableName);
	    default:
		break;
	    }
	    return "";
	}

	public String getQuery() {
	    String rp = "";
	    List<JobsTableInterface> uniques = new ArrayList<JobsTableInterface>();
	    for (JobsTableInterface one : this.getInterface()) {
		if (one.isUnique()) {
		    uniques.add(one);
		}

		rp += " , `" + one.getCollumn() + "` " + one.getType();
	    }

	    String unique = "";

	    for (JobsTableInterface one : uniques) {
		if (!unique.isEmpty()) {
		    unique += " ,";
		}
		unique += "`" + one.getCollumn() + "`";
	    }

	    if (!unique.isEmpty()) {
		switch (dbType) {
		case MySQL:
		    unique = " , UNIQUE KEY template_" + tableName + " (" + unique + ")";
		    break;
		case SqLite:
		    unique = " , UNIQUE (" + unique + ")";
		    break;
		default:
		    break;
		}
	    }

	    return getQR().replace("[fields]", rp + unique);
	}

	public JobsTableInterface[] getInterface() {
	    return this.c;
	}

	public String getTableName() {
	    return prefix + tableName;
	}
    }

    protected JobsDAO(Jobs plugin, String driverName, String url, String username, String password, String pr) {
	this.plugin = plugin;

	prefix = pr;

	try {
	    Class.forName(driverName);
	} catch (ClassNotFoundException c) {
	    c.printStackTrace();
	    return;
	}

	pool = new JobsConnectionPool(url, username, password);
    }

    public final synchronized void setUp() throws SQLException {
	if (getConnection() == null)
	    return;

	vacuum();
	setupConfig();

	try {
	    for (DBTables one : DBTables.values()) {
		createDefaultTable(one);
	    }
	    checkDefaultCollumns();
	} finally {
	}
    }

    @Deprecated
    protected abstract void setupConfig() throws SQLException;

    protected abstract void checkUpdate() throws SQLException;

    public abstract Statement prepareStatement(String query) throws SQLException;

    public abstract boolean createTable(String query) throws SQLException;

    public abstract boolean isTable(String table);

    public abstract boolean isCollumn(String table, String collumn);

    public abstract boolean truncate(String table);

    public abstract boolean addCollumn(String table, String collumn, String type);

    public abstract boolean drop(String table);

    public boolean isConnected() {
	try {
	    return pool != null && pool.getConnection() != null && !pool.getConnection().isClosed();
	} catch (SQLException e) {
	    return false;
	}
    }

    public void setAutoCommit(boolean state) {
	JobsConnection conn = getConnection();
	if (conn == null)
	    return;
	try {
	    conn.setAutoCommit(state);
	} catch (SQLException e) {
	    e.printStackTrace();
	}
    }

    public void commit() {
	JobsConnection conn = getConnection();
	if (conn == null)
	    return;

	try {
	    conn.commit();
	} catch (SQLException e) {
	    e.printStackTrace();
	}
    }

    private boolean createDefaultTable(DBTables table) {
	if (this.isTable(table.getTableName()))
	    return true;
	try {
	    this.createTable(table.getQuery());
	    return true;
	} catch (SQLException e) {
	    e.printStackTrace();
	}
	return false;
    }

    private boolean checkDefaultCollumns() {
	for (DBTables one : DBTables.values()) {
	    for (JobsTableInterface oneT : one.getInterface()) {
		if (this.isCollumn(one.getTableName(), oneT.getCollumn()))
		    continue;
		this.addCollumn(one.getTableName(), oneT.getCollumn(), oneT.getType());
	    }
	}

	return true;
    }

    public void truncateAllTables() {
	for (DBTables one : DBTables.values()) {
	    this.truncate(one.getTableName());
	}
    }

    public DataBaseType getDbType() {
	return dbType;
    }

    public void setDbType(DataBaseType dabType) {
	dbType = dabType;
    }

    /**
     * Gets the database prefix
     * @return the prefix
     */
    protected String getPrefix() {
	return prefix;
    }

    public List<JobsDAOData> getAllJobs(OfflinePlayer player) {
	return getAllJobs(player.getName(), player.getUniqueId());
    }

    /**
     * Get all jobs the player is part of.
     * @param playerUUID - the player being searched for
     * @return list of all of the names of the jobs the players are part of.
     */

    public List<JobsDAOData> getAllJobs(String playerName, UUID uuid) {

	int id = -1;
	PlayerInfo userData = null;

	if (Jobs.getGCManager().MultiServerCompatability())
	    userData = loadPlayerData(uuid);
	else
	    userData = Jobs.getPlayerManager().getPlayerInfo(uuid);

	ArrayList<JobsDAOData> jobs = new ArrayList<>();

	if (userData == null) {
	    recordNewPlayer(playerName, uuid);
	    return jobs;
	}
	id = userData.getID();

	JobsConnection conn = getConnection();
	if (conn == null)
	    return jobs;

	PreparedStatement prest = null;
	ResultSet res = null;
	try {
	    prest = conn.prepareStatement("SELECT * FROM `" + getJobsTableName() + "` WHERE `" + JobsTableFields.userid.getCollumn() + "` = ?;");
	    prest.setInt(1, id);
	    res = prest.executeQuery();
	    while (res.next()) {
		int jobId = res.getInt(JobsTableFields.jobid.getCollumn());
		if (jobId == 0) {
		    jobs.add(new JobsDAOData(res.getString(JobsTableFields.job.getCollumn()), res.getInt(JobsTableFields.level.getCollumn()), res.getDouble(JobsTableFields.experience.getCollumn())));
		} else {
		    Job job = Jobs.getJob(jobId);
		    if (job != null)
			jobs.add(new JobsDAOData(job.getName(), res.getInt(JobsTableFields.level.getCollumn()), res.getDouble(JobsTableFields.experience.getCollumn())));
		}
	    }
	} catch (SQLException e) {
	    e.printStackTrace();
	} finally {
	    close(res);
	    close(prest);
	}
	return jobs;
    }

    public HashMap<Integer, List<JobsDAOData>> getAllJobs() {
	HashMap<Integer, List<JobsDAOData>> map = new HashMap<>();
	JobsConnection conn = getConnection();
	if (conn == null)
	    return map;
	PreparedStatement prest = null;
	ResultSet res = null;
	try {
	    prest = conn.prepareStatement("SELECT * FROM `" + getJobsTableName() + "`;");
	    res = prest.executeQuery();
	    while (res.next()) {
		int id = res.getInt(JobsTableFields.userid.getCollumn());
		String jobName = res.getString(JobsTableFields.job.getCollumn());
		List<JobsDAOData> ls = map.get(id);
		if (ls == null)
		    ls = new ArrayList<>();

		int jobId = res.getInt(JobsTableFields.jobid.getCollumn());
		if (jobId == 0) {
		    ls.add(new JobsDAOData(jobName, res.getInt(JobsTableFields.level.getCollumn()), res.getDouble(JobsTableFields.experience.getCollumn())));
		    converted = false;
		} else {
		    // This should be removed when we switch over to id only method
		    if (converted)
			if (jobName == null || jobName.isEmpty())
			    converted = false;

		    Job job = Jobs.getJob(jobId);
		    if (job != null)
			ls.add(new JobsDAOData(job.getName(), res.getInt(JobsTableFields.level.getCollumn()), res.getDouble(JobsTableFields.experience.getCollumn())));
		}

		map.put(id, ls);
	    }
	} catch (SQLException e) {
	    e.printStackTrace();
	} finally {
	    close(res);
	    close(prest);
	}
	return map;
    }

    public HashMap<Integer, PlayerPoints> getAllPoints() {
	HashMap<Integer, PlayerPoints> map = new HashMap<>();
	JobsConnection conn = getConnection();
	if (conn == null)
	    return map;
	PreparedStatement prest = null;
	ResultSet res = null;
	try {
	    prest = conn.prepareStatement("SELECT * FROM `" + DBTables.PointsTable.getTableName() + "`;");
	    res = prest.executeQuery();
	    while (res.next()) {
		map.put(res.getInt(PointsTableFields.userid.getCollumn()), new PlayerPoints(res.getDouble(PointsTableFields.currentpoints.getCollumn()), res.getDouble(PointsTableFields.totalpoints
		    .getCollumn())));
	    }
	} catch (SQLException e) {
	    e.printStackTrace();
	} finally {
	    close(res);
	    close(prest);
	}
	return map;
    }

    public HashMap<Integer, ArchivedJobs> getAllArchivedJobs() {
	HashMap<Integer, ArchivedJobs> map = new HashMap<>();
	JobsConnection conn = getConnection();
	if (conn == null)
	    return map;
	PreparedStatement prest = null;
	ResultSet res = null;
	try {
	    prest = conn.prepareStatement("SELECT * FROM `" + DBTables.ArchiveTable.getTableName() + "`;");
	    res = prest.executeQuery();
	    while (res.next()) {

		int id = res.getInt(ArchiveTableFields.userid.getCollumn());
		String jobName = res.getString(ArchiveTableFields.job.getCollumn());
		Double exp = res.getDouble(ArchiveTableFields.experience.getCollumn());
		int lvl = res.getInt(ArchiveTableFields.level.getCollumn());
		Long left = res.getLong(ArchiveTableFields.left.getCollumn());
		int jobid = res.getInt(ArchiveTableFields.jobid.getCollumn());

		Job job = null;
		if (jobid != 0) {
		    job = Jobs.getJob(jobid);
		} else {
		    job = Jobs.getJob(jobName);
		    converted = false;
		}

		if (job == null)
		    continue;

		ArchivedJobs m = map.get(id);
		if (m == null)
		    m = new ArchivedJobs();
		JobProgression jp = new JobProgression(job, null, lvl, exp);
		if (left != 0L)
		    jp.setLeftOn(left);
		m.addArchivedJob(jp);
		map.put(id, m);
	    }
	} catch (Exception e) {
	    close(res);
	    close(prest);
	} finally {
	    close(res);
	    close(prest);
	}
	return map;
    }

    public HashMap<Integer, HashMap<String, Log>> getAllLogs() {
	HashMap<Integer, HashMap<String, Log>> map = new HashMap<>();
	JobsConnection conn = getConnection();
	if (conn == null)
	    return map;
	PreparedStatement prest = null;
	ResultSet res = null;
	try {
	    int time = TimeManage.timeInInt();
	    prest = conn.prepareStatement("SELECT * FROM `" + DBTables.LogTable.getTableName() + "` WHERE `" + LogTableFields.time.getCollumn() + "` = ? ;");
	    prest.setInt(1, time);
	    res = prest.executeQuery();
	    while (res.next()) {
		int id = res.getInt(LogTableFields.userid.getCollumn());

		HashMap<String, Log> m = map.get(id);
		if (m == null)
		    m = new HashMap<>();
		String action = res.getString(LogTableFields.action.getCollumn());
		Log log = m.get(action);

		if (log == null)
		    log = new Log(action);

		HashMap<CurrencyType, Double> amounts = new HashMap<>();
		amounts.put(CurrencyType.MONEY, res.getDouble(LogTableFields.money.getCollumn()));
		amounts.put(CurrencyType.EXP, res.getDouble(LogTableFields.exp.getCollumn()));
		amounts.put(CurrencyType.POINTS, res.getDouble(LogTableFields.points.getCollumn()));

		log.add(res.getString(LogTableFields.itemname.getCollumn()), res.getInt(LogTableFields.count.getCollumn()), amounts);

		m.put(action, log);
		map.put(id, m);

//		Jobs.getLoging().loadToLog(player, res.getString("action"), res.getString("itemname"), res.getInt("count"), res.getDouble("money"), res.getDouble("exp"));
	    }
	} catch (Exception e) {
	    close(res);
	    close(prest);
	} finally {
	    close(res);
	    close(prest);
	}
	return map;
    }

    private HashMap<Integer, ArrayList<JobsDAOData>> map = new HashMap<>();

    public List<JobsDAOData> getAllJobs(PlayerInfo pInfo) {
	return map.getOrDefault(pInfo.getID(), new ArrayList<JobsDAOData>());
    }

    public void cleanUsers() {
	if (!Jobs.getGCManager().DBCleaningUsersUse)
	    return;
	JobsConnection conn = getConnection();
	if (conn == null)
	    return;
	long mark = System.currentTimeMillis() - (Jobs.getGCManager().DBCleaningUsersDays * 24 * 60 * 60 * 1000);
	PreparedStatement prest = null;
	try {
	    prest = conn.prepareStatement("DELETE FROM `" + DBTables.UsersTable.getTableName() + "` WHERE `" + UserTableFields.seen.getCollumn() + "` < ?;");
	    prest.setLong(1, mark);
	    prest.execute();
	} catch (SQLException e) {
	    e.printStackTrace();
	} finally {
	    close(prest);
	}
    }

    public void cleanJobs() {
	if (!Jobs.getGCManager().DBCleaningJobsUse)
	    return;
	JobsConnection conn = getConnection();
	if (conn == null)
	    return;
	PreparedStatement prest = null;
	try {
	    prest = conn.prepareStatement("DELETE FROM `" + getJobsTableName() + "` WHERE `" + JobsTableFields.level.getCollumn() + "` <= ?;");
	    prest.setInt(1, Jobs.getGCManager().DBCleaningJobsLvl);
	    prest.execute();
	} catch (SQLException e) {
	    e.printStackTrace();
	} finally {
	    close(prest);
	}
    }

    public void recordNewPlayer(Player player) {
	recordNewPlayer((OfflinePlayer) player);
    }

    public void recordNewPlayer(OfflinePlayer player) {
	recordNewPlayer(player.getName(), player.getUniqueId());
    }

    public void recordNewPlayer(String playerName, UUID uuid) {
	JobsConnection conn = getConnection();
	if (conn == null)
	    return;
	PreparedStatement prestt = null;
	ResultSet res2 = null;
	try {
	    prestt = conn.prepareStatement("INSERT INTO `" + DBTables.UsersTable.getTableName() + "` (`" + UserTableFields.player_uuid.getCollumn()
		+ "`, `" + UserTableFields.username.getCollumn()
		+ "`, `" + UserTableFields.seen.getCollumn()
		+ "`, `" + UserTableFields.donequests.getCollumn()
		+ "`) VALUES (?, ?, ?, ?);",
		Statement.RETURN_GENERATED_KEYS);
	    prestt.setString(1, uuid.toString());
	    prestt.setString(2, playerName);
	    prestt.setLong(3, System.currentTimeMillis());
	    prestt.setInt(4, 0);
	    prestt.executeUpdate();

	    res2 = prestt.getGeneratedKeys();
	    int id = 0;
	    if (res2.next())
		id = res2.getInt(1);

	    Jobs.getPlayerManager().addPlayerToMap(new PlayerInfo(playerName, id, uuid, System.currentTimeMillis(), 0));
	} catch (SQLException e) {
	    e.printStackTrace();
	} finally {
	    close(prestt);
	    close(res2);
	}
    }

    int convertSchedId = -1;
    boolean converted = true;

    public void recordNewWorld(String worldName) {
	JobsConnection conn = getConnection();
	if (conn == null)
	    return;
	PreparedStatement prestt = null;
	ResultSet res2 = null;
	try {
	    prestt = conn.prepareStatement("INSERT INTO `" + DBTables.WorldTable.getTableName() + "` (`" + worldsTableFields.name.getCollumn() + "`) VALUES (?);",
		Statement.RETURN_GENERATED_KEYS);
	    prestt.setString(1, worldName);
	    prestt.executeUpdate();

	    res2 = prestt.getGeneratedKeys();
	    int id = 0;
	    if (res2.next())
		id = res2.getInt(1);

	    Util.addJobsWorld(new JobsWorld(worldName, id));

	} catch (SQLException e) {
	    e.printStackTrace();
	} finally {
	    close(prestt);
	    close(res2);
	}
    }

    public synchronized void loadAllJobsWorlds() {
	JobsConnection conn = getConnection();
	if (conn == null)
	    return;

	PreparedStatement prest = null;
	ResultSet res = null;
	try {
	    prest = conn.prepareStatement("SELECT * FROM `" + DBTables.WorldTable.getTableName() + "`;");
	    res = prest.executeQuery();
	    while (res.next()) {
		int id = res.getInt("id");
		String name = res.getString(worldsTableFields.name.getCollumn());
		Util.addJobsWorld(new JobsWorld(name, id));
	    }
	} catch (SQLException e) {
	    e.printStackTrace();
	} finally {
	    close(res);
	    close(prest);
	}

	for (World one : Bukkit.getWorlds()) {
	    if (Util.getJobsWorld(one.getName()) != null)
		continue;
	    this.recordNewWorld(one.getName());
	}

	return;
    }

    public void triggerTableIdUpdate() {
	// Lets convert old fields
	if (convertSchedId > 0)
	    Bukkit.getServer().getScheduler().cancelTask(convertSchedId);
	if (!converted) {
	    convertSchedId = Bukkit.getServer().getScheduler().scheduleSyncDelayedTask(plugin, new Runnable() {
		@Override
		public void run() {
		    Jobs.consoleMsg("&6[Jobs] Converting to new database format");
		    convertID();
		    Jobs.consoleMsg("&6[Jobs] Converted to new database format");
		    converted = true;
		    return;
		}
	    }, 60L);
	}
    }

    private void convertID() {
	JobsConnection conn = getConnection();
	if (conn == null)
	    return;
	PreparedStatement exploreStatement = null;
	try {
	    exploreStatement = conn.prepareStatement("UPDATE `" + DBTables.ExploreDataTable.getTableName() + "` SET `" + ExploreDataTableFields.worldid.getCollumn() + "` = ? WHERE `"
		+ ExploreDataTableFields.worldname.getCollumn() + "` = ?;");
	    for (Entry<String, JobsWorld> jobsWorld : Util.getJobsWorlds().entrySet()) {
		exploreStatement.setInt(1, jobsWorld.getValue().getId());
		exploreStatement.setString(2, jobsWorld.getValue().getName());
		exploreStatement.execute();
	    }
	} catch (SQLException e) {
	    e.printStackTrace();
	} finally {
	    close(exploreStatement);
	}

	PreparedStatement exploreStatementBack = null;
	try {
	    exploreStatementBack = conn.prepareStatement("UPDATE `" + DBTables.ExploreDataTable.getTableName() + "` SET `" + ExploreDataTableFields.worldname.getCollumn() + "` = ? WHERE `"
		+ ExploreDataTableFields.worldid.getCollumn() + "` = ?;");
	    for (Entry<String, JobsWorld> jobsWorld : Util.getJobsWorlds().entrySet()) {
		exploreStatementBack.setString(1, jobsWorld.getValue().getName());
		exploreStatementBack.setInt(2, jobsWorld.getValue().getId());
		exploreStatementBack.execute();
	    }
	} catch (SQLException e) {
	    e.printStackTrace();
	} finally {
	    close(exploreStatementBack);
	}

	PreparedStatement bpStatement = null;
	try {
	    bpStatement = conn.prepareStatement("UPDATE `" + DBTables.BlocksTable.getTableName() + "` SET `" + BlockTableFields.worldid.getCollumn() + "` = ?  WHERE `" + BlockTableFields.world
		.getCollumn() + "` = ?;");
	    for (Entry<String, JobsWorld> jobsWorld : Util.getJobsWorlds().entrySet()) {
		bpStatement.setInt(1, jobsWorld.getValue().getId());
		bpStatement.setString(2, jobsWorld.getValue().getName());
		bpStatement.execute();
	    }
	} catch (SQLException e) {
	    e.printStackTrace();
	} finally {
	    close(bpStatement);
	}

	PreparedStatement bpStatementback = null;
	try {
	    bpStatementback = conn.prepareStatement("UPDATE `" + DBTables.BlocksTable.getTableName() + "` SET `" + BlockTableFields.world.getCollumn() + "` = ?  WHERE `" + BlockTableFields.worldid
		.getCollumn() + "` = ?;");
	    for (Entry<String, JobsWorld> jobsWorld : Util.getJobsWorlds().entrySet()) {
		bpStatementback.setString(1, jobsWorld.getValue().getName());
		bpStatementback.setInt(2, jobsWorld.getValue().getId());
		bpStatementback.execute();
	    }
	} catch (SQLException e) {
	    e.printStackTrace();
	} finally {
	    close(bpStatementback);
	}

	PreparedStatement archiveStatement = null;
	try {
	    archiveStatement = conn.prepareStatement("UPDATE `" + DBTables.ArchiveTable.getTableName() + "` SET `" + ArchiveTableFields.jobid.getCollumn() + "` = ? WHERE `" + ArchiveTableFields.job
		.getCollumn() + "` = ?;");
	    for (Job job : Jobs.getJobs()) {
		archiveStatement.setInt(1, job.getId());
		archiveStatement.setString(2, job.getName());
		archiveStatement.execute();
	    }
	} catch (SQLException e) {
	    e.printStackTrace();
	} finally {
	    close(archiveStatement);
	}
	PreparedStatement archiveStatementBack = null;
	try {
	    archiveStatementBack = conn.prepareStatement("UPDATE `" + DBTables.ArchiveTable.getTableName() + "` SET `" + ArchiveTableFields.job.getCollumn() + "` = ? WHERE `" + ArchiveTableFields.jobid
		.getCollumn() + "` = ?;");
	    for (Job job : Jobs.getJobs()) {
		archiveStatementBack.setString(1, job.getName());
		archiveStatementBack.setInt(2, job.getId());
		archiveStatementBack.execute();
	    }
	} catch (SQLException e) {
	    e.printStackTrace();
	} finally {
	    close(archiveStatementBack);
	}

	PreparedStatement usersStatement = null;
	try {
	    usersStatement = conn.prepareStatement("UPDATE `" + getJobsTableName() + "` SET `" + JobsTableFields.jobid.getCollumn() + "` = ? WHERE `" + JobsTableFields.job.getCollumn() + "` = ?;");
	    for (Job job : Jobs.getJobs()) {
		usersStatement.setInt(1, job.getId());
		usersStatement.setString(2, job.getName());
		usersStatement.execute();
	    }
	} catch (SQLException e) {
	    e.printStackTrace();
	} finally {
	    close(usersStatement);
	}
	PreparedStatement usersStatementBack = null;
	try {
	    usersStatementBack = conn.prepareStatement("UPDATE `" + getJobsTableName() + "` SET `" + JobsTableFields.job.getCollumn() + "` = ? WHERE `" + JobsTableFields.jobid.getCollumn() + "` = ?;");
	    for (Job job : Jobs.getJobs()) {
		usersStatementBack.setString(1, job.getName());
		usersStatementBack.setInt(2, job.getId());
		usersStatementBack.execute();
	    }
	} catch (SQLException e) {
	    e.printStackTrace();
	} finally {
	    close(usersStatementBack);
	}

	PreparedStatement limitsStatement = null;
	try {
	    limitsStatement = conn.prepareStatement("UPDATE `" + DBTables.LimitsTable.getTableName() + "` SET `" + LimitTableFields.typeid.getCollumn() + "` = ? WHERE `" + LimitTableFields.type
		.getCollumn() + "` = ?;");
	    for (CurrencyType type : CurrencyType.values()) {
		limitsStatement.setInt(1, type.getId());
		limitsStatement.setString(2, type.getName());
		limitsStatement.execute();
	    }
	} catch (SQLException e) {
	    e.printStackTrace();
	} finally {
	    close(limitsStatement);
	}

	PreparedStatement limitsStatementBack = null;
	try {
	    limitsStatementBack = conn.prepareStatement("UPDATE `" + DBTables.LimitsTable.getTableName() + "` SET `" + LimitTableFields.type.getCollumn() + "` = ? WHERE `" + LimitTableFields.typeid
		.getCollumn() + "` = ?;");
	    for (CurrencyType type : CurrencyType.values()) {
		limitsStatementBack.setString(1, type.getName());
		limitsStatementBack.setInt(2, type.getId());
		limitsStatementBack.execute();
	    }
	} catch (SQLException e) {
	    e.printStackTrace();
	} finally {
	    close(limitsStatementBack);
	}
    }

    public void recordNewJobName(Job job) {
	JobsConnection conn = getConnection();
	if (conn == null)
	    return;

	PreparedStatement prestt = null;
	ResultSet res2 = null;
	try {
	    prestt = conn.prepareStatement("DELETE FROM `" + DBTables.JobNameTable.getTableName()
		+ "` WHERE `" + jobsNameTableFields.name.getCollumn() + "` = ?;");
	    prestt.setString(1, job.getName());
	    prestt.execute();

	    close(prestt);
	    prestt = null;

	    prestt = conn.prepareStatement("INSERT INTO `" + DBTables.JobNameTable.getTableName() + "` (`" + jobsNameTableFields.name.getCollumn() + "`) VALUES (?);",
		Statement.RETURN_GENERATED_KEYS);
	    prestt.setString(1, job.getName());
	    prestt.executeUpdate();

	    res2 = prestt.getGeneratedKeys();
	    int id = 0;
	    if (res2.next())
		id = res2.getInt(1);
	    job.setId(id);
	} catch (SQLException e) {
	    e.printStackTrace();
	} finally {
	    close(prestt);
	    close(res2);
	}
    }

    public synchronized void loadAllJobsNames() {
	JobsConnection conn = getConnection();
	if (conn == null)
	    return;

	PreparedStatement prest = null;
	ResultSet res = null;
	try {
	    prest = conn.prepareStatement("SELECT * FROM `" + DBTables.JobNameTable.getTableName() + "`;");
	    res = prest.executeQuery();
	    while (res.next()) {
		int id = res.getInt("id");
		String name = res.getString(worldsTableFields.name.getCollumn());

		Job job = Jobs.getJob(name);
		if (job != null)
		    job.setId(id);
	    }
	} catch (SQLException e) {
	    e.printStackTrace();
	} finally {
	    close(res);
	    close(prest);
	}

	for (Job one : Jobs.getJobs()) {
	    if (one.getId() == 0)
		recordNewJobName(one);
	}
    }

    /**
     * Get player count for a job.
     * @param JobName - the job name
     * @return amount of player currently working.
     */
    public synchronized int getTotalPlayerAmountByJobName(String JobName) {
	JobsConnection conn = getConnection();
	if (conn == null)
	    return 0;

	int count = 0;
	PreparedStatement prest = null;
	ResultSet res = null;
	try {
	    Job job = Jobs.getJob(JobName);
	    if (job != null && job.getId() != 0) {
		prest = conn.prepareStatement("SELECT COUNT(*) FROM `" + getJobsTableName() + "` WHERE `" + JobsTableFields.jobid + "` = ?;");
		prest.setInt(1, job.getId());
		res = prest.executeQuery();
		if (res.next()) {
		    count += res.getInt(1);
		}
	    }
	} catch (SQLException e) {
	    e.printStackTrace();
	} finally {
	    close(res);
	    close(prest);
	}

	return count;
    }

    /**
     * Get player count for a job.
     * @return total amount of player currently working.
     */
    public synchronized int getTotalPlayers() {
	int total = 0;
	for (Job one : Jobs.getJobs()) {
	    total += one.getTotalPlayers();
	}
	return total;
    }

    /**
     * Get all jobs the player is part of.
     * @param userName - the player being searched for
     * @return list of all of the names of the jobs the players are part of.
     */
    public synchronized List<JobsDAOData> getAllJobsOffline(String userName) {

	ArrayList<JobsDAOData> jobs = new ArrayList<>();

	PlayerInfo info = Jobs.getPlayerManager().getPlayerInfo(userName);
	if (info == null)
	    return jobs;

	JobsConnection conn = getConnection();
	if (conn == null)
	    return jobs;
	PreparedStatement prest = null;
	ResultSet res = null;
	try {
	    prest = conn.prepareStatement("SELECT * FROM `" + getJobsTableName() + "` WHERE `" + JobsTableFields.userid.getCollumn() + "` = ?;");
	    prest.setInt(1, info.getID());
	    res = prest.executeQuery();
	    while (res.next()) {
		int jobId = res.getInt(JobsTableFields.jobid.getCollumn());
		if (jobId == 0) {
		    jobs.add(new JobsDAOData(res.getString(JobsTableFields.job.getCollumn()), res.getInt(JobsTableFields.level.getCollumn()), res.getDouble(JobsTableFields.experience.getCollumn())));
		} else {
		    Job job = Jobs.getJob(jobId);
		    jobs.add(new JobsDAOData(job.getName(), res.getInt(JobsTableFields.level.getCollumn()), res.getDouble(JobsTableFields.experience.getCollumn())));
		}
	    }
	} catch (SQLException e) {
	    e.printStackTrace();
	} finally {
	    close(res);
	    close(prest);
	}
	return jobs;
    }

    public synchronized void recordPlayersLimits(JobsPlayer jPlayer) {
	JobsConnection conn = getConnection();
	if (conn == null)
	    return;
	PreparedStatement prest2 = null;
	try {
	    prest2 = conn.prepareStatement("DELETE FROM `" + DBTables.LimitsTable.getTableName() + "` WHERE `" + LimitTableFields.userid.getCollumn() + "` = ?;");
	    prest2.setInt(1, jPlayer.getUserId());
	    prest2.execute();
	} catch (SQLException e) {
	    e.printStackTrace();
	} finally {
	    close(prest2);
	}

	PreparedStatement prest = null;
	try {
	    PaymentData limit = jPlayer.getPaymentLimit();
	    prest = conn.prepareStatement("INSERT INTO `" + DBTables.LimitsTable.getTableName() + "` (`" +
		LimitTableFields.userid.getCollumn() + "`, `" +
		LimitTableFields.typeid.getCollumn() + "`, `" +
		LimitTableFields.collected.getCollumn() + "`, `" +
		LimitTableFields.started.getCollumn() + "`, `" +
		LimitTableFields.type.getCollumn() + "`) VALUES (?, ?, ?, ?, ?);");
	    conn.setAutoCommit(false);
	    for (CurrencyType type : CurrencyType.values()) {
		if (limit == null)
		    continue;
		if (limit.GetAmount(type) == 0D)
		    continue;
		if (limit.GetLeftTime(type) < 0)
		    continue;

		prest.setInt(1, jPlayer.getUserId());
		prest.setInt(2, type.getId());
		prest.setDouble(3, limit.GetAmount(type));
		prest.setLong(4, limit.GetTime(type));
		prest.setString(5, type.toString());
		prest.addBatch();
	    }
	    prest.executeBatch();
	    conn.commit();
	} catch (Exception e) {
	    e.printStackTrace();
	} finally {
	    close(prest);
	    try {
		conn.setAutoCommit(true);
	    } catch (SQLException e) {
		e.printStackTrace();
	    }
	}

    }

    public synchronized PaymentData getPlayersLimits(JobsPlayer jPlayer) {
	PaymentData data = new PaymentData();
	JobsConnection conn = getConnection();
	if (conn == null)
	    return data;
	PreparedStatement prest = null;
	ResultSet res = null;
	try {
	    prest = conn.prepareStatement("SELECT * FROM `" + DBTables.LimitsTable.getTableName() + "` WHERE `" + LimitTableFields.userid.getCollumn() + "` = ?;");
	    prest.setInt(1, jPlayer.getUserId());
	    res = prest.executeQuery();
	    while (res.next()) {

		String typeName = res.getString(LimitTableFields.type.getCollumn());
		int typeId = res.getInt(LimitTableFields.typeid.getCollumn());

		CurrencyType type = null;
		if (typeId != 0)
		    type = CurrencyType.get(typeId);
		else
		    type = CurrencyType.getByName(typeName);

		if (type == null)
		    continue;

		data.AddNewAmount(type, res.getDouble(LimitTableFields.collected.getCollumn()), res.getLong(LimitTableFields.started.getCollumn()));
	    }
	} catch (SQLException e) {
	    e.printStackTrace();
	} finally {
	    close(res);
	    close(prest);
	}
	return data;
    }

    public synchronized HashMap<Integer, PaymentData> loadPlayerLimits() {
	HashMap<Integer, PaymentData> map = new HashMap<>();
	JobsConnection conn = getConnection();
	if (conn == null)
	    return map;
	PreparedStatement prest = null;
	ResultSet res = null;
	try {
	    prest = conn.prepareStatement("SELECT * FROM `" + DBTables.LimitsTable.getTableName() + "`;");
	    res = prest.executeQuery();
	    while (res.next()) {
		int id = res.getInt(LimitTableFields.userid.getCollumn());
		PaymentData data = map.get(id);
		if (data == null)
		    data = new PaymentData();

		String typeName = res.getString(LimitTableFields.type.getCollumn());
		int typeId = res.getInt(LimitTableFields.typeid.getCollumn());

		CurrencyType type = null;
		if (typeId != 0)
		    type = CurrencyType.get(typeId);
		else
		    type = CurrencyType.getByName(typeName);

		if (type == null)
		    continue;

		data.AddNewAmount(type, res.getDouble(LimitTableFields.collected.getCollumn()), res.getLong(LimitTableFields.started.getCollumn()));
		map.put(id, data);
	    }
	} catch (SQLException e) {
	    e.printStackTrace();
	} finally {
	    close(res);
	    close(prest);
	}
	return map;
    }

    /**
     * Join a job (create player-job entry from storage)
     * @param player - player that wishes to join the job
     * @param job - job that the player wishes to join
     */
    public synchronized void joinJob(JobsPlayer jPlayer, JobProgression job) {
	JobsConnection conn = getConnection();
	if (conn == null)
	    return;
	PreparedStatement prest = null;
	try {
	    int level = job.getLevel();
	    Double exp = job.getExperience();
	    prest = conn.prepareStatement("INSERT INTO `" + getJobsTableName() + "` (`" + JobsTableFields.userid.getCollumn() + "`, `" + JobsTableFields.jobid.getCollumn()
		+ "`, `" + JobsTableFields.level.getCollumn() + "`, `" + JobsTableFields.experience.getCollumn() + "`, `" + JobsTableFields.job.getCollumn() + "`) VALUES (?, ?, ?, ?, ?);");
	    prest.setInt(1, jPlayer.getUserId());
	    prest.setInt(2, job.getJob().getId());
	    prest.setInt(3, level);
	    prest.setDouble(4, exp);
	    prest.setString(5, job.getJob().getName());
	    prest.execute();
	} catch (SQLException e) {
	    e.printStackTrace();
	} finally {
	    close(prest);
	}
    }

    /**
     * Join a job (create player-job entry from storage)
     * @param player - player that wishes to join the job
     * @param job - job that the player wishes to join
     */
    public synchronized void insertJob(JobsPlayer jPlayer, JobProgression prog) {
	JobsConnection conn = getConnection();
	if (conn == null)
	    return;
	PreparedStatement prest = null;
	try {
	    double exp = prog.getExperience();
	    if (exp < 0)
		exp = 0;
	    prest = conn.prepareStatement("INSERT INTO `" + getJobsTableName() + "` (`" + JobsTableFields.userid.getCollumn() + "`, `" + JobsTableFields.jobid.getCollumn()
		+ "`, `" + JobsTableFields.level.getCollumn() + "`, `" + JobsTableFields.experience.getCollumn() + "`, `" + JobsTableFields.job.getCollumn() + "`) VALUES (?, ?, ?, ?, ?);");
	    prest.setInt(1, jPlayer.getUserId());
	    prest.setInt(2, prog.getJob().getId());
	    prest.setInt(3, prog.getLevel());
	    prest.setDouble(4, exp);
	    prest.setString(5, prog.getJob().getName());
	    prest.execute();
	} catch (SQLException e) {
	    e.printStackTrace();
	} finally {
	    close(prest);
	}
    }

    /**
     * Join a job (create player-job entry from storage)
     * @param player - player that wishes to join the job
     * @param job - job that the player wishes to join
     * @throws SQLException 
     */
    public List<Convert> convertDatabase() throws SQLException {
	List<Convert> list = new ArrayList<>();
	JobsConnection conn = getConnection();
	if (conn == null)
	    return list;

	PreparedStatement prest = null;
	ResultSet res = null;
	try {
	    prest = conn.prepareStatement("SELECT * FROM `" + DBTables.ArchiveTable.getTableName() + "`");
	    res = prest.executeQuery();
	    while (res.next()) {
		int id = res.getInt(ArchiveTableFields.userid.getCollumn());
		PlayerInfo pi = Jobs.getPlayerManager().getPlayerInfo(id);
		if (pi == null)
		    continue;

		JobsPlayer jPlayer = Jobs.getPlayerManager().getJobsPlayer(pi.getUuid());
		if (jPlayer == null)
		    continue;

		String jobName = res.getString(ArchiveTableFields.job.getCollumn());
		int jobid = res.getInt(ArchiveTableFields.jobid.getCollumn());

		Job job = jobid != 0 ? Jobs.getJob(jobid) : Jobs.getJob(jobName);
		if (job == null)
		    continue;

		list.add(new Convert(res.getInt("id"), jPlayer.getUniqueId(), job.getId(), res.getInt(ArchiveTableFields.level.getCollumn()), res.getInt(ArchiveTableFields.experience.getCollumn())));
	    }
	} finally {
	    close(res);
	    close(prest);
	}

	conn.closeConnection();
	return list;
    }

    public void continueConvertions(List<Convert> list) throws SQLException {
	JobsConnection conns = this.getConnection();
	if (conns == null)
	    return;
	PreparedStatement insert = null;
	Statement statement = null;
	int i = list.size();
	try {
	    statement = conns.createStatement();
	    if (Jobs.getDBManager().getDbType() == DataBaseType.MySQL) {
		statement.executeUpdate("TRUNCATE TABLE `" + DBTables.ArchiveTable.getTableName() + "`");
	    } else {
		statement.executeUpdate("DELETE from `" + DBTables.ArchiveTable.getTableName() + "`");
	    }

	    insert = conns.prepareStatement("INSERT INTO `" + DBTables.ArchiveTable.getTableName() + "` (`" + ArchiveTableFields.userid.getCollumn()
		+ "`, `" + ArchiveTableFields.jobid.getCollumn() + "`, `" + ArchiveTableFields.level.getCollumn() + "`, `" + ArchiveTableFields.experience.getCollumn() + "`, `"
		+ ArchiveTableFields.job.getCollumn() + "`) VALUES (?, ?, ?, ?, ?);");
	    conns.setAutoCommit(false);
	    while (i > 0) {
		i--;

		Convert convertData = list.get(i);

		JobsPlayer jPlayer = Jobs.getPlayerManager().getJobsPlayer(convertData.getUserUUID());
		Job job = Jobs.getJob(convertData.getJobId());

		insert.setInt(1, jPlayer != null ? jPlayer.getUserId() : -1);
		insert.setInt(2, convertData.getJobId());
		insert.setInt(3, convertData.getLevel());
		insert.setInt(4, convertData.getExp());
		insert.setString(5, job != null ? job.getName() : "");
		insert.addBatch();
	    }
	    insert.executeBatch();
	    conns.commit();
	    conns.setAutoCommit(true);
	} finally {
	    close(statement);
	    close(insert);
	    try {
		conns.setAutoCommit(true);
	    } catch (SQLException e) {
		e.printStackTrace();
	    }
	}
    }

    /**
     * Quit a job (delete player-job entry from storage)
     * @param player - player that wishes to quit the job
     * @param job - job that the player wishes to quit
     */
    public synchronized boolean quitJob(JobsPlayer jPlayer, Job job) {
	JobsConnection conn = getConnection();
	if (conn == null)
	    return false;
	PreparedStatement prest = null;
	boolean ok = true;
	try {
	    prest = conn.prepareStatement("DELETE FROM `" + getJobsTableName() + "` WHERE `" + JobsTableFields.userid.getCollumn() + "` = ? AND `" + JobsTableFields.jobid.getCollumn()
		+ "` = ?;");
	    prest.setInt(1, jPlayer.getUserId());
	    prest.setInt(2, job.getId());
	    prest.execute();
	} catch (SQLException e) {
	    e.printStackTrace();
	    ok = false;
	} finally {
	    close(prest);
	}
	return ok;
    }

    /**
     * Record job to archive
     * @param player - player that wishes to quit the job
     * @param job - job that the player wishes to quit
     */
    public void recordToArchive(JobsPlayer jPlayer, Job job) {
	JobProgression jp = jPlayer.getJobProgression(job);
	if (jp == null)
	    return;
	jp.setLeftOn(System.currentTimeMillis());
	jPlayer.getArchivedJobs().addArchivedJob(jp);
	JobsConnection conn = getConnection();
	if (conn == null)
	    return;
	PreparedStatement prest = null;
	try {
	    int level = jp.getLevel();
	    Double exp = jp.getExperience();
	    prest = conn.prepareStatement("INSERT INTO `" + DBTables.ArchiveTable.getTableName() + "` (`" + ArchiveTableFields.userid.getCollumn()
		+ "`, `" + ArchiveTableFields.jobid.getCollumn()
		+ "`, `" + ArchiveTableFields.level.getCollumn()
		+ "`, `" + ArchiveTableFields.experience.getCollumn()
		+ "`, `" + ArchiveTableFields.left.getCollumn()
		+ "`, `" + ArchiveTableFields.job.getCollumn()
		+ "`) VALUES (?, ?, ?, ?, ?, ?);");
	    prest.setInt(1, jPlayer.getUserId());
	    prest.setInt(2, job.getId());
	    prest.setInt(3, level);
	    prest.setInt(4, exp.intValue());
	    prest.setLong(5, System.currentTimeMillis());
	    prest.setString(6, job.getName());
	    prest.execute();
	} catch (SQLException e) {
	    e.printStackTrace();
	} finally {
	    close(prest);
	}
    }

    public List<TopList> getGlobalTopList() {
	return getGlobalTopList(0);
    }

    /**
     * Get player list by total job level
     * @param start - starting entry
     * @return info - information about jobs
     */
    public List<TopList> getGlobalTopList(int start) {
	JobsConnection conn = getConnection();
	List<TopList> names = new ArrayList<>();
	if (conn == null)
	    return names;

	if (start < 0) {
	    start = 0;
	}

	PreparedStatement prest = null;
	ResultSet res = null;
	try {

	    prest = conn.prepareStatement("SELECT " + JobsTableFields.userid.getCollumn()
		+ ", COUNT(*) AS amount, sum(" + JobsTableFields.level.getCollumn() + ") AS totallvl FROM `" + getJobsTableName()
		+ "` GROUP BY userid ORDER BY totallvl DESC LIMIT " + start + "," + (Jobs.getGCManager().JobsTopAmount * 2) + ";");
	    res = prest.executeQuery();

	    while (res.next()) {
		PlayerInfo info = Jobs.getPlayerManager().getPlayerInfo(res.getInt(JobsTableFields.userid.getCollumn()));
		if (info == null)
		    continue;

		TopList top = new TopList(info, res.getInt("totallvl"), 0);
		names.add(top);

		if (names.size() >= Jobs.getGCManager().JobsTopAmount * 2)
		    break;
	    }
	} catch (SQLException e) {
	    e.printStackTrace();
	} finally {
	    close(res);
	    close(prest);
	}

	return names;
    }

    /**
     * Get players by quests done
     * @param start - starting entry
     * @param size - max count of entries
     * @return info - information about jobs
     */
    public List<TopList> getQuestTopList(int start) {
	JobsConnection conn = getConnection();
	List<TopList> names = new ArrayList<>();
	if (conn == null)
	    return names;

	if (start < 0) {
	    start = 0;
	}

	PreparedStatement prest = null;
	ResultSet res = null;
	try {
	    prest = conn.prepareStatement("SELECT `id`, `" + UserTableFields.player_uuid.getCollumn() + "`, `" + UserTableFields.donequests.getCollumn() + "` FROM `" + DBTables.UsersTable.getTableName()
		+ "` ORDER BY `" + UserTableFields.donequests.getCollumn() + "` DESC, LOWER(" + UserTableFields.seen.getCollumn() + ") DESC LIMIT " + start + ", 30;");

	    res = prest.executeQuery();

	    while (res.next()) {
		PlayerInfo info = Jobs.getPlayerManager().getPlayerInfo(res.getInt("id"));
		if (info == null)
		    continue;

		TopList top = new TopList(info, res.getInt(UserTableFields.donequests.getCollumn()), 0);
		names.add(top);

		if (names.size() >= Jobs.getGCManager().JobsTopAmount)
		    break;
	    }
	} catch (SQLException e) {
	    e.printStackTrace();
	} finally {
	    close(res);
	    close(prest);
	}
	return names;
    }

    public PlayerInfo loadPlayerData(UUID uuid) {
	PlayerInfo pInfo = null;
	JobsConnection conn = getConnection();
	if (conn == null)
	    return pInfo;
	PreparedStatement prest = null;
	ResultSet res = null;
	try {
	    prest = conn.prepareStatement("SELECT * FROM `" + DBTables.UsersTable.getTableName() + "` WHERE `" + UserTableFields.player_uuid.getCollumn() + "` = ?;");
	    prest.setString(1, uuid.toString());
	    res = prest.executeQuery();
	    while (res.next()) {
		pInfo = new PlayerInfo(
		    res.getString(UserTableFields.username.getCollumn()),
		    res.getInt("id"), uuid,
		    res.getLong(UserTableFields.seen.getCollumn()),
		    res.getInt(UserTableFields.donequests.getCollumn()),
		    res.getString(UserTableFields.quests.getCollumn()));
		Jobs.getPlayerManager().addPlayerToMap(pInfo);
	    }
	} catch (SQLException e) {
	    e.printStackTrace();
	} finally {
	    close(res);
	    close(prest);
	}
	return pInfo;
    }

    public void loadPlayerData() {
	Jobs.getPlayerManager().clearMaps();

	JobsConnection conn = getConnection();
	if (conn == null)
	    return;

	PreparedStatement prest = null;
	ResultSet res = null;
	try {
	    prest = conn.prepareStatement("SELECT * FROM `" + DBTables.UsersTable.getTableName() + "`;");
	    res = prest.executeQuery();
	    List<String> uuids = new ArrayList<>();
	    while (res.next()) {
		String uuid = res.getString(UserTableFields.player_uuid.getCollumn());
		if (uuid == null || uuid.isEmpty()) {
		    uuids.add(uuid);
		    continue;
		}

		long seen = res.getLong(UserTableFields.seen.getCollumn());

		Jobs.getPlayerManager().addPlayerToMap(new PlayerInfo(
		    res.getString(UserTableFields.username.getCollumn()),
		    res.getInt("id"),
		    UUID.fromString(uuid),
		    seen,
		    res.getInt(UserTableFields.donequests.getCollumn()),
		    res.getString(UserTableFields.quests.getCollumn())));
	    }

	    for (String u : uuids) {
		PreparedStatement ps = conn.prepareStatement("DELETE FROM `" + DBTables.UsersTable.getTableName()
		+ "` WHERE `" + UserTableFields.player_uuid.getCollumn() + "` = ?;");
		ps.setString(1, u);
		ps.execute();
		close(ps);
	    }

	    uuids.clear();
	} catch (SQLException e) {
	    e.printStackTrace();
	} finally {
	    close(res);
	    close(prest);
	}

	return;
    }

    public JobsPlayer loadFromDao(JobsPlayer jPlayer) {
	List<JobsDAOData> list = getAllJobs(jPlayer.getName(), jPlayer.getUniqueId());
	jPlayer.progression.clear();
	for (JobsDAOData jobdata : list) {
	    if (!plugin.isEnabled())
		return null;

	    // add the job
	    Job job = Jobs.getJob(jobdata.getJobName());
	    if (job == null)
		continue;

	    // create the progression object
	    JobProgression jobProgression = new JobProgression(job, jPlayer, jobdata.getLevel(), jobdata.getExperience());
	    // calculate the max level
	    // add the progression level.
	    jPlayer.progression.add(jobProgression);
	}
	jPlayer.reloadMaxExperience();
	jPlayer.reloadLimits();
	jPlayer.setUserId(Jobs.getPlayerManager().getPlayerId(jPlayer.getUniqueId()));
	loadPoints(jPlayer);
	return jPlayer;
    }

    public JobsPlayer loadFromDao(OfflinePlayer player) {
	JobsPlayer jPlayer = new JobsPlayer(player.getName());
	jPlayer.setPlayerUUID(player.getUniqueId());

	List<JobsDAOData> list = getAllJobs(player);
	jPlayer.progression.clear();
	for (JobsDAOData jobdata : list) {
	    if (!plugin.isEnabled())
		return null;

	    // add the job
	    Job job = Jobs.getJob(jobdata.getJobName());
	    if (job == null)
		continue;

	    // create the progression object
	    JobProgression jobProgression = new JobProgression(job, jPlayer, jobdata.getLevel(), jobdata.getExperience());
	    // calculate the max level
	    // add the progression level.
	    jPlayer.progression.add(jobProgression);
	}
	jPlayer.reloadMaxExperience();
	jPlayer.reloadLimits();
	jPlayer.setUserId(Jobs.getPlayerManager().getPlayerId(player.getUniqueId()));
	loadPoints(jPlayer);
	return jPlayer;
    }

//    public void loadAllData() {
//	Jobs.getPlayerManager().clearMaps();
//	JobsConnection conn = getConnection();
//	if (conn == null)
//	    return;
//	PreparedStatement prest = null;
//	ResultSet res = null;
//	try {
//	    prest = conn.prepareStatement("SELECT *  FROM `" + prefix + "users`;");
//	    res = prest.executeQuery();
//	    while (res.next()) {
//		try {
//		    Jobs.getPlayerManager().addPlayerToMap(new PlayerInfo(
//			res.getString("username"),
//			res.getInt("id"),
//			UUID.fromString(res.getString("player_uuid")),
//			res.getLong("seen"),
//			res.getInt("donequests"),
//			res.getString("quests")));
//		} catch (Exception e) {
//		}
//	    }
//	} catch (SQLException e) {
//	    e.printStackTrace();
//	} finally {
//	    close(res);
//	    close(prest);
//	}
//	return;
//    }

    /**
     * Delete job from archive
     * @param player - player that wishes to quit the job
     * @param job - job that the player wishes to quit
     */
    public synchronized void deleteArchive(JobsPlayer jPlayer, Job job) {
	jPlayer.getArchivedJobs().removeArchivedJob(job);
	JobsConnection conn = getConnection();
	if (conn == null)
	    return;
	PreparedStatement prest = null;
	try {
	    prest = conn.prepareStatement("DELETE FROM `" + DBTables.ArchiveTable.getTableName() + "` WHERE `" + ArchiveTableFields.userid.getCollumn() + "` = ? AND `" + ArchiveTableFields.jobid
		.getCollumn() + "` = ?;");
	    prest.setInt(1, jPlayer.getUserId());
	    prest.setInt(2, job.getId());
	    prest.execute();
	} catch (SQLException e) {
	    e.printStackTrace();
	} finally {
	    close(prest);
	}
    }

    /**
     * Save player-job information
     * @param jobInfo - the information getting saved
     */
    public void save(JobsPlayer player) {
	JobsConnection conn = getConnection();
	if (conn == null)
	    return;
	PreparedStatement prest = null;
	try {
	    prest = conn.prepareStatement("UPDATE `" + getJobsTableName() + "` SET `" + JobsTableFields.level.getCollumn() + "` = ?, `" + JobsTableFields.experience.getCollumn()
		+ "` = ? WHERE `" + JobsTableFields.userid.getCollumn() + "` = ? AND `" + JobsTableFields.jobid.getCollumn() + "` = ?;");
	    for (JobProgression progression : player.getJobProgression()) {
		prest.setInt(1, progression.getLevel());
		prest.setDouble(2, progression.getExperience());
		prest.setInt(3, player.getUserId());
		prest.setInt(4, progression.getJob().getId());
		prest.execute();
	    }
	} catch (SQLException e) {
	    e.printStackTrace();
	} finally {
	    close(prest);
	}
    }

    public void updateSeen(JobsPlayer player) {
	if (player.getUserId() == -1) {
	    insertPlayer(player);
	    return;
	}

	JobsConnection conn = getConnection();
	if (conn == null)
	    return;

	PreparedStatement prest = null;
	try {
	    prest = conn.prepareStatement("UPDATE `" + DBTables.UsersTable.getTableName() + "` SET `" + UserTableFields.seen.getCollumn()
		+ "` = ?, `" + UserTableFields.username.getCollumn()
		+ "` = ?, `" + UserTableFields.donequests.getCollumn()
		+ "` = ?, `" + UserTableFields.quests.getCollumn()
		+ "` = ? WHERE `id` = ?;");
	    prest.setLong(1, System.currentTimeMillis());
	    prest.setString(2, player.getName());
	    prest.setInt(3, player.getDoneQuests());
	    prest.setString(4, player.getQuestProgressionString());
	    prest.setInt(5, player.getUserId());
	    prest.execute();
	} catch (SQLException e) {
	    e.printStackTrace();
	} finally {
	    close(prest);
	}
    }

    private void insertPlayer(JobsPlayer player) {
	JobsConnection conn = getConnection();
	if (conn == null)
	    return;
	PreparedStatement prestt = null;
	try {
	    prestt = conn.prepareStatement("INSERT INTO `" + DBTables.UsersTable.getTableName() + "` (`" + UserTableFields.player_uuid.getCollumn()
		+ "`, `" + UserTableFields.username.getCollumn()
		+ "`, `" + UserTableFields.seen.getCollumn()
		+ "`, `" + UserTableFields.donequests.getCollumn()
		+ "`) VALUES (?, ?, ?, ?);");
	    prestt.setString(1, player.getUniqueId().toString());
	    prestt.setString(2, player.getName());
	    prestt.setLong(3, player.getSeen());
	    prestt.setInt(4, 0);
	    prestt.executeUpdate();
	} catch (SQLException e) {
	    e.printStackTrace();
	} finally {
	    close(prestt);
	}
	PreparedStatement prest = null;
	ResultSet res = null;
	try {
	    prest = conn.prepareStatement("SELECT `id`, `" + UserTableFields.donequests.getCollumn()
		+ "` FROM `" + DBTables.UsersTable.getTableName() + "` WHERE `" + UserTableFields.player_uuid.getCollumn() + "` = ?;");
	    prest.setString(1, player.getUniqueId().toString());
	    res = prest.executeQuery();
	    res.next();
	    int id = res.getInt("id");
	    player.setUserId(id);
	    Jobs.getPlayerManager().addPlayerToMap(new PlayerInfo(
		player.getName(),
		id,
		player.getUniqueId(),
		player.getSeen(),
		res.getInt(UserTableFields.donequests.getCollumn())));
	} catch (SQLException e) {
	    e.printStackTrace();
	} finally {
	    close(res);
	    close(prest);
	}
    }

    public void savePoints(JobsPlayer jPlayer) {
	JobsConnection conn = getConnection();
	if (conn == null)
	    return;
	PreparedStatement prest = null;
	try {
	    PlayerPoints pointInfo = jPlayer.getPointsData();
	    prest = conn.prepareStatement("INSERT INTO `" + DBTables.PointsTable.getTableName() + "` (`" + PointsTableFields.totalpoints.getCollumn() + "`, `" + PointsTableFields.currentpoints.getCollumn()
		+ "`, `" + PointsTableFields.userid.getCollumn() + "`) VALUES (?, ?, ?);");
	    prest.setDouble(1, pointInfo.getTotalPoints());
	    prest.setDouble(2, pointInfo.getCurrentPoints());
	    prest.setInt(3, jPlayer.getUserId());
	    prest.execute();
	} catch (SQLException e) {
	    e.printStackTrace();
	} finally {
	    close(prest);
	}
    }

    public void loadPoints(JobsPlayer player) {
	JobsConnection conn = getConnection();
	if (conn == null)
	    return;
	PreparedStatement prest = null;
	ResultSet res = null;
	try {
	    prest = conn.prepareStatement("SELECT `" + PointsTableFields.totalpoints.getCollumn() + "`, `" + PointsTableFields.currentpoints.getCollumn() + "` FROM `" + DBTables.PointsTable.getTableName()
		+ "` WHERE `" + PointsTableFields.userid.getCollumn() + "` = ?;");
	    prest.setInt(1, player.getUserId());
	    res = prest.executeQuery();

	    if (res.next()) {
		player.getPointsData().setPoints(res.getDouble(PointsTableFields.currentpoints.getCollumn()));
		player.getPointsData().setTotalPoints(res.getDouble(PointsTableFields.totalpoints.getCollumn()));
	    }
	} catch (SQLException e) {
	    e.printStackTrace();
	} finally {
	    close(res);
	    close(prest);
	}
    }

    /**
     * Save player-job information
     * @param jobInfo - the information getting saved
     */
    public void saveLog(JobsPlayer player) {
	JobsConnection conn = getConnection();
	if (conn == null)
	    return;
	PreparedStatement prest1 = null;
	PreparedStatement prest2 = null;
	try {

	    conn.setAutoCommit(false);

	    prest1 = conn.prepareStatement("UPDATE `" + DBTables.LogTable.getTableName()
		+ "` SET `" + LogTableFields.count.getCollumn() + "` = ?, `" + LogTableFields.money.getCollumn() + "` = ?, `" + LogTableFields.exp.getCollumn()
		+ "` = ?, `" + LogTableFields.points.getCollumn() + "` = ? WHERE `" + LogTableFields.userid.getCollumn() + "` = ? AND `" + LogTableFields.time.getCollumn()
		+ "` = ? AND `" + LogTableFields.action.getCollumn() + "` = ? AND `" + LogTableFields.itemname.getCollumn() + "` = ?;");

	    boolean added = false;
	    for (Entry<String, Log> l : player.getLog().entrySet()) {
		Log log = l.getValue();
		for (Entry<String, LogAmounts> one : log.getAmountList().entrySet()) {
		    if (one.getValue().isNewEntry())
			continue;

		    prest1.setInt(1, one.getValue().getCount());
		    prest1.setDouble(2, one.getValue().get(CurrencyType.MONEY));
		    prest1.setDouble(3, one.getValue().get(CurrencyType.EXP));
		    prest1.setDouble(4, one.getValue().get(CurrencyType.POINTS));

		    prest1.setInt(5, player.getUserId());
		    prest1.setInt(6, log.getDate());
		    prest1.setString(7, log.getActionType());
		    prest1.setString(8, one.getKey());
//		    prest1.addBatch();
		    added = true;
		}
	    }
	    if (added) {
		prest1.execute();
		conn.commit();
	    }
	    added = false;
	    prest2 = conn.prepareStatement("INSERT INTO `" + DBTables.LogTable.getTableName()
		+ "` (`" + LogTableFields.userid.getCollumn() + "`, `" + LogTableFields.time.getCollumn() + "`, `" + LogTableFields.action.getCollumn()
		+ "`, `" + LogTableFields.itemname.getCollumn() + "`, `" + LogTableFields.count.getCollumn() + "`, `" + LogTableFields.money.getCollumn()
		+ "`, `" + LogTableFields.exp.getCollumn() + "`, `" + LogTableFields.points.getCollumn() + "`) VALUES (?, ?, ?, ?, ?, ?, ?, ?);");
	    for (Entry<String, Log> l : player.getLog().entrySet()) {
		Log log = l.getValue();
		for (Entry<String, LogAmounts> one : log.getAmountList().entrySet()) {
		    if (!one.getValue().isNewEntry())
			continue;
		    one.getValue().setNewEntry(false);

		    prest2.setInt(1, player.getUserId());
		    prest2.setInt(2, log.getDate());
		    prest2.setString(3, log.getActionType());
		    prest2.setString(4, one.getKey());
		    prest2.setInt(5, one.getValue().getCount());
		    prest2.setDouble(6, one.getValue().get(CurrencyType.MONEY));
		    prest2.setDouble(7, one.getValue().get(CurrencyType.EXP));
		    prest2.setDouble(8, one.getValue().get(CurrencyType.POINTS));
//		    prest2.addBatch();
		    added = true;
		}
	    }
	    if (added) {
		prest2.execute();
		conn.commit();
	    }
	    conn.setAutoCommit(true);
	} catch (SQLException e) {
	    e.printStackTrace();
	    close(prest1);
	    close(prest2);
	} finally {
	    close(prest1);
	    close(prest2);
	    try {
		conn.setAutoCommit(true);
	    } catch (SQLException e) {
		e.printStackTrace();
	    }
	}
    }

    /**
     * Save player-job information
     * @param jobInfo - the information getting saved
     */
    public void loadLog(JobsPlayer player) {
	JobsConnection conn = getConnection();
	if (conn == null)
	    return;
	PreparedStatement prest = null;
	ResultSet res = null;
	try {
	    int time = TimeManage.timeInInt();
	    prest = conn.prepareStatement("SELECT * FROM `" + DBTables.LogTable.getTableName()
		+ "` WHERE `" + LogTableFields.userid.getCollumn() + "` = ?  AND `" + LogTableFields.time.getCollumn() + "` = ? ;");
	    prest.setInt(1, player.getUserId());
	    prest.setInt(2, time);
	    res = prest.executeQuery();
	    while (res.next()) {

		HashMap<CurrencyType, Double> amounts = new HashMap<>();
		amounts.put(CurrencyType.MONEY, res.getDouble(LogTableFields.money.getCollumn()));
		amounts.put(CurrencyType.EXP, res.getDouble(LogTableFields.exp.getCollumn()));
		amounts.put(CurrencyType.POINTS, res.getDouble(LogTableFields.points.getCollumn()));

		Jobs.getLoging().loadToLog(player, res.getString(LogTableFields.action.getCollumn()), res.getString(LogTableFields.itemname.getCollumn()), res.getInt(LogTableFields.count.getCollumn()),
		    amounts);
	    }
	} catch (Exception e) {
	    close(res);
	    close(prest);
	    drop(DBTables.LogTable.getTableName());
	    createDefaultTable(DBTables.LogTable);
	} finally {
	    close(res);
	    close(prest);
	}
    }

    /**
     * Save block protection information
     * @param jobBlockProtection - the information getting saved
     */
    public void saveBlockProtection(String world, ConcurrentHashMap<String, BlockProtection> concurrentHashMap) {
	JobsConnection conn = getConnection();
	if (conn == null)
	    return;
	PreparedStatement insert = null;
	PreparedStatement update = null;
	PreparedStatement delete = null;
	try {
	    conn.setAutoCommit(false);
	    JobsWorld jobsWorld = Util.getJobsWorld(world);

	    int worldId = jobsWorld == null ? 0 : jobsWorld.getId();

	    if (worldId == 0)
		return;

	    insert = conn.prepareStatement("INSERT INTO `" + DBTables.BlocksTable.getTableName() + "` (`" + BlockTableFields.worldid.getCollumn()
		+ "`, `" + BlockTableFields.x.getCollumn()
		+ "`, `" + BlockTableFields.y.getCollumn()
		+ "`, `" + BlockTableFields.z.getCollumn()
		+ "`, `" + BlockTableFields.recorded.getCollumn()
		+ "`, `" + BlockTableFields.resets.getCollumn()
		+ "`, `" + BlockTableFields.world.getCollumn()
		+ "`) VALUES (?, ?, ?, ?, ?, ?, ?);");
	    update = conn.prepareStatement("UPDATE `" + DBTables.BlocksTable.getTableName() + "` SET `" + BlockTableFields.recorded.getCollumn()
		+ "` = ?, `" + BlockTableFields.resets.getCollumn()
		+ "` = ? WHERE `id` = ?;");
	    delete = conn.prepareStatement("DELETE from `" + DBTables.BlocksTable.getTableName() + "` WHERE `id` = ?;");

	    Long current = System.currentTimeMillis();
	    Long mark = System.currentTimeMillis() - (Jobs.getGCManager().BlockProtectionDays * 24L * 60L * 60L * 1000L);

	    for (Entry<String, BlockProtection> block : concurrentHashMap.entrySet()) {
		if (block.getValue() == null)
		    continue;
		switch (block.getValue().getAction()) {
		case DELETE:
		    delete.setInt(1, block.getValue().getId());
		    delete.addBatch();

		    break;
		case INSERT:
		    if (block.getValue().getTime() < current && block.getValue().getTime() != -1)
			continue;

		    insert.setInt(1, worldId);
		    insert.setInt(2, block.getValue().getX());
		    insert.setInt(3, block.getValue().getY());
		    insert.setInt(4, block.getValue().getZ());
		    insert.setLong(5, block.getValue().getRecorded());
		    insert.setLong(6, block.getValue().getTime());
		    insert.setString(7, world);
		    insert.addBatch();
		    block.getValue().setAction(DBAction.NONE);

		    break;
		case UPDATE:
		    if (block.getValue().getTime() < current && block.getValue().getTime() != -1)
			continue;
		    update.setLong(1, block.getValue().getRecorded());
		    update.setLong(2, block.getValue().getTime());
		    update.setInt(3, block.getValue().getId());
		    update.addBatch();
		    block.getValue().setAction(DBAction.NONE);

		    break;
		case NONE:
		    if (block.getValue().getTime() < current && block.getValue().getTime() != -1)
			continue;
		    if (block.getValue().getTime() == -1 && block.getValue().getRecorded() > mark)
			continue;

		    delete.setInt(1, block.getValue().getId());
		    delete.addBatch();

		    break;
		default:
		    continue;
		}
	    }

	    insert.executeBatch();
	    update.executeBatch();
	    delete.executeBatch();
	    conn.commit();

	} catch (SQLException e) {
	    e.printStackTrace();
	} finally {
	    close(insert);
	    close(update);
	    close(delete);
	    try {
		conn.setAutoCommit(true);
	    } catch (SQLException e) {
		e.printStackTrace();
	    }
	}
    }

    /**
     * Save block protection information
     * @param jobBlockProtection - the information getting saved
     */
    public void loadBlockProtection() {
	JobsConnection conn = getConnection();
	if (conn == null)
	    return;
	PreparedStatement prest = null;
	PreparedStatement prestDel = null;
	ResultSet res = null;

	Long timer = System.currentTimeMillis();

	try {
	    Long mark = System.currentTimeMillis() - (Jobs.getGCManager().BlockProtectionDays * 24L * 60L * 60L * 1000L);
	    prestDel = conn.prepareStatement("DELETE FROM `" + DBTables.BlocksTable.getTableName() + "` WHERE `" + BlockTableFields.recorded.getCollumn() + "` < ?;");
	    prestDel.setLong(1, mark);
	    prestDel.execute();
	} catch (SQLException e) {
	    e.printStackTrace();
	} finally {
	    close(prestDel);
	}

	try {
	    prest = conn.prepareStatement("SELECT * FROM `" + DBTables.BlocksTable.getTableName() + "`;");
	    res = prest.executeQuery();
	    int i = 0;
	    int ii = 0;

	    while (res.next()) {

		String name = res.getString(BlockTableFields.world.getCollumn());
		int worldId = res.getInt(BlockTableFields.worldid.getCollumn());

		World world = null;
		if (worldId != 0) {
		    JobsWorld jobsWorld = Util.getJobsWorld(worldId);
		    if (jobsWorld != null)
			world = jobsWorld.getWorld();
		} else {
		    world = Bukkit.getWorld(name);
		}
		if (world == null)
		    continue;

		int id = res.getInt("id");
		int x = res.getInt(BlockTableFields.x.getCollumn());
		int y = res.getInt(BlockTableFields.y.getCollumn());
		int z = res.getInt(BlockTableFields.z.getCollumn());
		long resets = res.getLong(BlockTableFields.resets.getCollumn());
		Location loc = new Location(world, x, y, z);

		BlockProtection bp = Jobs.getBpManager().addP(loc, resets, true, false);
		bp.setId(id);
		bp.setRecorded(res.getLong(BlockTableFields.recorded.getCollumn()));
		bp.setAction(DBAction.NONE);
		i++;
		ii++;

		if (ii >= 100000) {
		    Jobs.consoleMsg("&6[Jobs] Loading (" + i + ") BP");
		    ii = 0;
		}
	    }
	    if (i > 0) {
		Jobs.consoleMsg("&e[Jobs] Loaded " + i + " block protection entries. " + (System.currentTimeMillis() - timer) + "ms");
	    }
	} catch (SQLException e) {
	    e.printStackTrace();
	} finally {
	    close(res);
	    close(prest);
	}
    }

    /**
     * Save player-explore information
     */
    public void saveExplore() {
	insertExplore();
	updateExplore();
    }

    public void insertExplore() {
	if (!Jobs.getExplore().isExploreEnabled())
	    return;

	JobsConnection conn = getConnection();
	if (conn == null)
	    return;
	PreparedStatement prest2 = null;
	try {

	    prest2 = conn.prepareStatement("INSERT INTO `" + DBTables.ExploreDataTable.getTableName() + "` (`" + ExploreDataTableFields.worldid.getCollumn()
		+ "`, `" + ExploreDataTableFields.chunkX.getCollumn()
		+ "`, `" + ExploreDataTableFields.chunkZ.getCollumn()
		+ "`, `" + ExploreDataTableFields.playerNames.getCollumn()
		+ "`, `" + ExploreDataTableFields.worldname.getCollumn()
		+ "`) VALUES (?, ?, ?, ?, ?);");
	    conn.setAutoCommit(false);
	    int i = 0;

	    HashMap<String, ExploreRegion> temp = new HashMap<>(Jobs.getExplore().getWorlds());

	    for (Entry<String, ExploreRegion> worlds : temp.entrySet()) {
		JobsWorld jobsWorld = Util.getJobsWorld(worlds.getKey());

		int id = jobsWorld == null ? 0 : jobsWorld.getId();
		if (id != 0)
		    for (Entry<String, ExploreChunk> oneChunk : worlds.getValue().getChunks().entrySet()) {
			if (oneChunk.getValue().getDbId() != null)
			    continue;
			prest2.setInt(1, id);
			prest2.setInt(2, oneChunk.getValue().getX());
			prest2.setInt(3, oneChunk.getValue().getZ());
			prest2.setString(4, oneChunk.getValue().serializeNames());
			prest2.setString(5, jobsWorld != null ? jobsWorld.getName() : "");
			prest2.addBatch();
			i++;
		    }
	    }
	    prest2.executeBatch();
	    conn.commit();
	    conn.setAutoCommit(true);

	    if (i > 0)
		Jobs.consoleMsg("&e[Jobs] Saved " + i + " new explorer entries.");

	} catch (SQLException e) {
	    e.printStackTrace();
	} finally {
	    close(prest2);
	    try {
		conn.setAutoCommit(true);
	    } catch (SQLException e) {
		e.printStackTrace();
	    }
	}
    }

    public void updateExplore() {
	if (!Jobs.getExplore().isExploreEnabled())
	    return;

	JobsConnection conn = getConnection();
	if (conn == null)
	    return;
	PreparedStatement prest = null;
	try {
	    conn.setAutoCommit(false);
	    prest = conn.prepareStatement("UPDATE `" + DBTables.ExploreDataTable.getTableName() + "` SET `" + ExploreDataTableFields.playerNames.getCollumn() + "` = ? WHERE `id` = ?;");

	    int i = 0;

	    HashMap<String, ExploreRegion> temp = new HashMap<>(Jobs.getExplore().getWorlds());

	    for (Entry<String, ExploreRegion> worlds : temp.entrySet()) {
		for (Entry<String, ExploreChunk> oneChunk : worlds.getValue().getChunks().entrySet()) {
		    if (oneChunk.getValue().getDbId() == null)
			continue;
		    if (!oneChunk.getValue().isUpdated())
			continue;
		    prest.setString(1, oneChunk.getValue().serializeNames());
		    prest.setInt(2, oneChunk.getValue().getDbId());
		    prest.addBatch();
		    i++;
		}
	    }
	    prest.executeBatch();
	    conn.commit();
	    conn.setAutoCommit(true);

	    if (i > 0)
		Jobs.consoleMsg("&e[Jobs] Updated " + i + " explorer entries.");

	} catch (SQLException e) {
	    e.printStackTrace();
	} finally {
	    close(prest);
	    try {
		conn.setAutoCommit(true);
	    } catch (SQLException e) {
		e.printStackTrace();
	    }
	}
    }

    /**
     * Save player-explore information
     * @param jobexplore - the information getting saved
     */
    public void loadExplore() {
	if (!Jobs.getExplore().isExploreEnabled())
	    return;

	JobsConnection conn = getConnection();
	if (conn == null)
	    return;

	PreparedStatement prest = null;
	ResultSet res = null;
	try {
	    prest = conn.prepareStatement("SELECT * FROM `" + DBTables.ExploreDataTable.getTableName() + "`;");
	    res = prest.executeQuery();
	    Set<Integer> missingWorlds = new HashSet<Integer>();
	    while (res.next()) {
		int worldId = res.getInt(ExploreDataTableFields.worldid.getCollumn());
		JobsWorld jworld = Util.getJobsWorld(worldId);
		if (jworld == null || jworld.getWorld() == null) {
		    missingWorlds.add(worldId);
		} else {
		    Jobs.getExplore().load(res);
		}
	    }

	    for (Integer one : missingWorlds) {
		PreparedStatement prest2 = null;
		prest2 = conn.prepareStatement("DELETE FROM `" + DBTables.ExploreDataTable.getTableName() + "` WHERE `" + ExploreDataTableFields.worldid.getCollumn() + "` = ?;");
		prest2.setInt(1, one);
		prest2.execute();
		close(prest2);
	    }

	} catch (SQLException e) {
	    e.printStackTrace();
	} finally {
	    close(res);
	    close(prest);
	}

    }

    /**
     * Save player-job information
     * @param jobInfo - the information getting saved
     * @return 
     */
    public List<Integer> getLognameList(int fromtime, int untiltime) {
	JobsConnection conn = getConnection();
	List<Integer> nameList = new ArrayList<>();
	if (conn == null)
	    return nameList;
	PreparedStatement prest = null;
	ResultSet res = null;
	try {
	    prest = conn.prepareStatement("SELECT `" + LogTableFields.userid.getCollumn() + "` FROM `" + DBTables.LogTable.getTableName() + "` WHERE `" + LogTableFields.time.getCollumn() + "` >= ?  AND `"
		+ LogTableFields.time.getCollumn() + "` <= ? ;");
	    prest.setInt(1, fromtime);
	    prest.setInt(2, untiltime);
	    res = prest.executeQuery();
	    while (res.next()) {
		int id = res.getInt(LogTableFields.userid.getCollumn());
		if (!nameList.contains(id))
		    nameList.add(id);
	    }
	} catch (SQLException e) {
	    e.printStackTrace();
	} finally {
	    close(res);
	    close(prest);
	}
	return nameList;
    }

    /**
     * Show top list
     * @param toplist - toplist by jobs name
     * @return 
     */
    public ArrayList<TopList> toplist(String jobsname) {
	return toplist(jobsname, 0);
    }

    /**
     * Show top list
     * @param toplist - toplist by jobs name
     * @return 
     */
    public ArrayList<TopList> toplist(String jobsname, int limit) {
	ArrayList<TopList> jobs = new ArrayList<>();
	JobsConnection conn = getConnection();
	if (conn == null)
	    return jobs;

	Job job = Jobs.getJob(jobsname);
	if (job == null)
	    return jobs;
	PreparedStatement prest = null;
	ResultSet res = null;

	if (limit < 0) {
	    limit = 0;
	}

	try {
	    prest = conn.prepareStatement("SELECT `" + JobsTableFields.userid.getCollumn() + "`, `" + JobsTableFields.level.getCollumn() + "`, `" + JobsTableFields.experience.getCollumn() + "` FROM `"
		+ getJobsTableName() + "` WHERE `" + JobsTableFields.jobid.getCollumn() + "` LIKE ? ORDER BY `" + JobsTableFields.level.getCollumn() + "` DESC, LOWER("
		+ JobsTableFields.experience.getCollumn() + ") DESC LIMIT " + limit + ", 50;");
	    prest.setInt(1, job.getId());
	    res = prest.executeQuery();

	    while (res.next()) {
		PlayerInfo info = Jobs.getPlayerManager().getPlayerInfo(res.getInt(JobsTableFields.userid.getCollumn()));
		if (info == null)
		    continue;

		jobs.add(new TopList(info, res.getInt(JobsTableFields.level.getCollumn()), res.getInt(JobsTableFields.experience.getCollumn())));
	    }
	} catch (SQLException e) {
	    e.printStackTrace();
	} finally {
	    close(res);
	    close(prest);
	}

	return jobs;
    }

    /**
     * Get the number of players that have a particular job
     * @param job - the job
     * @return  the number of players that have a particular job
     */
    public synchronized int getSlotsTaken(Job job) {
	int slot = 0;
	JobsConnection conn = getConnection();
	if (conn == null)
	    return slot;
	PreparedStatement prest = null;
	ResultSet res = null;
	try {
	    prest = conn.prepareStatement("SELECT COUNT(*) FROM `" + getJobsTableName() + "` WHERE `" + JobsTableFields.jobid.getCollumn() + "` = ?;");
	    prest.setInt(1, job.getId());
	    res = prest.executeQuery();
	    if (res.next()) {
		slot = res.getInt(1);
	    }
	} catch (SQLException e) {
	    e.printStackTrace();
	} finally {
	    close(res);
	    close(prest);
	}
	return slot;
    }

    /**
     * Executes an SQL query
     * @param sql - The SQL
     * @throws SQLException
     */
    public void executeSQL(String sql) throws SQLException {
	JobsConnection conn = getConnection();
	Statement stmt = null;
	try {
	    stmt = conn.createStatement();
	    stmt.execute(sql);
	} finally {
	    close(stmt);
	}
    }

    /**
     * Get a database connection
     * @return  DBConnection object
     * @throws SQLException 
     */
    protected JobsConnection getConnection() {
	try {
	    return isConnected() ? pool.getConnection() : null;
	} catch (SQLException e) {
	    Jobs.getPluginLogger().severe("Unable to connect to the database: " + e.getMessage());
	    return null;
	}
    }

    public synchronized void vacuum() {
	if (dbType != DataBaseType.SqLite)
	    return;
	JobsConnection conn = getConnection();
	if (conn == null)
	    return;

	PreparedStatement prest = null;
	try {
	    prest = conn.prepareStatement("VACUUM;");
	    prest.execute();
	} catch (Exception | Error e) {
	} finally {
	    close(prest);
	}
	return;
    }

    /**
     * Close all active database handles
     */
    public synchronized void closeConnections() {
	pool.closeConnection();
    }

    protected static void close(ResultSet res) {
	if (res != null)
	    try {
		res.close();
	    } catch (SQLException e) {
		e.printStackTrace();
	    }
    }

    protected static void close(Statement stmt) {
	if (stmt != null)
	    try {
		stmt.close();
	    } catch (SQLException e) {
		e.printStackTrace();
	    }
    }

    protected static void close(PreparedStatement stmt) {
	if (stmt != null)
	    try {
		stmt.close();
	    } catch (SQLException e) {
		e.printStackTrace();
	    }
    }

    public HashMap<Integer, ArrayList<JobsDAOData>> getMap() {
	return map;
    }

    public String getJobsTableName() {
	return DBTables.JobsTable.getTableName();
    }

}