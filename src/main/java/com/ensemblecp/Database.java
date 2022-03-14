package com.ensemblecp;// Imports
import javafx.scene.control.RadioButton;
import javafx.scene.control.ToggleGroup;

import java.sql.*;
import java.time.Instant;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;

// Project Class
/**
 * A class used to temporarily create a java object
 * which represents the data of a project.
 */
public class Database {
    /* Class variables */
    public Connection conn;
    public static String databaseName = "Ensemble"; // Name of database in system, prepended on table names

    /* Class Constructors */
    /**
     * Default constructor.
     */
    public Database() throws SQLException {
        this.conn = DriverManager.getConnection("jdbc:mysql://34.150.158.26:3306","root","G6DevsOP2487!");
    }

    public ResultSet getProject(int pid) throws SQLException {
        // Get tuple
        PreparedStatement preparedStmt = conn.prepareStatement("select * from " + databaseName + ".Project where pid = ?");
        preparedStmt.setInt(1, pid);
        ResultSet rs = preparedStmt.executeQuery();
        System.out.println("Success on get project");
        return rs;
    }


    /* Class Methods */
    /**
     *  Create project record
     */
    public ResultSet createProject(HashMap<String, String> info) throws SQLException {
        // Insert record
        String query = " insert into "+ databaseName + ".Project"
                + " values (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
        PreparedStatement preparedStmt = conn.prepareStatement(query);
        preparedStmt.setInt(1, Integer.parseInt(info.get("pid")));
        preparedStmt.setString (2, info.get("title"));
        preparedStmt.setString (3, info.get("description"));
        preparedStmt.setFloat(4, Float.parseFloat(info.get("investmentCosts")));
        preparedStmt.setFloat(5, Float.parseFloat(info.get("budget")));
        preparedStmt.setFloat(6, Float.parseFloat(info.get("roi")));
        preparedStmt.setDate(7, Date.valueOf(info.get("kickoff")));
        preparedStmt.setDate(8, Date.valueOf(info.get("deadline")));
        preparedStmt.setFloat(9, Float.parseFloat(info.get("issueScore")));
        preparedStmt.setString(10, info.get("tag1"));
        preparedStmt.setString(11, info.get("tag2"));
        preparedStmt.setString(12, info.get("tag3"));
        preparedStmt.setString(13, info.get("tag4"));
        preparedStmt.setBoolean(14, Boolean.parseBoolean(info.get("complete")));
        preparedStmt.setInt(15, Integer.parseInt(info.get("manid")));
        preparedStmt.execute();

        String charPid = Project.IDtoChars(Integer.parseInt(info.get("pid")));
        String createTable;
        Statement stmt = conn.createStatement();;
        // Add related tables
        createTable = "create table " + databaseName + "." + charPid + "_Components("
                + " cid int not null, template varchar(128) not null, constraint " + charPid + "_Component_pk primary key (cid));";
        stmt.execute(createTable);

        createTable = "create table " + databaseName + "." + charPid + "_Team("
                + " memid int, constraint " + charPid + "_Team_pk primary key (memid));";
        stmt.execute(createTable);

        createTable = "create table " + databaseName + "." + charPid + "_Tasks("
                + "tid int primary key,"
                + "memid int not null,"
                + "description varchar(128) not null,"
                + "kickoff date not null,"
                + "deadline date not null,"
                + "complete boolean not null)";
        stmt.execute(createTable);

        createTable = "create table " + databaseName + "." + charPid + "_Issues("
                + "memid int not null,"
                + "message varchar(128) not null,"
                + "type int not null,"
                + "state int not null,"
                + "constraint " + charPid + "_Issues_pk primary key (memid, message))";
        stmt.execute(createTable);

        // Get tuple
        preparedStmt = conn.prepareStatement("select * from " + databaseName + ".Project where pid = ?");
        preparedStmt.setInt(1, Integer.parseInt(info.get("pid")));
        ResultSet rs = preparedStmt.executeQuery();
        System.out.println("Success on create project");
        return rs;
    }

