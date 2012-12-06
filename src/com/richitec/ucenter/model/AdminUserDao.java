package com.richitec.ucenter.model;

import javax.sql.DataSource;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.jdbc.core.JdbcTemplate;

public class AdminUserDao {
	private static Log log = LogFactory.getLog(AdminUserDao.class);

	private JdbcTemplate jdbc;

	public void setDataSource(DataSource ds) {
		jdbc = new JdbcTemplate(ds);
	}
	
	public boolean validateUser(String userName, String password) {
		String sql = "SELECT count(*) FROM admin_user WHERE username = ? AND password = ?";
		int rows = jdbc.queryForInt(sql, userName, password);
		if (rows > 0) {
			return true;
		} else {
			return false;
		}
	}
}
