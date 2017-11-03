package com.gamingmesh.jobs.dao;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;
import java.util.Map.Entry;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.OfflinePlayer;
import org.bukkit.World;
import org.bukkit.command.ConsoleCommandSender;
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
import com.gamingmesh.jobs.container.Log;
import com.gamingmesh.jobs.container.LogAmounts;
import com.gamingmesh.jobs.container.PlayerInfo;
import com.gamingmesh.jobs.container.PlayerPoints;
import com.gamingmesh.jobs.container.TopList;
import com.gamingmesh.jobs.dao.JobsManager.DataBaseType;
import com.gamingmesh.jobs.economy.PaymentData;
import com.gamingmesh.jobs.stuff.Debug;
import com.gamingmesh.jobs.stuff.TimeManage;

public abstract class JobsDAO {

    private JobsConnectionPool pool;
    private static String prefix;
    private Jobs plugin;

    private static DataBaseType dbType = DataBaseType.SqLite;

    // Not in use currently
    public enum TablesFieldsType {
	decimal, number, text, varchar, stringList, stringLongMap, stringIntMap, locationMap, state, location, longNumber;
    }

    public enum UserTableFields implements JobsTableInterface {
	player_uuid("varchar(36)", TablesFieldsType.varchar),
	username("text", TablesFieldsType.text),
	seen("bigint", TablesFieldsType.longNumber);

	private String type;
	private TablesFieldsType fieldType;

	UserTableFields(String type, TablesFieldsType fieldType) {
	    this.type = type;
	    this.fieldType = fieldType;
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
	public TablesFieldsType getFieldType() {
	    return fieldType;
	}
    }

    public enum JobsTableFields implements JobsTableInterface {
	userid("int", TablesFieldsType.number),
	job("text", TablesFieldsType.text),
	experience("int", TablesFieldsType.number),
	level("int", TablesFieldsType.number);

	private String type;
	private TablesFieldsType fieldType;

	JobsTableFields(String type, TablesFieldsType fieldType) {
	    this.type = type;
	    this.fieldType = fieldType;
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
	public TablesFieldsType getFieldType() {
	    return fieldType;
	}
    }

    public enum ArchiveTableFields implements JobsTableInterface {
	userid("int", TablesFieldsType.number),
	job("text", TablesFieldsType.text),
	experience("int", TablesFieldsType.number),
	level("int", TablesFieldsType.number),
	left("bigint", TablesFieldsType.longNumber);

	private String type;
	private TablesFieldsType fieldType;

	ArchiveTableFields(String type, TablesFieldsType fieldType) {
	    this.type = type;
	    this.fieldType = fieldType;
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
	public TablesFieldsType getFieldType() {
	    return fieldType;
	}
    }

    public enum BlockTableFields implements JobsTableInterface {
	world("varchar(36)", TablesFieldsType.varchar),
	x("int", TablesFieldsType.number),
	y("int", TablesFieldsType.number),
	z("int", TablesFieldsType.number),
	recorded("bigint", TablesFieldsType.longNumber),
	resets("bigint", TablesFieldsType.longNumber);

	private String type;
	private TablesFieldsType fieldType;

	BlockTableFields(String type, TablesFieldsType fieldType) {
	    this.type = type;
	    this.fieldType = fieldType;
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
	public TablesFieldsType getFieldType() {
	    return fieldType;
	}
    }

    public enum LimitTableFields implements JobsTableInterface {
	userid("int", TablesFieldsType.number),
	type("varchar(36)", TablesFieldsType.number),
	collected("double", TablesFieldsType.decimal),
	started("bigint", TablesFieldsType.longNumber);

	private String ttype;
	private TablesFieldsType fieldType;

	LimitTableFields(String type, TablesFieldsType fieldType) {
	    this.ttype = type;
	    this.fieldType = fieldType;
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
	public TablesFieldsType getFieldType() {
	    return fieldType;
	}
    }

    public enum LogTableFields implements JobsTableInterface {
	userid("int", TablesFieldsType.number),
	time("bigint", TablesFieldsType.longNumber),
	action("varchar(20)", TablesFieldsType.varchar),
	itemname("text", TablesFieldsType.text),
	count("int", TablesFieldsType.number),
	money("double", TablesFieldsType.decimal),
	exp("double", TablesFieldsType.decimal),
	points("double", TablesFieldsType.decimal);

	private String type;
	private TablesFieldsType fieldType;

	LogTableFields(String type, TablesFieldsType fieldType) {
	    this.type = type;
	    this.fieldType = fieldType;
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
	public TablesFieldsType getFieldType() {
	    return fieldType;
	}
    }

    public enum PointsTableFields implements JobsTableInterface {
	userid("int", TablesFieldsType.number),
	totalpoints("double", TablesFieldsType.decimal),
	currentpoints("double", TablesFieldsType.decimal);

	private String type;
	private TablesFieldsType fieldType;

	PointsTableFields(String type, TablesFieldsType fieldType) {
	    this.type = type;
	    this.fieldType = fieldType;
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
	public TablesFieldsType getFieldType() {
	    return fieldType;
	}
    }

    public enum ExploreDataTableFields implements JobsTableInterface {
	worldname("varchar(64)", TablesFieldsType.varchar),
	chunkX("int", TablesFieldsType.number),
	chunkZ("int", TablesFieldsType.number),
	playerNames("text", TablesFieldsType.text);

	private String type;
	private TablesFieldsType fieldType;

	ExploreDataTableFields(String type, TablesFieldsType fieldType) {
	    this.type = type;
	    this.fieldType = fieldType;
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
	public TablesFieldsType getFieldType() {
	    return fieldType;
	}
    }

