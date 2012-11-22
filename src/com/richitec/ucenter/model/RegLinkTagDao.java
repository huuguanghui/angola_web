package com.richitec.ucenter.model;

import java.util.Map;

import javax.sql.DataSource;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.jdbc.core.JdbcTemplate;

import com.richitec.util.CryptoUtil;

public class RegLinkTagDao {
	private static Log log = LogFactory.getLog(RegLinkTagDao.class);
	private JdbcTemplate jdbc;

	public void setDataSource(DataSource ds) {
		jdbc = new JdbcTemplate(ds);
	}
	
	public String getRegLinkTag(String countryCode, String userName) {
		String inviterId = null;
		String sql = "SELECT inviter_id FROM invite_reg_link_tag WHERE country_code = ? AND username = ?";
		try {
			inviterId = jdbc.queryForObject(sql, new Object[] {countryCode, userName}, String.class);
		} catch (Exception e) {
			log.info(e.getMessage());
		}
		if (inviterId == null) {
			String digest = CryptoUtil.md5(countryCode + userName);
			inviterId = digest.substring(0, 10);
			log.info("inviter id: " + inviterId + " digest: " + digest);
			sql = "INSERT INTO invite_reg_link_tag (inviter_id, country_code, username) VALUES(?, ?, ?)";
			int rows = jdbc.update(sql, inviterId, countryCode, userName);
			if (rows > 0) {
				return inviterId;
			} else {
				return null;
			}
		} else {
			return inviterId;
		}
	}
	
	public Map<String, Object> getInviterMap(String inviterId) {
		String sql = "SELECT * FROM invite_reg_link_tag WHERE inviter_id = ?";
		return jdbc.queryForMap(sql, inviterId);
	}
}
