package com.angolacall.mvc.admin.model;

import java.util.Map;

import javax.sql.DataSource;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.jdbc.core.JdbcTemplate;

import com.angolacall.constants.RegisterActivityStatus;

public class RegisterActivityDao {
	private static Log log = LogFactory.getLog(RegisterActivityDao.class);
	private static final String TABLE_NAME = "reg_activity_config";
	private JdbcTemplate jdbc;

	public void setDataSource(DataSource ds) {
		jdbc = new JdbcTemplate(ds);
		setupData();
	}

	private void setupData() {
		String sql = "SELECT count(id) FROM " + TABLE_NAME + " WHERE id = ?";
		int rows = 0;
		try {
			rows = jdbc.queryForInt(sql, 0);
		} catch (Exception e) {
			log.info(e.getMessage());
		}
		if (rows <= 0) {
			// create new data
			sql = "INSERT INTO " + TABLE_NAME
					+ " (id, start_date, end_date) VALUES(?,?,?)";
			try {
				jdbc.update(sql, 0, "2013-01-22", "2013-01-22");
			} catch (Exception e) {
				log.error(e.getMessage());
				e.printStackTrace();
			}
		}
	}

	private void setActivityStatus(Integer id, RegisterActivityStatus status) {
		String sql = "UPDATE " + TABLE_NAME + " SET status = ? WHERE id = ?";
		jdbc.update(sql, status.name(), id);
	}

	public void closeActivity(Integer id) {
		setActivityStatus(id, RegisterActivityStatus.close);
	}

	public void openActivity(Integer id) {
		setActivityStatus(id, RegisterActivityStatus.open);
	}

	public void editActivity(Integer id, String startDate, String endDate, String giftMoney) {
		String sql = "UPDATE "
				+ TABLE_NAME
				+ " SET start_date = ?, end_date = ?, gift_money = ? WHERE id = ?";
		jdbc.update(sql, startDate, endDate, giftMoney, id);
	}

	/**
	 * get current activity gift money
	 * @param currentDate - format "0000-00-00"
	 * @return
	 */
	public Double getActivityGiftMoney(String currentDate) {
		String sql = "SELECT gift_money FROM "
				+ TABLE_NAME
				+ " WHERE id = ? AND status = ? AND start_date <= ? AND end_date >= ?";
		Double ret = 0.00;
		try {
			ret = jdbc.queryForObject(sql, new Object[] { 0,
					RegisterActivityStatus.open.name(), currentDate,
					currentDate }, Double.class);
		} catch (Exception e) {

		}
		return ret;
	}
	
	public Map<String, Object> getRegisterActivity() {
		String sql = "SELECT * FROM " + TABLE_NAME + " WHERE id = ?";
		return jdbc.queryForMap(sql, 0);
	}
}
