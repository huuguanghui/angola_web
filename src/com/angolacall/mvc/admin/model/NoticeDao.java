package com.angolacall.mvc.admin.model;

import java.util.List;
import java.util.Map;

import javax.sql.DataSource;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.jdbc.core.JdbcTemplate;

import com.angolacall.constants.NoticeStatus;

public class NoticeDao {
	private static Log log = LogFactory.getLog(NoticeDao.class);

	private JdbcTemplate jdbc;

	public void setDataSource(DataSource ds) {
		jdbc = new JdbcTemplate(ds);
	}
	
	/**
	 * add notice to all user
	 * @param content
	 */
	public void addNotice(String content) {
		String sql = "INSERT INTO notices (content) VALUES(?)";
		jdbc.update(sql, content);
	}
	
	/**
	 * send notice to specified user
	 * @param countryCode
	 * @param userName
	 * @param content
	 */
	public void sendNoticeToUser(String countryCode, String userName, String content) {
		String sql = "INSERT INTO notices (content, status, to_user) VALUES(?,?,?)";
		jdbc.update(sql, content, NoticeStatus.publish.name(), countryCode + userName);
	}
	
	public void hideNotice(Integer id) {
		updateStatus(id, NoticeStatus.hidden);
	}
	
	public void updateStatus(Integer id, NoticeStatus status) {
		String sql = "UPDATE notices SET status = ? WHERE id = ?";
		jdbc.update(sql, status.name(), id);
	}
	
	public void publishNotice(Integer id) {
		updateStatus(id, NoticeStatus.publish);
	}
	
	public void saveNotice(Integer id, String content) {
		String sql = "UPDATE notices SET content = ? WHERE id = ?";
		jdbc.update(sql, content, id);
	}
	
	public List<Map<String, Object>> getNotices() {
		String sql = "SELECT id, content, UNIX_TIMESTAMP(createtime) AS time, status FROM notices WHERE to_user = 'all' AND status <> ? ORDER BY createtime DESC";
		return jdbc.queryForList(sql, NoticeStatus.hidden.name());
	}
	
	/**
	 * get the published notices whose id is larger than currentId
	 * @param currentId
	 * @param countryCode
	 * @param userName
	 * 
	 * @return
	 */
	public List<Map<String, Object>> getNewPublishedNotices(Integer currentId, String countryCode, String userName) {
		String sql = "SELECT id, content, UNIX_TIMESTAMP(createtime) AS time, status FROM notices WHERE DATE(createtime) >= DATE_SUB(CURDATE(), INTERVAL 1 MONTH) AND status = ? AND id > ? AND (to_user = ? OR to_user = ?) ORDER BY createtime DESC";
		return jdbc.queryForList(sql, NoticeStatus.publish.name(), currentId, countryCode + userName, "all");
	}
}