    public enum DBTables {
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
	    }
	    return "";
	}

	public String getQuery() {
	    String rp = "";
	    for (JobsTableInterface one : this.getInterface()) {
		rp += ", " + "`" + one.getCollumn() + "` " + one.getType();
	    }
	    return getQR().replace("[fields]", rp);
	}

	public JobsTableInterface[] getInterface() {
	    return this.c;
	}

//	public String getUpdateQuery() {
//
//	    String rp = "";
//	    for (JobsTableInterface one : this.getInterface()) {
//		if (one.getCollumn().equalsIgnoreCase("userid") || one.getCollumn().equalsIgnoreCase("player_uuid"))
//		    continue;
//		if (!rp.isEmpty())
//		    rp += ", ";
//
//	    }
//
//	    switch (this) {
//	    case JobsTable:
//		for (JobsTableInterface one : this.getInterface()) {
//		    if (one == JobsTableFields.userid)
//			continue;
//		    if (!rp.isEmpty())
//			rp += ", ";
//		    rp += "`" + one.getCollumn() + "` = ?";
//		}
//		rp = "UPDATE `" + getTableName() + "` SET " + rp + " WHERE `player_id` = ?;";
//		return rp;
//	    default:
//		rp = "";
//		for (JobsTableInterface one : this.getInterface()) {
//		    if (one.getCollumn().equalsIgnoreCase("userid") || one.getCollumn().equalsIgnoreCase("player_uuid"))
//			continue;
//		    if (!rp.isEmpty())
//			rp += ", ";
//		    rp += "`" + one.getCollumn() + "` = ?";
//		}
//		rp = "UPDATE `" + getTableName() + "` SET " + rp + " WHERE `id` = ?;";
//		return rp;
//	    }
//	}
//
//	public String getInsertQuery() {
//	    String rp = "";
//	    String q = "";
//
//	    for (JobsTableInterface one : this.getInterface()) {
//		if (!rp.isEmpty())
//		    rp += ", ";
//		rp += "`" + one.getCollumn() + "`";
//
//		if (!q.isEmpty())
//		    q += ", ";
//		q += "?";
//	    }
//	    rp = "INSERT INTO `" + getTableName() + "` (" + rp + ") VALUES (" + q + ");";
//	    return rp;
//	}

	public String getTableName() {
	    return prefix + tableName;
	}
    }

    protected JobsDAO(Jobs plugin, String driverName, String url, String username, String password, String pr) {
	this.plugin = plugin;
	prefix = pr;
	try {
	    pool = new JobsConnectionPool(driverName, url, username, password);
	} catch (Exception e) {
	    e.printStackTrace();
	}
    }

    public final synchronized void setUp() throws SQLException {
	setupConfig();
	int version = getSchemaVersion();
	if (version == 0) {
	    Jobs.consoleMsg("&cCould not initialize database!  Could not determine schema version!");
	    return;
	}

	try {
	    version = 11;
	    updateSchemaVersion(version);
	    for (DBTables one : DBTables.values()) {
		createDefaultTable(one);
	    }
	    checkDefaultCollumns();
	} finally {
	}
    }

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
	    return pool.getConnection() != null;
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