    public ResultSet updateProject(HashMap<String, String> info) throws SQLException {
        // Update record
        String query = " update "+ databaseName +".Project" +
                " set investmentCosts = ?, budget = ?, roi = ?, kickoff = ?," +
                " deadline = ?, issueScore = ?, tag1 = ?, tag2 = ?, tag3 = ?, tag4 = ?," +
                " complete = ?, title = ?, description = ? where pid = ?";
        PreparedStatement preparedStmt = conn.prepareStatement(query);
        preparedStmt.setFloat(1, Float.parseFloat(info.get("investmentCosts")));
        preparedStmt.setFloat(2, Float.parseFloat(info.get("budget")));
        preparedStmt.setFloat(3, Float.parseFloat(info.get("roi")));
        preparedStmt.setDate(4, Date.valueOf(info.get("kickoff")));
        preparedStmt.setDate(5, Date.valueOf(info.get("deadline")));
        preparedStmt.setFloat(6, Float.parseFloat(info.get("issueScore")));
        preparedStmt.setString(7, info.get("tag1"));
        preparedStmt.setString(8, info.get("tag2"));
        preparedStmt.setString(9, info.get("tag3"));
        preparedStmt.setString(10, info.get("tag4"));
        preparedStmt.setBoolean(11, Boolean.parseBoolean(info.get("complete")));
        preparedStmt.setString(12, info.get("title"));
        preparedStmt.setString(13, info.get("description"));
        preparedStmt.setInt(14, Integer.parseInt(info.get("pid")));
        preparedStmt.execute();

        // Get tuple
        preparedStmt = conn.prepareStatement("select * from "+ databaseName +".Project where pid = ?");
        preparedStmt.setInt(1, Integer.parseInt(info.get("pid")));
        ResultSet rs = preparedStmt.executeQuery();
        System.out.println("Success on update project");
        return rs;
    }

    public void removeProject(int pid) throws SQLException {
        // Delete record
        String query = " delete from " + databaseName + ".Project where pid = ?";
        PreparedStatement preparedStmt = conn.prepareStatement(query);
        preparedStmt.setInt(1, pid);
        preparedStmt.execute();

        String charPid = Project.IDtoChars(pid);
        String dropTable;
        Statement stmt = conn.createStatement();
        // Delete related tables
        dropTable = "drop table " + databaseName + "." + charPid + "_Components;";
        stmt.execute(dropTable);

        dropTable = "drop table " + databaseName + "." + charPid + "_Team;";
        stmt.execute(dropTable);

        dropTable = "drop table " + databaseName + "." + charPid + "_Tasks;";
        stmt.execute(dropTable);

        dropTable = "drop table " + databaseName + "." + charPid + "_Issues;";
        stmt.execute(dropTable);

        // Remove all component tables
            // TODO: implement this

        // Display message
        System.out.println("Success on removing a project");
    }

    public ResultSet getProjects() throws SQLException {
        String query = "select * from " + databaseName + ".Project";
        PreparedStatement preparedStmt = conn.prepareStatement(query);
        ResultSet rs = preparedStmt.executeQuery();
        System.out.println("Success on querying project table");
        return rs;
    }

    public ResultSet getComponents() throws SQLException {
        String query = "select * from " + databaseName + ".Component";
        PreparedStatement preparedStmt = conn.prepareStatement(query);
        ResultSet rs = preparedStmt.executeQuery();
        System.out.println("Success on querying component table");
        return rs;
    }

    public void closeDB() throws SQLException {
        conn.close();
    }

    public ResultSet getComponentData(int pid, int cid) throws SQLException { // TODO: Confirm this works
        String charPid = Project.IDtoChars(pid);
        String charCid = Project.IDtoChars(cid);
        String query = "select * from " + databaseName + "." + charPid + "_" + charCid + "_Data";
        PreparedStatement preparedStmt = conn.prepareStatement(query);
        ResultSet rs = preparedStmt.executeQuery();
        System.out.println("Success on querying component data table");
        return rs;
    }

    public ResultSet getComponentTablePart(int pid, int cid, int partid) throws SQLException { // TODO: Confirm this works
        String charPid = Project.IDtoChars(pid);
        String charCid = Project.IDtoChars(cid);
        String charPartid = Project.IDtoChars(partid);
        String query = "select * from " + databaseName + "." + charPid + "_" + charCid + "_" + charPartid + "_Value";
        PreparedStatement preparedStmt = conn.prepareStatement(query);
        ResultSet rs = preparedStmt.executeQuery();
        System.out.println("Success on querying component data table");
        return rs;
    }

