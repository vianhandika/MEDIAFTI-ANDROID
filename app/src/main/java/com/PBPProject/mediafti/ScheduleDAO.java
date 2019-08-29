package com.PBPProject.mediafti;

public class ScheduleDAO {
    String hari,sesi,matkul,ruang;

    public ScheduleDAO() {
    }

    public ScheduleDAO(String hari, String sesi, String matkul, String ruang) {
        this.hari = hari;
        this.sesi = sesi;
        this.matkul = matkul;
        this.ruang = ruang;
    }

    public void setHari(String hari) {
        this.hari = hari;
    }

    public void setSesi(String sesi) {
        this.sesi = sesi;
    }

    public void setMatkul(String matkul) {
        this.matkul = matkul;
    }

    public void setRuang(String ruang) {
        this.ruang = ruang;
    }

    public String getHari() {
        return hari;
    }

    public String getSesi() {
        return sesi;
    }

    public String getMatkul() {
        return matkul;
    }

    public String getRuang() {
        return ruang;
    }

}
