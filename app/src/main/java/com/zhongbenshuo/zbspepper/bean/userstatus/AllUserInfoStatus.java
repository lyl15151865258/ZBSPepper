package com.zhongbenshuo.zbspepper.bean.userstatus;

import java.util.ArrayList;
import java.util.List;

public class AllUserInfoStatus {

    private int department_id;

    private String department;

    private int priority;

    private List<UserInfoStatus> users = new ArrayList<>();

    public int getDepartment_id() {
        return department_id;
    }

    public void setDepartment_id(int department_id) {
        this.department_id = department_id;
    }

    public String getDepartment() {
        return department;
    }

    public void setDepartment(String department) {
        this.department = department;
    }

    public int getPriority() {
        return priority;
    }

    public void setPriority(int priority) {
        this.priority = priority;
    }

    public List<UserInfoStatus> getUsers() {
        return users;
    }

    public void setUsers(List<UserInfoStatus> users) {
        this.users = users;
    }


}
