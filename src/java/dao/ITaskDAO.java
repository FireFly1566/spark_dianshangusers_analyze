package dao;


import domain.Task;

/**
*  任务管理 DAO 接口
*
* */
public interface ITaskDAO {

    /**
     * @Description:  根据主键查询任务
     * @param taskid 主键
     * @return domain.Task 任务
     */
    Task findById(long taskid);

}
