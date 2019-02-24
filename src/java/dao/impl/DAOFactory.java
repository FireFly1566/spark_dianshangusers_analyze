package dao.impl;

/*
*  DAO 工厂类
*
* */

import dao.ITaskDAO;

public class DAOFactory {

    /**
    * 获取任务管理DAO
    * */
    public static ITaskDAO getTaskDAO(){
        return new TaskDAOImpl();
    }



}
