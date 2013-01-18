package com.angolacall.mvc.admin.model;

public class InviteChargeGiftPlan {

	public static double calculateGiftMoney(double money) {
		return plan2(money);
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
	
	public static double plan2(double money) {
		double ret = 0;
		double baseMoney = 5;
		ret = ((int)(money / 50)) * baseMoney;
		return ret;
	}
	
	
	public static void main(String args[]) {
		double money = plan2(51);
		System.out.println("gift money: " + money);
	}
}
