package com.silentgo.core.plugin.db;

import java.util.List;

/**
 * Project : silentgo
 * com.silentgo.core.plugin.db.bridge
 *
 * @author <a href="mailto:teddyzhu15@gmail.com" target="_blank">teddyzhu</a>
 *         <p>
 *         Created by teddyzhu on 16/9/22.
 */
public interface BaseDao<T extends TableModel> {

    //QUERY
    public T queryByPrimaryKey(Object id);
    public List<T> queryByPrimaryKeys(List<Object> ids);

    public List<T> queryByModelSelective(T t);

    //ADD
    public int addByRow(T t);

    public int addByRows(List<T> t);

    //UPDATE
    public int updateByPrimaryKey(T t, String... columns);

    public int updateByPrimaryKeySelective(T t);

    //DELETE
    public int deleteByPrimaryKey(Object t);

    public int deleteByPrimaryKeys(List<Object> t);


}