    public ResultSet getProjectMembers(int pid) throws SQLException { // TODO: Confirm this works
        String charPid = Project.IDtoChars(pid);
        String query = "select * from " + databaseName + ".ProjectMember INNER JOIN " + databaseName + "." + charPid + "_Team USING(memid)";
        PreparedStatement preparedStmt = conn.prepareStatement(query);
        ResultSet rs = preparedStmt.executeQuery();
        System.out.println("Success on querying project team table");
        return rs;
    }

    public ResultSet getTimelines() throws SQLException {
        String query = "select title, kickoff, deadline from " + databaseName + ".Project";
        PreparedStatement preparedStmt = conn.prepareStatement(query);
        ResultSet rs = preparedStmt.executeQuery();
        System.out.println("Success on querying project timelines");
        return rs;
    }

    public ResultSet matchUsername(String username) throws SQLException { // TODO: Confirm this works
        String query = "select * from " + databaseName + ".Account where username=?";
        PreparedStatement preparedStmt = conn.prepareStatement(query);
        preparedStmt.setString(1, username.toString());
        ResultSet rs = preparedStmt.executeQuery();
        System.out.println("Success on querying users with matching usernames");
        return rs;
    }

    public ResultSet getMemberAccount(int id) throws SQLException { // TODO: Confirm this works
        String query = "select * from " + databaseName + ".ProjectMember where memid=?";
        PreparedStatement preparedStmt = conn.prepareStatement(query);
        preparedStmt.setInt(1, id);
        ResultSet rs = preparedStmt.executeQuery();
        System.out.println("Success on querying project members table");
        return rs;
    }

    public ResultSet getManagerAccount(int id) throws SQLException { // TODO: Confirm this works
        String query = "select * from " + databaseName + ".ProjectManager where manid=?";
        PreparedStatement preparedStmt = conn.prepareStatement(query);
        preparedStmt.setInt(1, id);
        ResultSet rs = preparedStmt.executeQuery();
        System.out.println("Success on querying project managers table");
        return rs;
    }

    public ResultSet getProjectComponents(int pid) throws SQLException {
        String charPid = Project.IDtoChars(pid);
        String query = "select * from " + databaseName + "." + charPid + "_Components NATURAL JOIN " + databaseName + ".Component";
        PreparedStatement preparedStmt = conn.prepareStatement(query);
        ResultSet rs = preparedStmt.executeQuery();
        System.out.println("Success on querying project components");
        return rs;
    }

    public void createLog(HashMap<String, String> info) throws SQLException, InterruptedException {
        String query = "insert into " + databaseName + ".Log values(?, ?, ?, ?);";
        PreparedStatement preparedStatement = conn.prepareStatement(query);
        preparedStatement.setInt(1, Integer.parseInt(info.get("manid")));
        preparedStatement.setInt(2, Integer.parseInt(info.get("pid")));
        preparedStatement.setString(4, info.get("message"));

        // Get time and try to insert
        while(true) {
            try {
                preparedStatement.setTimestamp(3, Timestamp.from(Instant.now()));
                preparedStatement.execute();
                break;
            } catch (SQLIntegrityConstraintViolationException e) {
                // Error on insertion, try again
                wait(100);
            }
        }

    }

    public ResultSet createIssue(HashMap<String, String> info) throws SQLException { // TODO: Confirm this works
        String charPid = Project.IDtoChars(Main.curProject.getPid());
        String query = "insert into " + databaseName + "." + charPid + "_Issues values (?, ?, ?, ?)";
        PreparedStatement preparedStmt = conn.prepareStatement(query);
        preparedStmt.setInt(1, Main.account.getId());
        preparedStmt.setString(2, info.get("message"));
        preparedStmt.setInt(3, Integer.parseInt(info.get("type")));
        preparedStmt.setInt(4, IssueType.NEW); // Set issue as new (not seen or done)
        preparedStmt.execute();

        query = "select * from " + databaseName + "." + charPid + "_Issues where memid=? and message=?";
        preparedStmt = conn.prepareStatement(query);
        preparedStmt.setInt(1, Main.account.getId());
        preparedStmt.setString(2, info.get("message"));
        ResultSet rs = preparedStmt.executeQuery();
        System.out.println("Success on creating issue record");
        return rs;
    }
}
// End of Database Class
