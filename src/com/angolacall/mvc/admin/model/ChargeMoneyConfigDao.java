package com.angolacall.mvc.admin.model;

import java.util.List;
import java.util.Map;

import javax.sql.DataSource;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.jdbc.core.JdbcTemplate;


public class ChargeMoneyConfigDao {
	private static Log log = LogFactory.getLog(ChargeMoneyConfigDao.class);

	private JdbcTemplate jdbc;

	public void setDataSource(DataSource ds) {
		jdbc = new JdbcTemplate(ds);
	}
	
	public void addChargeMoney(String chargeMoney, String giftMoney, String description) {
		String sql = "INSERT INTO charge_money_config (charge_money, gift_money, description) VALUES(?, ?, ?)";
		jdbc.update(sql, chargeMoney, giftMoney, description);
	}
	
	public List<Map<String, Object>> getChargeMoneyList() {
		String sql = "SELECT * FROM charge_money_config ORDER BY charge_money ASC";
		return jdbc.queryForList(sql);
	}
	
	public void deleteChargeMoney(String id) {
		String sql = "DELETE FROM charge_money_config WHERE id = ?";
		jdbc.update(sql, id);
	}
	
	public void editChargeMoney(String id, String chargeMoney, String giftMoney, String description) {
		String sql = "UPDATE charge_money_config SET charge_money = ?, gift_money = ?, description = ? WHERE id = ?";
		jdbc.update(sql, chargeMoney, giftMoney, description, id);
	}
}
