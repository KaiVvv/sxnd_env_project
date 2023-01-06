package com.briup.env.common.interfaces;

import java.util.Collection;

import com.briup.env.common.entity.Environment;

public interface DbStore extends EnvironmentInit{

    /**
     * 入库方法
     * @param
     */
    public void dbstore(Collection<Environment> coll);
}