	ArrayList<JobsDAOData> jobs = new ArrayList<JobsDAOData>();

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
	    prest = conn.prepareStatement("SELECT `job`, `level`, `experience` FROM `" + prefix + "jobs` WHERE `userid` = ?;");
	    prest.setInt(1, id);
	    res = prest.executeQuery();
	    while (res.next()) {
		jobs.add(new JobsDAOData(res.getString("job"), res.getInt("level"), res.getInt("experience")));
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
	HashMap<Integer, List<JobsDAOData>> map = new HashMap<Integer, List<JobsDAOData>>();
	JobsConnection conn = getConnection();
	if (conn == null)
	    return map;
	PreparedStatement prest = null;
	ResultSet res = null;
	try {
	    prest = conn.prepareStatement("SELECT * FROM `" + prefix + "jobs`;");
	    res = prest.executeQuery();
	    while (res.next()) {
		int id = res.getInt("userid");
		List<JobsDAOData> ls = map.get(id);
		if (ls == null)
		    ls = new ArrayList<JobsDAOData>();
		ls.add(new JobsDAOData(res.getString("job"), res.getInt("level"), res.getInt("experience")));
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
	HashMap<Integer, PlayerPoints> map = new HashMap<Integer, PlayerPoints>();
	JobsConnection conn = getConnection();
	if (conn == null)
	    return map;
	PreparedStatement prest = null;
	ResultSet res = null;
	try {
	    prest = conn.prepareStatement("SELECT * FROM `" + prefix + "points`;");
	    res = prest.executeQuery();
	    while (res.next()) {
		map.put(res.getInt(PointsTableFields.userid.getCollumn()), new PlayerPoints(res.getDouble("currentpoints"), res.getDouble("totalpoints")));
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
	HashMap<Integer, ArchivedJobs> map = new HashMap<Integer, ArchivedJobs>();
	JobsConnection conn = getConnection();
	if (conn == null)
	    return map;
	PreparedStatement prest = null;
	ResultSet res = null;
	try {
	    prest = conn.prepareStatement("SELECT * FROM `" + prefix + "archive`;");
	    res = prest.executeQuery();
	    while (res.next()) {

		int id = res.getInt("userid");
		String jobName = res.getString("job");
		Double exp = res.getDouble("experience");
		int lvl = res.getInt("level");
		Long left = res.getLong("left");

		Job job = Jobs.getJob(jobName);
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
	HashMap<Integer, HashMap<String, Log>> map = new HashMap<Integer, HashMap<String, Log>>();
	JobsConnection conn = getConnection();
	if (conn == null)
	    return map;
	PreparedStatement prest = null;
	ResultSet res = null;
	try {
	    int time = TimeManage.timeInInt();
	    prest = conn.prepareStatement("SELECT * FROM `" + prefix + "log` WHERE `time` = ? ;");
	    prest.setInt(1, time);
	    res = prest.executeQuery();
	    while (res.next()) {

		int id = res.getInt("userid");

		HashMap<String, Log> m = map.get(id);
		if (m == null)
		    m = new HashMap<String, Log>();
		String action = res.getString("action");
		Log log = m.get(action);

		if (log == null)
		    log = new Log(action);

		HashMap<CurrencyType, Double> amounts = new HashMap<CurrencyType, Double>();
		amounts.put(CurrencyType.MONEY, res.getDouble("money"));
		amounts.put(CurrencyType.EXP, res.getDouble("exp"));
		amounts.put(CurrencyType.POINTS, res.getDouble("points"));

		log.add(res.getString("itemname"), res.getInt("count"), amounts);

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

    private HashMap<Integer, ArrayList<JobsDAOData>> map = new HashMap<Integer, ArrayList<JobsDAOData>>();

    public List<JobsDAOData> getAllJobs(PlayerInfo pInfo) {
	List<JobsDAOData> list = map.get(pInfo.getID());
	if (list != null)
	    return list;
	return new ArrayList<JobsDAOData>();
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
	    prest = conn.prepareStatement("DELETE FROM `" + prefix + "users` WHERE `seen` < ?;");
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
	    prest = conn.prepareStatement("DELETE FROM `" + prefix + "jobs` WHERE `level` <= ?;");
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
	    prestt = conn.prepareStatement("INSERT INTO `" + prefix + "users` (`player_uuid`, `username`, `seen`) VALUES (?, ?, ?);", Statement.RETURN_GENERATED_KEYS);
	    prestt.setString(1, uuid.toString());
	    prestt.setString(2, playerName);
	    prestt.setLong(3, System.currentTimeMillis());
	    prestt.executeUpdate();

	    res2 = prestt.getGeneratedKeys();
	    int id = 0;
	    if (res2.next())
		id = res2.getInt(1);

	    Debug.D("got id " + id);

	    Jobs.getPlayerManager().addPlayerToMap(new PlayerInfo(playerName, id, uuid, System.currentTimeMillis()));
	} catch (SQLException e) {
	    e.printStackTrace();
	} finally {
	    close(prestt);
	    close(res2);
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
	    prest = conn.prepareStatement("SELECT COUNT(*) FROM `" + prefix + "jobs` WHERE `job` = ?;");
	    prest.setString(1, JobName);
	    res = prest.executeQuery();
	    while (res.next()) {
		count = res.getInt(1);
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

	ArrayList<JobsDAOData> jobs = new ArrayList<JobsDAOData>();

	PlayerInfo info = Jobs.getPlayerManager().getPlayerInfo(userName);
	if (info == null)
	    return jobs;

	JobsConnection conn = getConnection();
	if (conn == null)
	    return jobs;
	PreparedStatement prest = null;
	ResultSet res = null;
	try {
	    prest = conn.prepareStatement("SELECT `job`, `level`, `experience` FROM `" + prefix + "jobs` WHERE `userid` = ?;");
	    prest.setInt(1, info.getID());
	    res = prest.executeQuery();
	    while (res.next()) {
		jobs.add(new JobsDAOData(res.getString(2), res.getInt(3), res.getInt(4)));
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
	    prest2 = conn.prepareStatement("DELETE FROM `" + prefix + "limits` WHERE `userid` = ?;");
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
	    prest = conn.prepareStatement("INSERT INTO `" + prefix + "limits` (`userid`, `type`, `collected`, `started`) VALUES (?, ?, ?, ?);");
	    conn.setAutoCommit(false);
	    for (CurrencyType type : CurrencyType.values()) {
		if (limit == null)
		    continue;
		if (limit.GetAmount(type) == 0D)
		    continue;
		if (limit.GetLeftTime(type) < 0)
		    continue;

		prest.setInt(1, jPlayer.getUserId());
		prest.setString(2, type.getName());
		prest.setDouble(3, limit.GetAmount(type));
		prest.setLong(4, limit.GetTime(type));
		prest.addBatch();
	    }
	    prest.executeBatch();
	    conn.commit();
	    conn.setAutoCommit(true);
	} catch (Exception e) {
	    e.printStackTrace();
	} finally {
	    close(prest);
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
	    prest = conn.prepareStatement("SELECT `type`, `collected`, `started` FROM `" + prefix + "limits` WHERE `userid` = ?;");
	    prest.setInt(1, jPlayer.getUserId());
	    res = prest.executeQuery();
	    while (res.next()) {
		CurrencyType type = CurrencyType.getByName(res.getString("type"));
		if (type == null)
		    continue;
		data.AddNewAmount(type, res.getDouble("collected"), res.getLong("started"));
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
	HashMap<Integer, PaymentData> map = new HashMap<Integer, PaymentData>();
	JobsConnection conn = getConnection();
	if (conn == null)
	    return map;
	PreparedStatement prest = null;
	ResultSet res = null;
	try {
	    prest = conn.prepareStatement("SELECT * FROM `" + prefix + "limits`;");
	    res = prest.executeQuery();
	    while (res.next()) {
		int id = res.getInt(LimitTableFields.userid.getCollumn());
		PaymentData data = map.get(id);
		if (data == null)
		    data = new PaymentData();
		CurrencyType type = CurrencyType.getByName(res.getString("type"));
		if (type == null)
		    continue;
		data.AddNewAmount(type, res.getDouble("collected"), res.getLong("started"));
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
	    prest = conn.prepareStatement("INSERT INTO `" + prefix + "jobs` (`userid`, `job`, `level`, `experience`) VALUES (?, ?, ?, ?);");
	    prest.setInt(1, jPlayer.getUserId());
	    prest.setString(2, job.getJob().getName());
	    prest.setInt(3, level);
	    prest.setInt(4, exp.intValue());
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
	    int exp = (int) prog.getExperience();
	    if (exp < 0)
		exp = 0;
	    prest = conn.prepareStatement("INSERT INTO `" + prefix + "jobs` (`userid`, `job`, `level`, `experience`) VALUES (?, ?, ?, ?);");
	    prest.setInt(1, jPlayer.getUserId());
	    prest.setString(2, prog.getJob().getName());
	    prest.setInt(3, prog.getLevel());
	    prest.setInt(4, exp);
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
    public List<Convert> convertDatabase(String table) throws SQLException {
	JobsConnection conn = getConnection();
	if (conn == null)
	    return null;

	List<Convert> list = new ArrayList<Convert>();
	PreparedStatement prest = null;
	ResultSet res = null;
	try {
	    prest = conn.prepareStatement("SELECT * FROM `" + prefix + table + "`");
	    res = prest.executeQuery();
	    while (res.next()) {
		int id = res.getInt("userid");
		PlayerInfo pi = Jobs.getPlayerManager().getPlayerInfo(id);
		if (pi == null)
		    continue;
		JobsPlayer jPlayer = Jobs.getPlayerManager().getJobsPlayer(pi.getUuid());
		if (jPlayer == null)
		    continue;
		list.add(new Convert(res.getInt("id"), jPlayer.getPlayerUUID(), res.getString("job"), res.getInt("level"), res.getInt("experience")));
	    }
	} catch (SQLException e) {
	    e.printStackTrace();
	} finally {
	    close(res);
	    close(prest);
	}

	try {
	    conn.closeConnection();
	} catch (SQLException e) {
	    e.printStackTrace();
	}
	return list;
    }

    public void continueConvertions(List<Convert> list, String table) throws SQLException {
	JobsConnection conns = this.getConnection();
	if (conns == null)
	    return;
	PreparedStatement insert = null;
	Statement statement = null;
	int i = list.size();
	try {
	    statement = conns.createStatement();
	    if (Jobs.getDBManager().getDbType().toString().equalsIgnoreCase("sqlite")) {
		statement.executeUpdate("TRUNCATE `" + getPrefix() + table + "`");
	    } else {
		statement.executeUpdate("DELETE from `" + getPrefix() + table + "`");
	    }

	    insert = conns.prepareStatement("INSERT INTO `" + getPrefix() + table + "` (`userid`, `job`, `level`, `experience`) VALUES (?, ?, ?, ?);");
	    conns.setAutoCommit(false);
	    while (i > 0) {
		i--;

		Convert convertData = list.get(i);

		JobsPlayer jPlayer = Jobs.getPlayerManager().getJobsPlayer(convertData.GetUserUUID());
		if (jPlayer == null)
		    continue;

		insert.setInt(1, jPlayer.getUserId());
		insert.setString(2, convertData.GetJobName());
		insert.setInt(3, convertData.GetLevel());
		insert.setInt(4, convertData.GetExp());
		insert.addBatch();
	    }
	    insert.executeBatch();
	    conns.commit();
	    conns.setAutoCommit(true);
	} finally {
	    close(statement);
	    close(insert);
	}
    }

//    public void transferUsers() throws SQLException {
//	JobsConnection conns = this.getConnection();
//	if (conns == null)
//	    return;
//	PreparedStatement insert = null;
//	Statement statement = null;
//	try {
//	    statement = conns.createStatement();
//	    if (Jobs.getGCManager().storageMethod.equalsIgnoreCase("sqlite")) {
//		statement.executeUpdate("TRUNCATE `" + getPrefix() + "users`");
//	    } else {
//		statement.executeUpdate("DELETE from `" + getPrefix() + "users`");
//	    }
//
//	    insert = conns.prepareStatement("INSERT INTO `" + getPrefix() + "users` (`id`, `player_uuid`, `username`, `seen`) VALUES (?, ?, ?, ?);");
//	    conns.setAutoCommit(false);
//
//	    for (Entry<UUID, JobsPlayer> oneUser : Jobs.getPlayerManager().getPlayersCache().entrySet()) {
//		insert.setInt(1, oneUser.getValue().getUserId());
//		insert.setString(2, oneUser.getValue().getPlayerUUID().toString());
//		insert.setString(3, oneUser.getValue().getUserName());
//		insert.setLong(4, oneUser.getValue().getSeen() == null ? System.currentTimeMillis() : oneUser.getValue().getSeen());
//		insert.addBatch();
//	    }
//	    insert.executeBatch();
//	    conns.commit();
//	    conns.setAutoCommit(true);
//	} finally {
//	    close(statement);
//	    close(insert);
//	}
//    }

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
	    prest = conn.prepareStatement("DELETE FROM `" + prefix + "jobs` WHERE `userid` = ? AND `job` = ?;");
	    prest.setInt(1, jPlayer.getUserId());
	    prest.setString(2, job.getName());
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
	    prest = conn.prepareStatement("INSERT INTO `" + prefix + "archive` (`userid`, `job`, `level`, `experience`, `left`) VALUES (?, ?, ?, ?, ?);");
	    prest.setInt(1, jPlayer.getUserId());
	    prest.setString(2, job.getName());
	    prest.setInt(3, level);
	    prest.setInt(4, exp.intValue());
	    prest.setLong(5, System.currentTimeMillis());
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
     * Get all jobs from archive by player
     * @param player - targeted player
     * @return info - information about jobs
     */
    public List<TopList> getGlobalTopList(int start) {
	JobsConnection conn = getConnection();

	List<TopList> names = new ArrayList<TopList>();

	if (conn == null)
	    return names;
	PreparedStatement prest = null;
	ResultSet res = null;
	try {

	    prest = conn.prepareStatement("SELECT userid, COUNT(*) AS amount, sum(level) AS totallvl FROM `" + prefix
		+ "jobs` GROUP BY userid ORDER BY totallvl DESC LIMIT " + start + ",100;");
	    res = prest.executeQuery();

	    while (res.next()) {
		PlayerInfo info = Jobs.getPlayerManager().getPlayerInfo(res.getInt("userid"));
		if (info == null)
		    continue;
		if (info.getName() == null)
		    continue;
		TopList top = new TopList(info, res.getInt("totallvl"), 0);
		names.add(top);
		if (names.size() >= 15)
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
	    prest = conn.prepareStatement("SELECT * FROM `" + prefix + "users` WHERE `player_uuid` = ?;");
	    prest.setString(1, uuid.toString());
	    res = prest.executeQuery();
	    while (res.next()) {
		pInfo = new PlayerInfo(res.getString("username"), res.getInt("id"), uuid, res.getLong("seen"));
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
	    prest = conn.prepareStatement("SELECT *  FROM `" + prefix + "users`;");
	    res = prest.executeQuery();
	    while (res.next()) {
		long seen = System.currentTimeMillis();
		try {
		    seen = res.getLong("seen");
		    Jobs.getPlayerManager().addPlayerToMap(new PlayerInfo(res.getString("username"), res.getInt("id"), UUID.fromString(res.getString("player_uuid")), seen));
		} catch (Exception e) {
		}
	    }
	} catch (SQLException e) {
	    e.printStackTrace();
	} finally {
	    close(res);
	    close(prest);
	}
	return;
    }

    public JobsPlayer loadFromDao(OfflinePlayer player) {

	JobsPlayer jPlayer = new JobsPlayer(player.getName(), player);
	jPlayer.playerUUID = player.getUniqueId();
	List<JobsDAOData> list = getAllJobs(player);
//	synchronized (jPlayer.saveLock) {
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
	Jobs.getJobsDAO().loadPoints(jPlayer);
//	}
	return jPlayer;
    }

    public void loadAllData() {
	Jobs.getPlayerManager().clearMaps();
	JobsConnection conn = getConnection();
	if (conn == null)
	    return;
	PreparedStatement prest = null;
	ResultSet res = null;
	try {
	    prest = conn.prepareStatement("SELECT *  FROM `" + prefix + "users`;");
	    res = prest.executeQuery();
	    while (res.next()) {
		try {
		    Jobs.getPlayerManager().addPlayerToMap(new PlayerInfo(res.getString("username"), res.getInt("id"), UUID.fromString(res.getString("player_uuid")), res.getLong(
			"seen")));
		} catch (Exception e) {
		}
	    }
	} catch (SQLException e) {
	    e.printStackTrace();
	} finally {
	    close(res);
	    close(prest);
	}
	return;
    }

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
	    prest = conn.prepareStatement("DELETE FROM `" + prefix + "archive` WHERE `userid` = ? AND `job` = ?;");
	    prest.setInt(1, jPlayer.getUserId());
	    prest.setString(2, job.getName());
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
	    prest = conn.prepareStatement("UPDATE `" + prefix + "jobs` SET `level` = ?, `experience` = ? WHERE `userid` = ? AND `job` = ?;");
	    for (JobProgression progression : player.getJobProgression()) {
		prest.setInt(1, progression.getLevel());
		prest.setInt(2, (int) progression.getExperience());
		prest.setInt(3, player.getUserId());
		prest.setString(4, progression.getJob().getName());
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
	    prest = conn.prepareStatement("UPDATE `" + prefix + "users` SET `seen` = ?, `username` = ? WHERE `id` = ?;");
	    prest.setLong(1, System.currentTimeMillis());
	    prest.setString(2, player.getUserName());
	    prest.setInt(3, player.getUserId());
	    prest.execute();
	} catch (SQLException e) {
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
	    prestt = conn.prepareStatement("INSERT INTO `" + prefix + "users` (`player_uuid`, `username`, `seen`) VALUES (?, ?, ?);");
	    prestt.setString(1, player.getPlayerUUID().toString());
	    prestt.setString(2, player.getUserName());
	    prestt.setLong(3, player.getSeen());
	    prestt.executeUpdate();
	} catch (SQLException e) {
	    e.printStackTrace();
	} finally {
	    close(prestt);
	}
	PreparedStatement prest = null;
	ResultSet res = null;
	try {
	    prest = conn.prepareStatement("SELECT `id` FROM `" + prefix + "users` WHERE `player_uuid` = ?;");
	    prest.setString(1, player.getPlayerUUID().toString());
	    res = prest.executeQuery();
	    res.next();
	    int id = res.getInt("id");
	    player.setUserId(id);
	    Jobs.getPlayerManager().addPlayerToMap(new PlayerInfo(player.getUserName(), id, player.getPlayerUUID(), player.getSeen()));
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
	PreparedStatement prest2 = null;
	try {
	    prest2 = conn.prepareStatement("DELETE FROM `" + prefix + "points` WHERE `userid` = ?;");
	    prest2.setInt(1, jPlayer.getUserId());
	    prest2.execute();
	} catch (SQLException e) {
	    e.printStackTrace();
	} finally {
	    close(prest2);
	}

	PreparedStatement prest = null;
	try {
	    PlayerPoints pointInfo = Jobs.getPlayerManager().getPointsData().getPlayerPointsInfo(jPlayer.getPlayerUUID());
	    prest = conn.prepareStatement("INSERT INTO `" + prefix + "points` (`totalpoints`, `currentpoints`, `userid`) VALUES (?, ?, ?);");
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
	    prest = conn.prepareStatement("SELECT `totalpoints`, `currentpoints` FROM `" + prefix + "points` WHERE `userid` = ?;");
	    prest.setInt(1, player.getUserId());
	    res = prest.executeQuery();

	    if (res.next()) {
		Jobs.getPlayerManager().getPointsData().addPlayer(player.getPlayerUUID(), res.getDouble("currentpoints"), res.getDouble("totalpoints"));
	    } else {
		Jobs.getPlayerManager().getPointsData().addPlayer(player.getPlayerUUID());
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

	    prest1 = conn.prepareStatement("UPDATE `" + prefix
		+ "log` SET `count` = ?, `money` = ?, `exp` = ?, `points` = ? WHERE `userid` = ? AND `time` = ? AND `action` = ? AND `itemname` = ?;");

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
	    prest2 = conn.prepareStatement("INSERT INTO `" + prefix
		+ "log` (`userid`, `time`, `action`, `itemname`, `count`, `money`, `exp`, `points`) VALUES (?, ?, ?, ?, ?, ?, ?, ?);");
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
	    prest = conn.prepareStatement("SELECT * FROM `" + prefix + "log` WHERE `userid` = ?  AND `time` = ? ;");
	    prest.setInt(1, player.getUserId());
	    prest.setInt(2, time);
	    res = prest.executeQuery();
	    while (res.next()) {

		HashMap<CurrencyType, Double> amounts = new HashMap<CurrencyType, Double>();
		amounts.put(CurrencyType.MONEY, res.getDouble("money"));
		amounts.put(CurrencyType.EXP, res.getDouble("exp"));
		amounts.put(CurrencyType.POINTS, res.getDouble("points"));

		Jobs.getLoging().loadToLog(player, res.getString("action"), res.getString("itemname"), res.getInt("count"), amounts);
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
    public void saveBlockProtection() {
	JobsConnection conn = getConnection();
	if (conn == null)
	    return;
	PreparedStatement insert = null;
	PreparedStatement update = null;
	PreparedStatement delete = null;
	try {

	    insert = conn.prepareStatement("INSERT INTO `" + prefix + "blocks` (`world`, `x`, `y`, `z`, `recorded`, `resets`) VALUES (?, ?, ?, ?, ?, ?);");
	    update = conn.prepareStatement("UPDATE `" + prefix + "blocks` SET `recorded` = ?, `resets` = ? WHERE `id` = ?;");
	    delete = conn.prepareStatement("DELETE from `" + getPrefix() + "blocks` WHERE `id` = ?;");

	    Jobs.getPluginLogger().info("Saving blocks");

	    conn.setAutoCommit(false);
	    int inserted = 0;
	    int updated = 0;
	    int deleted = 0;
	    Long current = System.currentTimeMillis();
	    Long mark = System.currentTimeMillis() - (Jobs.getGCManager().BlockProtectionDays * 24L * 60L * 60L * 1000L);
	    ConsoleCommandSender console = Bukkit.getServer().getConsoleSender();

	    for (Entry<World, HashMap<String, HashMap<String, HashMap<String, BlockProtection>>>> worlds : Jobs.getBpManager().getMap().entrySet()) {
		for (Entry<String, HashMap<String, HashMap<String, BlockProtection>>> regions : worlds.getValue().entrySet()) {
		    for (Entry<String, HashMap<String, BlockProtection>> chunks : regions.getValue().entrySet()) {
			for (Entry<String, BlockProtection> block : chunks.getValue().entrySet()) {
			    if (block.getValue() == null)
				continue;
			    switch (block.getValue().getAction()) {
			    case DELETE:
				delete.setInt(1, block.getValue().getId());
				delete.addBatch();

				deleted++;
				if (deleted % 10000 == 0) {
				    delete.executeBatch();
				    String message = ChatColor.translateAlternateColorCodes('&', "&6[Jobs] Removed " + deleted + " old block protection entries.");
				    console.sendMessage(message);
				}
				break;
			    case INSERT:
				if (block.getValue().getTime() < current && block.getValue().getTime() != -1)
				    continue;
				insert.setString(1, worlds.getKey().getName());
				insert.setInt(2, block.getValue().getPos().getBlockX());
				insert.setInt(3, block.getValue().getPos().getBlockY());
				insert.setInt(4, block.getValue().getPos().getBlockZ());
				insert.setLong(5, block.getValue().getRecorded());
				insert.setLong(6, block.getValue().getTime());
				insert.addBatch();

				inserted++;
				if (inserted % 10000 == 0) {
				    insert.executeBatch();
				    String message = ChatColor.translateAlternateColorCodes('&', "&6[Jobs] Added " + inserted + " new block protection entries.");
				    console.sendMessage(message);
				}
				break;
			    case UPDATE:
				if (block.getValue().getTime() < current && block.getValue().getTime() != -1)
				    continue;
				update.setLong(1, block.getValue().getRecorded());
				update.setLong(2, block.getValue().getTime());
				update.setInt(3, block.getValue().getId());
				update.addBatch();

				updated++;
				if (updated % 10000 == 0) {
				    update.executeBatch();
				    String message = ChatColor.translateAlternateColorCodes('&', "&6[Jobs] Upadated " + updated + " old block protection entries.");
				    console.sendMessage(message);
				}
				break;
			    case NONE:
				if (block.getValue().getTime() < current && block.getValue().getTime() != -1)
				    continue;
				if (block.getValue().getTime() == -1 && block.getValue().getRecorded() > mark)
				    continue;

				delete.setInt(1, block.getValue().getId());
				delete.addBatch();

				deleted++;
				if (deleted % 10000 == 0) {
				    delete.executeBatch();
				    Jobs.getPluginLogger().info("[Jobs] Removed " + deleted + " old block protection entries.");
				}
				break;
			    default:
				continue;
			    }
			}
		    }
		}
	    }

	    insert.executeBatch();
	    update.executeBatch();
	    delete.executeBatch();
	    conn.commit();
	    conn.setAutoCommit(true);
	    if (inserted > 0) {
		String message = ChatColor.translateAlternateColorCodes('&', "&6[Jobs] Added " + inserted + " new block protection entries.");
		console.sendMessage(message);
	    }
	    if (updated > 0) {
		String message = ChatColor.translateAlternateColorCodes('&', "&6[Jobs] Updated " + updated + " with new block protection entries.");
		console.sendMessage(message);
	    }
	    if (deleted > 0) {
		String message = ChatColor.translateAlternateColorCodes('&', "&6[Jobs] Deleted " + deleted + " old block protection entries.");
		console.sendMessage(message);
	    }
	} catch (SQLException e) {
	    e.printStackTrace();
	} finally {
	    close(insert);
	    close(update);
	    close(delete);
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
	ResultSet res = null;

	Jobs.getBpManager().timer = 0L;

	try {
	    prest = conn.prepareStatement("SELECT * FROM `" + prefix + "blocks`;");
	    res = prest.executeQuery();
	    int i = 0;
	    int ii = 0;

	    while (res.next()) {
		World world = Bukkit.getWorld(res.getString("world"));
		if (world == null)
		    continue;

		int id = res.getInt("id");
		int x = res.getInt("x");
		int y = res.getInt("y");
		int z = res.getInt("z");
		long resets = res.getLong("resets");
		Location loc = new Location(world, x, y, z);
		BlockProtection bp = Jobs.getBpManager().addP(loc, resets, true);
		bp.setId(id);
		long t = System.currentTimeMillis();
		bp.setRecorded(res.getLong("recorded"));
		bp.setAction(DBAction.NONE);
		i++;
		ii++;

		if (ii >= 100000) {
		    String message = ChatColor.translateAlternateColorCodes('&', "&6[Jobs] Loading (" + i + ") BP");
		    Bukkit.getServer().getConsoleSender().sendMessage(message);
		    ii = 0;
		}
		Jobs.getBpManager().timer += System.currentTimeMillis() - t;
	    }
	    if (i > 0) {
		String message = ChatColor.translateAlternateColorCodes('&', "&e[Jobs] loaded " + i + " block protection entries. " + Jobs.getBpManager().timer);
		Bukkit.getServer().getConsoleSender().sendMessage(message);
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
     * @param jobexplore - the information getting saved
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

	    prest2 = conn.prepareStatement("INSERT INTO `" + prefix + "exploreData` (`worldname`, `chunkX`, `chunkZ`, `playerNames`) VALUES (?, ?, ?, ?);");
	    conn.setAutoCommit(false);
	    int i = 0;

	    HashMap<String, ExploreRegion> temp = new HashMap<String, ExploreRegion>(Jobs.getExplore().getWorlds());

	    for (Entry<String, ExploreRegion> worlds : temp.entrySet()) {
		for (Entry<String, ExploreChunk> oneChunk : worlds.getValue().getChunks().entrySet()) {
		    if (oneChunk.getValue().getDbId() != null)
			continue;
		    prest2.setString(1, worlds.getKey());
		    prest2.setInt(2, oneChunk.getValue().getX());
		    prest2.setInt(3, oneChunk.getValue().getZ());
		    prest2.setString(4, oneChunk.getValue().serializeNames());
		    prest2.addBatch();
		    i++;
		}
	    }
	    prest2.executeBatch();
	    conn.commit();
	    conn.setAutoCommit(true);

	    if (i > 0) {
		String message = ChatColor.translateAlternateColorCodes('&', "&e[Jobs] Saved " + i + " new explorer entries.");
		ConsoleCommandSender console = Bukkit.getServer().getConsoleSender();
		console.sendMessage(message);
	    }
	} catch (SQLException e) {
	    e.printStackTrace();
	} finally {
	    close(prest2);
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
	    prest = conn.prepareStatement("UPDATE `" + prefix + "exploreData` SET `playerNames` = ? WHERE `id` = ?;");

	    int i = 0;

	    HashMap<String, ExploreRegion> temp = new HashMap<String, ExploreRegion>(Jobs.getExplore().getWorlds());

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

	    if (i > 0) {
		String message = ChatColor.translateAlternateColorCodes('&', "&e[Jobs] Updated " + i + " explorer entries.");
		ConsoleCommandSender console = Bukkit.getServer().getConsoleSender();
		console.sendMessage(message);
	    }
	} catch (SQLException e) {
	    e.printStackTrace();
	} finally {
	    close(prest);
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

	if (this.isTable(prefix + "explore")) {
	    PreparedStatement prest = null;
	    ResultSet res = null;
	    try {
		prest = conn.prepareStatement("SELECT * FROM `" + prefix + "explore`;");
		res = prest.executeQuery();
		while (res.next()) {
		    Jobs.getExplore().ChunkRespond(res.getString("playerName"), res.getString("worldname"), res.getInt("chunkX"), res.getInt("chunkZ"));
		}
	    } catch (SQLException e) {
		e.printStackTrace();
	    } finally {
		close(res);
		close(prest);
	    }
	    Statement stmt = null;
	    try {
		stmt = conn.createStatement();
		stmt.executeUpdate("DROP TABLE `" + prefix + "explore`;");
	    } catch (SQLException e) {
		e.printStackTrace();
	    } finally {
		close(stmt);
	    }
	}

	PreparedStatement prest = null;
	ResultSet res = null;
	try {
	    prest = conn.prepareStatement("SELECT * FROM `" + prefix + "exploreData`;");
	    res = prest.executeQuery();
	    while (res.next()) {
		Jobs.getExplore().load(res);
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
	List<Integer> nameList = new ArrayList<Integer>();
	if (conn == null)
	    return nameList;
	PreparedStatement prest = null;
	ResultSet res = null;
	try {
	    prest = conn.prepareStatement("SELECT `userid` FROM `" + prefix + "log` WHERE `time` >= ?  AND `time` <= ? ;");
	    prest.setInt(1, fromtime);
	    prest.setInt(2, untiltime);
	    res = prest.executeQuery();
	    while (res.next()) {
		if (!nameList.contains(res.getInt("userid")))
		    nameList.add(res.getInt("userid"));
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
    public ArrayList<TopList> toplist(String jobsname, int limit) {
	ArrayList<TopList> jobs = new ArrayList<TopList>();
	JobsConnection conn = getConnection();
	if (conn == null)
	    return jobs;
	PreparedStatement prest = null;
	ResultSet res = null;
	try {
	    prest = conn.prepareStatement("SELECT `userid`, `level`, `experience` FROM `" + prefix
		+ "jobs` WHERE `job` LIKE ? ORDER BY `level` DESC, LOWER(experience) DESC LIMIT " + limit + ", 15;");
	    prest.setString(1, jobsname);
	    res = prest.executeQuery();

	    while (res.next()) {
		PlayerInfo info = Jobs.getPlayerManager().getPlayerInfo(res.getInt("userid"));

		if (info == null)
		    continue;

		if (info.getName() == null)
		    continue;

		String name = info.getName();
		Player player = Bukkit.getPlayer(name);
		if (player != null) {

		    JobsPlayer jobsinfo = Jobs.getPlayerManager().getJobsPlayer(player);
		    Job job = Jobs.getJob(jobsname);
		    if (job != null && jobsinfo != null) {
			JobProgression prog = jobsinfo.getJobProgression(job);
			if (prog != null)
			    jobs.add(new TopList(info, prog.getLevel(), (int) prog.getExperience()));
		    }
		} else {
		    jobs.add(new TopList(info, res.getInt("level"), res.getInt("experience")));
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
	    prest = conn.prepareStatement("SELECT COUNT(*) FROM `" + prefix + "jobs` WHERE `job` = ?;");
	    prest.setString(1, job.getName());
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
     * Gets the current schema version
     * @return schema version number
     */
    protected int getSchemaVersion() {
	JobsConnection conn = getConnection();
	if (conn == null)
	    return 0;
	PreparedStatement prest = null;
	ResultSet res = null;
	int schema = 0;
	try {
	    prest = conn.prepareStatement("SELECT `value` FROM `" + prefix + "config` WHERE `key` = ?;");
	    prest.setString(1, "version");
	    res = prest.executeQuery();
	    if (res.next()) {
		schema = Integer.valueOf(res.getString(1));
	    }
	    res.close();
	} catch (SQLException e) {
	    e.printStackTrace();
	} catch (NumberFormatException e) {
	    e.printStackTrace();
	} finally {
	    close(res);
	    close(prest);
	}
	return schema;
    }

    /**
     * Updates schema to version number
     * @param version
     */
    protected void updateSchemaVersion(int version) {
	updateSchemaConfig("version", Integer.toString(version));
    }

    /**
     * Updates configuration value
     * @param key - the configuration key
     * @param value - the configuration value
     */
    private void updateSchemaConfig(String key, String value) {
	JobsConnection conn = getConnection();
	if (conn == null)
	    return;
	PreparedStatement prest = null;
	try {
	    prest = conn.prepareStatement("UPDATE `" + prefix + "config` SET `value` = ? WHERE `key` = ?;");
	    prest.setString(1, value);
	    prest.setString(2, key);
	    prest.execute();
	} catch (SQLException e) {
	    e.printStackTrace();
	} finally {
	    close(prest);
	}
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
	    return pool.getConnection();
	} catch (SQLException e) {
	    Jobs.getPluginLogger().severe("Unable to connect to the database: " + e.getMessage());
	    return null;
	}
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

    public HashMap<Integer, ArrayList<JobsDAOData>> getMap() {
	return map;
    }

}
