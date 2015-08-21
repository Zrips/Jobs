package com.gamingmesh.jobs.economy;

public class PaymentData {

	Long time = 0L;
	Long lastAnnouced = 0L;
	Double Payment = 0.0;
	public boolean Informed = false;

	public PaymentData(Long time, Double Payment, Long lastAnnouced, boolean Informed) {
		this.time = time;
		this.Payment = Payment;
		this.lastAnnouced = lastAnnouced;
		this.Informed = Informed;
	}

	public PaymentData() {
	}

	public Long GetTime() {
		return this.time;
	}

	public Double GetAmount() {
		return this.Payment;
	}

	public Double GetAmountBylimit(int limit) {
		if (this.Payment > limit)
			return (double) limit;
		return (int) (this.Payment * 100) / 100.0;
	}

	public Long GetLastAnnounced() {
		return this.lastAnnouced;
	}

	public boolean IsAnnounceTime(int time) {
		if (this.lastAnnouced + (time * 1000) > System.currentTimeMillis())
			return false;
		SetAnnouncmentTime();
		return true;
	}

	public void SetAnnouncmentTime() {
		this.lastAnnouced = System.currentTimeMillis();
	}

	public void AddNewAmount(Double Payment) {
		this.time = System.currentTimeMillis();
		this.Payment = Payment;
	}

	public void Setinformed() {
		this.Informed = true;
	}

	public void SetNotInformed() {
		this.Informed = false;
	}

	public void AddAmount(Double Payment) {
		this.Payment = this.Payment + Payment;
	}

	public int GetLeftTime(int time) {
		int left = 0;
		if (this.time + (time * 1000) > System.currentTimeMillis())
			left = (int) ((this.time + (time * 1000) - System.currentTimeMillis()) / 1000);
		return left;
	}

	public boolean IsOverMoneyLimit(int limit) {
		if (this.Payment < limit)
			return false;
		return true;
	}

	public boolean IsOverTimeLimit(int time) {
		if (this.time + (time * 1000) > System.currentTimeMillis())
			return false;

		if (this.Informed)
			this.Informed = false;
		this.time = System.currentTimeMillis();
		this.Payment = 0.0;
		return true;
	}

	public boolean IsReachedLimit(int time, int limit) {
		if (IsOverMoneyLimit(limit) && !IsOverTimeLimit(time))
			return true;
		return false;
	}

	public int GetLeftsec(int time) {
		int lefttime1 = GetLeftTime(time);
		int sec = 0;
		if (lefttime1 >= 3600) {
			lefttime1 = lefttime1 - ((int) (lefttime1 / 3600) * 3600);
			if (lefttime1 > 60 && lefttime1 < 3600) {
				sec = lefttime1 - ((int) (lefttime1 / 60) * 60);
			} else if (lefttime1 < 60)
				sec = lefttime1;
		} else if (lefttime1 > 60 && lefttime1 < 3600) {
			sec = lefttime1 - ((int) (lefttime1 / 60) * 60);
		} else
			sec = lefttime1;
		return sec;
	}

	public int GetLeftMin(int time) {
		int lefttime1 = GetLeftTime(time);
		int min = 0;
		if (lefttime1 >= 3600) {
			lefttime1 = lefttime1 - ((int) (lefttime1 / 3600) * 3600);
			if (lefttime1 > 60 && lefttime1 < 3600)
				min = lefttime1 / 60;
		} else if (lefttime1 > 60 && lefttime1 < 3600)
			min = lefttime1 / 60;
		return min;
	}

	public int GetLeftHour(int time) {
		int lefttime1 = GetLeftTime(time);
		int hour = 0;
		if (lefttime1 >= 3600) {
			hour = lefttime1 / 3600;
		}
		return hour;
	}
}
