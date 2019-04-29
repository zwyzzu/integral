package com.zhangwy.user;

import java.io.Serializable;

import android.text.TextUtils;

/**
 * Author: zhangwy
 * 创建时间：2015年12月1日 下午5:36:27
 * 修改时间：2015年12月1日 下午5:36:27
 * Description: 
 **/
@SuppressWarnings("unused")
public class IUserInfoEntity extends IUserBaseEntity {
	private static final long serialVersionUID = 8925511102300072548L;

	private String nick_name;
	private String mail;
	private String user_name;
	private String sex;
	private String phone;
	private UserIcon user_icon;
	private String real_name;
	private String birthdate;
	private String user_id;
	private String token;
	private String model;

	public String getNick_name() {
		return nick_name;
	}

	public void setNick_name(String nick_name) {
		this.nick_name = nick_name;
	}

	public String getMail() {
		return mail;
	}

	public void setMail(String mail) {
		this.mail = mail;
	}

	public String getUser_name() {
		return user_name;
	}

	public void setUser_name(String user_name) {
		this.user_name = user_name;
	}

	public String getSex() {
		return sex;
	}

	public void setSex(String sex) {
		this.sex = sex;
	}

	public String getPhone() {
		return phone;
	}

	public void setPhone(String phone) {
		this.phone = phone;
	}

	public UserIcon getUser_icon() {
		return user_icon;
	}

	public void setUser_icon(UserIcon user_icon) {
		this.user_icon = user_icon;
	}

	public String getReal_name() {
		return real_name;
	}

	public void setReal_name(String real_name) {
		this.real_name = real_name;
	}

	public String getBirthdate() {
		return birthdate;
	}

	public void setBirthdate(String birthdate) {
		this.birthdate = birthdate;
	}

	public String getUser_id() {
		return user_id;
	}

	public void setUser_id(String user_id) {
		this.user_id = user_id;
	}

	public String getToken() {
		return token;
	}

	public void setToken(String token) {
		this.token = token;
	}

	public String getModel() {
		return model;
	}

	public void setModel(String model) {
		this.model = model;
	}

	public String getNickName(){
		if (!TextUtils.isEmpty(nick_name))
			return this.nick_name;
		return this.user_name;
	}

	public String getRealIcon() {
		if (user_icon == null)
			return "";

		if (!TextUtils.isEmpty(user_icon.getMiddle()))
			return user_icon.getMiddle();

		if (!TextUtils.isEmpty(user_icon.getBig()))
			return user_icon.getBig();

		if (!TextUtils.isEmpty(user_icon.getSmall()))
			return user_icon.getSmall();

		if (!TextUtils.isEmpty(user_icon.getOrig()))
			return user_icon.getOrig();

		return "";
	}

	public static class UserIcon implements Serializable {

		private static final long serialVersionUID = -553575094491736266L;

		private String big;
		private String middle;
		private String orig;
		private String small;

		public String getBig() {
			return big;
		}

		public void setBig(String big) {
			this.big = big;
		}

		public String getMiddle() {
			return middle;
		}

		public void setMiddle(String middle) {
			this.middle = middle;
		}

		public String getOrig() {
			return orig;
		}

		public void setOrig(String orig) {
			this.orig = orig;
		}

		public String getSmall() {
			return small;
		}

		public void setSmall(String small) {
			this.small = small;
		}

	}
}
