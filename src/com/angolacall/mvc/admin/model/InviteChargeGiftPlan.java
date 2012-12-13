package com.angolacall.mvc.admin.model;

public class InviteChargeGiftPlan {

	public static double calculateGiftMoney(double money) {
		return plan1(money);
	}
	
	public static double plan1(double money) {
		double ret = 0;
		double rate = 0.1;
		if (money >= 50 && money < 100) {
			ret = 50 * rate;
		} else if (money >= 100 && money < 150) {
			ret = 100 * rate;
		} else if (money >= 150 && money < 200) {
			ret = 150 * rate;
		} else if (money >= 200) {
			ret = 200 * rate;
		}
		return ret;
	}
}
