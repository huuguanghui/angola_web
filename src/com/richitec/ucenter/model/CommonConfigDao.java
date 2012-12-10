package com.richitec.ucenter.model;

import javax.sql.DataSource;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.jdbc.core.JdbcTemplate;

public class CommonConfigDao {
	private static Log log = LogFactory.getLog(CommonConfigDao.class);

	private JdbcTemplate jdbc;

	public void setDataSource(DataSource ds) {
		jdbc = new JdbcTemplate(ds);
	}

	public void setValue(String key, String value) {
		String sql = "UPDATE common_config SET attri_value = ? WHERE attri_key = ?";
		int rows = jdbc.update(sql, value, key);
		if (rows <= 0) {
			sql = "INSERT INTO common_config(attri_key, attri_value) VALUES(?,?)";
			jdbc.update(sql, key, value);
		}
	}

	public String getValue(String key) {
		String sql = "SELECT attri_value FROM common_config WHERE attri_key = ?";
		String ret = null;
		try {
			ret = jdbc.queryForObject(sql, new Object[] { key }, String.class);
		} catch (Exception e) {
			log.info(e.getMessage());
		}
		return ret;
	}
}
