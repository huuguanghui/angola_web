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
	
	public void addNotice(String content) {
		String sql = "INSERT INTO notices (content) VALUES(?)";
		jdbc.update(sql, content);
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
		String sql = "SELECT id, content, UNIX_TIMESTAMP(createtime) AS time, status FROM notices WHERE status <> ?";
		return jdbc.queryForList(sql, NoticeStatus.hidden.name());
	}
	
	/**
	 * get the published notices whose id is larger than currentId
	 * @param currentId
	 * @return
	 */
	public List<Map<String, Object>> getNewPublishedNotices(Integer currentId) {
		String sql = "SELECT id, content, UNIX_TIMESTAMP(createtime) AS time, status FROM notices WHERE status = ? AND id > ?";
		return jdbc.queryForList(sql, NoticeStatus.publish.name(), currentId);
	}
}
